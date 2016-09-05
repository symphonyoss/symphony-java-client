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

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.agent.model.MessageList;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frank Tarsillo on 7/8/2016.
 */
public class RoomWorker implements Runnable {

    private Room room;
    private SymphonyClient symClient;
    private int MESSAGE_OVERLAP_WINDOW = 2000;
    private int ROOM_POLL_INTERVAL = 2000;
    private boolean KILL = false;

    private final Logger logger = LoggerFactory.getLogger(RoomWorker.class);


    public RoomWorker(SymphonyClient symClient, Room room) {
        this.symClient = symClient;
        this.room = room;

    }

    @Override
    public void run() {


        // Start polling from 5 seconds back.
        long now = System.currentTimeMillis() - MESSAGE_OVERLAP_WINDOW;
        HashSet<String> ids = new HashSet<>();

        while (true) {


            if (KILL)
                break;


            List<SymMessage> msgs;
            try {
                msgs = symClient.getMessagesClient().getMessagesFromStream(room.getStream(), now, 0, 1000);
            } catch (Exception e) {

                logger.error("Failed to get messages from stream", e);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;

            }

            long lastReceivedTS = 0;
            if (msgs == null) {
                continue;
            }

            for (SymMessage message : msgs) {

                //Ignore all messages sent by running user.
                if(symClient.getLocalUser().getId().equals( message.getFromUserId()))
                    continue;


                // We retain a list of previously processed message
                // ID's because
                // it is possible for messages to be come available
                // on the query out of sequence.
                String msgid = message.getId();
                if (ids.contains(msgid)) {
                    // We have previously processed this message,
                    // skip it.
                    continue;
                }
                ids.add(msgid);
                logger.debug("Received new message for {} {} {}", room.getId(), msgid, message.getMessage());
                long l = Long.parseLong(message.getTimestamp());
                lastReceivedTS = l;

                now = lastReceivedTS - MESSAGE_OVERLAP_WINDOW; // Overlap
                Long from = message.getFromUserId();

                RoomMessage roomMessage = new RoomMessage();
                roomMessage.setId(room.getId());
                roomMessage.setRoomStream(room.getStream());
                roomMessage.setRoomMessage(message);

                if (room.getRoomListener() != null)
                    room.getRoomListener().onRoomMessage(roomMessage);
            }


            try {
                TimeUnit.MILLISECONDS.sleep(ROOM_POLL_INTERVAL);
            } catch (InterruptedException e) {
                // carry on
            }


        }

    }


    public void shutdown() {

        KILL = true;
    }

    public Room getRoom(){return room;}
}
