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
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.agent.api.AttachmentsApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.AttachmentInfo;
import org.symphonyoss.symphony.clients.AttachmentsClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import javax.ws.rs.client.Client;
import java.io.File;
import java.util.Base64;


/**
 * Support for message attachments
 *
 * @author Frank Tarsillo
 */
public class AttachmentsClientImpl implements AttachmentsClient {

    private final ApiClient apiClient;
    private final SymAuth symAuth;
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(AttachmentsClientImpl.class);


    /**
     * Init
     *
     * @param symAuth Authorization model containing session and key tokens
     * @param config  Symphony Client Config
     */
    public AttachmentsClientImpl(SymAuth symAuth, SymphonyClientConfig config) {

        this(symAuth, config, null);

    }

    /**
     * If you need to override HttpClient.  Important for handling individual client certs.
     *
     * @param symAuth    Authorization model containing session and key tokens
     * @param config     Symphony Client Config
     * @param httpClient Custom client utilized to access Symphony APIs
     */
    public AttachmentsClientImpl(SymAuth symAuth, SymphonyClientConfig config, Client httpClient) {
        this.symAuth = symAuth;

        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        if (httpClient != null)
            apiClient.setHttpClient(httpClient);

        apiClient.setBasePath(config.get(SymphonyClientConfigID.AGENT_URL));


    }


    @Override
    public byte[] getAttachmentData(SymAttachmentInfo symAttachmentInfo, SymMessage symMessage) throws AttachmentsException {

        AttachmentsApi attachmentsApi = new AttachmentsApi(apiClient);


        if (symAttachmentInfo.getId() == null || symMessage.getId() == null || symMessage.getStreamId() == null)
            throw new NullPointerException("Null values detected for attachments information or streamId");


        try {

            return Base64.getDecoder().decode(attachmentsApi.v1StreamSidAttachmentGet(symMessage.getStreamId(),
                    symAttachmentInfo.getId(),
                    symMessage.getId(),
                    symAuth.getSessionToken().getToken(),
                    symAuth.getKeyToken().getToken()));
        } catch (ApiException e) {
            throw new AttachmentsException("Could not retrieve or decode attachment from POD..", e);
        }


    }


    @Override
    public SymAttachmentInfo postAttachment(String sid, File attachment) throws AttachmentsException {
        AttachmentsApi attachmentsApi = new AttachmentsApi(apiClient);

        if (sid == null || attachment == null)
            throw new NullPointerException("Either stream ID or file is null..");


        AttachmentInfo attachmentInfo;
        try {
            attachmentInfo = attachmentsApi.v3StreamSidAttachmentCreatePost(sid,
                    symAuth.getSessionToken().getToken(),
                    attachment,
                    symAuth.getKeyToken().getToken()
            );
        } catch (ApiException e) {
            throw new AttachmentsException("Failed to post attachment for file " + attachment.getName(), e);
        }

        if (attachmentInfo == null)
            throw new AttachmentsException("Failed to post attachment.  Posting results returned no information.");


        return SymAttachmentInfo.toAttachmentInfo(attachmentInfo);
    }


}
