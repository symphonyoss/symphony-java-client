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
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.client.model;

import org.symphonyoss.client.services.RoomListener;
import org.symphonyoss.symphony.clients.model.SymRoomDetail;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.MembershipList;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.Objects;

/**
 * Room abstraction object which identifies and holds all properties of a Symphony Room.
 * Room objects work directly with the RoomService, which supports all room events.
 * @author Frank Tarsillo
 */
public class Room {
    private String id;
    private Stream stream;
    private String streamId;
    private MembershipList membershipList;
    private SymRoomDetail roomDetail;
    private RoomListener roomListener;

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
        if(stream == null){
            stream = new Stream();
            stream.setId(streamId);
        }


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MembershipList getMembershipList() {
        return membershipList;
    }

    public void setMembershipList(MembershipList membershipList) {
        this.membershipList = membershipList;
    }

    public SymRoomDetail getRoomDetail() {
        return roomDetail;
    }

    public void setRoomDetail(SymRoomDetail roomDetail) {
        this.roomDetail = roomDetail;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
        if(streamId == null)
            streamId=stream.getId();
    }

    public RoomListener getRoomListener() {
        return roomListener;
    }

    public void setRoomListener(RoomListener roomListener) {
        this.roomListener = roomListener;
    }


    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Room)) {
            return false;
        }
        Room room = (Room) o;
        return id.equals(room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stream);
    }
}
