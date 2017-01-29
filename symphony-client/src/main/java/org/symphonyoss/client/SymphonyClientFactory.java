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

package org.symphonyoss.client;

import org.symphonyoss.client.impl.SymphonyBasicClient;

/**
 * Supports the creation of SymphonyClient implementations.
 *
 * @author Frank Tarsillo
 */
public class SymphonyClientFactory {

    /**
     * Currently only one SymphonyClient implementation called BASIC
     */
    public enum TYPE {
        BASIC
    }

    /**
     * Generate a new SymphonyClient based on type
     *
     * @param type The type of SymphonyClient.  Currently only BASIC is available.
     * @return A SymphonyClient instance based on type
     */
    public static SymphonyClient getClient(TYPE type) {

        return new SymphonyBasicClient();

    }

}
