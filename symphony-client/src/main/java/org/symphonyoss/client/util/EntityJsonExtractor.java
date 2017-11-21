/*
 *
 * Copyright 2017 The Symphony Software Foundation
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

package org.symphonyoss.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.jayway.jsonpath.JsonPath;

/**
 * Helps extract key identifiers/important data from Entities in JSON which were easier
 * to extract when reading raw MessageML
 * 
 * @author Dov Katz
 *
 */
public class EntityJsonExtractor {
	public static final String HASHTAG_JSONPATH = "$..[?(@.type == 'org.symphonyoss.taxonomy')].id[0].value",
			CASHTAG_JSONPATH = "$..[?(@.type == 'org.symphonyoss.fin.security')].id[0].value",
			MENTION_JSONPATH = "$..[?(@.type == 'com.symphony.user.mention')].id[0].value";

	public static final String HASHTAG_ENTITY_ID="org.symphonyoss.taxonomy",
			CASHTAG_ENTITY_ID="org.symphonyoss.fin.security",
			MENTION_ENTITY_ID="com.symphony.user.mention";

	public List<String> getHashTags(String json) {
		return JsonPath.read(json, HASHTAG_JSONPATH);
	}

	public List<String> getCashTags(String json) {
		return JsonPath.read(json, CASHTAG_JSONPATH);
	}

	public List<Long> getMentions(String json) {
		List<String> uids = JsonPath.read(json, MENTION_JSONPATH);
		List<Long> values = new ArrayList<>();
		uids.forEach(new Consumer<String>() {
			public void accept(String t) {
				values.add(Long.valueOf(t));
			}
		});
		return values;
	}

}
