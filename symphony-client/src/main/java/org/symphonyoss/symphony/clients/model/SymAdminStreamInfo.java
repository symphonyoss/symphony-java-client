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


import org.symphonyoss.symphony.pod.model.AdminStreamInfo;
import org.symphonyoss.symphony.pod.model.AdminStreamInfoList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Frank Tarsillo
 */
public class SymAdminStreamInfo {

    private String id = null;


    private Boolean isExternal = null;


    private Boolean isActive = null;


    private Boolean isPublic = null;

    private String type = null;


    private SymAdminStreamAttributes attributes = null;

    public SymAdminStreamInfo id(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SymAdminStreamInfo isExternal(Boolean isExternal) {
        this.isExternal = isExternal;
        return this;
    }


    public Boolean getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    public SymAdminStreamInfo isActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }


    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public SymAdminStreamInfo isPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }


    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public SymAdminStreamInfo type(String type) {
        this.type = type;
        return this;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SymAdminStreamInfo attributes(SymAdminStreamAttributes attributes) {
        this.attributes = attributes;
        return this;
    }


    public SymAdminStreamAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(SymAdminStreamAttributes attributes) {
        this.attributes = attributes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SymAdminStreamInfo adminStreamInfo = (SymAdminStreamInfo) o;
        return Objects.equals(this.id, adminStreamInfo.id) &&
                Objects.equals(this.isExternal, adminStreamInfo.isExternal) &&
                Objects.equals(this.isActive, adminStreamInfo.isActive) &&
                Objects.equals(this.isPublic, adminStreamInfo.isPublic) &&
                Objects.equals(this.type, adminStreamInfo.type) &&
                Objects.equals(this.attributes, adminStreamInfo.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isExternal, isActive, isPublic, type, attributes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AdminStreamInfo {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    isExternal: ").append(toIndentedString(isExternal)).append("\n");
        sb.append("    isActive: ").append(toIndentedString(isActive)).append("\n");
        sb.append("    isPublic: ").append(toIndentedString(isPublic)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
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


    public static List<SymAdminStreamInfo> toStreamInfos(AdminStreamInfoList streams) {

        List<SymAdminStreamInfo> symAdminStreamInfos = new ArrayList<>();

        for (AdminStreamInfo adminStreamInfo : streams) {

            symAdminStreamInfos.add(SymAdminStreamInfo.toStreamInfo(adminStreamInfo));

        }

        return symAdminStreamInfos;

    }

    private static SymAdminStreamInfo toStreamInfo(AdminStreamInfo adminStreamInfo) {

        SymAdminStreamInfo symAdminStreamInfo = new SymAdminStreamInfo();
        symAdminStreamInfo.setId(adminStreamInfo.getId());
        symAdminStreamInfo.setIsActive(adminStreamInfo.isIsActive());
        symAdminStreamInfo.setIsExternal(adminStreamInfo.isIsExternal());
        symAdminStreamInfo.setIsPublic(adminStreamInfo.isIsPublic());
        symAdminStreamInfo.setType(adminStreamInfo.getType());
        symAdminStreamInfo.setAttributes(SymAdminStreamAttributes.toAdminStreamAttributes(adminStreamInfo.getAttributes()));
        return symAdminStreamInfo;
    }
}

