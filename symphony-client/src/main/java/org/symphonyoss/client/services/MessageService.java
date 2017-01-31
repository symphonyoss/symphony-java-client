/*
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
 */

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.exceptions.MessagesException;
import org.symphonyoss.exceptions.StreamsException;
import org.symphonyoss.exceptions.UsersClientException;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MessageService listens for all messages for a given BOT identity, identifies the type (Message, Chat, Room) of
 * message and then publishes the message registered listeners associated with type.
 * <p>
 * The service exposes methods for retrieving messages for a given stream or user historically.
 * <p>
 * The service provides convenience methods to support the sending of messages using standard models (Chat, Room)
 * <p>
 * The service converts all base messages to {@link SymMessage}.
 * <p>
 * Multiple listeners of different types can be registered at any time.
 * <p>
 * MessageListener - Listen for all messages
 * ChatListener - Listen for chat conversations (1:1 or multi-party)
 * RoomServiceListener - Listen for all Room related events.
 *
 * @author Frank Tarsillo on 5/15/2016.
 */
@SuppressWarnings("WeakerAccess")
public class MessageService implements DataFeedListener {

    private final SymphonyClient symClient;
    @SuppressWarnings("unused")
    private org.symphonyoss.symphony.agent.invoker.ApiClient agentClient;
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final Set<MessageListener> messageListeners = ConcurrentHashMap.newKeySet();
    private final Set<ChatListener> chatListeners = ConcurrentHashMap.newKeySet();
    private final Set<RoomServiceListener> roomServiceListeners = ConcurrentHashMap.newKeySet();
    private final Set<String> roomStreamCache = ConcurrentHashMap.newKeySet();
    private final Set<String> chatStreamCache = ConcurrentHashMap.newKeySet();
    MessageFeedWorker messageFeedWorker;


    /**
     * Constructor
     *
     * @param symClient Identifies the BOT user and exposes client APIs
     */
    public MessageService(SymphonyClient symClient) {

        this.symClient = symClient;

        //Lets startup the worker thread to listen for raw datafeed messages.
        messageFeedWorker = new MessageFeedWorker(symClient, this);
        new Thread(messageFeedWorker).start();

    }


    /**
     * Convenience method for sending messages to a room
     *
     * @param room       Room object
     * @param symMessage Message to send to the room
     * @throws MessagesException Generated from API calls into Symphony
     */
    public void sendMessage(Room room, SymMessage symMessage) throws MessagesException {

        symClient.getMessagesClient().sendMessage(room.getStream(), symMessage);

    }


    /**
     * Convenience method for sending messages to chat conversation (1:1 or Multi-party)
     *
     * @param chat       Chat object representing conversation
     * @param symMessage Message to send to the conversation
     * @throws MessagesException Generated from API calls into Symphony
     */
    public void sendMessage(Chat chat, SymMessage symMessage) throws MessagesException {

        symClient.getMessagesClient().sendMessage(chat.getStream(), symMessage);

    }


    /**
     * Convenience method to send a message to a given user by email address
     *
     * @param email      email of destination user
     * @param symMessage Message to send
     * @throws MessagesException Generated from API calls into Symphony
     */
    public void sendMessage(String email, SymMessage symMessage) throws MessagesException {

        SymUser remoteUser;
        try {

            remoteUser = symClient.getUsersClient().getUserFromEmail(email);

            symClient.getMessagesClient().sendMessage(symClient.getStreamsClient().getStream(remoteUser), symMessage);

        } catch (UsersClientException e) {
            throw new MessagesException("Failed to find user from email address: " + email, e);
        } catch (StreamsException e) {
            throw new MessagesException("Failed to send message. Unable to identify stream from email: " + email, e);
        }

    }

    /**
     * Retrieve messages for a given stream based on window of time
     *
     * @param stream      Identifier for the conversation (or room)
     * @param since       Starting point date (long value)
     * @param offset      (Optional) No. of messages to skip.
     * @param maxMessages (Optional) Maximum number of messages to retrieve from the starting point
     * @return {@link List<SymMessage>}  List of messages
     * @throws MessagesException Generated from API calls into Symphony
     */

    private List<SymMessage> getMessagesFromStream(Stream stream, Long since, Integer offset, Integer maxMessages) throws MessagesException {

        return symClient.getMessagesClient().getMessagesFromStream(
                stream, since, offset, maxMessages);

    }

    /**
     * Retrieve messages for a given user ID based on window of time
     *
     * @param userId      A userId (long value)
     * @param since       Starting point date (long value)
     * @param offset      (Optional) No. of messages to skip.
     * @param maxMessages (Optional) Maximum number of messages to retrieve from the starting point
     * @return {@link List}  List of messages
     * @throws MessagesException Generated from API calls into Symphony
     */
    @SuppressWarnings("unused")
    public List<SymMessage> getMessagesFromUserId(long userId, Long since, Integer offset, Integer maxMessages) throws MessagesException {


        SymUser user = new SymUser();
        user.setId(userId);


        try {
            return getMessagesFromStream(
                    symClient.getStreamsClient().getStream(user), since, offset, maxMessages);
        } catch (StreamsException e) {
            throw new MessagesException("Failed to retrieve messages. Unable to identity stream for userId: " + userId, e);
        }


    }

    /**
     * Process new Datafeed messages from worker
     *
     * @param message Incoming message from {@link DataFeedListener} registered to the {@link MessageFeedWorker}
     */
    @Override
    public void onMessage(V2BaseMessage message) {

        logger.debug("MessageID: {} StreamID: {}", message.getId(), message.getStreamId());


        if (message.getStreamId() == null)
            return;


        //Check for a basic message event as part of a chat or room
        if (message instanceof V2Message) {

            //Convert to SymMessage
            SymMessage symMessage = SymMessage.toSymMessage(message);

            //All incoming messages from POD are MESSAGEML based.
            symMessage.setFormat(SymMessage.Format.MESSAGEML);

            //Ignore messages the BOT is sending out.
            if (symClient.getLocalUser().getId().equals(symMessage.getFromUserId()))
                return;


            //Verify if this message is part of room conversation
            if (isRoomMessage(message)) {

                //Publish room messages to associated listeners
                for (RoomServiceListener roomServiceListener : roomServiceListeners)
                    roomServiceListener.onMessage(symMessage);
            } else {

                //Then it has to be a chat conversation (1:1 or Multi-Party)
                for (ChatListener chatListener : chatListeners)
                    chatListener.onChatMessage(symMessage);
            }


            //Publish all messages to registered Message Listeners...
            for (MessageListener messageListener : messageListeners) {
                messageListener.onMessage(symMessage);
            }

            logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nType: {}",
                    symMessage.getTimestamp(),
                    symMessage.getFromUserId(),
                    symMessage.getMessage(),
                    symMessage.getMessageType());


            //Publish associated room event messages
        } else if (message instanceof UserJoinedRoomMessage) {
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onUserJoinedRoomMessage((UserJoinedRoomMessage) message);
        } else if (message instanceof UserLeftRoomMessage) {
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onUserLeftRoomMessage((UserLeftRoomMessage) message);
        } else if (message instanceof RoomCreatedMessage) {
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomCreatedMessage((RoomCreatedMessage) message);
        } else if (message instanceof RoomDeactivatedMessage) {
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomDeactivatedMessage((RoomDeactivatedMessage) message);
        } else if (message instanceof RoomMemberDemotedFromOwnerMessage) {
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomMemberDemotedFromOwnerMessage((RoomMemberDemotedFromOwnerMessage) message);
        } else if (message instanceof RoomMemberPromotedToOwnerMessage) {
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomMemberPromotedToOwnerMessage((RoomMemberPromotedToOwnerMessage) message);
        } else if (message instanceof RoomUpdatedMessage) {
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomUpdatedMessage((RoomUpdatedMessage) message);
        }


    }

    /**
     * Identify if the message is associated with a room or chat conversation
     *
     * @param message base message being verified
     * @return True if room message type
     */
    private boolean isRoomMessage(V2BaseMessage message) {

        //We keep an internal cache to expedite future checks
        if (roomStreamCache.contains(message.getStreamId()))
            return true;

        if (chatStreamCache.contains(message.getStreamId()))
            return false;

        // #LLC-IMPROVEMENT
        //Unfortunately there is no easy way to identify stream types...so enter hacks.

        try {
            if (symClient.getStreamsClient().getRoomDetail(message.getStreamId()) != null) {
                roomStreamCache.add(message.getStreamId());
                logger.debug("Found new room stream to cache: {}", message.getStreamId());
                return true;
            }
        } catch (StreamsException e) {
            //Exception will be common here, so we are not going to throw exceptions every time.
            logger.debug("Failed to retrieve room detail, so this is a chat stream. {}",e);


        }

        //By default its a Chat stream..
        chatStreamCache.add(message.getStreamId());
        logger.debug("Found new chat stream to cache: {}", message.getStreamId());
        return false;


    }


    /**
     * Please use {@link #addMessageListener(MessageListener)}
     *
     * @param messageListener Listener to register
     * @return True if listener is registered successfully
     */
    @Deprecated
    public boolean registerMessageListener(MessageListener messageListener) {

        return messageListeners.add(messageListener);

    }


    /**
     * Add {@link MessageListener} to receive for all new messages
     *
     * @param messageListener listener that will be notified of events
     */
    @SuppressWarnings("unused")
    public void addMessageListener(MessageListener messageListener) {

        messageListeners.add(messageListener);

    }

    /**
     * Remove a registered {@link MessageListener} t
     *
     * @param messageListener listener that will removed from service
     * @return True if listener is removed
     */
    @SuppressWarnings("unused")
    public boolean removeMessageListener(MessageListener messageListener) {

        return messageListeners.remove(messageListener);

    }

    /**
     * Add {@link RoomListener} to service to receive new Room events
     *
     * @param roomServiceListener listener to register
     */
    public void addRoomListener(RoomServiceListener roomServiceListener) {

        roomServiceListeners.add(roomServiceListener);

    }

    /**
     * Please use {@link #addRoomListener(RoomServiceListener)}
     *
     * @param roomServiceListener Listener to register
     * @return True if registered without issue
     */
    @Deprecated
    public boolean registerRoomListener(RoomServiceListener roomServiceListener) {

        return roomServiceListeners.add(roomServiceListener);

    }

    /**
     * Remove room listener from service
     *
     * @param roomServiceListener listener to remove
     * @return True if listener is removed
     */
    @SuppressWarnings("unused")
    public boolean removeRoomListener(RoomServiceListener roomServiceListener) {

        return roomServiceListeners.remove(roomServiceListener);

    }


    /**
     * Add {@link ChatListener} to receive new conversation chat messages
     *
     * @param chatListener listener to register
     */
    public void addChatListener(ChatListener chatListener) {

        chatListeners.add(chatListener);

    }

    /**
     * Please use {@link #addChatListener(ChatListener)}
     *
     * @param chatListener Listener to register
     * @return True if registered
     */
    @Deprecated
    public boolean registerChatListener(ChatListener chatListener) {

        return chatListeners.add(chatListener);

    }

    /**
     * Remove a registered chat listener
     *
     * @param chatListener listener to remove
     * @return True if listener is registered
     */
    @SuppressWarnings("unused")
    public boolean removeChatListener(ChatListener chatListener) {

        return chatListeners.remove(chatListener);

    }

    /**
     * Shutdown the underlying threads and workers.
     */
    public void shutdown() {
        messageFeedWorker.shutdown();
        messageFeedWorker = null;

    }

}
