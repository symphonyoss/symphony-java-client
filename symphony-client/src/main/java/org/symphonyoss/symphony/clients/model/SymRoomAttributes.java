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



import org.symphonyoss.symphony.pod.model.V2RoomAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank.tarsillo on 9/12/2016.
 */
public class SymRoomAttributes {


    private String name = null;

    private List<SymRoomTag> keywords = new ArrayList<>();

    private String description = null;

    private Boolean membersCanInvite = null;

    private Boolean discoverable = null;

    private Boolean _public = null;

    private Boolean readOnly = null;

    private Boolean copyProtected = null;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SymRoomTag> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<SymRoomTag> keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getMembersCanInvite() {
        return membersCanInvite;
    }

    public void setMembersCanInvite(Boolean membersCanInvite) {
        this.membersCanInvite = membersCanInvite;
    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public Boolean getPublic() {
        return _public;
    }

    public void setPublic(Boolean _public) {
        this._public = _public;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getCopyProtected() {
        return copyProtected;
    }

    public void setCopyProtected(Boolean copyProtected) {
        this.copyProtected = copyProtected;
    }



    public static SymRoomAttributes toSymRoomAttributes(V2RoomAttributes roomAttributes){

        SymRoomAttributes symRoomAttributes = new SymRoomAttributes();
        symRoomAttributes.setPublic(roomAttributes.getPublic());
        symRoomAttributes.setCopyProtected(roomAttributes.getCopyProtected());
        symRoomAttributes.setDescription(roomAttributes.getDescription());
        symRoomAttributes.setDiscoverable(roomAttributes.getDiscoverable());
        symRoomAttributes.setKeywords(SymRoomTag.toSymRoomTags(roomAttributes.getKeywords()));
        symRoomAttributes.setMembersCanInvite(roomAttributes.getMembersCanInvite());
        symRoomAttributes.setName(roomAttributes.getName());
        symRoomAttributes.setReadOnly(roomAttributes.getReadOnly());

        return symRoomAttributes;
    }

    public static V2RoomAttributes toV2RoomAttributes(SymRoomAttributes roomAttributes){

      V2RoomAttributes v2RoomAttributes = new V2RoomAttributes();
        v2RoomAttributes.setPublic(roomAttributes.getPublic());
        v2RoomAttributes.setCopyProtected(roomAttributes.getCopyProtected());
        v2RoomAttributes.setDescription(roomAttributes.getDescription());
        v2RoomAttributes.setDiscoverable(roomAttributes.getDiscoverable());
        v2RoomAttributes.setKeywords(SymRoomTag.toRoomTags(roomAttributes.getKeywords()));
        v2RoomAttributes.setMembersCanInvite(roomAttributes.getMembersCanInvite());
        v2RoomAttributes.setName(roomAttributes.getName());
        v2RoomAttributes.setReadOnly(roomAttributes.getReadOnly());

        return v2RoomAttributes;
    }



}
