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
import org.symphonyoss.client.model.Room;
import org.symphonyoss.exceptions.MessagesException;
import org.symphonyoss.exceptions.StreamsException;
import org.symphonyoss.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.agent.model.MessageList;
import org.symphonyoss.symphony.agent.model.MessageSubmission;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class MessageService implements MessageListener {

    private final SymphonyClient symClient;
    private org.symphonyoss.symphony.agent.invoker.ApiClient agentClient;
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageFeedWorker messageFeedWorker;
    private final Set<MessageListener> messageListeners = ConcurrentHashMap.newKeySet();

    public MessageService(SymphonyClient symClient) {

        this.symClient = symClient;

        messageFeedWorker = new MessageFeedWorker(symClient, this);
        new Thread(messageFeedWorker).start();

    }

    @Deprecated
    public void sendMessage(Room room, MessageSubmission message) throws MessagesException {

        symClient.getMessagesClient().sendMessage(room.getStream(), message);

    }

    public void sendMessage(Room room, SymMessage message) throws MessagesException {

        symClient.getMessagesClient().sendMessage(room.getStream(), message);

    }

    @Deprecated
    public void sendMessage(Chat chat, MessageSubmission message) throws MessagesException {

        symClient.getMessagesClient().sendMessage(chat.getStream(), message);

    }


    public void sendMessage(Chat chat, SymMessage message) throws MessagesException {

        symClient.getMessagesClient().sendMessage(chat.getStream(), message);

    }


    @Deprecated
    public void sendMessage(String email, MessageSubmission message) throws MessagesException {

        SymUser remoteUser = null;
        try {
            remoteUser = symClient.getUsersClient().getUserFromEmail(email);

            symClient.getMessagesClient().sendMessage(symClient.getStreamsClient().getStream(remoteUser), message);

        } catch (UsersClientException e) {
            throw new MessagesException("Failed to find user from email address: " + email, e.getCause());
        } catch (StreamsException e) {
            throw new MessagesException("Failed to send message to user by email address: " + email, e.getCause());
        }

    }

    public void sendMessage(String email, SymMessage message) throws MessagesException {

        SymUser remoteUser = null;
        try {

            remoteUser = symClient.getUsersClient().getUserFromEmail(email);

            symClient.getMessagesClient().sendMessage(symClient.getStreamsClient().getStream(remoteUser), message);

        } catch (UsersClientException e) {
            throw new MessagesException("Failed to find user from email address: " + email, e.getCause());
        } catch (StreamsException e) {
            throw new MessagesException("Failed to send message. Unable to identify stream from email: " + email, e.getCause());
        }

    }

    private List<SymMessage> getMessagesFromStream(Stream stream, Long since, Integer offset, Integer maxMessages) throws MessagesException {

        return symClient.getMessagesClient().getMessagesFromStream(
                stream, since, offset, maxMessages);

    }


    public List<SymMessage> getMessagesFromUserId(long userId, Long since, Integer offset, Integer maxMessages) throws MessagesException {


        SymUser user = new SymUser();
        user.setId(userId);


        try {
            return getMessagesFromStream(
                    symClient.getStreamsClient().getStream(user), since, offset, maxMessages);
        } catch (StreamsException e) {
            throw new MessagesException("Failed to retrieve messages. Unable to identity stream for userId: " + userId, e.getCause());
        }


    }


    private void processMessage(SymMessage message) {

        logger.debug("LocalID: {} messageID: {}", symClient.getLocalUser().getId(), message.getFromUserId());
        if (symClient.getLocalUser().getId().equals(message.getFromUserId()))
            return;

        if (message.getStreamId() == null && message.getMessageType() != null)
            return;


        for (MessageListener messageListener : messageListeners) {
            if (messageListener != null)
                messageListener.onMessage(message);
        }
        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nType: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                message.getMessage(),
                message.getMessageType());


    }

    public void onMessage(Message message) {

        processMessage(SymMessage.toSymMessage(message));

    }

    @Override
    public void onMessage(SymMessage message) {
        processMessage(message);
    }

    public boolean registerMessageListener(MessageListener messageListener) {

        return messageListeners.add(messageListener);

    }

    public boolean removeMessageListener(MessageListener messageListener) {

        return messageListeners.remove(messageListener);

    }


}
