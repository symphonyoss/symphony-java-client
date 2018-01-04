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


import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.ConnectionsException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.ConnectionsClient;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.clients.model.SymUserConnection;
import org.symphonyoss.symphony.clients.model.SymUserConnectionRequest;
import org.symphonyoss.symphony.pod.api.ConnectionApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.UserConnectionList;

import javax.ws.rs.client.Client;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Client for accessing connections functions.
 *
 * @author Frank Tarsillo
 */
public class ConnectionsClientImpl implements ConnectionsClient {

    private final ApiClient apiClient;
    private final SymAuth symAuth;


    /**
     * Init
     *
     * @param symAuth Syphony Authorization tokens
     * @param config  Symphony Client config
     */
    public ConnectionsClientImpl(SymAuth symAuth, SymphonyClientConfig config) {

        this(symAuth, config, null);

    }


    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param config     Config updated with pod url
     * @param httpClient Custom HTTP Client to use
     */
    public ConnectionsClientImpl(SymAuth symAuth, SymphonyClientConfig config, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();

        if (httpClient != null)
            apiClient.setHttpClient(httpClient);

        apiClient.setBasePath(config.get(SymphonyClientConfigID.POD_URL));


    }

    @Override
    public List<SymUserConnection> getIncomingRequests() throws ConnectionsException {

        return getAllConnections(SymUserConnection.Status.PENDING_INCOMING, null);

    }


    @Override
    public List<SymUserConnection> getPendingRequests() throws ConnectionsException {


        return getAllConnections(SymUserConnection.Status.PENDING_OUTGOING, null);

    }


    @Override
    public List<SymUserConnection> getRejectedRequests() throws ConnectionsException {


        return getAllConnections(SymUserConnection.Status.REJECTED, null);

    }


    @Override
    public List<SymUserConnection> getAcceptedRequests() throws ConnectionsException {


        return getAllConnections(SymUserConnection.Status.ACCEPTED, null);

    }

    @Override
    public List<SymUserConnection> getAllConnections() throws ConnectionsException {


        return getAllConnections(SymUserConnection.Status.ALL, null);

    }


    /**
     * Remove an existing user connection
     *
     * @param symUser Symphony user to remove connection with
     * @throws ConnectionsException thrown by underlying API or not providing a valid SymUser
     */
    @Override
    public void removeConnectionRequest(SymUser symUser) throws ConnectionsException {
        ConnectionApi connectionApi = new ConnectionApi(apiClient);

        if (symUser == null)
            throw new NullPointerException("Symphony user was not provided.");


        try {
            connectionApi.v1ConnectionUserUidRemovePost(symAuth.getSessionToken().getToken(), symUser.getId());
        } catch (ApiException e) {
            throw new ConnectionsException("Error removing connected user: " + symUser.getId(), e);
        }

    }

    @Override
    public SymUserConnection sendConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException {
        ConnectionApi connectionApi = new ConnectionApi(apiClient);

        if (symUserConnectionRequest == null)
            throw new NullPointerException("Connection request was not provided.");


        try {
            return SymUserConnection.toSymUserConnection(connectionApi.v1ConnectionCreatePost(symAuth.getSessionToken().getToken(), symUserConnectionRequest));
        } catch (ApiException e) {
            throw new ConnectionsException("Error sending connection request to ID: " + symUserConnectionRequest.getUserId(), e);
        }


    }

    @Override
    public SymUserConnection acceptConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException {
        ConnectionApi connectionApi = new ConnectionApi(apiClient);
        if (symUserConnectionRequest == null)
            throw new NullPointerException("Connection request was not provided.");

        try {
            return SymUserConnection.toSymUserConnection(connectionApi.v1ConnectionAcceptPost(symAuth.getSessionToken().getToken(), symUserConnectionRequest));
        } catch (ApiException e) {
            throw new ConnectionsException("Failed to accept connection request from ID: " + symUserConnectionRequest.getUserId(), e);
        }


    }


    @Override
    public SymUserConnection acceptConnectionRequest(SymUserConnection symUserConnection) throws ConnectionsException {
        if (symUserConnection == null)
            throw new NullPointerException("SymUserConnection was not provided.. ");

        ConnectionApi connectionApi = new ConnectionApi(apiClient);

        try {
            return SymUserConnection.toSymUserConnection(connectionApi.v1ConnectionAcceptPost(symAuth.getSessionToken().getToken(), new SymUserConnectionRequest(symUserConnection)));
        } catch (ApiException e) {
            throw new ConnectionsException("Failed to accept connection request from ID: " + symUserConnection.getUserId(), e);
        }


    }


    @Override
    public SymUserConnection rejectConnectionRequest(SymUserConnectionRequest symUserConnectionRequest) throws ConnectionsException {
        ConnectionApi connectionApi = new ConnectionApi(apiClient);

        if (symUserConnectionRequest == null)
            throw new NullPointerException("Connection request was not provided.");

        try {
            return SymUserConnection.toSymUserConnection(connectionApi.v1ConnectionRejectPost(symAuth.getSessionToken().getToken(), symUserConnectionRequest));
        } catch (ApiException e) {
            throw new ConnectionsException("Failed to reject connection request from ID: " + symUserConnectionRequest.getUserId(), e);
        }


    }


    /**
     * @deprecated please use {@link #getUserConnection(SymUser)}
     *
     * @param userId Symphony user ID
     * @return Symphony User connection
     * @throws ConnectionsException from underlying API
     */
    @Override
    @Deprecated
    public SymUserConnection getUserConnection(String userId) throws ConnectionsException {

        if (userId == null) {
            throw new NullPointerException("UserID was not provided..");
        }
        SymUser symUser = new SymUser();
        symUser.setId(Long.valueOf(userId));


        return getUserConnection(symUser);
    }


    @Override
    public SymUserConnection getUserConnection(SymUser symUser) throws ConnectionsException {

        ConnectionApi connectionApi = new ConnectionApi(apiClient);

        if (symUser == null) {
            throw new NullPointerException("SymUser was not provided..");
        }

        try {
            return SymUserConnection.toSymUserConnection(connectionApi.v1ConnectionUserUserIdInfoGet(symAuth.getSessionToken().getToken(), symUser.getId().toString()));
        } catch (ApiException e) {
            throw new ConnectionsException("Unable to retrieve connection information for ID: " + symUser.getId(), e);
        }


    }

    @SuppressWarnings("SameParameterValue")
    private List<SymUserConnection> getAllConnections(SymUserConnection.Status status, String userIds) throws ConnectionsException {

        ConnectionApi connectionApi = new ConnectionApi(apiClient);

        SymUserConnection.Status theStatus = status;

        if (theStatus == null)
            theStatus = SymUserConnection.Status.ALL;

        UserConnectionList userConnectionList;
        try {
            userConnectionList = connectionApi.v1ConnectionListGet(symAuth.getSessionToken().getToken(), theStatus.toString(), userIds);
        } catch (ApiException e) {
            throw new ConnectionsException("Unable to retrieve all known connections..", e);
        }

        return userConnectionList.stream().map(SymUserConnection::toSymUserConnection).collect(Collectors.toList());


    }


}
