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

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.exceptions.RoomException;
import org.symphonyoss.exceptions.StreamsException;
import org.symphonyoss.exceptions.SymException;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymRoomDetail;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Frank Tarsillo on 7/8/2016.
 */
public class RoomService implements RoomServiceListener {


    private final ConcurrentHashMap<String, Room> roomsByStream = new ConcurrentHashMap<>();


    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private final Set<RoomServiceListener> roomServiceListeners = ConcurrentHashMap.newKeySet();

    public RoomService(SymphonyClient symClient) {
        this.symClient = symClient;

        symClient.getMessageService().addRoomListener(this);

    }


    public Room createRoom(SymRoomAttributes symRoomAttributes) throws RoomException {

        if (symRoomAttributes == null)
            throw new NullPointerException("Room attributes were not provided..");

        try {
            SymRoomDetail symRoomDetail = symClient.getStreamsClient().createChatRoom(symRoomAttributes);

            Room room = new Room();
            room.setId(symRoomDetail.getRoomSystemInfo().getId());
            room.setRoomDetail(symRoomDetail);
            room.setMembershipList(symClient.getRoomMembershipClient().getRoomMembership(room.getId()));

            return room;

        } catch (StreamsException e) {
            logger.error("Failed to obtain stream for room...", e);
            throw new RoomException("Could not create/join chat room: " + symRoomAttributes.getName(), e);
        } catch (SymException e1) {
            logger.error("Failed to retrieve room membership...", e1);
            throw new RoomException("Could not retrieve room membership for room: " + symRoomAttributes.getName());
        }

    }

    public Room getRoom(String stream) {

        return roomsByStream.get(stream);
    }

    //Logical join of the room which registers the listeners.
    public void joinRoom(Room room) throws RoomException {

        if (room.getStream() == null || room.getStreamId() == null || room.getId() == null)
            throw new RoomException("Room is not fully defined.  Check ID and stream ID");


        try {
            room.setRoomDetail(symClient.getStreamsClient().getRoomDetail(room.getStreamId()));
            roomsByStream.put(room.getStreamId(), room);
        } catch (StreamsException e) {
            logger.error("Failed to obtain room detail...", e);
            throw new RoomException("Failed to obtain room detail for requested room: " + room.getStreamId(), e);
        }


    }


    @Override
    public void onMessage(SymMessage symMessage) {

        try {
            if (roomsByStream.get(symMessage.getStreamId()) == null) {
                addRoom(symMessage.getStreamId());
            }

            for (String stream : roomsByStream.keySet()) {

                RoomListener roomListener = roomsByStream.get(stream).getRoomListener();
                if (roomListener != null)
                    roomListener.onRoomMessage(symMessage);

            }

        } catch (RoomException e) {
            logger.error("Unable to add new room from message: ", e);
        }
    }

    private void addRoom(String streamId) throws RoomException {

        Room room = new Room();

        Stream stream = new Stream();
        stream.setId(streamId);
        room.setStream(stream);
        room.setId(stream.getId());

        joinRoom(room);

        onNewRoom(room);
    }

    @Override
    public void onNewRoom(Room room) {

        for (RoomServiceListener roomServiceListener : roomServiceListeners)
            roomServiceListener.onNewRoom(room);
    }

    @Override
    public void onRoomCreatedMessage(RoomCreatedMessage roomCreatedMessage) {

        for (RoomServiceListener roomServiceListener : roomServiceListeners) {
            roomServiceListener.onRoomCreatedMessage(roomCreatedMessage);

        }


    }

    @Override
    public void onRoomDeactivatedMessage(RoomDeactivatedMessage roomDeactivatedMessage) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onRoomDeactivatedMessage(roomDeactivatedMessage);

        }
    }

    @Override
    public void onRoomMemberDemotedFromOwnerMessage(RoomMemberDemotedFromOwnerMessage roomMemberDemotedFromOwnerMessage) {
        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onRoomMemberDemotedFromOwnerMessage(roomMemberDemotedFromOwnerMessage);

        }
    }

    @Override
    public void onRoomMemberPromotedToOwnerMessage(RoomMemberPromotedToOwnerMessage roomMemberPromotedToOwnerMessage) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onRoomMemberPromotedToOwnerMessage(roomMemberPromotedToOwnerMessage);

        }
    }

    @Override
    public void onRoomReactivatedMessage(RoomReactivatedMessage roomReactivatedMessage) {
        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onRoomReactivatedMessage(roomReactivatedMessage);

        }
    }

    @Override
    public void onRoomUpdatedMessage(RoomUpdatedMessage roomUpdatedMessage) {
        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onRoomUpdatedMessage(roomUpdatedMessage);

        }
    }

    @Override
    public void onUserJoinedRoomMessage(UserJoinedRoomMessage userJoinedRoomMessage) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onUserJoinedRoomMessage(userJoinedRoomMessage);

        }
    }

    @Override
    public void onUserLeftRoomMessage(UserLeftRoomMessage userLeftRoomMessage) {
        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onUserLeftRoomMessage(userLeftRoomMessage);

        }

    }

    /**
     * Please use {@link #addRoomServiceListener(RoomServiceListener)}
     * @param roomServiceListener
     */
    @Deprecated
    public void registerRoomServiceListener(RoomServiceListener roomServiceListener) {
        addRoomServiceListener(roomServiceListener);
    }

    public void addRoomServiceListener(RoomServiceListener roomServiceListener) {
        roomServiceListeners.add(roomServiceListener);
    }

    public void removeRoomServiceListener(RoomServiceListener roomServiceListener) {
        roomServiceListeners.remove(roomServiceListener);
    }

}
