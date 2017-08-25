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

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.RestException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.agent.api.MessagesApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.ApiVersion;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;
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
    private ApiVersion apiVersion = ApiVersion.V2;
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(MessagesClientImpl.class);

    public MessagesClientImpl(SymAuth symAuth, String agentUrl) {

        this(symAuth, agentUrl, null, null);

    }

    /**
     * Constructor supports custom HTTP clients
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param agentUrl   Agent URL
     * @param httpClient Custom HTTP Client
     */
    public MessagesClientImpl(SymAuth symAuth, String agentUrl, Client httpClient) {

        this(symAuth, agentUrl, httpClient, null);

    }


    /**
     * @param symAuth    Authorization model containing session and key tokens
     * @param agentUrl   Agent URL
     * @param apiVersion Version of API to use
     */
    public MessagesClientImpl(SymAuth symAuth, String agentUrl, ApiVersion apiVersion) {

        this(symAuth, agentUrl, null, apiVersion);
    }


    /**
     * Constructor supports custom HTTP clients
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param agentUrl   Agent URL
     * @param httpClient Custom HTTP Client
     * @param apiVersion Version of API to use
     */
    public MessagesClientImpl(SymAuth symAuth, String agentUrl, Client httpClient, ApiVersion apiVersion) {
        this.symAuth = symAuth;

        if (apiVersion != null)
            this.apiVersion = apiVersion;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();

        if (httpClient != null)
            apiClient.setHttpClient(httpClient);

        apiClient.getHttpClient().register(MultiPartFeature.class);

        apiClient.setBasePath(agentUrl);

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
            throw new MessagesException("Failed to send message to stream: " + stream,
                    new RestException(messagesApi.getApiClient().getBasePath(), e.getCode(), e));
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
    @Deprecated
    public SymMessage sendMessage(Stream stream, SymMessage message) throws MessagesException {


        return sendMessage(SymStream.toSymStream(stream), message);


    }

    /**
     * Send message to SymStream
     *
     * @param stream  Stream to send message to
     * @param message Message to send
     * @return Message sent
     * @throws MessagesException Exception caused by Symphony API calls
     */
    @Override
    public SymMessage sendMessage(SymStream stream, SymMessage message) throws MessagesException {


        return apiVersion.equals(ApiVersion.V4) ? sendMessageV4(stream, message) : sendMessageV2(SymStream.toSymStream(stream), message);


    }


    /**
     * Retrieve historical messages from a given SymStream.  This is NOT a blocking call.
     *
     * @param symStream   Stream to retrieve messages from
     * @param since       Date (long) from point in time
     * @param offset      Offset
     * @param maxMessages Maximum number of messages to retrieve from the specified time (since)
     * @return List of messages
     * @throws MessagesException Exception caused by Symphony API calls
     */
    @Override
    public List<SymMessage> getMessagesFromStream(SymStream symStream, Long since, Integer offset, Integer maxMessages) throws MessagesException {

        return apiVersion.equals(ApiVersion.V4) ?
                getMessagesFromStreamV4(symStream, since, offset, maxMessages) :
                getMessagesFromStreamV2(SymStream.toSymStream(symStream), since, offset, maxMessages);
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

        return getMessagesFromStream(SymStream.toSymStream(stream), since, offset, maxMessages);

    }


    /**
     * Retrieve historical messages from a given SymStream.  This is NOT a blocking call.
     *
     * @param stream      SymStream to retrieve messages from
     * @param since       Date (long) from point in time
     * @param offset      Offset
     * @param maxMessages Maximum number of messages to retrieve from the specified time (since)
     * @return List of messages
     * @throws MessagesException Exception caused by Symphony API calls
     */
    private List<SymMessage> getMessagesFromStreamV4(SymStream stream, Long since, Integer offset, Integer maxMessages) throws MessagesException {

        if (stream == null) {
            throw new NullPointerException("Stream submission was not provided..");
        }


        MessagesApi messagesApi = new MessagesApi(apiClient);

        V4MessageList v4MessageList;
        try {
            v4MessageList = messagesApi.v4StreamSidMessageGet(stream.getStreamId(), since, symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), offset, maxMessages);
        } catch (ApiException e) {
            throw new MessagesException("Failed to retrieve messages from SymStream: " + stream,
                    new RestException(messagesApi.getApiClient().getBasePath(), e.getCode(), e));
        }

        List<SymMessage> symMessageList = new ArrayList<>();

        if (v4MessageList != null) {
            symMessageList.addAll(v4MessageList.stream().map(SymMessage::toSymMessage).collect(Collectors.toList()));
        }


        return symMessageList;
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
    private List<SymMessage> getMessagesFromStreamV2(Stream stream, Long since, Integer offset, Integer maxMessages) throws MessagesException {

        if (stream == null) {
            throw new NullPointerException("Stream submission was not provided..");
        }


        MessagesApi messagesApi = new MessagesApi(apiClient);

        V2MessageList v2MessageList;
        try {
            v2MessageList = messagesApi.v2StreamSidMessageGet(stream.getId(), since, symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), offset, maxMessages);
        } catch (ApiException e) {
            throw new MessagesException("Failed to retrieve messages from stream: " + stream,
                    new RestException(messagesApi.getApiClient().getBasePath(), e.getCode(), e));
        }

        List<SymMessage> symMessageList = new ArrayList<>();

        if (v2MessageList != null) {
            symMessageList.addAll(v2MessageList.stream().filter(v2BaseMessage -> v2BaseMessage instanceof V2Message).map(SymMessage::toSymMessage).collect(Collectors.toList()));
        }


        return symMessageList;
    }


    /**
     * Send new v4message to stream
     *
     * @param stream  Stream to send message to
     * @param message Message to send
     * @return Message sent
     * @throws MessagesException Exception caused by Symphony API calls
     */
    private SymMessage sendMessageV4(SymStream stream, SymMessage message) throws MessagesException {

        if (stream == null || message == null) {
            throw new NullPointerException("Stream or message submission was not provided..");
        }

        MessagesApi messagesApi = new MessagesApi(apiClient);
        V4Message v4Message;
        try {

            return SymMessage.toSymMessage(messagesApi.v4StreamSidMessageCreatePost(
                    stream.getStreamId(),
                    symAuth.getSessionToken().getToken(),
                    symAuth.getKeyToken().getToken(),
                    message.getMessage(),
                    message.getEntityData(),
                    ApiVersion.V4.toString(),
                    message.getAttachment(),
                    message.getAttachementThumbnail()
                    )

            );

        } catch (ApiException e) {
            throw new MessagesException("Failed to send message to stream: " + stream.getStreamId(),
                    new RestException(messagesApi.getApiClient().getBasePath(), e.getCode(), e));
        }

    }


    /**
     * Send  v2message to stream
     *
     * @param stream  Stream to send message to
     * @param message Message to send
     * @return Message sent
     * @throws MessagesException Exception caused by Symphony API calls
     */
    @Deprecated
    private SymMessage sendMessageV2(Stream stream, SymMessage message) throws MessagesException {

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


        try {
            return SymMessage.toSymMessage(messagesApi.v2StreamSidMessageCreatePost(stream.getId(), symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), messageSubmission));
        } catch (ApiException e) {
            throw new MessagesException("Failed to send message to stream: " + stream.getId(),
                    new RestException(messagesApi.getApiClient().getBasePath(), e.getCode(), e));
        }

    }




}
