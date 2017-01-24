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
import org.symphonyoss.exceptions.StreamsException;
import org.symphonyoss.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.UsersFactory;
import org.symphonyoss.symphony.clients.model.*;
import org.symphonyoss.symphony.pod.api.StreamsApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.UserIdList;

import javax.ws.rs.client.Client;
import java.util.Set;

import  org.symphonyoss.symphony.clients.model.SymRoomSearchResults;


/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class StreamsClientImpl implements org.symphonyoss.symphony.clients.StreamsClient {
    private final SymAuth symAuth;
    private final String serviceUrl;
    private final ApiClient apiClient;
    private Client httpClient = null;

    private final Logger logger = LoggerFactory.getLogger(StreamsClientImpl.class);


    public StreamsClientImpl(SymAuth symAuth, String serviceUrl) {

        this.symAuth = symAuth;
        this.serviceUrl = serviceUrl;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     *
     * @param symAuth
     * @param serviceUrl
     * @param httpClient
     */
    public StreamsClientImpl(SymAuth symAuth, String serviceUrl, Client httpClient) {
        this.symAuth = symAuth;
        this.serviceUrl = serviceUrl;
        this.httpClient = httpClient;
        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.pod.invoker.Configuration.getDefaultApiClient();
        apiClient.setHttpClient(httpClient);
        apiClient.setBasePath(serviceUrl);

        apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
        apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());


    }


    public Stream getStream(SymUser user) throws StreamsException {

        if (user == null) {
            throw new NullPointerException("User was not provided...");
        }

        UserIdList userIdList = new UserIdList();
        userIdList.add(user.getId());

        Stream stream = getStream(userIdList);
        logger.debug("Stream ID for one to one chat: {}:{} ", user.getEmailAddress(), stream.getId());

        return stream;


    }

    public Stream getStream(Set<SymUser> users) throws StreamsException {
        if (users == null) {
            throw new NullPointerException("Users were not provided...");
        }

        UserIdList userIdList = new UserIdList();
        String usersPrint = "";

        for (SymUser user : users) {
            userIdList.add(user.getId());
            usersPrint += " [" + user.getEmailAddress() + "] ";
        }


        Stream stream = getStream(userIdList);

        logger.debug("Stream ID for chat: {}:{} ", usersPrint, stream.getId());

        return stream;


    }


    public Stream getStream(UserIdList userIdList) throws StreamsException {
        if (userIdList == null) {
            throw new NullPointerException("UsersIds were not provided...");
        }

        StreamsApi streamsApi = new StreamsApi(apiClient);
        try {
            return streamsApi.v1ImCreatePost(userIdList, symAuth.getSessionToken().getToken());
        } catch (ApiException e) {
            throw new StreamsException("Failed to retrieve stream for given user ids...", e);
        }

    }

    public Stream getStreamFromEmail(String email) throws StreamsException {

        if (email == null) {
            throw new NullPointerException("Email was not provided...");
        }

        UsersClient usersClient;
        if (httpClient == null) {
            usersClient = UsersFactory.getClient(symAuth, serviceUrl, UsersFactory.TYPE.DEFAULT);
        } else {
            //not pretty..
            usersClient = UsersFactory.getClient(symAuth, serviceUrl, httpClient);
        }


        try {
            return getStream(usersClient.getUserFromEmail(email));
        } catch (UsersClientException e) {
            throw new StreamsException("Failed to find user from email : " + email, e);
        }
    }


    public SymRoomDetail getRoomDetail(String roomId) throws StreamsException {

        if (roomId == null) {
            throw new NullPointerException("Room ID was not provided..");
        }
        StreamsApi streamsApi = new StreamsApi(apiClient);

        try {
            return SymRoomDetail.toSymRoomDetail(streamsApi.v2RoomIdInfoGet(roomId, symAuth.getSessionToken().getToken()));
        } catch (ApiException e) {
            throw new StreamsException("Failed to obtain room information from ID: " + roomId, e);
        }
    }

    @Override
    public SymRoomDetail createChatRoom(SymRoomAttributes roomAttributes) throws StreamsException {

        if (roomAttributes == null) {
            throw new NullPointerException("Room Attributes were not provided..");
        }
        StreamsApi streamsApi = new StreamsApi(apiClient);

        try {
            return SymRoomDetail.toSymRoomDetail(streamsApi.v2RoomCreatePost(
                    SymRoomAttributes.toV2RoomAttributes(roomAttributes), symAuth.getSessionToken().getToken())
            );
        } catch (ApiException e) {
            throw new StreamsException("Failed to obtain room information while creating room: " + roomAttributes.getName(), e);
        }
    }

    @Override
    public SymRoomDetail updateChatRoom(String streamId, SymRoomAttributes roomAttributes) throws StreamsException {

        if (roomAttributes == null) {
            throw new NullPointerException("Room Attributes were not provided..");
        }
        StreamsApi streamsApi = new StreamsApi(apiClient);

        try {
            return SymRoomDetail.toSymRoomDetail(streamsApi.v2RoomIdUpdatePost(streamId,
                    SymRoomAttributes.toV2RoomAttributes(roomAttributes), symAuth.getSessionToken().getToken())
            );
        } catch (ApiException e) {
            throw new StreamsException("Failed to obtain room information while updating attributes on room: " + roomAttributes.getName(), e);
        }
    }


    @Override
    public SymRoomSearchResults roomSearch(SymRoomSearchCriteria searchCriteria, Integer skip, Integer limit) throws StreamsException{

        if (searchCriteria == null) {
            throw new NullPointerException("Room search criteria was not provided..");
        }
        StreamsApi streamsApi = new StreamsApi(apiClient);

        try {

            return SymRoomSearchResults.toSymRoomSearchResults(streamsApi.v2RoomSearchPost(symAuth.getSessionToken().getToken(), SymRoomSearchCriteria.toRoomSearchCriteria(searchCriteria), skip, limit));


        } catch (ApiException e) {
            throw new StreamsException("Failed room search...", e);
        }
    }
    
	@Override
	public void deactivateRoom(String roomId) throws StreamsException {
       if (roomId == null) {
            throw new IllegalArgumentException("Argument roomId must not be null");
        }
	       
       StreamsApi streamsApi = new StreamsApi(apiClient);
	    
       try {
            String sessionToken = symAuth.getSessionToken().getToken();
            
            streamsApi.v1RoomIdSetActivePost(roomId, false, sessionToken);
       } catch (Exception e) {
            String message = "Failed to deactivate room for roomId: " + roomId;
            logger.error(message, e);
            throw new StreamsException(message, e);
        }  
	}
}
