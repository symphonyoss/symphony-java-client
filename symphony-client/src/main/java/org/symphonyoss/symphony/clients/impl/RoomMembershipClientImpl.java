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

import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.RoomMembershipClient;
import org.symphonyoss.symphony.pod.api.RoomMembershipApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.MembershipList;
import org.symphonyoss.symphony.pod.model.UserId;

import com.google.common.base.Strings;


/**
 * Supports the ability to identify and administrate members of a room (stream).
 * It also supports identification of users for any streamID.  This includes 1:1 and multi-party chat conversations.
 *
 * @author Frank Tarsillo
 */
public class RoomMembershipClientImpl implements RoomMembershipClient {
    private final SymAuth symAuth;
    private final ApiClient apiClient;

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(RoomMembershipClientImpl.class);

    public RoomMembershipClientImpl(SymAuth symAuth, String serviceUrl) {

        this.symAuth = symAuth;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     *
     * @param symAuth    Authorization object holding session and key tokens
     * @param serviceUrl The Symphony service URL
     * @param httpClient The HttpClient to use when calling Symphony API
     */
    public RoomMembershipClientImpl(SymAuth symAuth, String serviceUrl, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setHttpClient(httpClient);
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());
    }

    /**
     * Provides room membership
     *
     * @param roomStreamId - stream-id of the chat room you want to add the member to
     * @return {@link MembershipList}
     * @throws SymException Exceptions from API calls into Symphony
     */
    @Override
    public MembershipList getRoomMembership(String roomStreamId) throws SymException {
        if (roomStreamId == null) {
            throw new NullPointerException("Room ID was not provided...");
        }
        RoomMembershipApi roomMembershipApi = new RoomMembershipApi(apiClient);

        try {
            return roomMembershipApi.v1RoomIdMembershipListGet(roomStreamId, symAuth.getSessionToken().getToken());
        } catch (ApiException e) {
            throw new SymException("Failed to retrieve room membership for room ID: " + roomStreamId, e);
        }
    }

    /**
     * Call this method to add a member to a chat room. Pass in two parameters - chat-room stream-id and user-id
     *
     * @param roomStreamId - stream-id of the chat room you want to add the member to
     * @param userId       userId for the user in Symphony
     * @throws SymException throws an {@link org.symphonyoss.symphony.pod.invoker.ApiException} if there were any issues while invoking the endpoint,
     *                   {@link IllegalArgumentException} if the arguments were wrong, {@link IllegalStateException} if the
     *                   session-token is null
     */
    @Override
    public void addMemberToRoom(String roomStreamId, long userId) throws SymException {
        if (Strings.isNullOrEmpty(roomStreamId)) {
            throw new IllegalArgumentException("Argument roomStreamId must not be empty or null");
        }

        RoomMembershipApi roomMembershipApi = new RoomMembershipApi(apiClient);
        String sessionToken = symAuth.getSessionToken().getToken();

        try {
            if (!Strings.isNullOrEmpty(sessionToken)) {
                roomMembershipApi.v1RoomIdMembershipAddPost(roomStreamId, new UserId().id(userId), sessionToken);
            } else {
                throw new IllegalStateException("Invalid session token. It must not be null or empty");
            }
        } catch (ApiException e) {
            throw new SymException("Symphony API exception adding member to room.", e);
        }

    }

    /**
     * Call this method to remove a member from a chat room. Pass in two parameters - chat-room stream-id and user-id
     *
     * @param roomStreamId - stream-id of the chat room you want to add the member to Room
     * @param userId       userId for the user in Symphony
     * @throws SymException throws an {@link org.symphonyoss.symphony.pod.invoker.ApiException} if there were any issues while invoking the endpoint,
     *                   {@link IllegalArgumentException} if the arguments were wrong, {@link IllegalStateException} if the
     *                   session-token is null
     */
    @Override
    public void removeMemberFromRoom(String roomStreamId, long userId) throws SymException {
        if (Strings.isNullOrEmpty(roomStreamId)) {
            throw new IllegalArgumentException("Argument roomStreamId must not be empty or null");
        }

        RoomMembershipApi roomMembershipApi = new RoomMembershipApi(apiClient);
        String sessionToken = symAuth.getSessionToken().getToken();

        try {
            if (!Strings.isNullOrEmpty(sessionToken)) {
                roomMembershipApi.v1RoomIdMembershipRemovePost(roomStreamId, new UserId().id(userId), sessionToken);
            } else {
                throw new IllegalStateException("Invalid session token. It must not be null or empty");
            }
        } catch (ApiException e) {
            throw new SymException("Symphony API exception removing member from room.", e);
        }
    }
}
