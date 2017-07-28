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

package org.symphonyoss.symphony.clients;

import org.symphonyoss.client.exceptions.AttachmentsException;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.File;

/**
 * Support for message attachments
 *
 * Created by Frank Tarsillo on 5/15/2016.
 */
public interface AttachmentsClient {

    /**
     * Retrieve attachment data from message
     *
     * @param symAttachmentInfo Attachment details
     * @param symMessage Message containing the attachment
     * @return byte stream
     * @throws AttachmentsException Exceptions generated from underlying Symphony API calls
     */
    byte[] getAttachmentData(SymAttachmentInfo symAttachmentInfo, SymMessage symMessage) throws AttachmentsException;

    /**
     * Send  attachment to a given stream
     *
     * @param streamId StreamID to send the attachment
     * @param attachment File to send
     * @return Attachment details associated the file transmission
     * @throws AttachmentsException Exceptions generated from underlying Symphony API calls
     */
    SymAttachmentInfo postAttachment(String streamId, File attachment) throws AttachmentsException;
}
