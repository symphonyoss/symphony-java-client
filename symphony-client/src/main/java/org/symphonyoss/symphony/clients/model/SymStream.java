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

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.symphony.agent.model.V4Stream;
import org.symphonyoss.symphony.pod.model.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Frank Tarsillo on 6/26/17.
 */
public class SymStream {


    private String streamId = null;


    private SymStreamTypes.Type streamType = null;


    private String roomName = null;


    private List<SymUser> members = new ArrayList<>();


    private Boolean external = null;


    public String getStreamId() {
        return streamId;
    }


    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public SymStreamTypes.Type getStreamType() {
        return streamType;
    }

    public void setStreamType(SymStreamTypes.Type streamType) {
        this.streamType = streamType;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<SymUser> getMembers() {
        return members;
    }

    public void setMembers(List<SymUser> members) {
        this.members = members;
    }

    /**
     * @deprecated  Replaced by {{@link #isExternal()}}
     * @return boolean external
     */
    public Boolean getExternal() {
        return external;
    }


    public Boolean isExternal() { return external; }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public static SymStream toSymStream(V4Stream stream) {

        if (stream == null)
            return null;


        SymStream symStream = new SymStream();

        symStream.setExternal(stream.isExternal());


        if(stream.getMembers() != null)
        symStream.setMembers(stream.getMembers().stream().map(SymUser::toSymUser).collect(Collectors.toList()));

        if (stream.getRoomName() != null)
            symStream.setRoomName(stream.getRoomName());

        symStream.setStreamId(stream.getStreamId());

        symStream.setStreamType(SymStreamTypes.Type.fromValue(stream.getStreamType()));

        return symStream;
    }

    public static Stream toSymStream(SymStream symStream) {


        if (symStream == null)
            return null;


        Stream stream = new Stream();

        stream.setId(symStream.getStreamId());


        return stream;

    }

    public static SymStream toSymStream(Stream stream) {

        if (stream == null)
            return null;


        SymStream symStream = new SymStream();


        symStream.setStreamId(stream.getId());


        return symStream;
    }
}
