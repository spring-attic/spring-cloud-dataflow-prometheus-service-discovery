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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Christian Tzolov
 */
@ConfigurationProperties(prefix = DataflowPrometheusServiceDiscoveryProperties.PREFIX)
public class DataflowPrometheusServiceDiscoveryProperties {

	public static final String PREFIX = "metrics.prometheus.target";

	public enum TargetMode {local, promregator}

	/**
	 * Url of the the SCDF Runtime REST endpoint:
	 * https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#api-guide-resources-runtime-information-applications
	 */
	private String discoveryUrl = "http://localhost:9393/runtime/apps";

	/**
	 * Path where the generated targets.json file is stored.
	 */
	private String filePath = "/tmp/targets.json";

	/**
	 * Name of the DataFlow runtime attribute to representing the target's app URL
	 */
	private String attributeName = "url";

	/**
	 * Use local for localhost SCDF deployment and promregator for CF
	 */
	private TargetMode mode = TargetMode.local;

	/**
	 *
	 */
	private String overrideIp = "";

	public String getDiscoveryUrl() {
		return discoveryUrl;
	}

	public void setDiscoveryUrl(String discoveryUrl) {
		this.discoveryUrl = discoveryUrl;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public TargetMode getMode() {
		return mode;
	}

	public void setMode(TargetMode mode) {
		this.mode = mode;
	}

	public String getOverrideIp() {
		return overrideIp;
	}

	public void setOverrideIp(String overrideIp) {
		this.overrideIp = overrideIp;
	}
}
