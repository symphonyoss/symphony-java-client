/*
 *
 * Copyright 20168 The Symphony Software Foundation
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

package org.symphonyoss.symphony.clients;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.impl.SignalsClientImpl;

/**
 * @author dovkatz on 3/13/2018
 */
public class SignalsFactory {

    public static SignalsClient getClient(SymphonyClient symClient){

        return new SignalsClientImpl(symClient.getSymAuth(),symClient.getConfig(), symClient.getAgentHttpClient());

    }

    public static SignalsClient getClient(SymAuth symAuth, SymphonyClientConfig config){

        return new SignalsClientImpl(symAuth, config);

    }

}
