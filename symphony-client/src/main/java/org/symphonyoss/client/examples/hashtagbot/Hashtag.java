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

package org.symphonyoss.client.examples.hashtagbot;

import org.symphonyoss.client.model.NodeTypes;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;


/**
 * Created by Frank Tarsillo on 5/22/2016.
 */
@XmlRootElement
public class Hashtag extends NodeObject {

    private long lastChange;
    private ArrayList<HashtagDef> definitions;

    public Hashtag(){
        setType(NodeTypes.CASHTAG);
        setName(NodeTypes.CASHTAG.toString());
    }

    public Hashtag(String name){
        this();
        setName(name);
    }


    public long getLastChange() {
        return lastChange;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    public ArrayList<HashtagDef> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(ArrayList<HashtagDef> definitions) {
        this.definitions = definitions;
    }

    public String getMlString(){

        if(getName()==null)
            return null;

        return "<hash tag=\"" + getName() + "\"/> ";
    }

    public String getTextString(){
        if(getName()==null)
            return null;

        return "#" + getName();

    }
}
