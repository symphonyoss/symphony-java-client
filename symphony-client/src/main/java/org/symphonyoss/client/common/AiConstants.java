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

package org.symphonyoss.client.common;

/**
 * @author  Nicholas Tarsillo
 */
public class AiConstants {
    public static final char COMMAND = '/';
    public static final String RUN_LAST_COMMAND = "Run Last";

    public static final String SUGGEST = "Did you mean ";
    public static final String USE_SUGGESTION = "? (Type " + MLTypes.START_BOLD + AiConstants.COMMAND + "Run Last"
            + MLTypes.END_BOLD + " to run command)";
    public static final String NO_PERMISSION = "Sorry, you do not have permission to use that command.";
    public static final String NOT_INTERPRETABLE = " is not a command or wrong # of arguments.";
    public static final String USAGE = "Check the usage:";
    public static final double CORRECTFACTOR = 0.5;
}
