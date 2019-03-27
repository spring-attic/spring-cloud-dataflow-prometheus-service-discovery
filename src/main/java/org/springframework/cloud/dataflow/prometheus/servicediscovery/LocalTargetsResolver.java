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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.dataflow.rest.resource.AppStatusResource;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author Christian Tzolov
 */
public class LocalTargetsResolver implements TargetsResolver {

	private final Logger logger = LoggerFactory.getLogger(LocalTargetsResolver.class);

	private final RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	private String discoveryUrl;
	private String attributeName;
	private String overrideIp;

	public LocalTargetsResolver(RestTemplate restTemplate, ObjectMapper objectMapper,
			String discoveryUrl, String attributeName, String overrideIp) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.discoveryUrl = discoveryUrl;
		this.attributeName = attributeName;
		this.overrideIp = overrideIp;
	}

	@Override
	public String getTargets() {
		try {
			AppStatusResource.Page page = this.restTemplate.getForObject(this.discoveryUrl,
					AppStatusResource.Page.class);

			List<String> targetUrls = page.getContent().stream()
					.map(appResource -> appResource.getInstances().getContent())
					.flatMap(instances -> instances.stream().map(inst -> inst.getAttributes().get(this.attributeName)))
					.map(this::formatTargetUrl)
					.collect(Collectors.toList());

			return this.buildPrometheusTargetsJson(targetUrls);
		}
		catch (Exception e) {
			logger.error(ExceptionUtils.getMessage(e));
		}
		return "";
	}

	private String formatTargetUrl(String runtimeAppUrl) {
		String targetUrl = runtimeAppUrl.replace("http://", "");

		return (StringUtils.hasText(this.overrideIp)) ?
				this.overrideIp + targetUrl.substring(targetUrl.indexOf(":")) : targetUrl;
	}

	/**
	 * Converts a list of urls into JSON list of static target configs, compliant with the file_sd_config format.
	 * @param targetUrls list of SCSt apps IP:PORT pairs to be used as prometheus metrics targets.
	 * @return json record compliant with file_sd_config
	 * @throws JsonProcessingException
	 */
	private String buildPrometheusTargetsJson(List<String> targetUrls) throws JsonProcessingException {
		StaticConfig staticConfig = new StaticConfig();
		staticConfig.setTargets(targetUrls);
		staticConfig.setLabels(Collections.singletonMap("job", "scdf")); // marker to indicate sd is used.
		return this.objectMapper.writeValueAsString(Arrays.asList(staticConfig));
	}
}
