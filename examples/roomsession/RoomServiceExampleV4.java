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

package roomsession;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.events.*;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.RoomEventListener;
import org.symphonyoss.client.services.RoomService;
import org.symphonyoss.client.services.RoomServiceEventListener;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;


/**
 * Simple example of the RoomService.
 * <p>
 * It will send a message to a room through from a stream (property: room.stream)
 * This will create a Room object, which is populated with all room attributes and
 * membership.  Adding a listener, will provide callbacks.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dsessionauth.url=https://pod_fqdn:port/sessionauth
 * -Dkeyauth.url=https://pod_fqdn:port/keyauth
 * -Dsymphony.agent.pod.url=https://agent_fqdn:port/pod
 * -Dsymphony.agent.agent.url=https://agent_fqdn:port/agent
 * -Dcerts.dir=/dev/certs/
 * -Dkeystore.password=(Pass)
 * -Dtruststore.file=/dev/certs/server.truststore
 * -Dtruststore.password=(Pass)
 * -Dbot.user=bot.user1
 * -Dbot.domain=@domain.com
 * -Duser.call.home=frank.tarsillo@markit.com
 * -Droom.stream=(Stream)
 *
 * @author Frank Tarsillo
 */
//NOSONAR
public class RoomServiceExampleV4 implements RoomServiceEventListener, RoomEventListener {


    private final Logger logger = LoggerFactory.getLogger(RoomServiceExampleV4.class);
    private RoomService roomService;

    RoomServiceExampleV4() {


        init();


    }

    public static void main(String[] args) {

        new RoomServiceExampleV4();

    }

    public void init() {

        logger.info("Room Example starting...");

        try {


            SymphonyClientConfig symphonyClientConfig = new SymphonyClientConfig();

            //Create an initialized client
            SymphonyClient symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.V4, symphonyClientConfig);


            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();
            aMessage.setMessage("<messageML>Hello master, I'm alive again in this room....</messageML>");


            Stream stream = new Stream();
            stream.setId(System.getProperty("room.stream"));


            roomService = symClient.getRoomService();
            roomService.addRoomServiceEventListener(this);

            Room room = new Room();
            room.setStream(stream);
            room.setId(stream.getId());
            room.addEventListener(this);

            symClient.getRoomService().joinRoom(room);


            //Send a message to the room.
            symClient.getMessageService().sendMessage(room, aMessage);


        } catch (RoomException | MessagesException e) {
            logger.error("error", e);
        }

    }


    @Override
    public void onRoomMessage(SymMessage roomMessage) {

        Room room = roomService.getRoom(roomMessage.getStreamId());

        if (room != null && roomMessage.getMessage() != null)
            logger.info("New room message detected from room: {} on stream: {} from: {} message: {}",
                    room.getRoomDetail().getRoomAttributes().getName(),
                    roomMessage.getStreamId(),
                    roomMessage.getFromUserId(),
                    roomMessage.getMessage()

            );


    }


    @Override
    public void onMessage(SymMessage symMessage) {
        logger.info("Message detected from stream: {} from: {} message: {}",
                symMessage.getStreamId(),
                symMessage.getFromUserId(),
                symMessage.getMessage());
    }

    @Override
    public void onSymRoomDeactivated(SymRoomDeactivated symRoomDeactivated) {

        logger.info("Room Deactivated  stream: {} room: {}",
                symRoomDeactivated.getStream().getStreamId(),
                symRoomDeactivated.getStream().getRoomName());

    }

    @Override
    public void onSymRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner) {

        logger.info("Room Member Demoted from Owner stream: {}, room: {}, user: {}:{}",
                symRoomMemberDemotedFromOwner.getStream().getStreamId(),
                symRoomMemberDemotedFromOwner.getStream().getRoomName(),
                symRoomMemberDemotedFromOwner.getAffectedUser().getId(),
                symRoomMemberDemotedFromOwner.getAffectedUser().getDisplayName());

    }

    @Override
    public void onSymRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner) {

        logger.info("Room Member Promoted to Owner stream: {}, room: {}, user: {}:{}",
                symRoomMemberPromotedToOwner.getStream().getStreamId(),
                symRoomMemberPromotedToOwner.getStream().getRoomName(),
                symRoomMemberPromotedToOwner.getAffectedUser().getId(),
                symRoomMemberPromotedToOwner.getAffectedUser().getDisplayName());

    }

    @Override
    public void onSymRoomReactivated(SymRoomReactivated symRoomReactivated) {

        logger.info("Room reactivated stream: {}, room: {}",
                symRoomReactivated.getStream().getStreamId(),
                symRoomReactivated.getStream().getRoomName());
    }

    @Override
    public void onSymRoomUpdated(SymRoomUpdated symRoomUpdated) {

        logger.info("Room updated stream: {}, room: {}, description: {}",
                symRoomUpdated.getStream().getStreamId(),
                symRoomUpdated.getStream().getRoomName(),
                symRoomUpdated.getNewRoomAttributes().getDescription());

    }

    @Override
    public void onSymUserJoinedRoom(SymUserJoinedRoom symUserJoinedRoom) {

        logger.info("User Joined Room: stream {}, room: {}, user: {}:{}",
                symUserJoinedRoom.getStream().getStreamId(),
                symUserJoinedRoom.getStream().getRoomName(),
                symUserJoinedRoom.getAffectedUser().getId(),
                symUserJoinedRoom.getAffectedUser().getDisplayName());

    }

    @Override
    public void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom) {
        logger.info("User Left Room: stream {}, room: {}, user: {}:{}",
                symUserLeftRoom.getStream().getStreamId(),
                symUserLeftRoom.getStream().getRoomName(),
                symUserLeftRoom.getAffectedUser().getId(),
                symUserLeftRoom.getAffectedUser().getDisplayName());

    }

    @Override
    public void onSymRoomCreated(SymRoomCreated symRoomCreated) {

    }

    @Override
    public void onNewRoom(Room room) {
        logger.info("Created new room instance from incoming message..{} {}", room.getId(), room.getRoomDetail().getRoomAttributes().getName());
        room.addEventListener(this);
    }


}
