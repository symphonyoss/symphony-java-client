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

	/**
	 * Creates a signal based on a given query
	 *
	 * @param signal
	 *            The Signal object containing the query, name, and
	 *            visibility/company-wide settings to use
	 *
	 * @return the SymSignal object that was created, including the timestamp, and
	 *         ID
	 */
	@Override
	public SymSignal createSignal(SymSignal signal) throws SignalsException {
		SignalsApi api = createSignalsApi();
		BaseSignal base = SymSignal.toBaseSignal(signal);
		try {
			Signal result = api.v1SignalsCreatePost(symAuth.getSessionToken().getToken(), base,
					symAuth.getKeyToken().getToken());
			return SymSignal.toSymSignal(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * @return SignalsApi instance
	 */
	public SignalsApi createSignalsApi() {
		SignalsApi api = new SignalsApi(apiClient);
		return api;
	}

	/**
	 * Updates an existing signal
	 * 
	 * @param id
	 *            The ID of the signal to be updated
	 * 
	 * @param signal
	 *            The SymSignal object containing the updated fields
	 *
	 * @return the SymSignal object that was updated
	 * 
	 */
	@Override
	public SymSignal updateSignal(String id, SymSignal signal) throws SignalsException {
		SignalsApi api = createSignalsApi();
		BaseSignal base = SymSignal.toBaseSignal(signal);
		try {
			Signal result = api.v1SignalsIdUpdatePost(symAuth.getSessionToken().getToken(), id, base,
					symAuth.getKeyToken().getToken());
			return SymSignal.toSymSignal(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * Deletes an existing signal
	 * 
	 * @param id
	 *            The ID of the signal to be deleted
	 * 
	 */
	@Override
	public void deleteSignal(String id) throws SignalsException {
		SignalsApi api = createSignalsApi();
		try {
			api.v1SignalsIdDeletePost(symAuth.getSessionToken().getToken(), id, symAuth.getKeyToken().getToken());
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * Retrieves an existing signal. Can be public signals, or signals owned by the
	 * current session owner
	 * 
	 * @param id
	 *            The ID of the signal to be updated
	 * 
	 * @return the SymSignal object with that ID
	 * 
	 */
	@Override
	public SymSignal getSignal(String id) throws SignalsException {
		SignalsApi api = createSignalsApi();
		try {
			Signal result = api.v1SignalsIdGetGet(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken());
			return SymSignal.toSymSignal(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * Lists all signals owned by or subscribed to by the current session owner.
	 * 
	 * @param skip
	 *            Offset - number of items to skip in the list
	 * 
	 * @param limit
	 *            Maximum number of results to return (Hard maximum according to
	 *            REST API is 500)
	 * 
	 * @return List of SymSignal instances
	 * 
	 */
	@Override
	public List<SymSignal> listSignals(int skip, int limit) throws SignalsException {
		SignalsApi api = createSignalsApi();
		try {
			SignalList result = api.v1SignalsListGet(symAuth.getSessionToken().getToken(),
					symAuth.getKeyToken().getToken(), skip, limit);
			return SymSignal.fromSignalList(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * Lists all subscribers of a given signal
	 * 
	 * @param id
	 *            ID of the signal
	 * 
	 * @param skip
	 *            Offset - number of items to skip in the list
	 * 
	 * @param limit
	 *            Maximum number of results to return (Hard maximum according to
	 *            REST API is 500)
	 * 
	 * @return List of subscribers
	 * 
	 */
	@Override
	public SymChannelSubscriberResponse listSubscribers(String id, BigDecimal skip, BigDecimal limit)
			throws SignalsException {
		SignalsApi api = createSignalsApi();
		try {
			ChannelSubscriberResponse result = api.v1SignalsIdSubscribersGet(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), skip, limit);
			return SymChannelSubscriberResponse.toSymChannelSubscriberResponse(result);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * Subscribes the current session owner to a given signal
	 * 
	 * @param id
	 *            ID of the signal
	 * 
	 * @return API response of subscription operation
	 * 
	 */
	@Override
	public ChannelSubscriptionResponse subscribe(String id) throws SignalsException {
		SignalsApi api = createSignalsApi();
		try {
			return api.v1SignalsIdSubscribePost(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), null, null);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * Subscribes the set of specified users to a given symbol
	 * 
	 * @param id
	 *            ID of the signal
	 * 
	 * @param pushed
	 *            Force-push the signal (they cannot unsubscribe)
	 * 
	 * @param userIds
	 *            List of userIDs to subscribe (maximum 100 per operation per REST
	 *            API Docs)
	 * 
	 * @return API response of subscription operation
	 * 
	 */
	@Override
	public ChannelSubscriptionResponse bulkSubscribe(String id, boolean pushed, List<BigDecimal> userIds)
			throws SignalsException {
		SignalsApi api = createSignalsApi();
		try {
			return api.v1SignalsIdSubscribePost(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), pushed, userIds);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * Unsubscribes the session owner of specified users to a given symbol
	 * 
	 * @param id
	 *            ID of the signal
	 *            
	 * @return API response of unsubscription operation
	 * 
	 */
	@Override
	public ChannelSubscriptionResponse unsubscribe(String id) throws SignalsException {
		SignalsApi api = createSignalsApi();
		try {
			return api.v1SignalsIdUnsubscribePost(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), null);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

	/**
	 * Unsubscribes the set of specified users to a given symbol
	 * 
	 * @param id
	 *            ID of the signal
	 * 
	 * @param userIds
	 *            List of userIDs to unsubscribe (maximum 100 per operation per REST
	 *            API Docs)
	 * 
	 * @return API response of unsubscription operation
	 * 
	 */
	@Override
	public ChannelSubscriptionResponse bulkUnsubscribe(String id, List<BigDecimal> userIds) throws SignalsException {
		SignalsApi api = createSignalsApi();
		try {
			return api.v1SignalsIdUnsubscribePost(symAuth.getSessionToken().getToken(), id,
					symAuth.getKeyToken().getToken(), userIds);
		} catch (ApiException ex) {
			throw new SignalsException(ex);
		}
	}

}
