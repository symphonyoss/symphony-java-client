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

import org.symphonyoss.symphony.pod.model.AdminStreamAttributes;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Frank Tarsillo on 5/6/17.
 */
public class SymAdminStreamAttributes {

    private String roomName = null;


    private String roomDescription = null;

    private List<Long> members = new ArrayList<Long>();

    private Long createdByUserId = null;


    private Long createdDate = null;

    private Long lastModifiedDate = null;


    private String originCompany = null;


    private Integer originCompanyId = null;


    private Integer membersCount = null;


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getOriginCompany() {
        return originCompany;
    }

    public void setOriginCompany(String originCompany) {
        this.originCompany = originCompany;
    }

    public Integer getOriginCompanyId() {
        return originCompanyId;
    }

    public void setOriginCompanyId(Integer originCompanyId) {
        this.originCompanyId = originCompanyId;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public static SymAdminStreamAttributes toAdminStreamAttributes(AdminStreamAttributes attributes) {

        SymAdminStreamAttributes symAdminStreamAttributes = new SymAdminStreamAttributes();

        symAdminStreamAttributes.setCreatedByUserId(attributes.getCreatedByUserId());
        symAdminStreamAttributes.setCreatedDate(attributes.getCreatedDate());
        symAdminStreamAttributes.setLastModifiedDate(attributes.getLastModifiedDate());
        symAdminStreamAttributes.setMembers(attributes.getMembers());
        symAdminStreamAttributes.setMembersCount(attributes.getMembersCount());
        symAdminStreamAttributes.setOriginCompany(attributes.getOriginCompany());
        symAdminStreamAttributes.setOriginCompanyId(attributes.getOriginCompanyId());
        symAdminStreamAttributes.setRoomDescription(attributes.getRoomDescription());
        symAdminStreamAttributes.setRoomName(attributes.getRoomName());

        return symAdminStreamAttributes;
    }

//
//
//    public static SymAdminStreamAttributes toAdminStreamAttributes(AdminStreamAttributes attributes) {
//
//        SymAdminStreamAttributes symAdminStreamAttributes = new SymAdminStreamAttributes();
//        symAdminStreamAttributes.setActive(attributes.getActive());
//        symAdminStreamAttributes.setCrossPod(a.getCrossPod());
//        symAdminStreamAttributes.setId(streamAttributes.getId());
//
//        SymRoomSpecificStreamAttributes symRoomSpecificStreamAttributes = new SymRoomSpecificStreamAttributes();
//
//        if (streamAttributes.getRoomAttributes() != null) {
//            symRoomSpecificStreamAttributes.setName(streamAttributes.getRoomAttributes().getName());
//            symAdminStreamAttributes.setSymRoomSpecificStreamAttributes(symRoomSpecificStreamAttributes);
//        }
//
//        SymStreamType symStreamType = new SymStreamType();
//        symStreamType.setType(SymStreamType.Type.fromValue(streamAttributes.getStreamType().getType().toString()));
//        symAdminStreamAttributes.setSymStreamType(symStreamType);
//
//        if (streamAttributes.getStreamAttributes() != null)
//            symAdminStreamAttributes.setSymChatSpecificStreamAttributes(SymChatSpecificStreamAttributes.toSymChatSpecificStreamAttributes(streamAttributes.getStreamAttributes()));
//
//
//        return symAdminStreamAttributes;
//    }
}
