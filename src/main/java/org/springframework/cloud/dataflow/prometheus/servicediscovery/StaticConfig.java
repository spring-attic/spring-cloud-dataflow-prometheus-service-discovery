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

import java.util.List;
import java.util.Map;

/**
 * Domain model representing the Prometheus static target format as defined here:
 * https://prometheus.io/docs/prometheus/latest/configuration/configuration/#file_sd_config
 *
 * <code>
 * [
 *   {
 *     "targets": [ "<host>", ... ],
 *     "labels": {
 *       "<labelname>": "<labelvalue>", ...
 *     }
 *   },
 *   ...
 * ]
 * </code>
 *
 * @author Christian Tzolov
 */
public class StaticConfig {
	private List<String> targets;

	private Map<String, String> labels;

	public List<String> getTargets() {
		return targets;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
}
