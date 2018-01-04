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
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
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

    private Logger logger = LoggerFactory.getLogger(MessagesClientImpl.class);

    /**
     * Constructor supports custom HTTP clients
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param config   Symphony Client config
     *
     */
    public MessagesClientImpl(SymAuth symAuth, SymphonyClientConfig config) {

        this(symAuth, config, null);

    }

    /**
     * Constructor supports custom HTTP clients
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param config   Symphony Client Config
     * @param httpClient Custom HTTP Client
     */
    public MessagesClientImpl(SymAuth symAuth, SymphonyClientConfig config, Client httpClient) {

        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();

        if (httpClient != null)
            apiClient.setHttpClient(httpClient);

        apiClient.getHttpClient().register(MultiPartFeature.class);

        apiClient.setBasePath(config.get(SymphonyClientConfigID.AGENT_URL));


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
     * Send message to SymStream with alternate session token (OBO)
     *
     *
     * @param stream  Stream to send message to
     * @param message Message to send
     * @param symAuth Alternate authorization containing session token to use.
     * @return Message sent
     * @throws MessagesException Exception caused by Symphony API calls
     */
    @Override
    public SymMessage sendMessage(SymStream stream, SymMessage message, SymAuth symAuth) throws MessagesException {

        return  sendMessageV4( stream, message,symAuth);

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

        return (ApiVersion.V4 == message.getApiVersion()) ? sendMessageV4(stream, message) : sendMessageV2(stream, message);

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
    public List<SymMessage> getMessagesFromStream(SymStream symStream, Long since, Integer offset, Integer maxMessages, ApiVersion apiVersion1) throws MessagesException {

        return (ApiVersion.V4 == apiVersion1) ? getMessagesFromStreamV4(symStream, since, offset, maxMessages) : getMessagesFromStreamV2(symStream, since, offset, maxMessages);

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

        return getMessagesFromStreamV4(symStream, since, offset, maxMessages);

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
    private List<SymMessage> getMessagesFromStreamV2(SymStream stream, Long since, Integer offset, Integer maxMessages) throws MessagesException {

        if (stream == null) {
            throw new NullPointerException("Stream submission was not provided..");
        }


        MessagesApi messagesApi = new MessagesApi(apiClient);

        V2MessageList v2MessageList;
        try {
            v2MessageList = messagesApi.v2StreamSidMessageGet(stream.getStreamId(), since, symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), offset, maxMessages);
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

        return sendMessageV4(stream, message, null);
    }


    /**
     * Send new v4message to stream on an alternate session ID
     *
     * @param altSymAuth Alternate SymAuth to use for things like OBO requests
     * @param stream  Stream to send message to
     * @param message Message to send
     * @return Message sent
     * @throws MessagesException Exception caused by Symphony API calls
     */
    private SymMessage sendMessageV4( SymStream stream, SymMessage message,SymAuth altSymAuth) throws MessagesException {

        if (stream == null || message == null) {
            throw new NullPointerException("Stream or message submission was not provided..");
        }

        String sessionToken = symAuth.getSessionToken().getToken();

        if(altSymAuth !=null && altSymAuth.getSessionToken()!=null)
            sessionToken = altSymAuth.getSessionToken().getToken();

        MessagesApi messagesApi = new MessagesApi(apiClient);
        V4Message v4Message;
        try {


            return SymMessage.toSymMessage(messagesApi.v4StreamSidMessageCreatePost(
                    stream.getStreamId(),
                    sessionToken,
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
    private SymMessage sendMessageV2(SymStream stream, SymMessage message) throws MessagesException {

        if (stream == null || message == null) {
            throw new NullPointerException("Stream or message submission was not provided..");
        }

        MessagesApi messagesApi = new MessagesApi(apiClient);

        V2MessageSubmission messageSubmission = new V2MessageSubmission();

        messageSubmission.setMessage(message.getMessage());
        messageSubmission.setFormat(

                V2MessageSubmission.FormatEnum.MESSAGEML
        );
        messageSubmission.setAttachments(SymAttachmentInfo.toV2AttachmentsInfo(message.getAttachments()));


        try {
            return SymMessage.toSymMessage(messagesApi.v2StreamSidMessageCreatePost(stream.getStreamId(), symAuth.getSessionToken().getToken(), symAuth.getKeyToken().getToken(), messageSubmission));
        } catch (ApiException e) {
            throw new MessagesException("Failed to send message to stream: " + stream.getStreamId() + ": " + message.getMessage(),
                    new RestException(messagesApi.getApiClient().getBasePath(), e.getCode(), e));
        }

    }


}





