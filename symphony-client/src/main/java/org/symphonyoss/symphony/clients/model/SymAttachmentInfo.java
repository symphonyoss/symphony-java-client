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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.symphonyoss.symphony.agent.model.AttachmentInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by frank.tarsillo on 8/12/2016.
 */
public class SymAttachmentInfo {
    private String id = null;
    private String name = null;
    private Long size = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }


public static List<AttachmentInfo> toV2AttachmentsInfo(List<SymAttachmentInfo> symAttachmentsInfo){

    List<AttachmentInfo> attachementsInfo = new ArrayList<>();

    for(SymAttachmentInfo symAttachmentInfo: symAttachmentsInfo) {
        AttachmentInfo attachmentInfo = new AttachmentInfo();
        attachmentInfo.setId(symAttachmentInfo.getId());
        attachmentInfo.setName(symAttachmentInfo.getName());
        attachmentInfo.setSize(symAttachmentInfo.getSize());
        attachementsInfo.add(attachmentInfo);
    }

    return attachementsInfo;
}


}
