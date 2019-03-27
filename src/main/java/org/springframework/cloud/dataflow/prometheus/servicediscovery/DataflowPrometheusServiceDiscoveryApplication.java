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

import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

/**
 * Prometheus Service Discovery for SCSt apps deployed with the Local Deployer (?k8s?). It is build on top of the
 * file_sd_config mechanism
 *
 * https://prometheus.io/docs/prometheus/latest/configuration/configuration/#file_sd_config
 *
 * @author Christian Tzolov
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DataflowPrometheusServiceDiscoveryProperties.class)
public class DataflowPrometheusServiceDiscoveryApplication {

	private final Logger logger = LoggerFactory.getLogger(DataflowPrometheusServiceDiscoveryApplication.class);

	@Autowired
	private TargetsResolver targetsResolver;

	@Autowired
	private DataflowPrometheusServiceDiscoveryProperties properties;

	public static void main(String[] args) {
		SpringApplication.run(DataflowPrometheusServiceDiscoveryApplication.class, args);
	}

	/**
	 * Use the metrics.prometheus.target.refresh.rate property (in milliseconds) to change the targets discovery rate.
	 *
	 */
	@Scheduled(cron = "${metrics.prometheus.target.cron:0/30 * * * * *}")
	public void updateTargets() throws IOException {

		String targetsJson = this.targetsResolver.getTargets();

		logger.info(this.properties.getMode() + ": " + targetsJson);

		this.updateTargetsFile(targetsJson);
	}

	/**
	 * Re-writes the updated targets json into output file used by Prometheus.
	 * @param targetsJson SCDF apps targets
	 * @throws IOException
	 */
	private void updateTargetsFile(String targetsJson) throws IOException {
		if (StringUtils.hasText(targetsJson)) {
			FileWriter fw = new FileWriter(this.properties.getFilePath());
			fw.write(targetsJson);
			fw.close();
		}
	}
}
