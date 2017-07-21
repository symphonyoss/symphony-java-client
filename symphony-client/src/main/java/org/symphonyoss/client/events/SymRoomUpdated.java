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

import org.symphonyoss.symphony.agent.model.V4RoomUpdated;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymStream;

/**
 * @author Frank Tarsillo on 6/26/17.
 */
public class SymRoomUpdated {


    private SymStream stream = null;


    private SymRoomAttributes roomAttributes = null;

    public SymStream getStream() {
        return stream;
    }

    public void setStream(SymStream stream) {
        this.stream = stream;
    }

    public SymRoomAttributes getNewRoomAttributes() {
        return roomAttributes;
    }

    public void setNewRoomAttributes(SymRoomAttributes roomAttributes) {
        this.roomAttributes = roomAttributes;
    }

    public static SymRoomUpdated toSymRoomUpdated(V4RoomUpdated roomUpdated) {

        if(roomUpdated==null)
            return null;

        SymRoomUpdated symRoomUpdated = new SymRoomUpdated();
        symRoomUpdated.setNewRoomAttributes(SymRoomAttributes.toSymRoomAttributes(roomUpdated.getNewRoomProperties()));
        symRoomUpdated.setStream(SymStream.toSymStream(roomUpdated.getStream()));

        return symRoomUpdated;

    }
}
