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


/**
 *
 * @author Frank Tarsillo
 */
package org.symphonyoss.symphony.clients.model;


import org.symphonyoss.symphony.pod.model.AdminStreamList;
import java.util.List;


public class SymAdminStreamList {

    private Long count = null;


    private Integer skip = null;


    private Integer limit = null;


    private SymAdminStreamFilter filter = null;

    List<SymAdminStreamInfo> streams = null;

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

    public SymAdminStreamFilter getFilter() {
        return filter;
    }

    public void setFilter(SymAdminStreamFilter filter) {
        this.filter = filter;
    }

    public List<SymAdminStreamInfo> getStreams() {
        return streams;
    }

    public void setStreams(List<SymAdminStreamInfo> streams) {
        this.streams = streams;
    }

    public static SymAdminStreamList toSymStreamList(AdminStreamList adminStreamList) {



            if (adminStreamList == null)
                return null;


            SymAdminStreamList symAdminStreamList = new SymAdminStreamList();

            symAdminStreamList.setCount(adminStreamList.getCount());
            symAdminStreamList.setFilter(SymAdminStreamFilter.toSymStreamFilter(adminStreamList.getFilter()));
            symAdminStreamList.setSkip(adminStreamList.getSkip());
            symAdminStreamList.setStreams(SymAdminStreamInfo.toStreamInfos(adminStreamList.getStreams()));
            symAdminStreamList.setLimit(adminStreamList.getLimit());


            return symAdminStreamList;






    }
}

