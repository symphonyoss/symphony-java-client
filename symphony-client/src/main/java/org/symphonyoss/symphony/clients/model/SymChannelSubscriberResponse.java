/*
 *
 *
 * Copyright 2018 The Symphony Software Foundation
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

import java.util.ArrayList;
import java.util.List;

import org.symphonyoss.symphony.agent.model.ChannelSubscriberResponse;

/**
 * @author dovkatz on 03/13/2018
 */
public class SymChannelSubscriberResponse {

	private Integer total = null;
	private Long offset = null;
	private Boolean hasMore = null;
	private List<SymChannelSubscriber> subscribers;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public List<SymChannelSubscriber> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(List<SymChannelSubscriber> subs) {
		this.subscribers = subs;
	}

	public Boolean getHasMore() {
		return hasMore;
	}

	public void setHasMore(Boolean hasMore) {
		this.hasMore = hasMore;
	}
	
	public static SymChannelSubscriberResponse toSymChannelSubscriberResponse(ChannelSubscriberResponse response) {
		SymChannelSubscriberResponse symResponse=new SymChannelSubscriberResponse();
		symResponse.setHasMore(response.getHasMore());
		symResponse.setTotal(response.getTotal());
		symResponse.setOffset(response.getOffset());
		List<SymChannelSubscriber> list=new ArrayList<>();
		response.getData().forEach((subscriber)->list.add(SymChannelSubscriber.toSymChannelSubscriber(subscriber)));;
		symResponse.setSubscribers(list);
		return symResponse;
	}
}
