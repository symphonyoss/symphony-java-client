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

import org.symphonyoss.symphony.pod.model.RoomSearchCriteria;
import org.symphonyoss.symphony.pod.model.UserId;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Frank Tarsillo
 */
@SuppressWarnings("WeakerAccess")
public class SymRoomSearchCriteria {


    private String query = null;


    private List<String> labels = new ArrayList<>();


    private Boolean active = null;


    private Boolean _private = null;


    private SymUser owner = null;


    private SymUser creator = null;


    private SymUser member = null;

    public SymRoomSearchCriteria() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean get_private() {
        return _private;
    }

    public void set_private(Boolean _private) {
        this._private = _private;
    }


    public SymUser getOwner() {
        return owner;
    }

    public void setOwner(SymUser owner) {
        this.owner = owner;
    }

    public SymUser getCreator() {
        return creator;
    }

    public void setCreator(SymUser creator) {
        this.creator = creator;
    }

    public SymUser getMember() {
        return member;
    }

    public void setMember(SymUser member) {
        this.member = member;
    }

    public static RoomSearchCriteria toRoomSearchCriteria(SymRoomSearchCriteria searchCriteria) {

        RoomSearchCriteria roomSearchCriteria = new RoomSearchCriteria();
        roomSearchCriteria.setActive(searchCriteria.getActive());
        roomSearchCriteria.setLabels(searchCriteria.getLabels());
        if (searchCriteria.getMember() != null)
            roomSearchCriteria.setMember(new UserId() {{
                setId(searchCriteria.getMember().getId());
            }});
        if (searchCriteria.getCreator() != null)
            roomSearchCriteria.setCreator(new UserId() {{
                setId(searchCriteria.getCreator().getId());
            }});
        if (searchCriteria.getOwner() != null)
            roomSearchCriteria.setOwner(new UserId() {{
                setId(searchCriteria.getOwner().getId());
            }});
        roomSearchCriteria.setPrivate(searchCriteria.get_private());
        roomSearchCriteria.setQuery(searchCriteria.getQuery());


        return roomSearchCriteria;
    }
}
