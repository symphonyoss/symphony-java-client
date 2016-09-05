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
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Frank Tarsillo on 5/16/2016.
 */
public class ChatService implements MessageListener {


    private final ConcurrentHashMap<String, Chat> chatsByStream = new ConcurrentHashMap<String, Chat>();
    private final ConcurrentHashMap<Long, Set<Chat>> chatsByUser = new ConcurrentHashMap<Long, Set<Chat>>();

    private final Set<ChatServiceListener> chatServiceListeners =  ConcurrentHashMap.newKeySet();

    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);


    public ChatService(SymphonyClient symClient) throws Exception {
        this.symClient = symClient;

        symClient.getMessageService().registerMessageListener(this);

    }


    public boolean addChat(Chat chat) {

        if (chatsByStream.get(chat.getStream().getId()) == null) {

            chatsByStream.put(chat.getStream().getId(), chat);

            for (User user : chat.getRemoteUsers()) {

                Set<Chat> userChats = chatsByUser.get(user.getId());

                if(userChats == null){
                    userChats = new HashSet<Chat>();
                    chatsByUser.put(user.getId(),userChats);
                }


                if (userChats.add(chat)) {
                    logger.debug("Adding new chat for user {}:{}", user.getId(), user.getEmailAddress());
                } else {
                    logger.debug("Chat with user {}:{} already exists..ignoring", user.getId(), user.getEmailAddress());

                }


            }

            for(ChatServiceListener chatServiceListener: chatServiceListeners)
                chatServiceListener.onNewChat(chat);

            return true;
        }
        return false;
    }

    public boolean removeChat(Chat chat) {

        if (chat != null && chatsByStream.remove(chat.getStream().getId()) != null) {

            for (User user : chat.getRemoteUsers()) {

                Set<Chat> userChats = chatsByUser.get(user.getId());

                if (userChats.remove(chat)) {
                    logger.debug("Removed chat for user {}:{}", user.getId(), user.getEmailAddress());
                } else {
                    logger.debug("Could not remove chats for user {}:{} on stream {}", user.getId(), user.getEmailAddress(), chat.getStream());
                }


            }
            for(ChatServiceListener chatServiceListener: chatServiceListeners)
                chatServiceListener.onRemovedChat(chat);

            return true;
        }
        return false;

    }

    private void newChatUpdate(Chat chat){


    }

    private Chat createNewChatFromMessage(SymMessage message){

        try {
            Chat chat = new Chat();
            chat.setLocalUser(symClient.getLocalUser());
            Stream stream = new Stream();
            stream.setId(message.getStreamId());
            chat.setStream(stream);
            chat.setLastMessage(message);
            User remoteUser = symClient.getUsersClient().getUserFromId(message.getFromUserId());

            if(remoteUser != null) {

                Set<User> remoteUserSet = new HashSet<User>();
                remoteUserSet.add(remoteUser);
                chat.setRemoteUsers(remoteUserSet);
                return chat;
            }
        }catch(Exception e){
            logger.error("Could not create new chat from message {} {}", message.getStreamId(), message.getFromUserId(),e);
        }

        return null;
    }

    @Deprecated
    public void onMessage(Message message) {

      onMessage(SymMessage.toSymMessage(message));

    }

    @Override
    public void onMessage(SymMessage message) {
        if(message== null)
            return;



        String streamId = message.getStreamId();


        logger.debug("New message from stream {}", streamId);

        if(streamId!= null){
            Chat chat = chatsByStream.get(streamId);

            if(chat == null){

                try {
                    if (symClient.getStreamsClient().getRoomDetail(streamId) != null) {
                        logger.debug("Rejecting message from room stream {}", streamId);
                        return;
                    }
                }catch(Exception e){
                    logger.debug("Stream {} not a room", streamId);

                }
                chat = createNewChatFromMessage(message);
                if(chat!=null) {
                    addChat(chat);
                }else{
                    logger.error("Failed to add new chat from message {} {}",message.getStreamId(), message.getFromUserId());
                    return;
                }
            }else{

                chat.onChatMessage(message);

            }


        }


    }

    public boolean registerListener(ChatServiceListener chatServiceListener) {

        return chatServiceListeners.add(chatServiceListener);
    }

    public boolean removeListener(ChatServiceListener chatServiceListener){
        return chatServiceListeners.remove(chatServiceListener);
    }

    public Set<Chat> getChatsByEmail(String email) {

        try {
            User user = symClient.getUsersClient().getUserFromEmail(email);

            if (user != null)
                return chatsByUser.get(user.getId());
        }catch(Exception e){
            logger.error("Could not locate user by email {}", email,e);
        }
        return null;
    }


    public Set<Chat> getChats(User user) {

            if (user != null)
                return chatsByUser.get(user.getId());

        return null;
    }

    public Chat getChatByStream(String streamId) {

        return chatsByStream.get(streamId);

    }


}
