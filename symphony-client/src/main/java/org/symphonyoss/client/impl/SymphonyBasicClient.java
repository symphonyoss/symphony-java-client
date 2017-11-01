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
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.common.Constants;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.SymCacheException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.CacheType;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.*;
import org.symphonyoss.symphony.clients.*;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
    private ChatService chatService;
    private RoomService roomService;
    private SymUser localUser;
    private String agentUrl;
    private String podUrl;
    private MessagesClient messagesClient;
    private DataFeedClient dataFeedClient;
    private UsersClient usersClient;
    private StreamsClient streamsClient;
    private PresenceClient presenceClient;
    private RoomMembershipClient roomMembershipClient;
    private AttachmentsClient attachmentsClient;
    private ConnectionsClient connectionsClient;
    private ShareClient shareClient;
    private SymphonyApis symphonyApis;
    private Client defaultHttpClient = ClientBuilder.newClient();
    private Client podHttpClient;
    private Client agentHttpClient;
    private final long SYMAUTH_REFRESH_TIME = Long.parseLong(System.getProperty(Constants.SYMAUTH_REFRESH_TIME, "7200000"));
    SymUserCache symUserCache;
    private ApiVersion apiVersion = ApiVersion.V4;


    public SymphonyBasicClient() {
        this(ApiVersion.V4);
    }


    public SymphonyBasicClient(ApiVersion apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public void init(SymphonyClientConfig initParams) throws InitException, AuthenticationException {
       try {
           Client httpClient;

           //If a truststore file is provided..
           if (initParams.get(SymphonyClientConfigID.TRUSTSTORE_FILE) != null) {
              httpClient =CustomHttpClient.getClient(
                       initParams.get(SymphonyClientConfigID.USER_CERT_FILE),
                       initParams.get(SymphonyClientConfigID.USER_CERT_PASSWORD),
                       initParams.get(SymphonyClientConfigID.TRUSTSTORE_FILE),
                       initParams.get(SymphonyClientConfigID.TRUSTSTORE_PASSWORD));

           }else{
               httpClient=CustomHttpClient.getClient(
                       initParams.get(SymphonyClientConfigID.USER_CERT_FILE),
                       initParams.get(SymphonyClientConfigID.USER_CERT_PASSWORD));
           }


           init(httpClient, initParams);
       }catch(Exception e){
           throw new InitException("Failed to initialize network...", e);
       }



    }

    @Override
    public void init(Client podHttpClient, Client agentHttpClient, SymphonyClientConfig initParams) throws InitException, AuthenticationException {
        this.podHttpClient = podHttpClient;
        this.agentHttpClient = agentHttpClient;
        this.defaultHttpClient = podHttpClient;


        AuthenticationClient authClient = new AuthenticationClient(
                initParams.get(SymphonyClientConfigID.SESSIONAUTH_URL),
                initParams.get(SymphonyClientConfigID.KEYAUTH_URL),
                podHttpClient);


        SymAuth symAuth = authClient.authenticate();


        boolean disableServices = Boolean.parseBoolean(initParams.get(SymphonyClientConfigID.DISABLE_SERVICES, "False"));

        init(
                symAuth,
                initParams.get(SymphonyClientConfigID.USER_EMAIL),
                initParams.get(SymphonyClientConfigID.AGENT_URL),
                initParams.get(SymphonyClientConfigID.POD_URL),
                disableServices
        );

    }

    @Override
    public void init(Client httpClient, SymphonyClientConfig initParams) throws InitException, AuthenticationException {
        this.defaultHttpClient = httpClient;
        init(defaultHttpClient, defaultHttpClient, initParams);

    }

    /**
     * Initialize client with required parameters.
     *
     * @param symAuth  Contains valid key and session tokens generated from AuthenticationClient.
     * @param email    Email address of the BOT
     * @param agentUrl The Agent URL
     * @param podUrl   The Service URL (in most cases it's the POD URL)
     * @throws InitException Failure of a specific service most likely due to connectivity issues
     */
    @Override
    public void init(SymAuth symAuth, String email, String agentUrl, String podUrl) throws InitException {


        init(symAuth, email, agentUrl, podUrl, false);


    }


    /**
     * Initialize client with required parameters.  Services will be enabled by default.
     *
     * @param httpClient Custom http client to use when initiating the client
     * @param symAuth    Contains valid key and session tokens generated from AuthenticationClient.
     * @param email      Email address of the BOT
     * @param agentUrl   The Agent URL
     * @param podUrl     The Service URL (in most cases it's the POD URL)
     * @throws InitException Failure of a specific service most likely due to connectivity issues
     */
    @Override
    public void init(Client httpClient, SymAuth symAuth, String email, String agentUrl, String podUrl) throws InitException {

        this.defaultHttpClient = httpClient;
        this.agentHttpClient = httpClient;
        this.defaultHttpClient = httpClient;
        init(symAuth, email, agentUrl, podUrl);
    }


    /**
     * Initialize client with required parameters.
     *
     * @param symAuth         Contains valid key and session tokens generated from AuthenticationClient.
     * @param email           Email address of the BOT
     * @param agentUrl        The Agent URL
     * @param podUrl          The Service URL (in most cases it's the POD URL)
     * @param disableServices Disable all real-time services (MessageService, RoomService, ChatService)
     * @throws InitException Failure of a specific service most likely due to connectivity issues
     */
    @Override
    public void init(SymAuth symAuth, String email, String agentUrl, String podUrl, boolean disableServices) throws InitException {

        if(podHttpClient==null)
            podHttpClient = defaultHttpClient;

        if(agentHttpClient==null)
            agentHttpClient = defaultHttpClient;



        String NOT_LOGGED_IN_MESSAGE = "Currently not logged into Agent, please check certificates and tokens.";
        if (symAuth == null || symAuth.getSessionToken() == null || symAuth.getKeyToken() == null)
            throw new InitException("Symphony Authorization is not valid", new Throwable(NOT_LOGGED_IN_MESSAGE));

        if (agentUrl == null)
            throw new InitException("Failed to provide agent URL", new Throwable("Failed to provide agent URL"));

        if (podUrl == null)
            throw new InitException("Failed to provide service URL", new Throwable("Failed to provide service URL"));

        this.symAuth = symAuth;
        this.agentUrl = agentUrl;
        this.podUrl = podUrl;


        //Init all clients.
        dataFeedClient = DataFeedFactory.getClient(this, DataFeedFactory.TYPE.HTTPCLIENT);
        messagesClient = MessagesFactory.getClient(this, MessagesFactory.TYPE.HTTPCLIENT, apiVersion);
        presenceClient = PresenceFactory.getClient(this, PresenceFactory.TYPE.HTTPCLIENT);
        streamsClient = StreamsFactory.getClient(this, StreamsFactory.TYPE.HTTPCLIENT);
        usersClient = UsersFactory.getClient(this, UsersFactory.TYPE.HTTPCLIENT);
        shareClient = ShareFactory.getClient(this, ShareFactory.TYPE.HTTPCLIENT);
        attachmentsClient = AttachmentsFactory.getClient(this, AttachmentsFactory.TYPE.HTTPCLIENT);
        roomMembershipClient = RoomMembershipFactory.getClient(this, RoomMembershipFactory.TYPE.HTTPCLIENT);
        connectionsClient = ConnectionsFactory.getClient(this, ConnectionsFactory.TYPE.HTTPCLIENT);
        symphonyApis = SymphonyApisFactory.getClient(this,SymphonyApisFactory.TYPE.HTTPCLIENT);

        try {


            if (!disableServices) {
                messageService = new MessageService(this, apiVersion);
                chatService = new ChatService(this, apiVersion);
                roomService = new RoomService(this, apiVersion);
            }

            localUser = usersClient.getUserBySession(symAuth);
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
                    "podUrl: " + podUrl);
        }


        symUserCache = new DefaultUserCache(this);

        //Refresh token every so often..
        TimerTask authRefreshTask = new AuthRefreshTask(this);
        // running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(authRefreshTask, SYMAUTH_REFRESH_TIME, SYMAUTH_REFRESH_TIME);


    }


    @Override
    public SymAuth getSymAuth() {
        return symAuth;
    }

    /**
     * @param symAuth Contains valid key and session tokens generated from AuthenticationClient.
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
    public void setAgentUrl(String agentUrl) {
        this.agentUrl = agentUrl;
    }


    /**
     * @return Service URL which can be either the Agent URL or POD URL
     */
    @Override
    public String getPodUrl() {
        return podUrl;
    }

    /**
     * @param podUrl Service URL which can be either the Agent URL or POD URL
     */
    @SuppressWarnings("unused")
    public void setPodUrl(String podUrl) {
        this.podUrl = podUrl;
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

        if (symCache.getCacheType() == null)
            throw new SymCacheException("Cache type not set...");

        if (symCache.getCacheType() == CacheType.USER)
            symUserCache = (SymUserCache) symCache;

    }

    @Override
    public SymCache getCache(CacheType cacheType) {

        if (cacheType == CacheType.USER)
            return symUserCache;

        return null;
    }


    @Override
    public void shutdown() {
        if (getMessageService() != null)
            getMessageService().shutdown();
    }


    @Override
    public Client getPodHttpClient() {
        return podHttpClient;
    }

    @Override
    public void setPodHttpClient(Client podHttpClient) {
        this.podHttpClient = podHttpClient;
    }

    @Override
    public Client getAgentHttpClient() {
        return agentHttpClient;
    }

    @Override
    public void setAgentHttpClient(Client agentHttpClient) {
        this.agentHttpClient = agentHttpClient;
    }

    @Override
    public SymphonyApis getSymphonyApis() {
        return symphonyApis;
    }
}


