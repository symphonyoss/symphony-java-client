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

package org.symphonyoss.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.PresenceException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.*;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUserConnection;
import org.symphonyoss.symphony.pod.model.Presence;
import org.symphonyoss.symphony.pod.model.UserPresence;

/**
 * This is the test BOT that will receive messages from the sjcTestClient.  These messages will request certain
 * functions be executed.  If the event has been executed, the BOT will send a confirmation reply.   The idea is you
 * can synchronously test features.
 *
 * @author Frank Tarsillo
 */
public class SjcTestBot implements ChatListener, ChatServiceListener, RoomListener, RoomServiceListener, PresenceListener, ConnectionsListener {

    SymphonyClient symphonyClient;
    String sjcClient = System.getProperty("sender.user.email", "sjc.testclient");
    private final Logger logger = LoggerFactory.getLogger(SjcTestBot.class);
    private String testClientStreamId;

    public SjcTestBot() throws SymException {

        symphonyClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC,
                System.getProperty("bot.user.email", "sjc.testbot"),
                System.getProperty("bot.user.cert.file"),
                System.getProperty("bot.user.cert.password"),
                System.getProperty("truststore.file"),
                System.getProperty("truststore.password"));

        symphonyClient.getRoomService().addRoomServiceListener(this);


        testClientStreamId = symphonyClient.getStreamsClient().getStreamFromEmail(sjcClient).getId();

        Room room = new Room();
        room.setStreamId(System.getProperty("test.room.stream"));
        room.setId(System.getProperty("test.room.stream"));
        room.addListener(this);
        symphonyClient.getRoomService().joinRoom(room);

        symphonyClient.getChatService().addListener(this);

    }


    @Override
    public void onRoomMessage(SymMessage symMessage) {

        logger.info("TestBot: New room message detected: {}", symMessage.getMessageText());
        sendResponse(symMessage);


    }

    @Override
    public void onRoomDeactivatedMessage(RoomDeactivatedMessage roomDeactivatedMessage) {

    }

    @Override
    public void onRoomMemberDemotedFromOwnerMessage(RoomMemberDemotedFromOwnerMessage roomMemberDemotedFromOwnerMessage) {

    }

    @Override
    public void onRoomMemberPromotedToOwnerMessage(RoomMemberPromotedToOwnerMessage roomMemberPromotedToOwnerMessage) {

    }

    @Override
    public void onRoomReactivatedMessage(RoomReactivatedMessage roomReactivatedMessage) {

    }

    @Override
    public void onRoomUpdatedMessage(RoomUpdatedMessage roomUpdatedMessage) {

    }

    @Override
    public void onUserJoinedRoomMessage(UserJoinedRoomMessage userJoinedRoomMessage) {

    }

    @Override
    public void onUserLeftRoomMessage(UserLeftRoomMessage userLeftRoomMessage) {

    }

    @Override
    public void onRoomCreatedMessage(RoomCreatedMessage roomCreatedMessage) {

    }

    @Override
    public void onMessage(SymMessage symMessage) {

    }

    @Override
    public void onNewRoom(Room room) {
        room.addListener(this);

    }

    @Override
    public void onUserPresence(UserPresence userPresence) {

    }

    @Override
    public void onNewChat(Chat chat) {
        chat.addListener(this);
        //onChatMessage(chat.getLastMessage());

    }

    @Override
    public void onRemovedChat(Chat chat) {

    }

    @Override
    public void onChatMessage(SymMessage symMessage) {

        String text = symMessage.getMessageText();

        String[] chunks = text.split(" ");

        if (chunks == null)
            return;

        if (chunks[0].equals(SymphonyClientIT.CHAT_COMMAND_MESSAGE)) {
            logger.info("Test Bot: New chat message detected: {}", symMessage.getMessageText());
            sendResponse(symMessage);
        }else if (chunks[0].equals(SymphonyClientIT.MULTI_PARTY_CHAT_COMMAND_MESSAGE)) {
            logger.info("Test Bot: New chat message detected: {}", symMessage.getMessageText());
            sendResponse(symMessage);
        }else if (chunks[0].equals(SymphonyClientIT.PRESENCE_COMMAND_MESSAGE)) {

            sendPresenceResponse();

        }


    }

    @Override
    public void onConnectionNotification(SymUserConnection userConnection) {

    }

    /**
     * Send the sjcClient presence back to sjcClient
     */
    private void sendPresenceResponse() {

        try {
            Presence presence = symphonyClient.getPresenceService().getUserPresence(sjcClient);


            SymMessage message = new SymMessage();
            message.setMessage(SymphonyClientIT.PRESENCE_COMMAND_MESSAGE + " " + presence.getCategory().toString());

            sendResponse(message);

        } catch (PresenceException e) {
            logger.error("Could not obtain presence for SymphonyClientIT user", e);
        }
    }

    /**
     * Send response message
     * @param symMessage the response message to a command
     */
    private void sendResponse(SymMessage symMessage) {

        try {

            symMessage.setStreamId(testClientStreamId);
            symphonyClient.getMessageService().sendMessage(sjcClient, symMessage);
        } catch (MessagesException e) {
            logger.error("Could not send onRoomMessage reply", e);
        }

    }

    public void shutdown() {
        if (symphonyClient != null)
            symphonyClient.shutdown();
    }

}
