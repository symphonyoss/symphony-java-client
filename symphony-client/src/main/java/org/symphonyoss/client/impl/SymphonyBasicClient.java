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

package org.symphonyoss.client.impl;/**
 * Created by Frank Tarsillo on 5/15/2016.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.ChatService;
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.client.services.PresenceService;
import org.symphonyoss.symphony.clients.*;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.User;

public class SymphonyBasicClient implements SymphonyClient {


    private Logger logger = LoggerFactory.getLogger(SymphonyBasicClient.class);
    private SymAuth symAuth;
    private MessageService messageService;
    private PresenceService presenceService;
    private ChatService chatService;
    private SymUser localUser;
    private String agentUrl;
    private String serviceUrl;
    private MessagesClient messagesClient;
    private DataFeedClient dataFeedClient;
    private UsersClient usersClient;
    private StreamsClient streamsClient;
    private PresenceClient presenceClient;
    private RoomMembershipClient roomMembershipClient;
    private AttachmentsClient attachmentsClient;
    private ConnectionsClient connectionsClient;


    public SymphonyBasicClient() {

    }


    public boolean init(SymAuth symAuth, String email, String agentUrl, String serviceUrl) throws Exception {

        String NOT_LOGGED_IN_MESSAGE = "Currently not logged into Agent, please check certificates and tokens.";
        if (symAuth == null || symAuth.getSessionToken() == null || symAuth.getKeyToken() == null)
            throw new Exception("Symphony Authorization is not valid", new Throwable(NOT_LOGGED_IN_MESSAGE));

        if (agentUrl == null)
            throw new Exception("Failed to provide agent URL", new Throwable("Failed to provide agent URL"));

        if (serviceUrl == null)
            throw new Exception("Failed to provide service URL", new Throwable("Failed to provide service URL"));

        this.symAuth = symAuth;
        this.agentUrl = agentUrl;
        this.serviceUrl = serviceUrl;

        try {
            //Init all clients.
            dataFeedClient = DataFeedFactory.getClient(this, DataFeedFactory.TYPE.DEFAULT);
            messagesClient = MessagesFactory.getClient(this, MessagesFactory.TYPE.DEFAULT);
            presenceClient = PresenceFactory.getClient(this, PresenceFactory.TYPE.DEFAULT);
            streamsClient = StreamsFactory.getClient(this, StreamsFactory.TYPE.DEFAULT);
            usersClient = UsersFactory.getClient(this, UsersFactory.TYPE.DEFAULT);
            attachmentsClient = AttachementsFactory.getClient(this, AttachementsFactory.TYPE.DEFAULT);
            roomMembershipClient = RoomMembershipFactory.getClient(this, RoomMembershipFactory.TYPE.DEFAULT);
            connectionsClient = ConnectionsFactory.getClient(this, ConnectionsFactory.TYPE.DEFAULT);
        } catch (Exception e) {
            logger.error("Could not initialize one of the Symphony API services." +
                    " This is most likely due to not having the right agent or pod URLs." +
                    " This can also be an issue with the client certificate or server.trustore." +
                    " Here is what you have configured: {}");

        }
        messageService = new MessageService(this);
        presenceService = new PresenceService(this);
        chatService = new ChatService(this);

        localUser = usersClient.getUserFromEmail(email);

        return true;
    }

    public SymAuth getSymAuth() {
        return symAuth;
    }

    public void setSymAuth(SymAuth symAuth) {
        this.symAuth = symAuth;
    }

    public String getAgentUrl() {
        return agentUrl;
    }

    public void setAgentUrl(String agentUrl) {
        this.agentUrl = agentUrl;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public DataFeedClient getDataFeedClient() {
        return dataFeedClient;
    }

    public MessagesClient getMessagesClient() {
        return messagesClient;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public PresenceService getPresenceService() {
        return presenceService;
    }

    public SymUser getLocalUser() {
        return localUser;
    }

    public void setLocalUser(SymUser localUser) {
        this.localUser = localUser;
    }

    public ChatService getChatService() {
        return chatService;
    }

    public PresenceClient getPresenceClient() {
        return presenceClient;
    }

    public StreamsClient getStreamsClient() {
        return streamsClient;
    }

    public UsersClient getUsersClient() {
        return usersClient;
    }

    public RoomMembershipClient getRoomMembershipClient() {
        return roomMembershipClient;
    }

    public AttachmentsClient getAttachmentsClient() {
        return attachmentsClient;
    }

    public ConnectionsClient getConnectionsClient() {
        return connectionsClient;
    }
}


