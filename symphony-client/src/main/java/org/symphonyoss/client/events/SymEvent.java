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

package org.symphonyoss.client.events;

import org.symphonyoss.symphony.agent.model.V4Event;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frank Tarsillo on 6/26/17.
 */
public class SymEvent {

    private String id = null;

    private Long timestamp = null;

    private String type = null;


    private SymUser initiator = null;


    private SymEventPayload payload = null;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SymUser getInitiator() {
        return initiator;
    }

    public void setInitiator(SymUser initiator) {
        this.initiator = initiator;
    }

    public SymEventPayload getPayload() {
        return payload;
    }

    public void setPayload(SymEventPayload payload) {
        this.payload = payload;
    }

    public static List<SymEvent> toSymEvent(List<V4Event> v4Events) {

        if(v4Events == null)
            return null;

        List<SymEvent> symEvents = new ArrayList<>();

        for(V4Event v4Event : v4Events){

            SymEvent symEvent = new SymEvent();
            symEvent.setId(v4Event.getId());
            symEvent.setInitiator(SymUser.toSymUser(v4Event.getInitiator().getUser()));
            symEvent.setType(v4Event.getType());
            symEvent.setTimestamp(v4Event.getTimestamp());
            symEvent.setPayload(SymEventPayload.toSymEventPayLoad(v4Event.getPayload()));

            symEvents.add(symEvent);
        }

        return symEvents;
    }
}
