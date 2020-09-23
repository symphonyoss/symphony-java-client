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

import org.symphonyoss.symphony.agent.model.ChannelSubscriber;

/**
 * @author dovkatz on 03/13/2018
 */
public class SymChannelSubscriber {
	
	private Boolean owner;
	private Boolean pushed;
	private String subscriberName;
	private Long userId;
	private Long timestamp;
	
	public Boolean getOwner() {
		return owner;
	}

	public void setOwner(Boolean owner) {
		this.owner = owner;
	}

	public Boolean getPushed() {
		return pushed;
	}

	public void setPushed(Boolean pushed) {
		this.pushed = pushed;
	}

	public String getSubscriberName() {
		return subscriberName;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	 public static ChannelSubscriber toChannelSubscriber(SymChannelSubscriber symSub) {
		 ChannelSubscriber sub=new ChannelSubscriber();
		 sub.owner(symSub.getOwner());
		 sub.pushed(symSub.getPushed());
		 sub.subscriberName(symSub.getSubscriberName());
		 sub.userId(symSub.getUserId());
		 sub.timestamp(symSub.getTimestamp());
		 return sub;
	 }
	 
	 public static SymChannelSubscriber toSymChannelSubscriber(ChannelSubscriber sub) {
		 SymChannelSubscriber symSub=new SymChannelSubscriber();
		 symSub.setOwner(sub.isOwner());
		 symSub.setPushed(sub.isPushed());
		 symSub.setSubscriberName(sub.getSubscriberName());
		 symSub.setUserId(sub.getUserId());
		 symSub.setTimestamp(sub.getTimestamp());
		 return symSub;
	 }
}
