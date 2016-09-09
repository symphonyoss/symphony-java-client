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

import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.*;

import java.util.Set;

/**
 * Created by frank.tarsillo on 6/6/2016.
 */
public interface StreamsClient {
    Stream getStream(SymUser user) throws Exception;

    Stream getStream(Set<SymUser> users) throws Exception;

    Stream getStream(UserIdList userIdList) throws Exception;

    Stream getStreamFromEmail(String email) throws Exception;

    RoomDetail getRoomDetail(String id) throws Exception;
}
