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
import org.symphonyoss.client.exceptions.SystemException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.agent.api.SystemApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.clients.AgentSystemClient;
import org.symphonyoss.symphony.clients.model.SymAgentHealthCheck;

import javax.ws.rs.client.Client;

/**
 *
 * Used for agent server health check
 *
 *
 * @author Frank Tarsillo on 10/15/17.
 */
public class AgentSystemClientImpl implements AgentSystemClient {


    private final ApiClient apiClient;
    private final SymAuth symAuth;

    private Logger logger = LoggerFactory.getLogger(AgentSystemClientImpl.class);

    public AgentSystemClientImpl(SymAuth symAuth, String agentUrl) {

        this.symAuth = symAuth;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(agentUrl);


    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     * @param symAuth Authorization model containing session and key tokens
     * @param agentUrl Agent URL
     * @param httpClient Custom client utilized to access Symphony APIs
     */
    public AgentSystemClientImpl(SymAuth symAuth, String agentUrl, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setHttpClient(httpClient);
        apiClient.setBasePath(agentUrl);


    }




    public SymAgentHealthCheck getAgentHealthCheck() throws SystemException{

        SystemApi systemApi = new SystemApi(apiClient);

        try {
            return SymAgentHealthCheck.toSymAgentHealthCheck(systemApi.v2HealthCheckGet(symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken()));
        } catch (ApiException e) {
            throw new SystemException("Could not execute health check on agent server",e);
        }


    }




}
