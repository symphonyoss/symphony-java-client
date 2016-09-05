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

import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;

/**
 * Created by Frank Tarsillo on 7/8/2016.
 */
public class RoomMessage {

    Stream roomStream;
    String id;
    SymMessage message;

    public Stream getRoomStream() {
        return roomStream;
    }

    public void setRoomStream(Stream roomStream) {
        this.roomStream = roomStream;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SymMessage getRoomMessage() {
        return message;
    }

    public void setRoomMessage(SymMessage message) {
        this.message = message;
    }

    @Deprecated
    public Message getMessage() {
        return SymMessage.toV1Message(message);
    }

    @Deprecated
    public void setMessage(Message message) {
        this.message = SymMessage.toSymMessage(message);
    }
}
