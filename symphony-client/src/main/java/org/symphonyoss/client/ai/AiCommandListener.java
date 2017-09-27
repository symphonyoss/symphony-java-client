/*
 *
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.symphonyoss.client.ai;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.ai.utils.AiSpellParser;
import org.symphonyoss.client.common.AiConstants;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that listens in on a chat, and determines if the user's input
 * matches a command.
 *
 * @author Nichalas Tarsillo
 */
public class AiCommandListener implements ChatListener {
    private static final Logger logger = LoggerFactory.getLogger(AiCommandListener.class);

    private static final LinkedHashMap<String, HashSet<AiCommandListener>> listeners = new LinkedHashMap<>();
    private SymMessage lastAnsweredMessage;

    private final SymphonyClient symClient;

    private final ArrayList<AiCommand> activeCommands = new ArrayList<>();
    private final ConcurrentHashMap<String, AiLastCommand> lastResponse = new ConcurrentHashMap<>();
    private final AiResponder aiResponder;

    public AiCommandListener(SymphonyClient symClient) {
        this.symClient = symClient;
        aiResponder = new AiResponder(symClient);
    }

    /**
     * A method that allows other classes to determine if a given message
     * matches a command in this command listener
     *
     * @param message   the message
     * @param symClient Symphony client
     * @return True if the message is a command
     */
    @SuppressWarnings("unused")
    public static boolean isCommand(SymMessage message, SymphonyClient symClient) {

        logger.debug("Received message for response.");

        MlMessageParser mlMessageParser;

        try {

            mlMessageParser = new MlMessageParser(symClient);
            mlMessageParser.parseMessage(message.getMessage());

            String[] chunks = mlMessageParser.getTextChunks();

            return chunks[0].charAt(0) == AiConstants.COMMAND;

        } catch (Exception e) {
            logger.error("Could not parse message {}", message.getMessage(), e);
        }

        return false;
    }

    /**
     * When a chat message is received, check if it starts with
     * the command char. If it does, process message.
     *
     * @param message the received message
     */
    @Override
    public void onChatMessage(SymMessage message) {


        if (message == null || message.getFromUserId() == null) {

            logger.warn("Received null message. Ignoring.");

            return;
        }

        logger.debug("Received message for response.");

        MlMessageParser mlMessageParser;

        try {

            mlMessageParser = new MlMessageParser(symClient);

            //logger.info("Convert Message {}",message.getMessage().replace("\u00a0"," ")  );

            //Minor cleanup
            message.setMessage(message.getMessage().replace("\u00a0", " "));
            mlMessageParser.parseMessage(message.getMessage().trim());

            String[] chunks = mlMessageParser.getTextChunks();

            //logger.debug("THERE ARE {} {} CHUNKS..", chunks[0], chunks.length);

            if (chunks[0].charAt(0) == AiConstants.COMMAND) {

                mlMessageParser.parseMessage(message.getMessage().replaceFirst(">" + AiConstants.COMMAND, ">"));
                chunks = mlMessageParser.getTextChunks();

//                if (!isBestResponse(mlMessageParser, chunks, message))
//                    return;

                processMessage(mlMessageParser, chunks, message);

            }

        } catch (Exception e) {
            logger.error("Could not parse message {}", message.getMessage(), e);
        }

    }


    @SuppressWarnings("unused")
    private boolean isBestResponse(MlMessageParser mlMessageParser, String[] chunks, SymMessage message) {
        if (hasResponse(mlMessageParser, chunks, message))
            return true;

        if (listeners.get(message.getStreamId()) != null) {
            for (AiCommandListener aiCommandListener : listeners.get(message.getStreamId()))
                if (aiCommandListener != this && aiCommandListener.hasResponse(mlMessageParser, chunks, message))
                    return false;

        } else {
            return false;
        }
        return true;
    }

    /**
     * Check to see if the message matches any of the commands.
     * If it matches, do actions and received responses.
     * If it doesn't check if the org.org.symphonyoss.ai can suggest a command from the unmatched command.
     * If it can suggest, then suggest the command and save the suggested command as the last command.
     * If it can't suggest and the sent command does not match run last command, send usage
     * If it does equal run last command, run the last command
     *
     * @param mlMessageParser the parser containing the received input in ML
     * @param chunks          the received input in text chunks
     * @param message         the received message
     */
    private void processMessage(MlMessageParser mlMessageParser, String[] chunks, SymMessage message) {

        if (activeCommands.isEmpty()) {

            if (logger != null)
                logger.warn("There are no active commands added to the listener. " +
                        "Ignoring process.");

            return;
        }

        lastAnsweredMessage = message;

        for (AiCommand command : activeCommands) {

            if (command.isCommand(chunks) && command.userIsPermitted(message.getFromUserId())) {

                aiResponder.respond(command.getResponses(mlMessageParser, message), message.getStreamId());
                lastResponse.put(message.getId(), new AiLastCommand(mlMessageParser, command));
                return;

            } else if (command.isCommand(chunks)) {


                aiResponder.sendNoPermission(message);
                return;

            }

        }

        if (!equalsRunLastCommand(mlMessageParser, message)
                && !canSuggest(chunks)) {

            aiResponder.sendUsage(message, mlMessageParser, activeCommands);


        } else if (!equalsRunLastCommand(mlMessageParser, message)) {

            AiLastCommand lastCommand = AiSpellParser.parse(activeCommands, chunks, symClient, AiConstants.CORRECTFACTOR);

            if (lastCommand != null) {
                aiResponder.sendSuggestionMessage(lastCommand, message);
                lastResponse.put(message.getFromUserId().toString(), lastCommand);
            }

        } else {

            AiLastCommand lastBotResponse = lastResponse.get(message.getFromUserId().toString());
            aiResponder.respondToEachUserWith(lastBotResponse.getAiCommand().getResponses(lastBotResponse.getMlMessageParser(), message));

        }
    }

    /**
     * Checks to see if this command listener has a real response (One that matches a AiCommand).
     *
     * @param mlMessageParser the parser
     * @param chunks          the message in text chunks
     * @param message         the message
     * @return if ai command listener has response
     */
    private boolean hasResponse(MlMessageParser mlMessageParser, String[] chunks, SymMessage message) {

        for (AiCommand command : activeCommands) {

            if (command.isCommand(chunks) && command.userIsPermitted(message.getFromUserId())) {

                return true;

            } else if (command.isCommand(chunks)) {

                return false;

            }

        }

        return !(!equalsRunLastCommand(mlMessageParser, message)
                && !canSuggest(chunks)) && equalsRunLastCommand(mlMessageParser, message);

    }

    /**
     * Determines if the given input matches the run last command
     *
     * @param mlMessageParser the parser that contains the input in ML
     * @param message         the received message
     * @return if the input matches the run last command
     */
    private boolean equalsRunLastCommand(MlMessageParser mlMessageParser, SymMessage message) {

        return (mlMessageParser.getText().trim().equalsIgnoreCase(AiConstants.RUN_LAST_COMMAND))
                && lastResponse.get(message.getFromUserId().toString()) != null;

    }

    /**
     * Determines if the org.org.symphonyoss.ai can suggest a command based on the input
     *
     * @param chunks the text input
     * @return if the org.org.symphonyoss.ai can suggest a command
     */
    private boolean canSuggest(String[] chunks) {
        return AiSpellParser.canParse(activeCommands, chunks, AiConstants.CORRECTFACTOR);
    }

    /**
     * Registers this listener to a given chat appropriately.
     *
     * @param chat The chat to listen on
     */
    @SuppressWarnings("unused")
    public void listenOn(Chat chat) {

        if (chat != null) {

            if (listeners.containsKey(chat.getStream().getStreamId())) {

                listeners.get(chat.getStream().getStreamId()).add(this);

            } else {

                HashSet<AiCommandListener> newChat = new HashSet<>();
                newChat.add(this);

                listeners.put(chat.getStream().getStreamId(), newChat);

            }

            chat.addListener(this);

        }
    }

    /**
     * Removes this listener from the provided chat appropriately
     *
     * @param chat The chat to listen on
     */
    @SuppressWarnings("unused")
    public void stopListening(Chat chat) {

        if (chat != null) {

            chat.removeListener(this);

            if (chat.getStream() != null
                    && chat.getStream().getStreamId() != null) {

                if (listeners.containsKey(chat.getStream().getStreamId())
                        && listeners.get(chat.getStream().getStreamId()).contains(this)) {

                    listeners.get(chat.getStream().getStreamId()).remove(this);

                }

            } else {
                logChatError(chat, new NullPointerException());
            }

        } else {
            logChatError(null, new NullPointerException());
        }

    }

    private void logChatError(Chat chat, Exception e) {
        if (logger != null) {

            if (chat == null) {
                logger.error("Ignored method call. Chat was null value.", e);

            } else if (chat.getStream() == null) {
                logger.error("Could not put stream in push hash. " +
                        "Chat stream was null value.", e);

            } else if (chat.getStream().getStreamId() == null) {
                logger.error("Could not put stream in push hash. " +
                        "Chat stream id was null value.", e);
            }

        }
    }

    @SuppressWarnings("unused")
    public ArrayList<AiCommand> getActiveCommands() {
        return activeCommands;
    }

    //List of unique commands
    public boolean addCommand(AiCommand command) {

        if (activeCommands.contains(command))
            return false;


        activeCommands.add(command);
        return true;
    }


    @SuppressWarnings("unused")
    public boolean removeCommand(AiCommand command) {

        if (!activeCommands.contains(command))
            return false;

        activeCommands.remove(command);

        return true;

    }


    @SuppressWarnings("unused")
    public SymphonyClient getSymClient() {
        return symClient;
    }

    @SuppressWarnings("unused")
    public SymMessage getLastAnsweredMessage() {
        return lastAnsweredMessage;
    }
}


