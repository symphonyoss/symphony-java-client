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

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.symphony.agent.model.V4KeyValuePair;
import org.symphonyoss.symphony.pod.model.RoomTag;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Frank Tarsillo
 */
@SuppressWarnings("WeakerAccess")
public class SymRoomTag {
    private String key = null;

    private String value = null;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public static SymRoomTag toSymRoomTag(RoomTag roomTag){
        SymRoomTag symRoomTag =  new SymRoomTag();

        symRoomTag.setKey(roomTag.getKey());
        symRoomTag.setValue(roomTag.getValue());

        return symRoomTag;
    }

    public static RoomTag toRoomTag(SymRoomTag symRoomTag){
        RoomTag roomTag =  new RoomTag();

        roomTag.setKey(symRoomTag.getKey());
        roomTag.setValue(symRoomTag.getValue());

        return roomTag;
    }

    public static SymRoomTag toSymRoomTag(V4KeyValuePair keyValuePair){


        SymRoomTag symRoomTag = new SymRoomTag();
        symRoomTag.setKey(keyValuePair.getKey());
        symRoomTag.setValue(keyValuePair.getValue());

        return symRoomTag;

    }

    public static List<SymRoomTag> toSymRoomTagsV2(List<RoomTag> roomTags){
        if(roomTags==null)
            return null;

        return roomTags.stream().map(SymRoomTag::toSymRoomTag).collect(Collectors.toList());

    }

    public static List<RoomTag> toRoomTags(List<SymRoomTag> roomTags){
        if(roomTags==null)
            return null;

        return roomTags.stream().map(SymRoomTag::toRoomTag).collect(Collectors.toList());

    }


    public static List<SymRoomTag> toSymRoomTags(List<V4KeyValuePair> v4KeyValuePairs){

        if(v4KeyValuePairs==null)
            return null;

        return v4KeyValuePairs.stream().map(SymRoomTag::toSymRoomTag).collect(Collectors.toList());


    }



}
