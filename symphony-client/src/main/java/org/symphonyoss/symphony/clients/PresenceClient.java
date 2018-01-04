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

package org.symphonyoss.symphony.clients;

import org.symphonyoss.client.exceptions.PresenceException;
import org.symphonyoss.symphony.clients.model.SymPresence;
import org.symphonyoss.symphony.clients.model.SymPresenceFeed;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Presence;

import java.util.List;


/**
 * Created by frank.tarsillo on 6/6/2016.
 */
public interface PresenceClient {


    SymPresence getUserPresence(Long userId, Boolean local) throws PresenceException;

    SymPresence getUserPresence(SymUser symUser, Boolean local) throws PresenceException;

    SymPresenceFeed createPresenceFeed() throws PresenceException;

    void removePresenceFeed(SymPresenceFeed symPresenceFeed) throws PresenceException;

    List<SymPresence> getPresenceFeedUpdates(SymPresenceFeed symPresenceFeed) throws PresenceException;


    SymPresence setUserPresence(SymPresence presence) throws PresenceException;
}
