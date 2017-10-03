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
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.CacheType;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
import org.symphonyoss.symphony.clients.model.SymUser;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The chat service provides capabilities to construct and monitor chat conversations. Supports the ability to add chat
 * conversations which are validated and enriched.  Supports the creation and callback of new chat conversations from
 * incoming messages.
 * <p>
 * NOTE: Multi-party conversations that are constructed via incoming messages are enriched over time.  Currently there
 * is no way of identifying all users of an incoming message from a given stream.
 *
 * @author Frank Tarsillo on 5/16/2016.
 */
@SuppressWarnings("unused")
public class ChatService implements ChatListener {


    private final ConcurrentHashMap<String, Chat> chatsByStream = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Set<Chat>> chatsByUser = new ConcurrentHashMap<>();

    private final Set<ChatServiceListener> chatServiceListeners = ConcurrentHashMap.newKeySet();

    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private ApiVersion apiVersion;


    /**
     * @param symClient Symphony Client required to access all underlying clients functions.
     */
    public ChatService(SymphonyClient symClient) {

        this(symClient, ApiVersion.getDefault());


    }

    /**
     * Specify a version of ChatServer to use.  Version is aligning with LLC REST API endpoint versions.
     * @param symClient Symphony client required to access all underlying clients functions.
     * @param apiVersion The version of the ChatServer to use which is aligned with LLC REST API endpoint versions.
     */
    public ChatService(SymphonyClient symClient, ApiVersion apiVersion) {

        this.apiVersion = apiVersion;
        this.symClient = symClient;

        //Register this service against the message service which is backed by datafeed.
        symClient.getMessageService().addChatListener(this);

    }


    /**
     * Add a predefined chat to the service.  RemoteUsers are required and streams will be generated and verified,
     * so if one of the remote users are not connected to the BOT this will fail and return false.
     * <p>
     * Note: In the future response object will be available to provide additional detail.
     *
     * @param chat Chat with remote users defined.  Stream not required and will be generated.
     * @return True if chat has been verified and accepted.
     */
    public boolean addChat(Chat chat) {

        //True to verify streams.
        return addChat(chat, true);
    }

    /**
     * @param chat         Chat with remote users defined.
     * @param updateStream Verify and generate stream.  This should be false for generated chats from incoming messages
     * @return True if validated and accepted
     * @see #addChat(Chat)
     */
    private boolean addChat(Chat chat, boolean updateStream) {

        //Check for min requirements. We need at least one user...
        if (chat == null || chat.getRemoteUsers() == null)
            return false;

        //Lets verify and enrich SymUsers..
        if (!updateSymUsers(chat)) {
            logger.error("Failed to register chat conversation because some or all users can not be identified...please check!");
            return false;
        }

        //Good if you are generating a new chat from BOT, bad if you are receiving generated chats incoming.
        if (updateStream) {
            //Lets find the stream ID for all users in conversation.
            try {

                SymStream stream = symClient.getStreamsClient().getStream(chat.getRemoteUsers());
                if (stream != null) {
                    chat.setStreamId(stream.getStreamId());

                } else {
                    logger.error("Failed to obtain stream ID for chat...");
                    return false;
                }
            } catch (StreamsException e) {
                logger.error("Failed to obtain stream ID for chat...", e);
                return false;
            }
        }

        //If all checks out, we need to make sure the chat is added into chats by streams and linked to chats by user.
        if (chatsByStream.get(chat.getStreamId()) == null) {

            chatsByStream.put(chat.getStreamId(), chat);

            for (SymUser user : chat.getRemoteUsers()) {

                Set<Chat> userChats = chatsByUser.computeIfAbsent(user.getId(), k -> new HashSet<>());


                if (userChats != null && userChats.add(chat)) {
                    logger.debug("Adding new chat for user {}:{}", user.getId(), user.getEmailAddress());
                } else {
                    logger.debug("Chat with user {}:{} already exists..ignoring", user.getId(), user.getEmailAddress());

                }


            }

            //Issue event to listeners.
            for (ChatServiceListener chatServiceListener : chatServiceListeners)
                chatServiceListener.onNewChat(chat);


            return true;
        }

        //no need to add it, because it exists.
        return false;
    }


    /**
     * Remove Chat conversation
     *
     * @param chat Chat object to remove
     * @return Removed chat
     */
    public boolean removeChat(Chat chat) {

        //Make sure something exists..
        if (chat != null && chat.getStreamId() != null && chatsByStream.remove(chat.getStreamId()) != null) {

            for (SymUser user : chat.getRemoteUsers()) {

                Set<Chat> userChats = chatsByUser.get(user.getId());

                if (userChats.remove(chat)) {
                    logger.debug("Removed chat for user {}:{}", user.getId(), user.getEmailAddress());
                } else {
                    logger.debug("Could not remove chats for user {}:{} on stream {}", user.getId(), user.getEmailAddress(), chat.getStreamId());
                }


            }
            for (ChatServiceListener chatServiceListener : chatServiceListeners)
                chatServiceListener.onRemovedChat(chat);

            return true;
        }

        return false;

    }


    /**
     * Construct a Chat from incoming message.  This includes enrichment of user detail.
     *
     * @param message Incoming {@link SymMessage}
     * @return Constructed chat
     */
    private Chat createNewChatFromMessage(SymMessage message) {

        logger.info("Detected new chat on stream ID: {} FromUserID: {}", message.getStreamId(), message.getFromUserId());
        try {
            Chat chat = new Chat();
            chat.setLocalUser(symClient.getLocalUser());
            chat.setStreamId(message.getStreamId());
            chat.setLastMessage(message);

            //Enrich all user data..
            Set<SymUser> remoteUsers = ((SymUserCache) symClient.getCache(CacheType.USER)).getUsersByStream(message.getStreamId());

            if (remoteUsers != null) {

                chat.setRemoteUsers(remoteUsers);

                return chat;
            }

        } catch (UsersClientException e) {
            logger.error("Could not create new chat from message {} {}", message.getStreamId(), message.getFromUserId(), e);
        }

        //Unable to identify user...
        return null;
    }


    /**
     * Process incoming message from ChatListener
     *
     * @param symMessage Incoming {@link  SymMessage}
     */
    @Override
    public void onChatMessage(SymMessage symMessage) {
        if (symMessage == null)
            return;


        String streamId = symMessage.getStreamId();


        logger.debug("New message from stream {}", streamId);

        //There has to be a streamID to do anything.
        if (streamId != null) {

            //Lets see if we can find an existing chat session for this stream.
            Chat chat = chatsByStream.get(streamId);

            //Create a chat from the message if Chat doesn't exist.
            if (chat == null) {
                //Construct it.
                chat = createNewChatFromMessage(symMessage);

                //Good...
                if (chat != null) {

                    //Lets add it to the service...but don't check for streams as it could be a multi-party conversation.
                    //Currently no way to identify all remote users from a given stream. (BUG on REST API)
                    addChat(chat, false);

                } else {
                    logger.error("Failed to add new chat from message {} {}", symMessage.getStreamId(), symMessage.getFromUserId());
                }
                //Chat already exist..
            } else {


                //Inform all Chat listeners of new message..
                chat.onChatMessage(symMessage);

            }


        }


    }

    /**
     * Please use {@link #addListener(ChatServiceListener)}
     *
     * @param chatServiceListener {@link ChatServiceListener}
     * @return True if successful.
     */
    @Deprecated
    public boolean registerListener(ChatServiceListener chatServiceListener) {

        return chatServiceListeners.add(chatServiceListener);
    }

    /**
     * @param chatServiceListener {@link ChatServiceListener}
     * @return True if successful.
     */
    public boolean addListener(ChatServiceListener chatServiceListener) {

        return chatServiceListeners.add(chatServiceListener);
    }

    /**
     * @param chatServiceListener {@link ChatServiceListener}
     * @return True if successful
     */
    public boolean removeListener(ChatServiceListener chatServiceListener) {
        return chatServiceListeners.remove(chatServiceListener);
    }

    /**
     * Returns a set of chats from a given email address (resolved to UserID). This can be BOT or users that
     * the BOT is communicating with.
     *
     * @param email User email
     * @return A set of Chats associated with the given user.
     */
    public Set<Chat> getChatsByEmail(String email) {

        //Resolve the UserID to pull chat set.
        try {
            SymUser user = ((SymUserCache) symClient.getCache(CacheType.USER)).getUserByEmail(email);

            if (user != null)
                return chatsByUser.get(user.getId());
        } catch (UsersClientException e) {
            logger.error("Could not locate user by email {}", email, e);
        }
        return null;
    }


    /**
     * Return a set of chats for a given user
     *
     * @param user {@link SymUser}
     * @return A set of Chats associated with the given user.
     */
    public Set<Chat> getChats(SymUser user) {

        if (user != null)
            return chatsByUser.get(user.getId());

        return null;
    }

    /**
     * Get a given Chat by streamId
     *
     * @param streamId Stream ID
     * @return A Chat
     */
    public Chat getChatByStream(String streamId) {

        return chatsByStream.get(streamId);

    }

    /**
     * Enrich users from a given chat
     *
     * @param chat Chat object to be updated
     * @return Success
     */
    private boolean updateSymUsers(Chat chat) {


        Set<SymUser> verifiedSymUsers = new HashSet<>();


        //Now lets not trust what was provided and update all user details.
        for (SymUser symUser : chat.getRemoteUsers()) {
            try {
                SymUser updatedSymUser = new SymUser();

                if (symUser.getId() != null) {
                    updatedSymUser = ((SymUserCache) symClient.getCache(CacheType.USER)).getUserById(symUser.getId());
                } else if (symUser.getEmailAddress() != null) {
                    updatedSymUser = ((SymUserCache) symClient.getCache(CacheType.USER)).getUserByEmail(symUser.getEmailAddress());
                } else if (symUser.getUsername() != null) {
                    updatedSymUser = ((SymUserCache) symClient.getCache(CacheType.USER)).getUserByName(symUser.getUsername());
                } else {
                    logger.error("Failed to retrieve user detail for chat session..(nothing to identify the user)..");
                }

                if (updatedSymUser.getId() != null)
                    verifiedSymUsers.add(updatedSymUser);

            } catch (UsersClientException e) {
                logger.error("Failed to retrieve user detail for chat session..(nothing to identify the user)..", e);
            }
        }

        //What we verified is not the same as what was defined...bad thing! (strict)
        //Note: in future release we can add an option to allow multi-party with users who are verified.
        if (chat.getRemoteUsers().size() != verifiedSymUsers.size()) {
            return false;
        } else {

            chat.setRemoteUsers(verifiedSymUsers);
            return true;
        }


    }


}
