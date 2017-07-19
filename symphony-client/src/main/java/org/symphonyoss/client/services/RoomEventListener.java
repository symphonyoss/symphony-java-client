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

package org.symphonyoss.client.services;


import org.symphonyoss.client.events.*;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Callback interface for room events
 *
 * @author  Frank Tarsillo
 */
public interface RoomEventListener {

    void onRoomMessage(SymMessage symMessage);

    void onSymRoomDeactivated(SymRoomDeactivated symRoomDeactivated);

    void onSymRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner);

    void onSymRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner);

    void onSymRoomReactivated(SymRoomReactivated symRoomReactivated);

    void onSymRoomUpdated(SymRoomUpdated symRoomUpdated);

    void onSymUserJoinedRoom(SymUserJoinedRoom symUserJoinedRoom);

    void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom);


}
