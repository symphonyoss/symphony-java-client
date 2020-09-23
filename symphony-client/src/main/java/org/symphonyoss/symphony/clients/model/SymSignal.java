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

import org.symphonyoss.symphony.agent.model.BaseSignal;
import org.symphonyoss.symphony.agent.model.Signal;
import org.symphonyoss.symphony.agent.model.SignalList;

/**
 * @author dovkatz on 03/13/2018
 */
public class SymSignal {
	
	private Boolean companyWide = null;
	private Boolean visibleOnProfile = null;
	private Long timestamp = null;
	private String id = null;
	private String name = null;
	private String query = null;
	
	

	public Boolean getCompanyWide() {
		return companyWide;
	}

	public void setCompanyWide(Boolean companyWide) {
		this.companyWide = companyWide;
	}

	public Boolean getVisibleOnProfile() {
		return visibleOnProfile;
	}

	public void setVisibleOnProfile(Boolean visibleOnProfile) {
		this.visibleOnProfile = visibleOnProfile;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public static BaseSignal toBaseSignal(SymSignal symSignal) {
		BaseSignal base=new BaseSignal();
		base.companyWide(symSignal.getCompanyWide());
		base.query(symSignal.getQuery());
		base.name(symSignal.getName());
		base.visibleOnProfile(symSignal.getVisibleOnProfile());
		return base;
	}
	
	public static Signal toSignal(SymSignal symSignal) {
		Signal signal=new Signal();
		signal.companyWide(symSignal.getCompanyWide());
		signal.id(symSignal.getId());
		signal.name(symSignal.getName());
		signal.query(symSignal.getQuery());
		signal.visibleOnProfile(symSignal.getVisibleOnProfile());
		signal.timestamp(symSignal.getTimestamp());
		return signal;
	}
	
	public static SymSignal toSymSignal(Signal signal) {
		SymSignal symSignal=new SymSignal();
		symSignal.setCompanyWide(signal.isCompanyWide());
		symSignal.setId(signal.getId());
		symSignal.setName(signal.getName());
		symSignal.setQuery(signal.getQuery());
		symSignal.setVisibleOnProfile(signal.isVisibleOnProfile());
		symSignal.setTimestamp(signal.getTimestamp());
		return symSignal;
	}
	
	public static List<SymSignal> fromSignalList(SignalList list){
		List<SymSignal> result=new ArrayList<>();
		list.forEach((signal)->result.add(SymSignal.toSymSignal(signal)));
		return result;
	}
}
