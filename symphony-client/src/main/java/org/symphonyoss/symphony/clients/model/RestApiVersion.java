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

package org.symphonyoss.symphony.clients.model;

/**
 * @author Frank Tarsillo on 11/17/17.
 */

public enum RestApiVersion {

    v1_46_0(14600),
    v1_47_0(14700),
    v1_48_0(14800);




    private Integer apiVersion;

    RestApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public  boolean isCompatible( String actual) {
        int actualValue;

        try{
            actualValue = Integer.valueOf(actual.replace(".",""));

        }catch(NumberFormatException e){
            return false;
        }

        return actualValue >= this.apiVersion;


    }



}


