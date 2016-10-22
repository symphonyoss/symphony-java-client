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

package org.symphonyoss.symphony.clients.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.exceptions.SymException;
import org.symphonyoss.symphony.clients.RoomMembershipClient;
import org.symphonyoss.symphony.pod.api.RoomMembershipApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.MembershipList;




/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class RoomMembershipClientImpl implements RoomMembershipClient {
    private final SymAuth symAuth;
    private final ApiClient apiClient;

    private Logger logger = LoggerFactory.getLogger(RoomMembershipClientImpl.class);

    public RoomMembershipClientImpl(SymAuth symAuth, String serviceUrl) {

        this.symAuth = symAuth;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

    }


    public MembershipList getRoomMembership(String roomId) throws SymException {


        if (roomId == null) {
            throw new NullPointerException("Room ID was not provided...");
        }
        RoomMembershipApi roomMembershipApi = new RoomMembershipApi(apiClient);

        try {
            return roomMembershipApi.v1RoomIdMembershipListGet(roomId,symAuth.getSessionToken().getToken());
        } catch (ApiException e) {
            throw new SymException("Failed to retrieve room membership for room ID: " + roomId, e);
        }


    }





}
