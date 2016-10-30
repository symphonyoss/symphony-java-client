/*
 *
 *  *
 *  * Copyright 2016 The Symphony Software Foundation
 *  *
 *  * Licensed to The Symphony Software Foundation (SSF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.exceptions.StreamsException;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [Experimental]  Implementation of firehose for a given POD.
 *
 * Is equivalent to the MessageService, but broader coverage.
 *
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class FirehoseService implements DataFeedListener {

    private final SymphonyClient symClient;
    private org.symphonyoss.symphony.agent.invoker.ApiClient agentClient;
    private final Logger logger = LoggerFactory.getLogger(FirehoseService.class);
    private final Set<MessageListener> messageListeners = ConcurrentHashMap.newKeySet();
    private final Set<ChatListener> chatListeners = ConcurrentHashMap.newKeySet();
    private final Set<RoomServiceListener> roomServiceListeners = ConcurrentHashMap.newKeySet();
    private final Set<String> roomStreamCache = ConcurrentHashMap.newKeySet();
    private final Set<String> chatStreamCache = ConcurrentHashMap.newKeySet();
    FirehoseWorker firehoseWorker;

    /**
     *
     * @param symClient
     */
    public FirehoseService(SymphonyClient symClient) {

        this.symClient = symClient;

        firehoseWorker = new FirehoseWorker(symClient, this);
        new Thread(firehoseWorker).start();

    }


    /**
     * Message callback from firehose datafeed.
     * @param message
     */
    public void onMessage(V2BaseMessage message) {

        logger.debug("MessageID: {} StreamID: {}", message.getId(), message.getStreamId());


        if (message.getStreamId() == null)
            return;


        if (message instanceof V2Message) {
            SymMessage symMessage = SymMessage.toSymMessage(message);

            if (symClient.getLocalUser().getId().equals(symMessage.getFromUserId()))
                return;


            if (isRoomMessage(message)) {

                for (RoomServiceListener roomServiceListener : roomServiceListeners)
                    roomServiceListener.onMessage(symMessage);
            } else {

                for (ChatListener chatListener : chatListeners)
                    chatListener.onChatMessage(symMessage);
            }


            //Listen for all messages...
            for (MessageListener messageListener : messageListeners) {
                messageListener.onMessage(symMessage);
            }

            logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nType: {}",
                    symMessage.getTimestamp(),
                    symMessage.getFromUserId(),
                    symMessage.getMessage(),
                    symMessage.getMessageType());


        }else if(message instanceof UserJoinedRoomMessage){
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onUserJoinedRoomMessage((UserJoinedRoomMessage) message);
        }else if(message instanceof UserLeftRoomMessage){
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onUserLeftRoomMessage((UserLeftRoomMessage) message);
        }else if(message instanceof RoomCreatedMessage){
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomCreatedMessage((RoomCreatedMessage) message);
        }else if(message instanceof RoomDeactivatedMessage){
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomDeactivatedMessage((RoomDeactivatedMessage) message);
        }else if(message instanceof RoomMemberDemotedFromOwnerMessage){
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomMemberDemotedFromOwnerMessage((RoomMemberDemotedFromOwnerMessage) message);
        }else if(message instanceof RoomMemberPromotedToOwnerMessage){
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomMemberPromotedToOwnerMessage((RoomMemberPromotedToOwnerMessage) message);
        }else if(message instanceof RoomUpdatedMessage){
            for (RoomServiceListener roomServiceListener : roomServiceListeners)
                roomServiceListener.onRoomUpdatedMessage((RoomUpdatedMessage) message);
        }


    }

    /**
     * Verify if incoming message is a associated with a room
     * @param message
     * @return
     */
    private boolean isRoomMessage(V2BaseMessage message) {


        if (roomStreamCache.contains(message.getStreamId()))
            return true;

        if (chatStreamCache.contains(message.getStreamId()))
            return false;


        try {
            if (symClient.getStreamsClient().getRoomDetail(message.getStreamId()) != null) {
                roomStreamCache.add(message.getStreamId());
                logger.debug("Found new room stream to cache: {}", message.getStreamId());
                return true;
            }
        } catch (StreamsException e) {
            //Exception will be common here, so we are not going to throw exceptions every time.
            logger.debug("Failed to retrieve room detail, so this is a chat stream.");


        }
        chatStreamCache.add(message.getStreamId());
        logger.debug("Found new chat stream to cache: {}", message.getStreamId());
        return false;


    }


    /**
     * Please use {@link #addMessageListener(MessageListener)}
     * @param messageListener
     * @return
     */
    @Deprecated
    public boolean registerMessageListener(MessageListener messageListener) {

        return messageListeners.add(messageListener);

    }

    public void addMessageListener(MessageListener messageListener) {

         messageListeners.add(messageListener);

    }


    public boolean removeMessageListener(MessageListener messageListener) {

        return messageListeners.remove(messageListener);

    }


    public void addRoomListener(RoomServiceListener roomServiceListener) {

         roomServiceListeners.add(roomServiceListener);

    }
    /**
     * Please use {@link #addRoomListener(RoomServiceListener)}
     * @param roomServiceListener
     * @return
     */
    @Deprecated
    public boolean registerRoomListener(RoomServiceListener roomServiceListener) {

        return roomServiceListeners.add(roomServiceListener);

    }

    public boolean removeRoomListener(RoomServiceListener roomServiceListener) {

        return roomServiceListeners.remove(roomServiceListener);

    }



    public void addChatListener(ChatListener chatListener) {

         chatListeners.add(chatListener);

    }

    /**
     * Please use {@link #addChatListener(ChatListener)}
     * @param chatListener
     * @return
     */
    @Deprecated
    public boolean registerChatListener(ChatListener chatListener) {

        return chatListeners.add(chatListener);

    }

    public boolean removeChatListener(ChatListener chatListener) {

        return chatListeners.remove(chatListener);

    }

    /**
     * Shutdown underlying threads associated with datafeed reads.
     */
    public void shutdown(){
        firehoseWorker.shutdown();
        firehoseWorker = null;

    }

}
