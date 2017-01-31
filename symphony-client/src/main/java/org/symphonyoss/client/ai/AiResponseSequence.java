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

import java.util.HashSet;
import java.util.Set;

/**
 * A model that represents a sequence of responses from the ai
 *
 * @author Nicholas Tarsillo
 */
public class AiResponseSequence {
    private Set<AiResponse> aiResponseSet = new HashSet<>();

    public AiResponseSequence() {

    }

    /**
     * Add a response to the sequence
     *
     * @param response {@link AiResponse} to add to sequence
     */
    public void addResponse(AiResponse response) {
        aiResponseSet.add(response);
    }

    /**
     * Remove response from sequence
     *
     * @param response {@link AiResponse} to remove from sequence
     */
    @SuppressWarnings("unused")
    public void removeResponse(AiResponse response) {
        aiResponseSet.remove(response);
    }

    /**
     * Retrieve all the responses set in the sequence
     *
     * @return Set of AI responses
     */
    public Set<AiResponse> getAiResponseSet() {
        return aiResponseSet;
    }

    /**
     * Set the entire sequence of responses
     *
     * @param aiResponseSet Responses to set
     */
    @SuppressWarnings("unused")
    public void setAiResponseSet(Set<AiResponse> aiResponseSet) {
        this.aiResponseSet = aiResponseSet;
    }
}
