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
import org.symphonyoss.symphony.pod.model.MembershipList;
import org.symphonyoss.symphony.pod.model.Stream;

/**
 * Created by Frank Tarsillo on 6/11/2016.
 */
public class Room {
    private String id;
    private Stream stream;
    private MembershipList membershipList;
    private SymRoomDetail roomDetail;
    private RoomListener roomListener;

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
    }

    public RoomListener getRoomListener() {
        return roomListener;
    }

    public void setRoomListener(RoomListener roomListener) {
        this.roomListener = roomListener;
    }
}
