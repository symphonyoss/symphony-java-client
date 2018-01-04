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

import org.symphonyoss.symphony.pod.model.Presence;
import org.symphonyoss.symphony.pod.model.V2Presence;
import org.symphonyoss.symphony.pod.model.V2PresenceList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Frank Tarsillo on 12/2/17.
 */
public class SymPresence {

   // UNDEFINED   - AVAILABLE   - BUSY   - DO_NOT_DISTURB   - ON_THE_PHONE   - BE_RIGHT_BACK   - IN_A_MEETING   - AWAY   - OUT_OF_OFFICE   - OFF_WORK   - OFFLINE

    /**
     * Gets or Sets category
     */
    public enum Category {
        UNDEFINED("UNDEFINED"),

        AVAILABLE("AVAILABLE"),

        BUSY("BUSY"),

        DO_NOT_DISTURB("DO_NOT_DISTURB"),

        ON_THE_PHONE("ON_THE_PHONE"),

        BE_RIGHT_BACK("BE_RIGHT_BACK"),

        AWAY("AWAY"),

        OUT_OF_OFFICE("OUT_OF_OFFICE"),

        IN_A_MEETING("IN_A_MEETING"),

        OFF_WORK("OFF_WORK"),


        OFFLINE("OFFLINE");



        private String value;

        Category(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }


        public static Category fromValue(String text) {
            for (Category b : Category.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }




    private Long userId = null;


    private Long timestamp = null;


    private Category category = null;

    public Category state(Category category) {
        this.category = category;
        return this.category;
    }

    /**
     * Get category
     * @return category
     **/
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SymPresence symPresence = (SymPresence) o;
        return Objects.equals(this.category, symPresence.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SymPresence {\n");

        sb.append("    category: ").append(toIndentedString(category)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Extract SymPresence from Presence
     * @param presence Presence from REST API
     * @return Converted SymPresence
     */
    public static SymPresence toSymPresence(Presence presence){
        if(presence==null)
            return null;

        SymPresence symPresence = new SymPresence();
        symPresence.setCategory(Category.fromValue(presence.getCategory().toString()));
        return symPresence;

    }




    /**
     * Extract SymPresence from Presence
     * @param presence Presence from REST API
     * @return Converted SymPresence
     */
    public static SymPresence toSymPresence(V2Presence presence){
        if(presence==null)
            return null;

        SymPresence symPresence = new SymPresence();
        symPresence.setCategory(Category.fromValue(presence.getCategory()));
        symPresence.setTimestamp(presence.getTimestamp());
        symPresence.setUserId(presence.getUserId());
        return symPresence;

    }



    /**
     * Extract SymPresence from Presence
     * @param presences Presence list from REST API
     * @return Converted SymPresence
     */
    public static List<SymPresence> toSymPresence(V2PresenceList presences){
        if(presences==null)
            return null;

        List<SymPresence> symPresences = new ArrayList<>();

        presences.forEach(v2Presence -> symPresences.add(SymPresence.toSymPresence(v2Presence)));

        return symPresences;

    }
}
