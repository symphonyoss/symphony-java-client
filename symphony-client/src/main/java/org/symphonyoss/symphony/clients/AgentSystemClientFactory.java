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

package org.symphonyoss.symphony.clients;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.impl.AgentSystemClientImpl;

import javax.ws.rs.client.Client;

/**
 * @author Frank Tarsillo
 */
public class AgentSystemClientFactory {


    /**
     * Create instance from SymClient which should have already set SymAuth
     *
     * @param symClient Symphony client instance
     * @return AgentSystemClient
     */
    public static AgentSystemClient getClient(SymphonyClient symClient) {

        return new AgentSystemClientImpl(symClient.getSymAuth(), symClient.getConfig(), symClient.getAgentHttpClient());

    }

    /**
     * Create instance from SymClient
     *
     * @param symAuth SymAuth
     * @param config  Symphony client config
     * @param client  HttpClient to use
     * @return AgentSystemClient
     */
    public static AgentSystemClient getClient(SymAuth symAuth, SymphonyClientConfig config, Client client) {

        return new AgentSystemClientImpl(symAuth, config, client);
    }
}
