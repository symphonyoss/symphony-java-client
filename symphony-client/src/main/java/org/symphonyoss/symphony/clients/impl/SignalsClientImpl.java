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

/**
 * @author dovkatz on 03/13/2018
 */
package org.symphonyoss.symphony.clients.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.client.Client;

import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.SignalsException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.agent.api.SignalsApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.model.BaseSignal;
import org.symphonyoss.symphony.agent.model.ChannelSubscriberResponse;
import org.symphonyoss.symphony.agent.model.ChannelSubscriptionResponse;
import org.symphonyoss.symphony.agent.model.Signal;
import org.symphonyoss.symphony.agent.model.SignalList;
import org.symphonyoss.symphony.clients.SignalsClient;
import org.symphonyoss.symphony.clients.model.SymChannelSubscriberResponse;
import org.symphonyoss.symphony.clients.model.SymSignal;

public class SignalsClientImpl implements SignalsClient {

	private final SymAuth symAuth;
	private final ApiClient apiClient;

	/**
	 * Init
	 *
	 * @param symAuth
	 *            Authorization model containing session and key tokens
	 * @param config
	 *            Symphony Client Config
	 */
	public SignalsClientImpl(SymAuth symAuth, SymphonyClientConfig config) {

		this(symAuth, config, null);

	}

	/**
	 * If you need to override HttpClient. Important for handling individual client
	 * certs.
	 *
	 * @param symAuth
	 *            Authorization model containing session and key tokens
	 * @param config
	 *            Symphony client config
	 * @param httpClient
	 *            Custom HTTP client
	 */
	public SignalsClientImpl(SymAuth symAuth, SymphonyClientConfig config, Client httpClient) {
		this.symAuth = symAuth;

		// Get Service client to query for userID.
		apiClient = org.symphonyoss.symphony.agent.invoker.Configuration.getDefaultApiClient();

		if (httpClient != null)
			apiClient.setHttpClient(httpClient);

		apiClient.setBasePath(config.get(SymphonyClientConfigID.AGENT_URL));
	}

	@Override
	public SymSignal createSignal(SymSignal signal) throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		BaseSignal base = SymSignal.toBaseSignal(signal);
		try {
			Signal result = api.v1SignalsCreatePost(symAuth.getSessionToken().getToken(), base,
					symAuth.getKeyToken().getToken());
			return SymSignal.toSymSignal(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public SymSignal updateSignal(String id, SymSignal signal) throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		BaseSignal base = SymSignal.toBaseSignal(signal);
		try {
			Signal result = api.v1SignalsIdUpdatePost(symAuth.getSessionToken().getToken(), id, base,
					symAuth.getKeyToken().getToken());
			return SymSignal.toSymSignal(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public void deleteSignal(String id) throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		try {
			api.v1SignalsIdDeletePost(symAuth.getSessionToken().getToken(), id, symAuth.getKeyToken().getToken());
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public SymSignal getSignal(String id) throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		try {
			Signal result = api.v1SignalsIdGetGet(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken());
			return SymSignal.toSymSignal(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public List<SymSignal> listSignals(int skip, int limit) throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		try {
			SignalList result = api.v1SignalsListGet(symAuth.getSessionToken().getToken(),
					symAuth.getKeyToken().getToken(), skip, limit);
			return SymSignal.fromSignalList(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public SymChannelSubscriberResponse listSubscribers(String id, BigDecimal skip, BigDecimal limit)
			throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		try {
			ChannelSubscriberResponse result = api.v1SignalsIdSubscribersGet(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), skip, limit);
			return SymChannelSubscriberResponse.toSymChannelSubscriberResponse(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public ChannelSubscriptionResponse subscribe(String id) throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		try {
			return api.v1SignalsIdSubscribePost(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), null, null);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public ChannelSubscriptionResponse bulkSubscribe(String id, boolean pushed, List<BigDecimal> userIds)
			throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		try {
			return api.v1SignalsIdSubscribePost(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), pushed, userIds);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public ChannelSubscriptionResponse unsubscribe(String id) throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		try {
			return api.v1SignalsIdUnsubscribePost(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), null);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	@Override
	public ChannelSubscriptionResponse bulkUnsubscribe(String id, List<BigDecimal> userIds) throws SignalsException {
		SignalsApi api = new SignalsApi(apiClient);
		try {
			return api.v1SignalsIdUnsubscribePost(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), userIds);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

}
