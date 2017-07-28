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
 *
 * A set of enums, representing ML strings, that make coding
 * ML types a lot easier.
 *
 * @author Nicholas Tarsillo
 */
public enum MLTypes {
    START_ML("<messageML>"),
    END_ML("</messageML>"),
    START_BOLD("<b>"),
    END_BOLD("</b>"),
    BREAK("<br/>"),
    START_PML("<div data-format=\"PresentationML\" data-version=\"2.0\">"),
    END_PML("</div>");


    private final String text;

    MLTypes(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
