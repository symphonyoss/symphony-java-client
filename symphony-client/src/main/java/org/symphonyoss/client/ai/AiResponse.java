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

package org.symphonyoss.client.ai;

import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A model that represents a single response from the ai.
 *
 * @author Nicholas Tarsillo
 */
public class AiResponse {
    private String message;
    private List<SymUser> symUsers = new ArrayList<>();

    public AiResponse(String message, List<SymUser> userIdList) {
        this.message = message;
        this.symUsers = userIdList;
    }

    /**
     * @return the response message
     */
    public String getMessage() {
        return message;
    }


    /**
     * Set response message
     *
     * @param message Response message
     */
    @SuppressWarnings("unused")
    public void setMessage(String message) {
        this.message = message;
    }



    public List<SymUser> getSymUsers() {
        return symUsers;
    }


    @SuppressWarnings("unused")
    public void setSymUsers(List<SymUser> symUsers) {
        this.symUsers = symUsers;
    }
}
