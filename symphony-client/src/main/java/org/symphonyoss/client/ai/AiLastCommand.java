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

import org.symphonyoss.client.util.MlMessageParser;

/**
 * Created by nicktarsillo on 6/15/16.
 * A model for saving the last command used by the ai.
 */
public class AiLastCommand {
    private MlMessageParser mlMessageParser;
    private AiCommand aiCommand;

    public AiLastCommand(MlMessageParser mlMessageParser, AiCommand aiCommand) {
        this.aiCommand = aiCommand;
        this.mlMessageParser = mlMessageParser;
    }

    public MlMessageParser getMlMessageParser() {
        return mlMessageParser;
    }

    public void setMlMessageParser(MlMessageParser mlMessageParser) {
        this.mlMessageParser = mlMessageParser;
    }

    public AiCommand getAiCommand() {
        return aiCommand;
    }

    public void setAiCommandImpl(AiCommand aiCommand) {
        this.aiCommand = aiCommand;
    }
}
