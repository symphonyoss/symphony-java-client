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


import org.symphonyoss.symphony.agent.model.V4SharedPost;

/**
 * @author Frank Tarsillo on 6/26/17.
 */
public class SymSharedPost {


    private SymMessage message = null;


    private SymMessage sharedMessage = null;

    public SymMessage getMessage() {
        return message;
    }

    public void setMessage(SymMessage message) {
        this.message = message;
    }

    public SymMessage getSharedMessage() {
        return sharedMessage;
    }

    public void setSharedMessage(SymMessage sharedMessage) {
        this.sharedMessage = sharedMessage;
    }

    public static SymSharedPost toSymSharedPost(V4SharedPost sharedPost) {

        if(sharedPost == null)
            return null;

        SymSharedPost symSharedPost = new SymSharedPost();

        symSharedPost.setMessage(SymMessage.toSymMessage(sharedPost.getMessage()));
        symSharedPost.setSharedMessage(SymMessage.toSymMessage(sharedPost.getSharedMessage()));

        return symSharedPost;
    }
}
