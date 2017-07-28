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

import org.symphonyoss.client.common.MLTypes;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.agent.model.V2BaseMessage;
import org.symphonyoss.symphony.agent.model.V2Message;
import org.symphonyoss.symphony.agent.model.V4Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Frank Tarsillo
 */
@SuppressWarnings("WeakerAccess")
public class SymMessage {


    public enum Format {
        TEXT("TEXT"),

        MESSAGEML("MESSAGEML");

        private String value;

        Format(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    private Format format = Format.TEXT;

    private String id = null;

    private String timestamp = null;

    @Deprecated  //Under SymStream now
    private String messageType = null;

    private String streamId = null;

    private String message = null;

    private Long fromUserId = null;

    private SymUser symUser = null;

    private SymStream stream = null;

    private List<SymAttachmentInfo> attachments = new ArrayList<>();

    private String entityData = null;

    private File attachment = null;

    public SymUser getSymUser() {
        return symUser;
    }



    @SuppressWarnings("unused")
    public void setSymUser(SymUser symUser) {
        this.symUser = symUser;
        fromUserId = symUser.getId();
    }

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

        if (symUser == null)
            symUser = new SymUser();


        symUser.setId(fromUserId);

    }


    public List<SymAttachmentInfo> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SymAttachmentInfo> attachments) {
        this.attachments = attachments;
    }

    public String getEntityData() {
        return entityData;
    }

    public void setEntityData(String entityData) {
        this.entityData = entityData;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }


    public SymStream getStream() {
        return stream;
    }

    public void setStream(SymStream stream) {
        this.stream = stream;
    }


    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    @Deprecated
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

    public static SymMessage toSymMessage(V2BaseMessage v2BaseMessage) {


        SymMessage symMessage = new SymMessage();
        symMessage.setTimestamp(v2BaseMessage.getTimestamp());
        symMessage.setId(v2BaseMessage.getId());
        symMessage.setStreamId(v2BaseMessage.getStreamId());
        symMessage.setMessageType(v2BaseMessage.getV2messageType());

        if (v2BaseMessage instanceof V2Message) {
            symMessage.setMessage(((V2Message) v2BaseMessage).getMessage());
            symMessage.setFromUserId(((V2Message) v2BaseMessage).getFromUserId());
            symMessage.setAttachments(SymAttachmentInfo.toAttachmentsInfo(((V2Message) v2BaseMessage).getAttachments()));
        }


        return symMessage;
    }


    public static SymMessage toSymMessage(V4Message v4Message) {


        SymMessage symMessage = new SymMessage();
        symMessage.setTimestamp(Long.toString(v4Message.getTimestamp()));
        symMessage.setId(v4Message.getMessageId());
        symMessage.setStreamId(v4Message.getStream().getStreamId());
        symMessage.setMessageType(Format.MESSAGEML.toString());
        symMessage.setFromUserId(v4Message.getUser().getUserId());
        symMessage.setSymUser(SymUser.toSymUser(v4Message.getUser()));
        symMessage.setMessage(v4Message.getMessage());
        symMessage.setStream(SymStream.toSymStream(v4Message.getStream()));
        symMessage.setAttachments(SymAttachmentInfo.toAttachmentsInfos(v4Message.getAttachments()));


        return symMessage;
    }

    @Deprecated
    public static Message toV1Message(V2BaseMessage v2BaseMessage) {

        return toV2Message(v2BaseMessage);
    }

    @Deprecated
    public static Message toV1Message(SymMessage symMessage) {

        Message v1Message = new Message();
        v1Message.setId(symMessage.getId());
        v1Message.setStreamId(symMessage.getStreamId());
        v1Message.setMessage(symMessage.getMessage());
        v1Message.setMessageType(symMessage.getMessageType());
        v1Message.setFromUserId(symMessage.getFromUserId());
        v1Message.setTimestamp(symMessage.getTimestamp());
        return v1Message;
    }


    @Deprecated
    public static Message toV2Message(V2BaseMessage v2BaseMessage) {

        Message v1Message = new Message();
        v1Message.setId(v2BaseMessage.getId());
        v1Message.setStreamId(v2BaseMessage.getStreamId());
        v1Message.setMessageType(v2BaseMessage.getV2messageType());
        v1Message.setTimestamp(v2BaseMessage.getTimestamp());
        if (v2BaseMessage instanceof V2Message) {
            v1Message.setMessage(((V2Message) v2BaseMessage).getMessage());
            v1Message.setFromUserId(((V2Message) v2BaseMessage).getFromUserId());
        }

        return v1Message;
    }


    public String getMessageText() {
        MlMessageParser mlMessageParser = new MlMessageParser();

        if (message != null) {
            try {
                mlMessageParser.parseMessage(message);
                return mlMessageParser.getText();
            } catch (SymException e) {
                System.out.println("Could not parse message...");
            }

        }
        return null;
    }

    public void setMessageText(ApiVersion apiVersion, String text) {

        if (apiVersion != null && !apiVersion.equals(ApiVersion.V2)) {
            setMessage(MLTypes.START_PML + text + MLTypes.END_PML);
        } else {
            setMessage(text);
        }

    }

    public void setMessageText(String text){
        setMessageText(null,text);

    }

}
