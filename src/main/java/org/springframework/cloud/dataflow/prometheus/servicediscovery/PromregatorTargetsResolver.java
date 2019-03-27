/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.prometheus.servicediscovery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.client.RestTemplate;

/**
 * @author Christian Tzolov
 */
public class PromregatorTargetsResolver implements TargetsResolver {

	private final Logger logger = LoggerFactory.getLogger(PromregatorTargetsResolver.class);

	private ObjectMapper objectMapper;
	private String discoveryUrl;

	public PromregatorTargetsResolver(ObjectMapper objectMapper, String discoveryUrl) {
		this.objectMapper = objectMapper;
		this.discoveryUrl = discoveryUrl;
	}

	@Override
	public String getTargets() {
		try {
			Object targets = new RestTemplate().getForObject(this.discoveryUrl, Object.class);
			return this.objectMapper.writeValueAsString(targets);
		}
		catch (JsonProcessingException e) {
			logger.warn("Failed to resolve targets", e);
		}
		return "";
	}
}
