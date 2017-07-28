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
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.RoomException;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.RoomListener;
import org.symphonyoss.client.services.RoomService;
import org.symphonyoss.client.services.RoomServiceListener;
import org.symphonyoss.symphony.agent.model.*;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;


/**
 * Simple example to create a chat room
 * <p>
 * <p>
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
public class CreateRoomExample implements RoomServiceListener, RoomListener {


    private final Logger logger = LoggerFactory.getLogger(CreateRoomExample.class);
    private RoomService roomService;

    CreateRoomExample() {


        init();


    }

    public static void main(String[] args) {

        new CreateRoomExample();

    }

    public void init() {

        logger.info("Room Example starting...");

        try {

            logger.debug("{} {}", System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            //Create an initialized client
            SymphonyClient symClient = SymphonyClientFactory.getClient(
                    SymphonyClientFactory.TYPE.BASIC,
                    System.getProperty("bot.user") + System.getProperty("bot.domain"), //bot email
                    System.getProperty("certs.dir") + System.getProperty("bot.user") + ".p12", //bot cert
                    System.getProperty("keystore.password"), //bot cert/keystore pass
                    System.getProperty("truststore.file"), //truststore file
                    System.getProperty("truststore.password"));  //truststore password


            //A message to send when the BOT comes online.
            SymMessage aMessage = new SymMessage();
            aMessage.setFormat(SymMessage.Format.TEXT);
            aMessage.setMessage("Hello master, I'm alive again in this room....");


            //Define the room to create
            SymRoomAttributes roomAttributes = new SymRoomAttributes();
            roomAttributes.setName("TEST ROOM 1");
            roomAttributes.setDescription("SJC Test room creation");
            roomAttributes.setDiscoverable(true);
            roomAttributes.setPublic(true);

            //Create the room
            Room room = symClient.getRoomService().createRoom(roomAttributes);


            //Add the listener
            symClient.getRoomService().addRoomServiceListener(this);


            //Register the room to the service
            symClient.getRoomService().joinRoom(room);


            //Send a message to the room.
            symClient.getMessageService().sendMessage(room, aMessage);


        } catch (RoomException | MessagesException e) {
            logger.error("error", e);
        }

    }


    //Chat sessions callback method.
    public void onChatMessage(Message message) {
        if (message == null)
            return;

        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                message.getMessage(),
                message.getMessageType());


    }


    @Override
    public void onRoomMessage(SymMessage roomMessage) {

        Room room = roomService.getRoom(roomMessage.getStreamId());

        if (room != null && roomMessage.getMessage() != null)
            logger.debug("New room message detected from room: {} on stream: {} from: {} message: {}",
                    room.getRoomDetail().getRoomAttributes().getName(),
                    roomMessage.getStreamId(),
                    roomMessage.getFromUserId(),
                    roomMessage.getMessage()

            );


    }

    @Override
    public void onRoomCreatedMessage(RoomCreatedMessage roomCreatedMessage) {

    }

    @Override
    public void onMessage(SymMessage symMessage) {

    }

    @Override
    public void onNewRoom(Room room) {
        logger.info("Created new room instance from incoming message..{} {}", room.getId(), room.getRoomDetail().getRoomAttributes().getName());
        room.addListener(this);
    }

    @Override
    public void onRoomDeactivatedMessage(RoomDeactivatedMessage roomDeactivatedMessage) {

    }

    @Override
    public void onRoomMemberDemotedFromOwnerMessage(RoomMemberDemotedFromOwnerMessage roomMemberDemotedFromOwnerMessage) {

    }

    @Override
    public void onRoomMemberPromotedToOwnerMessage(RoomMemberPromotedToOwnerMessage roomMemberPromotedToOwnerMessage) {

    }

    @Override
    public void onRoomReactivatedMessage(RoomReactivatedMessage roomReactivatedMessage) {

    }

    @Override
    public void onRoomUpdatedMessage(RoomUpdatedMessage roomUpdatedMessage) {

    }

    @Override
    public void onUserJoinedRoomMessage(UserJoinedRoomMessage userJoinedRoomMessage) {

    }

    @Override
    public void onUserLeftRoomMessage(UserLeftRoomMessage userLeftRoomMessage) {

    }
}
