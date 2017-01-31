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
import org.symphonyoss.exceptions.MessagesException;
import org.symphonyoss.symphony.agent.api.MessagesApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;

import javax.ws.rs.client.Client;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Frank Tarsillo
 */
public class MessagesClientImpl implements org.symphonyoss.symphony.clients.MessagesClient {

    private final ApiClient apiClient;
    private final SymAuth symAuth;
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(MessagesClientImpl.class);

    public MessagesClientImpl(SymAuth symAuth, String agentUrl) {

        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(agentUrl);

    }

    /**
     * Constructor supports custom HTTP clients
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param serviceUrl Service URL
     * @param httpClient Custom HTTP Client
     */
    public MessagesClientImpl(SymAuth symAuth, String serviceUrl, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setHttpClient(httpClient);
        apiClient.setBasePath(serviceUrl);

    }

    /**
     * Send a message using Symphony defined model. Please use {@link #sendMessage(Stream, SymMessage)} which supports
     * {@link SymMessage}.
     *
     * @param stream  Stream object identifying destination endpoint
     * @param message Message to send leveraging the MessageSubmission message
     * @return Message that was submitted
     * @throws MessagesException Caused by Symphony API problems
     */
    @Deprecated
    public Message sendMessage(Stream stream, MessageSubmission message) throws MessagesException {
        if (stream == null || message == null) {
            throw new NullPointerException("Stream or message submission was not provided..");
        }


        MessagesApi messagesApi = new MessagesApi(apiClient);


        try {
            return messagesApi.v1StreamSidMessageCreatePost(stream.getId(), symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), message);
        } catch (ApiException e) {
            throw new MessagesException("Failed to send message to stream: " + stream, e);
        }

    }


    /**
     * Send message to stream
     *
     * @param stream  Stream to send message to
     * @param message Message to send
     * @return Message sent
     * @throws MessagesException Exception caused by Symphony API calls
     */
    @Override
    public SymMessage sendMessage(Stream stream, SymMessage message) throws MessagesException {

        if (stream == null || message == null) {
            throw new NullPointerException("Stream or message submission was not provided..");
        }

        MessagesApi messagesApi = new MessagesApi(apiClient);


        V2MessageSubmission messageSubmission = new V2MessageSubmission();

        messageSubmission.setMessage(message.getMessage());
        messageSubmission.setFormat(
                message.getFormat().toString().equals(V2MessageSubmission.FormatEnum.TEXT.toString()) ?
                        V2MessageSubmission.FormatEnum.TEXT :
                        V2MessageSubmission.FormatEnum.MESSAGEML
        );
        messageSubmission.setAttachments(SymAttachmentInfo.toV2AttachmentsInfo(message.getAttachments()));

        V2Message v2Message;
        try {
            v2Message = messagesApi.v2StreamSidMessageCreatePost(stream.getId(), symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), messageSubmission);
        } catch (ApiException e) {
            throw new MessagesException("Failed to send message to stream: " + stream.getId(), e);
        }

        return SymMessage.toSymMessage(v2Message);
    }


    /**
     * Retrieve historical messages from a given stream.  This is NOT a blocking call.
     *
     * @param stream      Stream to retrieve messages from
     * @param since       Date (long) from point in time
     * @param offset      Offset
     * @param maxMessages Maximum number of messages to retrieve from the specified time (since)
     * @return List of messages
     * @throws MessagesException Exception caused by Symphony API calls
     */
    @Override
    public List<SymMessage> getMessagesFromStream(Stream stream, Long since, Integer offset, Integer maxMessages) throws MessagesException {

        if (stream == null) {
            throw new NullPointerException("Stream submission was not provided..");
        }


        MessagesApi messagesApi = new MessagesApi(apiClient);

        V2MessageList v2MessageList;
        try {
            v2MessageList = messagesApi.v2StreamSidMessageGet(stream.getId(), since, symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), offset, maxMessages);
        } catch (ApiException e) {
            throw new MessagesException("Failed to retrieve messages from stream: " + stream, e);
        }

        List<SymMessage> symMessageList = new ArrayList<>();

        if (v2MessageList != null) {
            symMessageList.addAll(v2MessageList.stream().filter(v2BaseMessage -> v2BaseMessage instanceof V2Message).map(SymMessage::toSymMessage).collect(Collectors.toList()));
        }

        return symMessageList;
    }


}
