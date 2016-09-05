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

package org.symphonyoss.client.model;

import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.User;
import org.symphonyoss.client.services.ChatListener;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class Chat {
    private Set<User> remoteUsers;
    private User localUser;
    private Stream stream;
    private final Set<ChatListener> chatListeners = ConcurrentHashMap.newKeySet();

    private SymMessage lastMessage;


    public Set<User> getRemoteUsers() {
        return remoteUsers;
    }

    public void setRemoteUsers(Set<User> remoteUsers) {
        this.remoteUsers = remoteUsers;
    }

    public User getLocalUser() {
        return localUser;
    }

    public void setLocalUser(User localUser) {
        this.localUser = localUser;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public void onChatMessage(Message message){

        onChatMessage(SymMessage.toSymMessage(message));

    }

    public void onChatMessage(SymMessage message){

        lastMessage = message;

        for(ChatListener chatListener:chatListeners)
            chatListener.onChatMessage(message);

    }

    public  boolean registerListener(ChatListener chatListener){

        if(lastMessage !=null)
            chatListener.onChatMessage(lastMessage);

        return chatListeners.add(chatListener);

    }

    public boolean removeListener(ChatListener chatListener){
        return chatListeners.remove(chatListener);
    }

    public SymMessage getLastMessage() {

        return lastMessage;
    }


    public void setLastMessage(SymMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final Chat other = (Chat) obj;

        if (this.stream==null || !this.stream.equals(other.stream)) {
            return false;
        }
        return true;
    }

}
