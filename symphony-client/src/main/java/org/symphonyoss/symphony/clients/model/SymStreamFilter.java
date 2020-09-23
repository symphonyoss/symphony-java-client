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


import org.symphonyoss.symphony.pod.model.StreamFilter;
import org.symphonyoss.symphony.pod.model.StreamType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 *
 * @author Frank Tarsillo
 */
public class SymStreamFilter {

    private List<SymStreamTypes> streamTypes = new ArrayList<>();


    private Boolean includeInactiveStreams = null;

    public List<SymStreamTypes> getStreamTypes() {
        return streamTypes;
    }

    public void setStreamTypes(List<SymStreamTypes> streamTypes) {
        this.streamTypes = streamTypes;
    }

    public Boolean getIncludeInactiveStreams() {
        return includeInactiveStreams;
    }

    public void setIncludeInactiveStreams(Boolean includeInactiveStreams) {
        this.includeInactiveStreams = includeInactiveStreams;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SymStreamFilter streamFilter = (SymStreamFilter) o;
        return Objects.equals(this.streamTypes, streamFilter.streamTypes) &&
                Objects.equals(this.includeInactiveStreams, streamFilter.includeInactiveStreams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streamTypes, includeInactiveStreams);
    }


    public static StreamFilter toStreamFilter(SymStreamFilter symStreamFilter) {

        if(symStreamFilter == null)
            return null;

        StreamFilter streamFilter = new StreamFilter();

        streamFilter.setIncludeInactiveStreams(symStreamFilter.getIncludeInactiveStreams());

        if (symStreamFilter.getStreamTypes() != null) {

            List<StreamType> streamTypes = new ArrayList<>();

            for (SymStreamTypes symStreamTypes : symStreamFilter.getStreamTypes()) {

                StreamType streamType = new StreamType();
                streamType.setType(StreamType.TypeEnum.fromValue(symStreamTypes.getType().toString()));
                streamTypes.add(streamType);


            }
            streamFilter.setStreamTypes(streamTypes);
        }


        return streamFilter;

    }

    public static SymStreamFilter toSymStreamFilter(StreamFilter streamFilter) {

        if(streamFilter == null)
            return null;

        SymStreamFilter symStreamFilter = new SymStreamFilter();

        symStreamFilter.setIncludeInactiveStreams(streamFilter.isIncludeInactiveStreams());

        if (streamFilter.getStreamTypes() != null) {

            List<SymStreamTypes> symStreamTypes = new ArrayList<>();

            for (StreamType streamType : streamFilter.getStreamTypes()) {

                SymStreamTypes symStreamType = new SymStreamTypes();
                symStreamType.setType(SymStreamTypes.Type.fromValue(streamType.getType().toString()));
                symStreamTypes.add(symStreamType);


            }
            symStreamFilter.setStreamTypes(symStreamTypes);
        }


        return symStreamFilter;



    }



}

