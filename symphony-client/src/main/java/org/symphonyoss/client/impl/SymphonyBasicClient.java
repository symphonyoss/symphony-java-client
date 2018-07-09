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


import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
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
import org.symphonyoss.symphony.clients.jmx.ClientCheck;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymUser;


import javax.management.*;
import javax.ws.rs.client.Client;
import java.lang.management.ManagementFactory;
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
    private PresenceService presenceService;
    private SymUser localUser;
    private String agentUrl;
    private String podUrl;
    private MessagesClient messagesClient;
    private DataFeedClient dataFeedClient;
    private UsersClient usersClient;
    private StreamsClient streamsClient;
    private PresenceClient presenceClient;
    private SignalsClient signalsClient;
    private RoomMembershipClient roomMembershipClient;
    private AttachmentsClient attachmentsClient;
    private ConnectionsClient connectionsClient;
    private ShareClient shareClient;
    private SymphonyApis symphonyApis;
    private Client defaultHttpClient;
    private Client podHttpClient;
    private Client agentHttpClient;
    private SymphonyClientConfig config;
    private final long SYMAUTH_REFRESH_TIME = Long.parseLong(System.getProperty(Constants.SYMAUTH_REFRESH_TIME, "7200000"));
    private SymUserCache symUserCache;
    private ApiVersion apiVersion = ApiVersion.V4;
    private String name;
    private Timer timer;


    public SymphonyBasicClient() {
        this(ApiVersion.V4);
    }

    public SymphonyBasicClient(ApiVersion apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public void init(SymphonyClientConfig config) throws InitException, AuthenticationException {

        this.config = config;
        try {

            init(CustomHttpClient.getDefaultHttpClient(config), config);

        } catch (Exception e) {
            throw new InitException("Failed to initialize network...", e);
        }

    }

    /**
     * Set name of SJC to be used for threading
     *
     * @param name Used for threading
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return name of SJC
     *
     * @return Name of SJC
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Initialize the SJC with a custom http client for both pod and agent connectivity.
     *
     * @param podHttpClient   Custom http client to use when connecting to the pod
     * @param agentHttpClient Custom http client to use when connecting to the agent server
     * @param config          Configuration object
     * @throws InitException           Exception from initialization
     * @throws AuthenticationException Exception from authorization.
     */
    @Override
    public void init(Client podHttpClient, Client agentHttpClient, SymphonyClientConfig config) throws InitException, AuthenticationException {
        this.podHttpClient = podHttpClient;
        this.agentHttpClient = agentHttpClient;
        this.defaultHttpClient = podHttpClient;
        this.config = config;


        AuthenticationClient authClient = new AuthenticationClient(
                config.get(SymphonyClientConfigID.SESSIONAUTH_URL),
                config.get(SymphonyClientConfigID.KEYAUTH_URL),
                agentHttpClient);


        SymAuth symAuth = authClient.authenticate();

        init(symAuth, config);


    }

    @Override
    public void init(Client httpClient, SymphonyClientConfig config) throws InitException, AuthenticationException {
        this.defaultHttpClient = httpClient;
        init(defaultHttpClient, defaultHttpClient, config);

    }

    /**
     * Initialize client with required parameters.
     *
     * @param symAuth   Contains valid key and session tokens generated from AuthenticationClient.
     * @param userEmail Email address of the BOT
     * @param agentUrl  The Agent URL
     * @param podUrl    The Service URL (in most cases it's the POD URL)
     * @throws InitException Failure of a specific service most likely due to connectivity issues
     */
    @Override
    @Deprecated
    public void init(SymAuth symAuth, String userEmail, String agentUrl, String podUrl) throws InitException {
        updateConfig(agentUrl, podUrl, userEmail);
        init(symAuth, config);


    }


    /**
     * Initialize client with required parameters.  Services will be enabled by default.
     *
     * @param httpClient Custom http client to use when initiating the client
     * @param symAuth    Contains valid key and session tokens generated from AuthenticationClient.
     * @param userEmail  Email address of the BOT
     * @param agentUrl   The Agent URL
     * @param podUrl     The Service URL (in most cases it's the POD URL)
     * @throws InitException Failure of a specific service most likely due to connectivity issues
     */
    @Override
    @Deprecated
    public void init(Client httpClient, SymAuth symAuth, String userEmail, String agentUrl, String podUrl) throws InitException {

        this.defaultHttpClient = httpClient;
        this.agentHttpClient = httpClient;
        this.defaultHttpClient = httpClient;

        updateConfig(agentUrl, podUrl, userEmail);

        init(symAuth, config);
    }

    /**
     * Initialize client with required parameters.
     *
     * @param symAuth         Contains valid key and session tokens generated from AuthenticationClient.
     * @param userEmail       Email address of the BOT
     * @param agentUrl        The Agent URL
     * @param podUrl          The Service URL (in most cases it's the POD URL)
     * @param disableServices Disable all real-time services (MessageService, RoomService, ChatService)
     * @throws InitException Failure of a specific service most likely due to connectivity issues
     */
    @Override
    @Deprecated
    public void init(SymAuth symAuth, String userEmail, String agentUrl, String podUrl, boolean disableServices) throws InitException {

        updateConfig(agentUrl, podUrl, userEmail);
        config.set(SymphonyClientConfigID.DISABLE_SERVICES, String.valueOf(disableServices));

        init(symAuth, config);

    }


    /**
     * Initialize client with required parameters.
     *
     * @param symAuth Contains valid key and session tokens generated from AuthenticationClient.
     * @param config  Symphony client config
     * @throws InitException Failure of a specific service most likely due to connectivity issues
     */
    @Override
    public void init(SymAuth symAuth, SymphonyClientConfig config) throws InitException {

        this.config = config;
        this.symAuth = symAuth;

        updateConfig(config);


        try {
            if (defaultHttpClient == null)
                defaultHttpClient = CustomHttpClient.getDefaultHttpClient(config);
        } catch (Exception e) {
            logger.error("Could not set default http client from config...", e);
        }


        if (podHttpClient == null)
            podHttpClient = defaultHttpClient;

        if (agentHttpClient == null)
            agentHttpClient = defaultHttpClient;


        String NOT_LOGGED_IN_MESSAGE = "Currently not logged into Agent, please check certificates and tokens.";
        if (symAuth == null || symAuth.getSessionToken() == null || symAuth.getKeyToken() == null)
            throw new InitException("Symphony Authorization is not valid", new Throwable(NOT_LOGGED_IN_MESSAGE));

        if (config.get(SymphonyClientConfigID.AGENT_URL) == null)
            throw new InitException("Failed to provide agent URL", new Throwable("Failed to provide agent URL"));

        if (config.get(SymphonyClientConfigID.POD_URL) == null)
            throw new InitException("Failed to provide service URL", new Throwable("Failed to provide service URL"));

        this.symAuth = symAuth;
        this.name = config.get(SymphonyClientConfigID.USER_EMAIL);

        //Init all clients.
        dataFeedClient = DataFeedFactory.getClient(this);
        messagesClient = MessagesFactory.getClient(this);
        presenceClient = PresenceFactory.getClient(this);
        signalsClient  = SignalsFactory.getClient(this);
        streamsClient = StreamsFactory.getClient(this);
        usersClient = UsersFactory.getClient(this);
        shareClient = ShareFactory.getClient(this);
        attachmentsClient = AttachmentsFactory.getClient(this);
        roomMembershipClient = RoomMembershipFactory.getClient(this);
        connectionsClient = ConnectionsFactory.getClient(this);
        symphonyApis = SymphonyApisFactory.getClient(this);

        try {


            if (!Boolean.parseBoolean(config.get(SymphonyClientConfigID.DISABLE_SERVICES, "False"))) {
                messageService = new MessageService(this);
                chatService = new ChatService(this);
                roomService = new RoomService(this);
                presenceService = new PresenceService(this);

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
                    "Email: " + config.get(SymphonyClientConfigID.USER_EMAIL) + "\n" +
                    "AgentUrl: " + config.get(SymphonyClientConfigID.AGENT_URL) + "\n" +
                    "podUrl: " + config.get(SymphonyClientConfigID.POD_URL));
        }


        symUserCache = new DefaultUserCache(this);

        //Refresh token every so often..
        TimerTask authRefreshTask = new AuthRefreshTask(this);
        // running timer task as daemon thread
        timer = new Timer("AuthRefresh:" + this.getName(), true);
        timer.scheduleAtFixedRate(authRefreshTask, SYMAUTH_REFRESH_TIME, SYMAUTH_REFRESH_TIME);


        //Publish MBean via JMX
        //Default is true
        if (Boolean.parseBoolean(config.get(SymphonyClientConfigID.HEALTHCHECK_JMX_ENABLED, "True"))) {
            logger.info("Registering JMX Health Bean...");
            this.registerHealthMBean();
        }
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

    @Override 
    public SignalsClient getSignalsClient() {
    	    return signalsClient;
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


        if (getPresenceService() != null)
            getPresenceService().shutdown();

        if (timer != null)
            timer.cancel();


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


    @Override
    public SymphonyClientConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(SymphonyClientConfig config) {
        this.config = config;
    }

    @Override
    public PresenceService getPresenceService() {
        return presenceService;
    }

    /**
     * Added to support transition to SymphonyClientConfig init.  Will be removed in next major version.
     *
     * @param agentUrl  Agent URL
     * @param podUrl    Pod URL
     * @param userEmail Bot user email
     */
    private void updateConfig(String agentUrl, String podUrl, String userEmail) {

        if (config == null) {
            config = new SymphonyClientConfig(false);
        }

        config.set(SymphonyClientConfigID.AGENT_URL, agentUrl);
        config.set(SymphonyClientConfigID.POD_URL, podUrl);
        config.set(SymphonyClientConfigID.USER_EMAIL, userEmail);


    }

    /**
     * Added to support transition to SymphonyClientConfig init.  Will be removed in next major version.
     *
     * @param config SymphonyClientConfig
     */
    private void updateConfig(SymphonyClientConfig config) {

        agentUrl = config.get(SymphonyClientConfigID.AGENT_URL);
        podUrl = config.get(SymphonyClientConfigID.POD_URL);

    }


    private void registerHealthMBean() {
        logger.info("Exposing SymAgentHealthCheck as JMX MBean...");
        ClientCheck clientCheck = new ClientCheck(this);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        String mBeanName = "org.symphonyoss.client:type=ClientCheckMBean";
        try {
            ObjectName mBean = new ObjectName(mBeanName);
            mbs.registerMBean(clientCheck, mBean);
            logger.info("Registered JMX Mbean: "+mbs.getMBeanInfo(mBean).getClassName());
        } catch (ReflectionException e) {
            logger.error("Cannot register JMX Mbean: "+mBeanName,e);
        } catch (InstanceNotFoundException e) {
            logger.error("Cannot register JMX Mbean: "+mBeanName,e);
        } catch (IntrospectionException e) {
            logger.error("Cannot register JMX Mbean: "+mBeanName,e);
        } catch (MBeanRegistrationException e) {
            logger.error("Cannot register JMX Mbean: "+mBeanName,e);
        } catch (MalformedObjectNameException e) {
            logger.error("Cannot register JMX Mbean: "+mBeanName,e);
        } catch (InstanceAlreadyExistsException e) {
            logger.error("Cannot register JMX Mbean: "+mBeanName,e);
        } catch (NotCompliantMBeanException e) {
            logger.error("Cannot register JMX Mbean: "+mBeanName,e);
        }
    }

}


