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

import org.symphonyoss.symphony.pod.model.StreamAttributes;

/**
 * @author Frank Tarsillo on 5/6/17.
 */
public class SymStreamAttributes {


    private String id = null;


    private Boolean crossPod = null;


    private Boolean active = null;


    private SymStreamType symStreamType = null;


    private SymChatSpecificStreamAttributes symChatSpecificStreamAttributes = null;


    private SymRoomSpecificStreamAttributes symRoomSpecificStreamAttributes = null;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getCrossPod() {
        return crossPod;
    }

    public void setCrossPod(Boolean crossPod) {
        this.crossPod = crossPod;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public SymStreamType getSymStreamType() {
        return symStreamType;
    }

    public void setSymStreamType(SymStreamType symStreamType) {
        this.symStreamType = symStreamType;
    }

    public SymChatSpecificStreamAttributes getSymChatSpecificStreamAttributes() {
        return symChatSpecificStreamAttributes;
    }

    public void setSymChatSpecificStreamAttributes(SymChatSpecificStreamAttributes symChatSpecificStreamAttributes) {
        this.symChatSpecificStreamAttributes = symChatSpecificStreamAttributes;
    }

    public SymRoomSpecificStreamAttributes getSymRoomSpecificStreamAttributes() {
        return symRoomSpecificStreamAttributes;
    }

    public void setSymRoomSpecificStreamAttributes(SymRoomSpecificStreamAttributes symRoomSpecificStreamAttributes) {
        this.symRoomSpecificStreamAttributes = symRoomSpecificStreamAttributes;
    }

    public static SymStreamAttributes toStreamAttributes(StreamAttributes streamAttributes) {

        SymStreamAttributes symStreamAttributes = new SymStreamAttributes();
        symStreamAttributes.setActive(streamAttributes.getActive());
        symStreamAttributes.setCrossPod(streamAttributes.getCrossPod());
        symStreamAttributes.setId(streamAttributes.getId());

        SymRoomSpecificStreamAttributes symRoomSpecificStreamAttributes = new SymRoomSpecificStreamAttributes();

        if (streamAttributes.getRoomAttributes() != null) {
            symRoomSpecificStreamAttributes.setName(streamAttributes.getRoomAttributes().getName());
            symStreamAttributes.setSymRoomSpecificStreamAttributes(symRoomSpecificStreamAttributes);
        }

        SymStreamType symStreamType = new SymStreamType();
        symStreamType.setType(SymStreamType.Type.fromValue(streamAttributes.getStreamType().getType().toString()));
        symStreamAttributes.setSymStreamType(symStreamType);

        if (streamAttributes.getStreamAttributes() != null)
            symStreamAttributes.setSymChatSpecificStreamAttributes(SymChatSpecificStreamAttributes.toSymChatSpecificStreamAttributes(streamAttributes.getStreamAttributes()));


        return symStreamAttributes;
    }
}
