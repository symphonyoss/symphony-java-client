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

import org.symphonyoss.symphony.agent.model.V4Payload;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymSharedPost;

/**
 * @author Frank Tarsillo on 6/26/17.
 */
public class SymEventPayload {


    private SymMessage messageSent = null;


    private SymSharedPost sharedPost = null;


    private SymIMCreated instantMessageCreated = null;


    private SymRoomCreated roomCreated = null;


    private SymRoomUpdated roomUpdated = null;


    private SymRoomDeactivated roomDeactivated = null;


    private SymRoomReactivated roomReactivated = null;


    private SymUserJoinedRoom userJoinedRoom = null;


    private SymUserLeftRoom userLeftRoom = null;


    private SymRoomMemberPromotedToOwner roomMemberPromotedToOwner = null;


    private SymRoomMemberDemotedFromOwner roomMemberDemotedFromOwner = null;


    private SymConnectionRequested connectionRequested = null;


    private SymConnectionAccepted connectionAccepted = null;


    private SymMessageSuppressed messageSuppressed = null;


    public SymMessage getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(SymMessage messageSent) {
        this.messageSent = messageSent;
    }

    public SymSharedPost getSharedPost() {
        return sharedPost;
    }

    public void setSharedPost(SymSharedPost sharedPost) {
        this.sharedPost = sharedPost;
    }

    public SymIMCreated getInstantMessageCreated() {
        return instantMessageCreated;
    }

    public void setInstantMessageCreated(SymIMCreated instantMessageCreated) {
        this.instantMessageCreated = instantMessageCreated;
    }

    public SymRoomCreated getRoomCreated() {
        return roomCreated;
    }

    public void setRoomCreated(SymRoomCreated roomCreated) {
        this.roomCreated = roomCreated;
    }

    public SymRoomUpdated getRoomUpdated() {
        return roomUpdated;
    }

    public void setRoomUpdated(SymRoomUpdated roomUpdated) {
        this.roomUpdated = roomUpdated;
    }

    public SymRoomDeactivated getRoomDeactivated() {
        return roomDeactivated;
    }

    public void setRoomDeactivated(SymRoomDeactivated roomDeactivated) {
        this.roomDeactivated = roomDeactivated;
    }

    public SymRoomReactivated getRoomReactivated() {
        return roomReactivated;
    }

    public void setRoomReactivated(SymRoomReactivated roomReactivated) {
        this.roomReactivated = roomReactivated;
    }

    public SymUserJoinedRoom getUserJoinedRoom() {
        return userJoinedRoom;
    }

    public void setUserJoinedRoom(SymUserJoinedRoom userJoinedRoom) {
        this.userJoinedRoom = userJoinedRoom;
    }

    public SymUserLeftRoom getUserLeftRoom() {
        return userLeftRoom;
    }

    public void setUserLeftRoom(SymUserLeftRoom userLeftRoom) {
        this.userLeftRoom = userLeftRoom;
    }

    public SymRoomMemberPromotedToOwner getRoomMemberPromotedToOwner() {
        return roomMemberPromotedToOwner;
    }

    public void setRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner roomMemberPromotedToOwner) {
        this.roomMemberPromotedToOwner = roomMemberPromotedToOwner;
    }

    public SymRoomMemberDemotedFromOwner getRoomMemberDemotedFromOwner() {
        return roomMemberDemotedFromOwner;
    }

    public void setRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner roomMemberDemotedFromOwner) {
        this.roomMemberDemotedFromOwner = roomMemberDemotedFromOwner;
    }

    public SymConnectionRequested getConnectionRequested() {
        return connectionRequested;
    }

    public void setConnectionRequested(SymConnectionRequested connectionRequested) {
        this.connectionRequested = connectionRequested;
    }

    public SymConnectionAccepted getConnectionAccepted() {
        return connectionAccepted;
    }

    public void setConnectionAccepted(SymConnectionAccepted connectionAccepted) {
        this.connectionAccepted = connectionAccepted;
    }

    public SymMessageSuppressed getMessageSuppressed() {
        return messageSuppressed;
    }

    public void setMessageSuppressed(SymMessageSuppressed messageSuppressed) {
        this.messageSuppressed = messageSuppressed;
    }

    public static SymEventPayload toSymEventPayLoad(V4Payload payload) {


        SymEventPayload symEventPayload = new SymEventPayload();

        if (payload.getMessageSent() != null && payload.getMessageSent().getMessage() != null)
            symEventPayload.setMessageSent(SymMessage.toSymMessage(payload.getMessageSent().getMessage()));

        if (payload.getConnectionAccepted() != null)
            symEventPayload.setConnectionAccepted(SymConnectionAccepted.toSymConnectionAccepted(payload.getConnectionAccepted()));

        if (payload.getConnectionRequested() != null)
            symEventPayload.setConnectionRequested(SymConnectionRequested.toSymConnectionRequested(payload.getConnectionRequested()));

        if (payload.getInstantMessageCreated() != null)
            symEventPayload.setInstantMessageCreated(SymIMCreated.toSymIMCreated(payload.getInstantMessageCreated()));

        if (payload.getMessageSuppressed() != null)
            symEventPayload.setMessageSuppressed(SymMessageSuppressed.toSymMessageSuppressed(payload.getMessageSuppressed()));

        if (payload.getRoomCreated() != null)
            symEventPayload.setRoomCreated(SymRoomCreated.toSymRoomCreated(payload.getRoomCreated()));

        if (payload.getRoomDeactivated() != null)
            symEventPayload.setRoomDeactivated(SymRoomDeactivated.toSymDeactivated(payload.getRoomDeactivated()));

        if (payload.getRoomReactivated() != null)
            symEventPayload.setRoomReactivated(SymRoomReactivated.toSymRoomReactivated(payload.getRoomReactivated()));

        if (payload.getRoomMemberDemotedFromOwner() != null)
            symEventPayload.setRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner.toSymRoomMemberDemotedFromOwner(payload.getRoomMemberDemotedFromOwner()));

        if (payload.getRoomMemberPromotedToOwner() != null)
            symEventPayload.setRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner.toSymRoomMemberPromotedFromOwner(payload.getRoomMemberPromotedToOwner()));

        if (payload.getRoomUpdated() != null)
            symEventPayload.setRoomUpdated(SymRoomUpdated.toSymRoomUpdated(payload.getRoomUpdated()));

        if (payload.getUserJoinedRoom() != null)
            symEventPayload.setUserJoinedRoom(SymUserJoinedRoom.toSymUserJoinedRoom(payload.getUserJoinedRoom()));

        if (payload.getUserLeftRoom() != null)
            symEventPayload.setUserLeftRoom(SymUserLeftRoom.toSymUserLeftRoom(payload.getUserLeftRoom()));

        if (payload.getSharedPost() != null)
            symEventPayload.setSharedPost(SymSharedPost.toSymSharedPost(payload.getSharedPost()));


        return symEventPayload;


    }
}
