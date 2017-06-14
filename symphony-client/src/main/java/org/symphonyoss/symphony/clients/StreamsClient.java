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

package org.symphonyoss.symphony.clients;

import org.symphonyoss.exceptions.StreamsException;
import org.symphonyoss.symphony.clients.model.*;
import org.symphonyoss.symphony.pod.model.*;

import java.util.List;
import java.util.Set;


/**
 * @author Frank Tarsillo
 */
@SuppressWarnings("unused")
public interface StreamsClient {
    Stream getStream(SymUser user) throws StreamsException;

    Stream getStream(Set<SymUser> users) throws StreamsException;

    Stream getStream(UserIdList userIdList) throws StreamsException;

    SymAdminStreamList getStreams(Integer skip, Integer limit, SymAdminStreamFilter symAdminStreamFilter) throws StreamsException;

    List<SymStreamAttributes> getStreams(Integer skip, Integer limit, SymStreamFilter symStreamFilter) throws StreamsException;

    Stream getStreamFromEmail(String email) throws StreamsException;

    SymRoomDetail getRoomDetail(String id) throws StreamsException;

    SymRoomDetail createChatRoom(SymRoomAttributes roomAttributes) throws StreamsException;

    SymRoomDetail updateChatRoom(String streamId, SymRoomAttributes roomAttributes) throws StreamsException;

    SymRoomSearchResults roomSearch(SymRoomSearchCriteria searchCriteria, Integer skip, Integer limit) throws StreamsException;

    SymStreamAttributes getStreamAttributes(String streamId) throws StreamsException;

    /**
     * Deactivates a room for a given roomId
     * @param roomId 
     *              the room to be deactivated
     * @throws StreamsException
     *              if the deactivation failed
     */
    void deactivateRoom(String roomId) throws StreamsException;
}
