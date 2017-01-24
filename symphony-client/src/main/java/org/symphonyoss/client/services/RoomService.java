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
 * The room service provides capabilities that support room access and events.  The running service will construct
 * room objects detected from new message events.  The service supports listeners that provide callbacks to all
 * registered room events and general messaging.
 *
 * Its important to note the distinction between creating a room and joining a room.  Creating a room implies the
 * creation of the room object, but not the monitoring of it by the service.  You must explicitly join a room (register
 * with the RoomService) in order to receive individual room events.
 *
 * Created by Frank Tarsillo on 7/8/2016.
 */
public class RoomService implements RoomServiceListener {


    private final ConcurrentHashMap<String, Room> roomsByStream = new ConcurrentHashMap<>();
    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private final Set<RoomServiceListener> roomServiceListeners = ConcurrentHashMap.newKeySet();

    /**
     *
     * @param symClient SymphonyClient provides access to client implementations and dependant services such as the
     *                  {@link MessageService}
     *
     */
    public RoomService(SymphonyClient symClient) {
        this.symClient = symClient;

        symClient.getMessageService().addRoomListener(this);

    }


    /**
     * Create a new room object from {@link SymRoomAttributes} provided.  The room object will be enriched with all
     * associated room metadata at the point of creation.
     *
     * Note: Future lifecycle room events are not automatically reflected in the created room object.
     * updated.
     * @param symRoomAttributes Room attributes required to create the room object
     * @return Fully populated {@link Room} object
     * @throws RoomException Exception generated from the logical creation of the room object
     */
    public Room createRoom(SymRoomAttributes symRoomAttributes) throws RoomException {

        if (symRoomAttributes == null)
            throw new NullPointerException("Room attributes were not provided..");


        try {

            //Create the room if it doesn't exist or retrieve the detail fo pre-existing room
            SymRoomDetail symRoomDetail = symClient.getStreamsClient().createChatRoom(symRoomAttributes);

            //Construct the room object
            Room room = new Room();
            room.setId(symRoomDetail.getRoomSystemInfo().getId());
            room.setRoomDetail(symRoomDetail);

            //Enrich
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

    /**
     * Return registered room by provided stream.  The room object must be registered to the service through the
     * {@link #joinRoom(Room)} method.
     *
     * @param stream Stream ID as key to lookup registered room
     * @return {@link Room} based on stream ID provided
     */
    public Room getRoom(String stream) {

        return roomsByStream.get(stream);
    }

    /**
     * Logical join of the room which registers the listeners and the Room object to the service.
     *
     * This call will also update room details within the Room object.
     *
     * @param room Room to register
     *
     * @throws RoomException Caused by room details that prevent logical monitoring of the room object
     *
     */
    public void joinRoom(Room room) throws RoomException {

        if (room.getStream() == null || room.getStreamId() == null || room.getId() == null)
            throw new RoomException("Room is not fully defined.  Check ID and stream ID");


        try {
            //Lets refresh the room details
            room.setRoomDetail(symClient.getStreamsClient().getRoomDetail(room.getStreamId()));

            //Register room object to internal cache
            roomsByStream.put(room.getStreamId(), room);
        } catch (StreamsException e) {
            logger.error("Failed to obtain room detail...", e);
            throw new RoomException("Failed to obtain room detail for requested room: " + room.getStreamId(), e);
        }


    }


    /**
     * Callback from registered room listener on the MessageService.  All messages will be associated with Rooms.
     * If a message underlying stream is not associated with a registered room, then a new room object is created and
     * events are published to registered {@link RoomServiceListener}
     *
     * Messages associated with registered rooms are published to room object listeners.
     *
     * @param symMessage Room messages detected and published
     */
    @Override
    public void onMessage(SymMessage symMessage) {

        try {

            //Automatically register new room events
            if (roomsByStream.get(symMessage.getStreamId()) == null) {
                addRoom(symMessage.getStreamId());
            }


            //Publish messages to any generic RoomService Listners
            for(RoomServiceListener roomServiceListener: roomServiceListeners){

                roomServiceListener.onMessage(symMessage);

            }

            //For specific rooms that are registered publish new message events
            for (String stream : roomsByStream.keySet()) {


                Room room = roomsByStream.get(symMessage.getStreamId());

                //Publish a message event to a room
                if (room != null)
                    room.onRoomMessage(symMessage);

            }

        } catch (RoomException e) {
            logger.error("Unable to add new room from message: ", e);
        }
    }

    /**
     * Add a room to the service
     * @param streamId Stream ID of the room
     * @throws RoomException
     */
    private void addRoom(String streamId) throws RoomException {

        //Create a new room object and register to the service
        Room room = new Room();

        Stream stream = new Stream();
        stream.setId(streamId);
        room.setStream(stream);
        room.setId(stream.getId());

        joinRoom(room);

        onNewRoom(room);
    }

    /**
     * Publish new room objects based on detection and/or registration to the service
     * @param room Room object published from callback
     */
    @Override
    public void onNewRoom(Room room) {

        for (RoomServiceListener roomServiceListener : roomServiceListeners)
            roomServiceListener.onNewRoom(room);
    }

    /**
     * Events provided by the MessageService related to new rooms created (defined)
     * @param roomCreatedMessage {@link RoomCreatedMessage}
     */
    @Override
    public void onRoomCreatedMessage(RoomCreatedMessage roomCreatedMessage) {

        for (RoomServiceListener roomServiceListener : roomServiceListeners) {
            roomServiceListener.onRoomCreatedMessage(roomCreatedMessage);

        }


    }

    /**
     * Room deactivated events triggered by administration event.
     * @param roomDeactivatedMessage {@link RoomDeactivatedMessage}
     */
    @Override
    public void onRoomDeactivatedMessage(RoomDeactivatedMessage roomDeactivatedMessage) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onRoomDeactivatedMessage(roomDeactivatedMessage);

        }
    }

    /**
     * Room member demotion event triggered from administration changes
     *
     * @param roomMemberDemotedFromOwnerMessage {@link RoomMemberDemotedFromOwnerMessage}
     */
    @Override
    public void onRoomMemberDemotedFromOwnerMessage(RoomMemberDemotedFromOwnerMessage roomMemberDemotedFromOwnerMessage) {
        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onRoomMemberDemotedFromOwnerMessage(roomMemberDemotedFromOwnerMessage);

        }
    }

    /**
     * Room member promotion event triggered from administration changes
     * @param roomMemberPromotedToOwnerMessage {@link RoomMemberPromotedToOwnerMessage}
     */
    @Override
    public void onRoomMemberPromotedToOwnerMessage(RoomMemberPromotedToOwnerMessage roomMemberPromotedToOwnerMessage) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            RoomListener roomListener = entry.getValue().getRoomListener();
            if (roomListener != null)
                roomListener.onRoomMemberPromotedToOwnerMessage(roomMemberPromotedToOwnerMessage);

        }
    }

    /**
     * Room reactivated event triggered from administration changes
     * @param roomReactivatedMessage  {@link RoomReactivatedMessage}
     */
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
     * @param roomServiceListener Listener to register
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
