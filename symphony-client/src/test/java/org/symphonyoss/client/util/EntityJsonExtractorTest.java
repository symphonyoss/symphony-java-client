/*
 *
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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.symphonyoss.client.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * 
 * @author Dov Katz
 *
 */
public class EntityJsonExtractorTest {
	private final Logger logger = LoggerFactory.getLogger(EntityJsonExtractorTest.class);

	static class TaxonomyItem {
		public String type;
		public Id[] id;

		public TaxonomyItem(String type, Id id) {
			this.type = type;
			this.id = new Id[] { id };
		}

		static TaxonomyItem HashTag(String id) {
			return new TaxonomyItem(EntityJsonExtractor.HASHTAG_ENTITY_ID, new Id(id, "Other_" + id));
		}

		static TaxonomyItem CashTag(String id) {
			return new TaxonomyItem(EntityJsonExtractor.CASHTAG_ENTITY_ID, new Id(id, "Other_" + id));
		}

		static TaxonomyItem Mention(Long id) {
			return new TaxonomyItem(EntityJsonExtractor.MENTION_ENTITY_ID, new Id(String.valueOf(id), "Other_" + id));
		}
	}

	static class Id {
		public String value;
		public String name;

		public Id(String val, String other) {
			this.name = other;
			this.value = val;
		}
	}

	Map<String, Object> entities;
	EntityJsonExtractor extractor;

	@Before
	public void setUp() {
		entities = new HashMap<>();
		extractor = new EntityJsonExtractor();
	}

	@Test
	public void testSingleHashTag() {
		String TAG1 = "HashTag1";
		entities.put("mytag", TaxonomyItem.HashTag(TAG1));
		String json = (new Gson().toJson(entities));
		logger.info("JSON to Parse: {}",json);
		logger.info("Searching based on JSONPath {}",EntityJsonExtractor.HASHTAG_JSONPATH);
		List<String> tags = extractor.getHashTags(json);
		logger.info("Found Tags: {}",tags);
		Assert.assertEquals(1, tags.size());
		Assert.assertTrue(tags.contains(TAG1));
	}

	@Test
	public void testMultipleHashTags() {
		String TAG1 = "HashTag1", TAG2 = "HashTag2";
		entities.put("tag1", TaxonomyItem.HashTag(TAG1));
		entities.put("tag2", TaxonomyItem.HashTag(TAG2));
		String json = (new Gson().toJson(entities));
		logger.info("JSON to Parse: {}",json);
		logger.info("Searching based on JSONPath {}",EntityJsonExtractor.HASHTAG_JSONPATH);
		List<String> tags = extractor.getHashTags(json);
		logger.info("Found Tags: {}",tags);
		Assert.assertEquals(2, tags.size());
		Assert.assertTrue(tags.contains(TAG1));
		Assert.assertTrue(tags.contains(TAG2));
	}

	@Test
	public void testSingleMention() {
		Long UID1 = 232323232323L;
		entities.put("mention", TaxonomyItem.Mention(UID1));
		String json = (new Gson().toJson(entities));
		logger.info("JSON to Parse: {}",json);
		logger.info("Searching based on JSONPath {}",EntityJsonExtractor.MENTION_JSONPATH);
		List<Long> tags = extractor.getMentions(json);
		logger.info("Found Mentions: {}",tags);
		Assert.assertEquals(1, tags.size());
		Assert.assertTrue(tags.contains(UID1));
	}

	@Test
	public void testMultipleMentions() {
		Long UID1 = 232323232323L, UID2 = 332323L;
		entities.put("mention1", TaxonomyItem.Mention(UID1));
		entities.put("mention2", TaxonomyItem.Mention(UID2));
		String json = (new Gson().toJson(entities));
		logger.info("JSON to Parse: {}",json);
		logger.info("Searching based on JSONPath {}",EntityJsonExtractor.MENTION_JSONPATH);
		List<Long> tags = extractor.getMentions(json);
		logger.info("Found Mentions: {}",tags);
		Assert.assertEquals(2, tags.size());
		Assert.assertTrue(tags.contains(UID1));
		Assert.assertTrue(tags.contains(UID2));
	}

	@Test
	public void testSingleCashTag() {
		String TAG1 = "CashTag1";
		entities.put("mytag", TaxonomyItem.CashTag(TAG1));
		String json = (new Gson().toJson(entities));
		logger.info("JSON to Parse: {}",json);
		logger.info("Searching based on JSONPath {}",EntityJsonExtractor.CASHTAG_JSONPATH);
		List<String> tags = extractor.getCashTags(json);
		logger.info("Found Tags: {}",tags);
		Assert.assertEquals(1, tags.size());
		Assert.assertTrue(tags.contains(TAG1));
	}

	@Test
	public void testMultipleCashTags() {
		String TAG1 = "CashTag1", TAG2 = "CashTag2";
		entities.put("tag1", TaxonomyItem.CashTag(TAG1));
		entities.put("tag2", TaxonomyItem.CashTag(TAG2));
		String json = (new Gson().toJson(entities));
		logger.info("JSON to Parse: {}",json);
		logger.info("Searching based on JSONPath {}",EntityJsonExtractor.CASHTAG_JSONPATH);
		List<String> tags = extractor.getCashTags(json);
		logger.info("Found Tags: {}",tags);
		Assert.assertEquals(2, tags.size());
		Assert.assertTrue(tags.contains(TAG1));
		Assert.assertTrue(tags.contains(TAG2));
	}

	@Test
	public void testMultipleCashTagsFromMixedBag() {
		String TAG1 = "CashTag1", TAG2 = "CashTag2";
		entities.put("tag1", TaxonomyItem.CashTag(TAG1));
		entities.put("tag2", TaxonomyItem.CashTag(TAG2));
		entities.put("ht1", TaxonomyItem.HashTag(TAG2));
		entities.put("ht2", TaxonomyItem.HashTag(TAG2));
		entities.put("m1", TaxonomyItem.Mention(101010101L));
		entities.put("m2", TaxonomyItem.Mention(101010101L));
		String json = (new Gson().toJson(entities));
		logger.info("JSON to Parse: {}",json);
		logger.info("Searching based on JSONPath {}",EntityJsonExtractor.CASHTAG_JSONPATH);
		List<String> tags = extractor.getCashTags(json);
		logger.info("Found Tags: {}",tags);
		Assert.assertEquals(2, tags.size());
		Assert.assertTrue(tags.contains(TAG1));
		Assert.assertTrue(tags.contains(TAG2));
	}

}
