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


import org.symphonyoss.symphony.pod.model.AdminStreamFilter;
import org.symphonyoss.symphony.pod.model.AdminStreamTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Frank Tarsillo
 */
public class SymAdminStreamFilter {

    private List<SymStreamType> streamTypes = new ArrayList<>();



    public enum Scope {
        INTERNAL("INTERNAL"),

        EXTERNAL("EXTERNAL");

        private String value;

        Scope(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }


        public static Scope fromValue(String text) {
            for (Scope b : Scope.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    private Scope scope = null;

    /**
     * Origin of the room. It indicate whether the room was created by a user within the company by another company.  If not specified, it will include both Internal and External origin
     */
    public enum Origin {
        INTERNAL("INTERNAL"),

        EXTERNAL("EXTERNAL");

        private String value;

        Origin(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }


        public static Origin fromValue(String text) {
            for (Origin b : Origin.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    private Origin origin = null;

    /**
     * Status of the room. If not specified, it will include both Active and Inactive status
     */
    public enum Status {
        ACTIVE("ACTIVE"),

        INACTIVE("INACTIVE");

        private String value;

        Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }


        public static Status fromValue(String text) {
            for (Status b : Status.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    private Status status = null;

    /**
     * Privacy setting of the stream.  If not specified, it will include both public and private stream
     */
    public enum Privacy {
        PUBLIC("PUBLIC"),

        PRIVATE("PRIVATE");

        private String value;

        Privacy(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }


        public static Privacy fromValue(String text) {
            for (Privacy b : Privacy.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    private Privacy privacy = null;


    private Long startDate = null;


    private Long endDate = null;

    public SymAdminStreamFilter streamTypes(List<SymStreamType> streamTypes) {
        this.streamTypes = streamTypes;
        return this;
    }

    public SymAdminStreamFilter addStreamTypesItem(SymStreamType streamTypesItem) {
        this.streamTypes.add(streamTypesItem);
        return this;
    }


    public List<SymStreamType> getStreamTypes() {
        return streamTypes;
    }

    public void setStreamTypes(List<SymStreamType> streamTypes) {
        this.streamTypes = streamTypes;
    }

    public SymAdminStreamFilter scope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public SymAdminStreamFilter origin(Origin origin) {
        this.origin = origin;
        return this;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public SymAdminStreamFilter status(Status status) {
        this.status = status;
        return this;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SymAdminStreamFilter privacy(Privacy privacy) {
        this.privacy = privacy;
        return this;
    }


    public Privacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Privacy privacy) {
        this.privacy = privacy;
    }

    public SymAdminStreamFilter startDate(Long startDate) {
        this.startDate = startDate;
        return this;
    }


    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public SymAdminStreamFilter endDate(Long endDate) {
        this.endDate = endDate;
        return this;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SymAdminStreamFilter adminStreamFilter = (SymAdminStreamFilter) o;
        return Objects.equals(this.streamTypes, adminStreamFilter.streamTypes) &&
                Objects.equals(this.scope, adminStreamFilter.scope) &&
                Objects.equals(this.origin, adminStreamFilter.origin) &&
                Objects.equals(this.status, adminStreamFilter.status) &&
                Objects.equals(this.privacy, adminStreamFilter.privacy) &&
                Objects.equals(this.startDate, adminStreamFilter.startDate) &&
                Objects.equals(this.endDate, adminStreamFilter.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streamTypes, scope, origin, status, privacy, startDate, endDate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SymAdminStreamFilter {\n");

        sb.append("    streamTypes: ").append(toIndentedString(streamTypes)).append("\n");
        sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
        sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    privacy: ").append(toIndentedString(privacy)).append("\n");
        sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
        sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public static AdminStreamFilter toAdminStreamFilter(SymAdminStreamFilter symAdminStreamFilter) {

        if(symAdminStreamFilter == null)
            return null;

        AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

        adminStreamFilter.setEndDate(symAdminStreamFilter.getEndDate());

        if (symAdminStreamFilter.getOrigin() != null)
            adminStreamFilter.setOrigin(AdminStreamFilter.OriginEnum.fromValue(symAdminStreamFilter.getOrigin().toString()));

        if (symAdminStreamFilter.getPrivacy() != null)
            adminStreamFilter.setPrivacy(AdminStreamFilter.PrivacyEnum.fromValue(symAdminStreamFilter.getPrivacy().toString()));

        if (symAdminStreamFilter.getScope() != null)
            adminStreamFilter.setScope(AdminStreamFilter.ScopeEnum.fromValue(symAdminStreamFilter.getScope().toString()));

        if (symAdminStreamFilter.getStatus() != null)
            adminStreamFilter.setStatus(AdminStreamFilter.StatusEnum.fromValue(symAdminStreamFilter.getStatus().toString()));

        if (symAdminStreamFilter.getStreamTypes() != null) {

            List<AdminStreamTypeEnum> adminStreamTypeEnums = new ArrayList<>();

            for (SymStreamType symStreamType : symAdminStreamFilter.getStreamTypes()) {

                AdminStreamTypeEnum adminStreamTypeEnum = new AdminStreamTypeEnum();
                adminStreamTypeEnum.setType(AdminStreamTypeEnum.TypeEnum.fromValue(symStreamType.getType().toString()));
                adminStreamTypeEnums.add(adminStreamTypeEnum);


            }
            adminStreamFilter.setStreamTypes(adminStreamTypeEnums);
        }


        return adminStreamFilter;

    }

    public static SymAdminStreamFilter toSymStreamFilter(AdminStreamFilter filter) {

        SymAdminStreamFilter symAdminStreamFilter = new SymAdminStreamFilter();

        symAdminStreamFilter.setEndDate(filter.getEndDate());


        if (filter.getOrigin() != null)
            symAdminStreamFilter.setOrigin(SymAdminStreamFilter.Origin.fromValue(filter.getOrigin().toString()));

        if (filter.getPrivacy() != null)
            symAdminStreamFilter.setPrivacy(SymAdminStreamFilter.Privacy.fromValue(filter.getPrivacy().toString()));

        if (filter.getScope() != null)
            symAdminStreamFilter.setScope(SymAdminStreamFilter.Scope.fromValue(filter.getScope().toString()));

        if (filter.getStatus() != null)
            symAdminStreamFilter.setStatus(SymAdminStreamFilter.Status.fromValue(filter.getStatus().toString()));

        if (filter.getStreamTypes() != null) {

            List<SymStreamType> symStreamTypes = new ArrayList<>();

            for (AdminStreamTypeEnum adminStreamTypeEnum : filter.getStreamTypes()) {

                SymStreamType symStreamType = new SymStreamType();
                symStreamType.setType(SymStreamType.Type.fromValue(adminStreamTypeEnum.getType().toString()));
                symStreamTypes.add(symStreamType);


            }
            symAdminStreamFilter.setStreamTypes(symStreamTypes);
        }

        return symAdminStreamFilter;



    }



}

