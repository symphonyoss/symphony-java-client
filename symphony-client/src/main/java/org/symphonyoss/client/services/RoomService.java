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
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.pod.model.RoomDetail;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Frank Tarsillo on 7/8/2016.
 */
public class RoomService {


    private final ConcurrentHashMap<String, RoomWorker> roomsByStream = new ConcurrentHashMap<String, RoomWorker>();


    private final SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(RoomService.class);


    public RoomService(SymphonyClient symClient) throws Exception {
        this.symClient = symClient;

    }


    public void joinRoom(Room room) throws Exception {

        room.setMembershipList(symClient.getRoomMembershipClient().getRoomMembership(room.getId()));
        room.setRoomDetail(symClient.getStreamsClient().getRoomDetail(room.getId()));

        RoomWorker roomWorker = new RoomWorker(symClient,room);
        new Thread(roomWorker).start();

        roomsByStream.put(room.getId(), roomWorker);



    }

    public void leaveRoom(Room room) {

        RoomWorker roomWorker = roomsByStream.get(room.getId());

        roomWorker.shutdown();

        roomsByStream.remove(room.getId());


    }

    public Room getRoom(String id){

        RoomWorker roomWorker = roomsByStream.get(id);

        return (roomWorker != null) ? roomWorker.getRoom(): null;
    }



}
