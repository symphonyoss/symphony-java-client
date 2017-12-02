/*
 *
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
 *
 */

package org.symphonyoss.symphony.clients.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.agent.api.AttachmentsApi;
import org.symphonyoss.symphony.agent.api.DatafeedApi;
import org.symphonyoss.symphony.agent.api.MessagesApi;
import org.symphonyoss.symphony.agent.api.ShareApi;
import org.symphonyoss.symphony.clients.SymphonyApis;
import org.symphonyoss.symphony.pod.api.*;

import javax.ws.rs.client.Client;

/**
 * Convenience class to access underlying language binding API's.
 * <p>
 * Use of the underlying language binding APIs is not recommended.
 *
 * @author Frank Tarsillo on 10/15/17.
 */
public class SymphonyApisImpl implements SymphonyApis {

    private final SymAuth symAuth;
    private org.symphonyoss.symphony.pod.invoker.ApiClient podApiClient;
    private org.symphonyoss.symphony.agent.invoker.ApiClient agentApiCient;


    private final Logger logger = LoggerFactory.getLogger(SymphonyApisImpl.class);


    /**
     * Init
     *
     * @param symAuth         Authorization model containing session and key tokens
     * @param config Symphony Client config
     */
    public SymphonyApisImpl(SymAuth symAuth, SymphonyClientConfig config) {

        this(symAuth, config, null, null);


    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     *
     * @param symAuth         Authorization model containing session and key tokens
     * @param config Symphony Client config
     * @param podHttpClient   Custom pod HTTP Client to use
     * @param agentHttpClient Custom agent HTTP client
     */
    public SymphonyApisImpl(SymAuth symAuth, SymphonyClientConfig config, Client podHttpClient, Client agentHttpClient) {
        this.symAuth = symAuth;


        podApiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        if (podHttpClient != null)
            podApiClient.setHttpClient(podHttpClient);
        podApiClient.setBasePath(config.get(SymphonyClientConfigID.POD_URL));


        //Get Service client to query for userID.
        agentApiCient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        if (agentHttpClient != null)
            agentApiCient.setHttpClient(agentHttpClient);
        agentApiCient.setBasePath(config.get(SymphonyClientConfigID.AGENT_URL));


    }


    @Override
    public AttachmentsApi getAttachmentsApi() {
        return new AttachmentsApi(agentApiCient);
    }

    @Override
    public ConnectionApi getConnectionApi() {
        return new ConnectionApi(podApiClient);
    }


    @Override
    public DatafeedApi getDatafeedApi() {
        return new DatafeedApi(agentApiCient);
    }

    @Override
    public MessagesApi getMessagesApi() {
        return new MessagesApi(agentApiCient);
    }

    @Override
    public PresenceApi getPresenceApi() {
        return new PresenceApi(podApiClient);
    }

    @Override
    public RoomMembershipApi getRoomMembershipApi() {
        return new RoomMembershipApi(podApiClient);
    }

    @Override
    public StreamsApi getStreamsApi() {
        return new StreamsApi(podApiClient);
    }

    @Override
    public UsersApi getUsersApi() {
        return new UsersApi(podApiClient);
    }

    @Override
    public UserApi getUserApi() {
        return new UserApi(podApiClient);
    }

    @Override
    public ShareApi getShareApi() {
        return new ShareApi(agentApiCient);
    }


    @Override
    public org.symphonyoss.symphony.pod.api.SystemApi getPodSystemApi() {
        return new org.symphonyoss.symphony.pod.api.SystemApi(podApiClient);
    }

    @Override
    public org.symphonyoss.symphony.agent.api.SystemApi getAgentSystemApi() {
        return new org.symphonyoss.symphony.agent.api.SystemApi(agentApiCient);
    }


    @Override
    public AppEntitlementApi getAppEntitlementApi() {
        return new AppEntitlementApi(podApiClient);
    }

    @Override
    public ApplicationApi getApplicationApi() {
        return new ApplicationApi(podApiClient);
    }

    @Override
    public DisclaimerApi getDisclaimerApi() {
        return new DisclaimerApi(podApiClient);
    }

    @Override
    public InfoBarriersApi getInfoBarriersApi() {
        return new InfoBarriersApi(podApiClient);
    }

    @Override
    public MessageSuppressionApi getMessageSuppressionApi() {
        return new MessageSuppressionApi(podApiClient);
    }

    @Override
    public SecurityApi getSecurityApi() {
        return new SecurityApi(podApiClient);
    }

    @Override
    public SessionApi getSessionApi() {
        return new SessionApi(podApiClient);
    }


}


