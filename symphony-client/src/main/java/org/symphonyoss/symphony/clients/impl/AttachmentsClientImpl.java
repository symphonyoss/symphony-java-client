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
import org.symphonyoss.symphony.agent.api.AttachmentsApi;
import org.symphonyoss.symphony.agent.api.DatafeedApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.model.AttachmentInfo;
import org.symphonyoss.symphony.clients.AttachmentsClient;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;


/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class AttachmentsClientImpl implements AttachmentsClient {

    private final ApiClient apiClient;
    private final SymAuth symAuth;
    private Logger logger = LoggerFactory.getLogger(AttachmentsClientImpl.class);

    public AttachmentsClientImpl(SymAuth symAuth, String agentUrl) {

        this.symAuth = symAuth;
        String agentUrl1 = agentUrl;


        //Get Service client to query for userID.
        apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();
        apiClient.setBasePath(agentUrl);

    }


    public byte[] getAttachmentData(SymAttachmentInfo symAttachmentInfo, SymMessage symMessage) throws Exception {

        AttachmentsApi attachmentsApi = new AttachmentsApi(apiClient);


        if (symAttachmentInfo.getId() == null || symMessage.getId() == null || symMessage.getStreamId() == null)
            return null;


        return  Base64.getDecoder().decode(attachmentsApi.v1StreamSidAttachmentGet(symMessage.getStreamId(),
                symAttachmentInfo.getId(),
                symMessage.getId(),
                symAuth.getSessionToken().getToken(),
                symAuth.getKeyToken().getToken()));


    }


    public SymAttachmentInfo postAttachment(String sid, File attachment)throws Exception {
        AttachmentsApi attachmentsApi = new AttachmentsApi(apiClient);

        AttachmentInfo attachmentInfo = attachmentsApi.v1StreamSidAttachmentCreatePost(sid,
                symAuth.getSessionToken().getToken(),
                symAuth.getKeyToken().getToken(),
                attachment);

        return SymAttachmentInfo.toAttachmentInfo(attachmentInfo);
    }


}
