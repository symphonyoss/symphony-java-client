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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.symphonyoss.symphony.pod.model.FacetedMatchCount;

/**
 * @author Frank Tarsillo
 */
public class SymFacetedMatchCount {

    private String facet = null;

    private Integer count = null;

    public SymFacetedMatchCount() {
    }

    public String getFacet() {
        return facet;
    }

    public void setFacet(String facet) {
        this.facet = facet;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }


    public static SymFacetedMatchCount toSymFacetedMatchCount(FacetedMatchCount facetedMatchCount) {

        SymFacetedMatchCount symFacetedMatchCount = new SymFacetedMatchCount();

        symFacetedMatchCount.setCount(facetedMatchCount.getCount());
        symFacetedMatchCount.setFacet(facetedMatchCount.getFacet());

        return symFacetedMatchCount;
    }
}
