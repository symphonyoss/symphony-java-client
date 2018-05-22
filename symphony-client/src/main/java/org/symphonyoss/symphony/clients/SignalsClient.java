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

package org.symphonyoss.symphony.clients;


import java.math.BigDecimal;
import java.util.List;

import org.symphonyoss.client.exceptions.SignalsException;
import org.symphonyoss.symphony.agent.model.ChannelSubscriptionResponse;
import org.symphonyoss.symphony.clients.model.SymChannelSubscriberResponse;
import org.symphonyoss.symphony.clients.model.SymSignal;

/**
 * @author dovkatz on 03/13/2018
 */
public interface SignalsClient {

	public SymSignal createSignal(SymSignal signal) throws SignalsException;
	
	public SymSignal updateSignal(String id, SymSignal signal)  throws SignalsException;

	public void deleteSignal(String id) throws SignalsException;

	public SymSignal getSignal(String id) throws SignalsException;
	
	public List<SymSignal> listSignals(int skip, int limit)  throws SignalsException;
	
	public SymChannelSubscriberResponse listSubscribers(String id, Integer skip, Integer limit)  throws SignalsException;
	
	public ChannelSubscriptionResponse subscribe(String id)  throws SignalsException;
	
	public ChannelSubscriptionResponse bulkSubscribe(String id, boolean pushed, List<Long> userIds)  throws SignalsException;
	
	public ChannelSubscriptionResponse unsubscribe(String id)  throws SignalsException;
	
	public ChannelSubscriptionResponse bulkUnsubscribe(String id, List<Long> userIds)  throws SignalsException;
	
}
