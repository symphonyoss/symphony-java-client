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

import java.util.Objects;

/**
 * @author Frank Tarsillo on 5/6/17.
 */
public class SymStreamType {
    /**
     * Gets or Sets type
     */
    public enum Type {
        IM("IM"),

        MIM("MIM"),

        ROOM("ROOM"),

        POST("POST");

        private String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }


        public static Type fromValue(String text) {
            for (Type b : Type.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    private Type type = null;

    public SymStreamType type(Type type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     **/

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SymStreamType streamType = (SymStreamType) o;
        return Objects.equals(this.type, streamType.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "class StreamType {\n" +
                "    type: " + toIndentedString(type) + "\n" +
                "}";


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
}
