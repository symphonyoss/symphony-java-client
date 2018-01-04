/*
 *
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
 *
 */

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.symphony.agent.model.V5FirehoseReadRequest;

/**
 * @author Frank Tarsillo on 12/17/17.
 */
public class SymFirehoseRequest {


    private String ackId = null;


    private Integer maxMsgs = null;


    private Integer timeout = null;


    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }

    public Integer getMaxMsgs() {
        return maxMsgs;
    }

    public void setMaxMsgs(Integer maxMsgs) {
        this.maxMsgs = maxMsgs;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public static V5FirehoseReadRequest toV5FirehoseRequest(SymFirehoseRequest symFirehoseRequest) {

        if (symFirehoseRequest == null) {
            return null;
        }

        V5FirehoseReadRequest v5FirehoseReadRequest = new V5FirehoseReadRequest();
        v5FirehoseReadRequest.setAckId(symFirehoseRequest.getAckId());
        v5FirehoseReadRequest.setMaxMsgs(symFirehoseRequest.getMaxMsgs());
        v5FirehoseReadRequest.setTimeout(symFirehoseRequest.getTimeout());
        return v5FirehoseReadRequest;
    }
}
