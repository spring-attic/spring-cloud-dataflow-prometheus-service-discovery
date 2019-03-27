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

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @author Christian Tzolov
 */
@Configuration
@EnableConfigurationProperties(DataflowPrometheusServiceDiscoveryProperties.class)
public class DataflowPrometheusServiceDiscoveryConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder.json().modules(new Jackson2HalModule()).build();
	}

	@Bean
	public RestTemplate restTemplate(ObjectMapper objectMapper) {
		return new RestTemplate(Arrays.asList(
				new MappingJackson2HttpMessageConverter(objectMapper)));
	}

	@Bean
	@ConditionalOnProperty(name = "metrics.prometheus.target.mode", havingValue = "local", matchIfMissing = true)
	public LocalTargetsResolver localTargetsResolver(RestTemplate restTemplate,
			ObjectMapper objectMapper, DataflowPrometheusServiceDiscoveryProperties properties) {
		return new LocalTargetsResolver(restTemplate, objectMapper,
				properties.getDiscoveryUrl(), properties.getAttributeName(), properties.getOverrideIp());
	}

	@Bean
	@ConditionalOnProperty(name = "metrics.prometheus.target.mode", havingValue = "promregator")
	public PromregatorTargetsResolver promregatorTargetsResolver(ObjectMapper objectMapper,
			DataflowPrometheusServiceDiscoveryProperties properties) {
		return new PromregatorTargetsResolver(objectMapper, properties.getDiscoveryUrl());
	}
}
