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

import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.client.services.ChatListener;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Chat is a core model that defines an active conversation between users.  It includes the ability to register
 * ChatListener callbacks, which are monitored by the ChatService. Once defined, make sure to add the chat model to the
 * ChatService in order to receive events when a listeners are defined.
 *
 * @author Frank Tarsillo
 */
public class Chat {
    private Set<SymUser> remoteUsers;
    private SymUser localUser;
    private Stream stream;
    private String streamId;


    private final Set<ChatListener> chatListeners = ConcurrentHashMap.newKeySet();

    private SymMessage lastMessage;


    /**
     * @return current stream ID for chat session
     */
    public String getStreamId() {

        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;

        if (stream == null)
            stream = new Stream();

        stream.setId(streamId);

    }

    public Set<SymUser> getRemoteUsers() {
        return remoteUsers;
    }

    public void setRemoteUsers(Set<SymUser> remoteUsers) {
        this.remoteUsers = remoteUsers;
    }

    public SymUser getLocalUser() {
        return localUser;
    }

    public void setLocalUser(SymUser localUser) {
        this.localUser = localUser;
    }

    public Stream getStream() {
        return stream;
    }


    public void setStream(Stream stream) {
        this.stream = stream;
        streamId = stream.getId();

    }


    /**
     * Push message to all registered listeners.
     * @param message
     */
    public void onChatMessage(SymMessage message) {

        lastMessage = message;

        for (ChatListener chatListener : chatListeners)
            chatListener.onChatMessage(message);

    }

    /**
     * Register Chat listeners. A chat can have more than one listener at any time.
     * @param chatListener
     * @return Success
     */
    public boolean registerListener(ChatListener chatListener) {

        if (lastMessage != null)
            chatListener.onChatMessage(lastMessage);

        return chatListeners.add(chatListener);

    }

    /**
     * Remove a specific listener
     * @param chatListener
     * @return
     */
    public boolean removeListener(ChatListener chatListener) {
        return chatListeners.remove(chatListener);
    }

    public SymMessage getLastMessage() {

        return lastMessage;
    }


    /**
     * Sometimes you want to have the last message...just in case!
     * @param lastMessage
     */
    public void setLastMessage(SymMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final Chat other = (Chat) obj;

        return !(this.stream == null || !this.stream.equals(other.stream));
    }

}
