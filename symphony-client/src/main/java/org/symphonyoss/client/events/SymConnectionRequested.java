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

package org.symphonyoss.client.events;

import org.symphonyoss.symphony.agent.model.V4ConnectionRequested;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * @author Frank Tarsillo on 6/26/17.
 */
public class SymConnectionRequested {

    private SymUser toUser = null;

    public SymUser getToUser() {
        return toUser;
    }

    public void setToUser(SymUser toUser) {
        this.toUser = toUser;
    }

    public static SymConnectionRequested toSymConnectionRequested(V4ConnectionRequested connectionRequested) {

        if (connectionRequested == null)
            return null;

        SymConnectionRequested symConnectionRequested = new SymConnectionRequested();

        if(connectionRequested.getToUser() !=null)
            symConnectionRequested.setToUser(SymUser.toSymUser(connectionRequested.getToUser()));

        return symConnectionRequested;
    }
}
