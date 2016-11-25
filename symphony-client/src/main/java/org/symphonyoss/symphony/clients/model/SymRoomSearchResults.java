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

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.symphony.pod.model.RoomDetail;
import org.symphonyoss.symphony.pod.model.RoomSearchCriteria;
import org.symphonyoss.symphony.pod.model.RoomSearchResults;
import org.symphonyoss.symphony.pod.model.V2RoomDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by frank on 11/25/16.
 */
public class SymRoomSearchResults {
    private Long count = null;

    private Integer skip = null;

    private Integer limit = null;

    private RoomSearchCriteria query = null;

    private List<SymRoomDetail> rooms = new ArrayList<SymRoomDetail>();

    private List<SymFacetedMatchCount> facetedMatchCount = new ArrayList<SymFacetedMatchCount>();


    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public RoomSearchCriteria getQuery() {
        return query;
    }

    public void setQuery(RoomSearchCriteria query) {
        this.query = query;
    }

    public List<SymRoomDetail> getRooms() {
        return rooms;
    }

    public void setRooms(List<SymRoomDetail> rooms) {
        this.rooms = rooms;
    }

    public List<SymFacetedMatchCount> getFacetedMatchCount() {
        return facetedMatchCount;
    }

    public void setFacetedMatchCount(List<SymFacetedMatchCount> facetedMatchCount) {
        this.facetedMatchCount = facetedMatchCount;

    }

    //Convert from base models
    public static SymRoomSearchResults toSymRoomSearchResults(RoomSearchResults roomSearchResults) {

        SymRoomSearchResults symRoomSearchResults = new SymRoomSearchResults();
        symRoomSearchResults.setCount(roomSearchResults.getCount());
        symRoomSearchResults.setFacetedMatchCount(roomSearchResults.getFacetedMatchCount().stream().map(SymFacetedMatchCount::toSymFacetedMatchCount).collect(Collectors.toList()));
        symRoomSearchResults.setLimit(roomSearchResults.getLimit());
        symRoomSearchResults.setQuery(roomSearchResults.getQuery());
        symRoomSearchResults.setRooms(roomSearchResults.getRooms().stream().map(SymRoomDetail::toSymRoomDetail).collect(Collectors.toList()));
        symRoomSearchResults.setSkip(roomSearchResults.getSkip());

        return symRoomSearchResults;

    }


}
