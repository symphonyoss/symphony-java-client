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
import org.symphonyoss.client.events.*;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.CacheType;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.clients.model.*;

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
    private final Set<RoomServiceEventListener> roomServiceEventListeners = ConcurrentHashMap.newKeySet();
    private final Set<ConnectionsEventListener> connectionsEventListeners = ConcurrentHashMap.newKeySet();
    private final Set<String> roomStreamCache = ConcurrentHashMap.newKeySet();
    private final Set<String> chatStreamCache = ConcurrentHashMap.newKeySet();
    DataFeedWorker dataFeedWorker;


    /**
     * Constructor
     *
     * @param symClient Identifies the BOT user and exposes client APIs
     */
    public MessageService(SymphonyClient symClient) {

        this(symClient, ApiVersion.getDefault());

    }


    /**
     * Specify a version of MessageService to use.  Version is aligning with LLC REST API endpoint versions.
     *
     * @param symClient  Symphony client required to access all underlying clients functions.
     * @param apiVersion The version of the ChatServer to use which is aligned with LLC REST API endpoint versions.
     */
    public MessageService(SymphonyClient symClient, ApiVersion apiVersion) {


        this.symClient = symClient;


        //Lets startup the worker thread to listen for raw datafeed messages
        dataFeedWorker = new DataFeedWorker(symClient, this);

        Thread thread=new Thread(dataFeedWorker);
        thread.setName("DataFeedWorker: "+ symClient.getName());
        thread.start();


    }


    /**
     * Convenience method for sending messages to a room
     *
     * @param room       Room object
     * @param symMessage Message to send to the room
     * @return Symphony message
     * @throws MessagesException Generated from API calls into Symphony
     */
    public SymMessage sendMessage(Room room, SymMessage symMessage) throws MessagesException {

        return symClient.getMessagesClient().sendMessage(room.getStream(), symMessage);

    }


    /**
     * Convenience method for sending messages to chat conversation (1:1 or Multi-party)
     *
     * @param chat       Chat object representing conversation
     * @param symMessage Message to send to the conversation
     * @return Symphony Message
     * @throws MessagesException Generated from API calls into Symphony
     */
    public SymMessage sendMessage(Chat chat, SymMessage symMessage) throws MessagesException {

        return symClient.getMessagesClient().sendMessage(chat.getStream(), symMessage);

    }


    /**
     * Convenience method to send a message to a given user by email address
     *
     * @param email      email of destination user
     * @param symMessage Message to send
     * @return Symphony message
     * @throws MessagesException Generated from API calls into Symphony
     */
    public SymMessage sendMessage(String email, SymMessage symMessage) throws MessagesException {

        SymUser remoteUser;
        try {

            remoteUser = ((SymUserCache) symClient.getCache(CacheType.USER)).getUserByEmail(email);

            return symClient.getMessagesClient().sendMessage(symClient.getStreamsClient().getStream(remoteUser), symMessage);

        } catch (UsersClientException e) {
            throw new MessagesException("Failed to find user from email address: " + email, e);
        } catch (StreamsException e) {
            throw new MessagesException("Failed to send message. Unable to identify stream from email: " + email, e);
        }

    }


    /**
     * Send a message to a given user
     *
     * @param symUser  User to send message to
     * @param symMessage Message to send
     * @return Symphony message
     * @throws MessagesException Generated from API calls into Symphony
     */
    public SymMessage sendMessage(SymUser symUser, SymMessage symMessage) throws MessagesException {

        if (symUser.getId() == null) {
            throw new MessagesException("Failed to send message. SymUser ID not provided");
        }

        try {

            return symClient.getMessagesClient().sendMessage(symClient.getStreamsClient().getStream(symUser), symMessage);

        } catch (StreamsException e) {
            throw new MessagesException("Failed to send message. Unable to identify stream from userId: " + symUser.getId(), e);
        }

    }


    /**
     * Send a message to a given stream
     *
     * @param symStream  Stream to send message to
     * @param symMessage Message to send
     * @return Symphony message
     * @throws MessagesException Generated from API calls into Symphony
     */
    public SymMessage sendMessage(SymStream symStream, SymMessage symMessage) throws MessagesException {

        if (symStream.getStreamId() == null) {
            throw new MessagesException("Failed to send message. StreamID not provided");
        }

        try {

            return symClient.getMessagesClient().sendMessage(symStream, symMessage);

        } catch (MessagesException e) {
            throw new MessagesException("Failed to send message to streamId" + symStream.getStreamId(), e);
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

    private List<SymMessage> getMessagesFromStream(SymStream stream, Long since, Integer offset, Integer maxMessages) throws MessagesException {

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


    @Override
    public void onEvent(SymEvent symEvent) {


        logger.debug("{} event type received...", symEvent.getType());


        if (symEvent.getId() == null && symEvent.getType() != null)
            return;


        SymEventTypes.Type type = SymEventTypes.Type.fromValue(symEvent.getType());


        if (type == null)
            return;


        switch (type) {
            case MESSAGESENT:

                SymMessage symMessage = symEvent.getPayload().getMessageSent();

                if (symMessage != null && !symClient.getLocalUser().getId().equals(symMessage.getFromUserId())) {


                    //Verify if this message is part of room conversation
                    if (symMessage.getStream().getStreamType().equals(SymStreamTypes.Type.ROOM)) {

                        //Publish room messages to associated listeners
                        for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                            roomServiceEventListener.onMessage(symMessage);


                    } else if (symMessage.getStream().getStreamType().equals(SymStreamTypes.Type.POST)) {

                        logger.warn("POST services not implemented..");

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
                            symMessage.getStream().getStreamType().toString());


                }


                break;

            case INSTANTMESSAGECREATED:

                SymIMCreated symIMCreated = symEvent.getPayload().getInstantMessageCreated();

                if (symIMCreated != null) {
                    logger.debug("Instant message create event not implemented");

                }


                break;

            case ROOMCREATED:

                SymRoomCreated symRoomCreated = symEvent.getPayload().getRoomCreated();

                if (symRoomCreated != null) {

                    for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                        roomServiceEventListener.onSymRoomCreated(symRoomCreated);
                }

                break;
            case ROOMUPDATED:
                SymRoomUpdated symRoomUpdated = symEvent.getPayload().getRoomUpdated();

                if (symRoomUpdated != null) {

                    for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                        roomServiceEventListener.onSymRoomUpdated(symRoomUpdated);
                }

                break;
            case ROOMDEACTIVATED:
                SymRoomDeactivated symRoomDeactivated = symEvent.getPayload().getRoomDeactivated();

                if (symRoomDeactivated != null) {

                    for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                        roomServiceEventListener.onSymRoomDeactivated(symRoomDeactivated);


                }
                break;

            case ROOMREACTIVATED:
                SymRoomReactivated symRoomReactivated = symEvent.getPayload().getRoomReactivated();

                if (symRoomReactivated != null) {

                    for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                        roomServiceEventListener.onSymRoomReactivated(symRoomReactivated);


                }
                break;

            case USERJOINEDROOM:

                SymUserJoinedRoom symUserJoinedRoom = symEvent.getPayload().getUserJoinedRoom();

                if (symUserJoinedRoom != null) {
                    for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                        roomServiceEventListener.onSymUserJoinedRoom(symUserJoinedRoom);


                }

                break;
            case USERLEFTROOM:
                SymUserLeftRoom symUserLeftRoom = symEvent.getPayload().getUserLeftRoom();

                if (symUserLeftRoom != null) {
                    for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                        roomServiceEventListener.onSymUserLeftRoom(symUserLeftRoom);


                }

                break;

            case ROOMMEMBERPROMOTEDTOOWNER:
                SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner = symEvent.getPayload().getRoomMemberPromotedToOwner();

                if (symRoomMemberPromotedToOwner != null) {
                    for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                        roomServiceEventListener.onSymRoomMemberPromotedToOwner(symRoomMemberPromotedToOwner);


                }

                break;
            case ROOMMEMBERDEMOTEDFROMOWNER:
                SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner = symEvent.getPayload().getRoomMemberDemotedFromOwner();

                if (symRoomMemberDemotedFromOwner != null) {
                    for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
                        roomServiceEventListener.onSymRoomMemberDemotedFromOwner(symRoomMemberDemotedFromOwner);


                }

                break;


            case CONNECTIONACCEPTED:
                SymConnectionAccepted symConnectionAccepted = symEvent.getPayload().getConnectionAccepted();

                if (symConnectionAccepted != null) {
                    for (ConnectionsEventListener connectionsEventListener : connectionsEventListeners)
                        connectionsEventListener.onSymConnectionAccepted(symConnectionAccepted);


                }
                break;

            case CONNECTIONREQUESTED:
                SymConnectionRequested symConnectionRequested = symEvent.getPayload().getConnectionRequested();

                if (symConnectionRequested != null) {
                    for (ConnectionsEventListener connectionsEventListener : connectionsEventListeners)
                        connectionsEventListener.onSymConnectionRequested(symConnectionRequested);

                }
                break;


            default:

        }


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
     * Add {@link RoomServiceEventListener} to service to receive new Room events
     *
     * @param roomServiceEventListener listener to register
     */
    public void addRoomServiceEventListener(RoomServiceEventListener roomServiceEventListener) {

        roomServiceEventListeners.add(roomServiceEventListener);

    }


    /**
     * Remove room event listener from service
     *
     * @param roomServiceEventListener listener to remove
     * @return True if listener is removed
     */
    @SuppressWarnings("unused")
    public boolean removeRoomServiceEventListener(RoomServiceEventListener roomServiceEventListener) {

        return roomServiceEventListeners.remove(roomServiceEventListener);

    }


    /**
     * Add {@link ConnectionsEventListener} to service to receive new Connection events
     *
     * @param connectionsEventListener listener to register
     */
    public void addConnectionsEventListener(ConnectionsEventListener connectionsEventListener) {

        connectionsEventListeners.add(connectionsEventListener);

    }


    /**
     * Remove connections event listener from service
     *
     * @param connectionsEventListener listener to remove
     * @return True if listener is removed
     */
    @SuppressWarnings("unused")
    public boolean removeConnectionsEventListener(ConnectionsEventListener connectionsEventListener) {

        return connectionsEventListeners.remove(connectionsEventListener);

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

        if (dataFeedWorker != null) {
            dataFeedWorker.shutdown();
            dataFeedWorker = null;
        }


    }

}
