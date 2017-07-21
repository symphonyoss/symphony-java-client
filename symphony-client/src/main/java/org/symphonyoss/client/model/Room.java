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

import org.symphonyoss.client.services.RoomEventListener;
import org.symphonyoss.client.services.RoomListener;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymRoomDetail;
import org.symphonyoss.symphony.pod.model.MembershipList;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Room abstraction object which identifies and holds all properties of a Symphony Room.
 * Room objects work directly with the RoomService, which supports all room events.
 *
 * @author Frank Tarsillo
 */
public class Room {
    private String id;
    private Stream stream;
    private String streamId;
    private MembershipList membershipList;
    private SymRoomDetail roomDetail;
    private RoomListener roomListener;
    private final Set<RoomListener> roomListeners = ConcurrentHashMap.newKeySet();
    private final Set<RoomEventListener> roomEventListeners = ConcurrentHashMap.newKeySet();

    public String getStreamId() {
        return streamId;
    }

    @SuppressWarnings("unused")
    public void setStreamId(String streamId) {
        this.streamId = streamId;
        if (stream == null) {
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

    @SuppressWarnings("unused")
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
        if (streamId == null)
            streamId = stream.getId();
    }


    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public RoomListener getRoomListener() {
        return roomListener;
    }


    @Deprecated
    public void setRoomListener(RoomListener roomListener) {
        this.roomListener = roomListener;
        roomListeners.add(roomListener);
    }


    /**
     * Return all event listeners for this room
     *
     * @return A set of all room event listeners.
     */
    public Set<RoomEventListener> getRoomEventListeners() {

        return roomEventListeners;
    }


    /**
     * Push message to all registered listeners.
     *
     * @param message New incoming message.
     */
    public void onRoomMessage(SymMessage message) {


        for (RoomListener roomListener : roomListeners)
            roomListener.onRoomMessage(message);

        for (RoomEventListener roomEventListener : roomEventListeners)
            roomEventListener.onRoomMessage(message);

    }

    @Deprecated
    public void addListener(RoomListener roomListener) {
        roomListeners.add(roomListener);

    }

    @Deprecated
    public void removeListener(RoomListener roomListener) {

        roomListeners.remove(roomListener);


    }

    public void addEventListener(RoomEventListener roomEventListener) {
        roomEventListeners.add(roomEventListener);

    }


    public void removeEventListener(RoomEventListener roomEventListener) {

        roomEventListeners.remove(roomEventListener);


    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this.getClass() != o.getClass())
            return false;


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
