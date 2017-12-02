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
import org.symphonyoss.client.events.*;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.clients.model.*;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The room service provides capabilities that support room access and events.  The running service will construct
 * room objects detected from new message events.  The service supports listeners that provide callbacks to all
 * registered room events and general messaging.
 * <p>
 * Its important to note the distinction between creating a room and joining a room.  Creating a room implies the
 * creation of the room object, but not the monitoring of it by the service.  You must explicitly join a room (register
 * with the RoomService) in order to receive individual room events.
 * <p>
 *
 * @author Frank Tarsillo
 */
public class RoomService implements RoomServiceEventListener {


    private final ConcurrentHashMap<String, Room> roomsByStream = new ConcurrentHashMap<>();

    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private final Set<RoomServiceEventListener> roomServiceEventListeners = ConcurrentHashMap.newKeySet();
    private ApiVersion apiVersion;

    /**
     * @param symClient SymphonyClient provides access to client implementations and dependant services such as the
     *                  {@link MessageService}
     */
    public RoomService(SymphonyClient symClient) {
        this(symClient, ApiVersion.getDefault());
    }


    /**
     * Specify a version of RoomService to use.  Version is aligning with LLC REST API endpoint versions.
     *
     * @param symClient  Symphony client required to access all underlying clients functions.
     * @param apiVersion The version of the ChatServer to use which is aligned with LLC REST API endpoint versions.
     */
    public RoomService(SymphonyClient symClient, ApiVersion apiVersion) {

        this.apiVersion = apiVersion;
        this.symClient = symClient;

        MessageService messageService = symClient.getMessageService();

        if (messageService != null) {
            messageService.addRoomServiceEventListener(this);
        }


    }

    /**
     * Create a new room object from {@link SymRoomAttributes} provided.  The room object will be enriched with all
     * associated room metadata at the point of creation.
     * <p>
     * Note: Future lifecycle room events are not automatically reflected in the created room object.
     * updated.
     *
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
            room.setStreamId(room.getId());

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
     * @param streamId Stream Room ID as key to lookup registered room
     * @return {@link Room} based on stream ID provided
     */
    public Room getRoom(String streamId) {

        return roomsByStream.get(streamId);
    }



    /**
     * Return registered room by provided stream.  The room object must be registered to the service through the
     * {@link #joinRoom(Room)} method.
     *
     * @param symStream Stream ID as key to lookup registered room
     * @return {@link Room} based on stream ID provided
     */
    public Room getRoom(SymStream symStream) {

        return roomsByStream.get(symStream.getStreamId());
    }

    /**
     * Logical join of the room which registers the listeners and the Room object to the service.
     * <p>
     * This call will also update room details within the Room object.
     *
     * @param room Room to register
     * @throws RoomException Caused by room details that prevent logical monitoring of the room object
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
     * events are published to registered {@link RoomServiceEventListener}
     * <p>
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


            //Publish messages to any generic RoomService Listeners
            for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners) {

                roomServiceEventListener.onMessage(symMessage);

            }

            Room room = roomsByStream.get(symMessage.getStreamId());

            //Publish a message event to a room
            if (room != null)
                room.onRoomMessage(symMessage);


        } catch (RoomException e) {
            logger.error("Unable to add new room from message: ", e);
        }
    }

    @Override
    public void onSymRoomDeactivated(SymRoomDeactivated symRoomDeactivated) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            for (RoomEventListener roomEventListener : entry.getValue().getRoomEventListeners())
                roomEventListener.onSymRoomDeactivated(symRoomDeactivated);

        }

    }

    @Override
    public void onSymRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            for (RoomEventListener roomEventListener : entry.getValue().getRoomEventListeners())
                roomEventListener.onSymRoomMemberDemotedFromOwner(symRoomMemberDemotedFromOwner);


        }

    }

    @Override
    public void onSymRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner) {
        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            for (RoomEventListener roomEventListener : entry.getValue().getRoomEventListeners())
                roomEventListener.onSymRoomMemberPromotedToOwner(symRoomMemberPromotedToOwner);


        }
    }

    @Override
    public void onSymRoomReactivated(SymRoomReactivated symRoomReactivated) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            for (RoomEventListener roomEventListener : entry.getValue().getRoomEventListeners())
                roomEventListener.onSymRoomReactivated(symRoomReactivated);


        }

    }

    @Override
    public void onSymRoomUpdated(SymRoomUpdated symRoomUpdated) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            for (RoomEventListener roomEventListener : entry.getValue().getRoomEventListeners())
                roomEventListener.onSymRoomUpdated(symRoomUpdated);


        }

    }

    @Override
    public void onSymUserJoinedRoom(SymUserJoinedRoom symUserJoinedRoom) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {
            for (RoomEventListener roomEventListener : entry.getValue().getRoomEventListeners())
                roomEventListener.onSymUserJoinedRoom(symUserJoinedRoom);


        }

    }

    @Override
    public void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom) {

        for (Map.Entry<String, Room> entry : roomsByStream.entrySet()) {

            for (RoomEventListener roomEventListener : entry.getValue().getRoomEventListeners())
                roomEventListener.onSymUserLeftRoom(symUserLeftRoom);


        }

    }

    @Override
    public void onSymRoomCreated(SymRoomCreated symRoomCreated) {

        for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners) {
            roomServiceEventListener.onSymRoomCreated(symRoomCreated);

        }

    }


    /**
     * Add a room to the service
     *
     * @param streamId Stream ID of the room
     * @throws RoomException Exception generated by underlying Symphony API's
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
     *
     * @param room Room object published from callback
     */
    @Override
    public void onNewRoom(Room room) {


        for (RoomServiceEventListener roomServiceEventListener : roomServiceEventListeners)
            roomServiceEventListener.onNewRoom(room);
    }



    public void addRoomServiceEventListener(RoomServiceEventListener roomServiceEventListener) {
        roomServiceEventListeners.add(roomServiceEventListener);
    }


    public void removeRoomServiceEventListener(RoomServiceEventListener roomServiceEventListener) {
        roomServiceEventListeners.remove(roomServiceEventListener);
    }
}
