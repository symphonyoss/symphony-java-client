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

import org.symphonyoss.symphony.pod.model.V2RoomDetail;
import org.symphonyoss.symphony.pod.model.V3RoomDetail;


/**
 * @author Frank Tarsillo
 */
@SuppressWarnings("WeakerAccess")
public class SymRoomDetail {

    private SymRoomAttributes roomAttributes = null;


    private SymRoomSystemInfo roomSystemInfo = null;

    public SymRoomAttributes getRoomAttributes() {
        return roomAttributes;
    }

    public void setRoomAttributes(SymRoomAttributes roomAttributes) {
        this.roomAttributes = roomAttributes;
    }

    public SymRoomSystemInfo getRoomSystemInfo() {
        return roomSystemInfo;
    }

    public void setRoomSystemInfo(SymRoomSystemInfo roomSystemInfo) {
        this.roomSystemInfo = roomSystemInfo;
    }


    public static SymRoomDetail toSymRoomDetail(V2RoomDetail roomDetail){

        SymRoomDetail symRoomDetail = new SymRoomDetail();
        symRoomDetail.setRoomAttributes(SymRoomAttributes.toSymRoomAttributes(roomDetail.getRoomAttributes()));
        symRoomDetail.setRoomSystemInfo(SymRoomSystemInfo.toSymRoomSystemInfo(roomDetail.getRoomSystemInfo()));
        return symRoomDetail;

    }
    public static SymRoomDetail toSymRoomDetail(V3RoomDetail roomDetail){

        SymRoomDetail symRoomDetail = new SymRoomDetail();
        symRoomDetail.setRoomAttributes(SymRoomAttributes.toSymRoomAttributes(roomDetail.getRoomAttributes()));
        symRoomDetail.setRoomSystemInfo(SymRoomSystemInfo.toSymRoomSystemInfo(roomDetail.getRoomSystemInfo()));
        return symRoomDetail;

    }
}
