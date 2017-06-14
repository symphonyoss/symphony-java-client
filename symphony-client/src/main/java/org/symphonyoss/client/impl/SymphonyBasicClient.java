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

package org.symphonyoss.client.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.SymCacheException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.CacheType;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.*;
import org.symphonyoss.symphony.clients.*;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.client.Client;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a full abstraction of underlying clients and exposes services to simplify
 * functional collaboration elements.  Although all clients are exposed, it's highly recommended
 * to access core functions through services.
 * <p>
 * You must init this class with valid key and session tokens with stored in a SymAuth object.
 * Please note, that the SymAuth expires and must be updated periodically to maintain access to
 * the Symphony network.
 *
 * @author Frank Tarsillo
 */
public class SymphonyBasicClient implements SymphonyClient {


    private final Logger logger = LoggerFactory.getLogger(SymphonyBasicClient.class);
    private SymAuth symAuth;
    private MessageService messageService;
    private PresenceService presenceService;
    private ChatService chatService;
    private RoomService roomService;
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
    private ShareClient shareClient;
    private Client defaultHttpClient;
    private final long SYMAUTH_REFRESH_TIME = Long.parseLong(System.getProperty(Constants.SYMAUTH_REFRESH_TIME, "7200000"));
    SymUserCache symUserCache;

    public SymphonyBasicClient() {



    }

    /**
     * Initialize client with required parameters.
     *
     * @param symAuth    Contains valid key and session tokens generated from AuthorizationClient.
     * @param email      Email address of the BOT
     * @param agentUrl   The Agent URL
     * @param serviceUrl The Service URL (in most cases it's the POD URL)
     * @throws InitException Failure of a specific service most likely due to connectivity issues
     */
    @Override
    public void init(SymAuth symAuth, String email, String agentUrl, String serviceUrl) throws InitException {

        String NOT_LOGGED_IN_MESSAGE = "Currently not logged into Agent, please check certificates and tokens.";
        if (symAuth == null || symAuth.getSessionToken() == null || symAuth.getKeyToken() == null)
            throw new InitException("Symphony Authorization is not valid", new Throwable(NOT_LOGGED_IN_MESSAGE));

        if (agentUrl == null)
            throw new InitException("Failed to provide agent URL", new Throwable("Failed to provide agent URL"));

        if (serviceUrl == null)
            throw new InitException("Failed to provide service URL", new Throwable("Failed to provide service URL"));

        this.symAuth = symAuth;
        this.agentUrl = agentUrl;
        this.serviceUrl = serviceUrl;


        //Init all clients.
        dataFeedClient = (defaultHttpClient == null) ? DataFeedFactory.getClient(this, DataFeedFactory.TYPE.DEFAULT) : DataFeedFactory.getClient(this, DataFeedFactory.TYPE.HTTPCLIENT);
        messagesClient = (defaultHttpClient == null) ? MessagesFactory.getClient(this, MessagesFactory.TYPE.DEFAULT) : MessagesFactory.getClient(this, MessagesFactory.TYPE.HTTPCLIENT);
        presenceClient = (defaultHttpClient == null) ? PresenceFactory.getClient(this, PresenceFactory.TYPE.DEFAULT) : PresenceFactory.getClient(this, PresenceFactory.TYPE.HTTPCLIENT);
        streamsClient = (defaultHttpClient == null) ? StreamsFactory.getClient(this, StreamsFactory.TYPE.DEFAULT) : StreamsFactory.getClient(this, StreamsFactory.TYPE.HTTPCLIENT);
        usersClient = (defaultHttpClient == null) ? UsersFactory.getClient(this, UsersFactory.TYPE.DEFAULT) : UsersFactory.getClient(this, UsersFactory.TYPE.HTTPCLIENT);
        shareClient = (defaultHttpClient == null) ? ShareFactory.getClient(this, ShareFactory.TYPE.DEFAULT) : ShareFactory.getClient(this, ShareFactory.TYPE.HTTPCLIENT);
        attachmentsClient = (defaultHttpClient == null) ? AttachmentsFactory.getClient(this, AttachmentsFactory.TYPE.DEFAULT) : AttachmentsFactory.getClient(this, AttachmentsFactory.TYPE.HTTPCLIENT);
        roomMembershipClient = (defaultHttpClient == null) ? RoomMembershipFactory.getClient(this, RoomMembershipFactory.TYPE.DEFAULT) : RoomMembershipFactory.getClient(this, RoomMembershipFactory.TYPE.HTTPCLIENT);
        connectionsClient = (defaultHttpClient == null) ? ConnectionsFactory.getClient(this, ConnectionsFactory.TYPE.DEFAULT) : ConnectionsFactory.getClient(this, ConnectionsFactory.TYPE.HTTPCLIENT);

        try {
            messageService = new MessageService(this);
            presenceService = new PresenceService(this);
            chatService = new ChatService(this);
            roomService = new RoomService(this);

            localUser = usersClient.getUserFromEmail(email);
        } catch (SymException e) {
            logger.error("Failed to initialize client..", e);

            throw new InitException("Could not initialize one of the Symphony API services." +
                    " This is most likely due to not having the right agent or pod URLs." +
                    " This can also be an issue with the client certificate or server.truststore." +
                    " Here is what you have configured:\n" +
                    "SessionToken: " + symAuth.getSessionToken() + "\n" +
                    "KeyToken: " + symAuth.getKeyToken() + "\n" +
                    "Email: " + email + "\n" +
                    "AgentUrl: " + agentUrl + "\n" +
                    "ServiceUrl: " + serviceUrl);
        }


        symUserCache = new DefaultUserCache(this);

        //Refresh token every so often..
        TimerTask authRefreshTask = new AuthRefreshTask(this);
        // running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(authRefreshTask, SYMAUTH_REFRESH_TIME, SYMAUTH_REFRESH_TIME);


    }


    @Override
    public void init(Client httpClient, SymAuth symAuth, String email, String agentUrl, String serviceUrl) throws InitException {

        this.defaultHttpClient = httpClient;
        init(symAuth, email, agentUrl, serviceUrl);
    }

    @Override
    public SymAuth getSymAuth() {
        return symAuth;
    }

    /**
     * @param symAuth Contains valid key and session tokens generated from AuthorizationClient.
     */
    @Override
    public void setSymAuth(SymAuth symAuth) {
        this.symAuth = symAuth;
    }

    @Override
    public String getAgentUrl() {
        return agentUrl;
    }

    /**
     * @param agentUrl Agent URL
     */
    @SuppressWarnings("unused")
    public void setAgentUrl(String agentUrl) {
        this.agentUrl = agentUrl;
    }

    /**
     * @return Service URL which can be either the Agent URL or POD URL
     */
    @Override
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * @param serviceUrl Service URL which can be either the Agent URL or POD URL
     */
    @SuppressWarnings("unused")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * @return DataFeedClient
     */
    @Override
    public DataFeedClient getDataFeedClient() {
        return dataFeedClient;
    }

    /**
     * @return MessagesClient
     */
    @Override
    public MessagesClient getMessagesClient() {
        return messagesClient;
    }

    /**
     * @return MessagesService
     */
    @Override
    public MessageService getMessageService() {
        return messageService;
    }

    @Override
    public PresenceService getPresenceService() {
        return presenceService;
    }

    @Override
    public RoomService getRoomService() {
        return roomService;
    }

    @Override
    public SymUser getLocalUser() {
        return localUser;
    }

    @SuppressWarnings("unused")
    public void setLocalUser(SymUser localUser) {
        this.localUser = localUser;
    }

    @Override
    public ChatService getChatService() {
        return chatService;
    }

    @Override
    public PresenceClient getPresenceClient() {
        return presenceClient;
    }

    @Override
    public StreamsClient getStreamsClient() {
        return streamsClient;
    }

    @Override
    public UsersClient getUsersClient() {
        return usersClient;
    }

    @Override
    public RoomMembershipClient getRoomMembershipClient() {
        return roomMembershipClient;
    }

    @Override
    public AttachmentsClient getAttachmentsClient() {
        return attachmentsClient;
    }

    @Override
    public ConnectionsClient getConnectionsClient() {
        return connectionsClient;
    }

    @Override
    public ShareClient getShareClient() {
        return shareClient;
    }

    /**
     * Provides the default http client if one is set.
     *
     * @return Default http client if set
     */
    @Override
    public Client getDefaultHttpClient() {
        return defaultHttpClient;
    }

    @Override
    public void setDefaultHttpClient(Client defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
    }

    @Override
    public void setCache(SymCache symCache) throws SymCacheException {

        if(symCache.getCacheType() == null)
            throw new SymCacheException("Cache type not set...");

        if(symCache.getCacheType() == CacheType.USER)
            symUserCache = (SymUserCache)symCache;

    }

    @Override
    public SymCache getCache(CacheType cacheType) {

        if (cacheType == CacheType.USER)
            return symUserCache;

        return null;
    }


    @Override
    public void shutdown() {
        getMessageService().shutdown();
        getPresenceService().shutdown();
    }

}


