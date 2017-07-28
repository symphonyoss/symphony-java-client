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


import org.symphonyoss.symphony.agent.model.V4InstantMessageCreated;
import org.symphonyoss.symphony.clients.model.SymStream;

/**
 * @author Frank Tarsillo on 6/26/17.
 */
public class SymIMCreated {
    private SymStream stream = null;

    public SymStream getStream() {

        return stream;
    }

    public void setStream(SymStream stream) {
        this.stream = stream;
    }

    public static SymIMCreated toSymIMCreated(V4InstantMessageCreated instantMessageCreated) {

        if(instantMessageCreated == null)
            return null;

        SymIMCreated symIMCreated = new SymIMCreated();

        if(instantMessageCreated.getStream() != null)
            symIMCreated.setStream(SymStream.toSymStream(instantMessageCreated.getStream()));

        return symIMCreated;
    }
}
