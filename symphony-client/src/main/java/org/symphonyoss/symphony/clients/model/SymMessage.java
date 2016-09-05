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

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.client.model.SymAttachmentInfo;
import org.symphonyoss.symphony.agent.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank.tarsillo on 8/12/2016.
 */
public class SymMessage {


    private String id = null;

    private String timestamp = null;

    private String messageType = null;

    private String streamId = null;

    private String message = null;

    private Long fromUserId = null;

    private List<SymAttachmentInfo> attachments = new ArrayList<SymAttachmentInfo>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public List<SymAttachmentInfo> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SymAttachmentInfo> attachments) {
        this.attachments = attachments;
    }

    public static SymMessage toSymMessage(Message message) {
        SymMessage symMessage = new SymMessage();
        symMessage.setId(message.getId());
        symMessage.setStreamId(message.getStreamId());
        symMessage.setMessage(message.getMessage());
        symMessage.setMessageType(message.getMessageType());
        symMessage.setFromUserId(message.getFromUserId());
        symMessage.setTimestamp(message.getTimestamp());
        return symMessage;
    }
}
