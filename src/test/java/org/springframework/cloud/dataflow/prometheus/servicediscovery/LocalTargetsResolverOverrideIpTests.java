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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"metrics.prometheus.target.cron=" + Scheduled.CRON_DISABLED,
		"metrics.prometheus.target.overrideIp=66.66.66.66",
		"metrics.prometheus.target.filePath=./target/my-targets.json"})
public class LocalTargetsResolverOverrideIpTests {

	private MockRestServiceServer mockServer;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LocalTargetsResolver localTargetsResolver;

	@Before
	public void init() {
		mockServer = MockRestServiceServer.createServer(this.restTemplate);
	}

	@Test
	public void testOverrideIp() throws IOException, URISyntaxException {

		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://localhost:9393/runtime/apps")))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(asString("classpath:/runtime_apps_ticktock.json"))
				);

		String targets = localTargetsResolver.getTargets();

		mockServer.verify();

		StaticConfig[] scs = objectMapper.readValue(targets, StaticConfig[].class);

		assertThat(scs.length, is(1));
		assertThat(scs[0].getLabels().get("job"), is("scdf"));
		assertThat(scs[0].getTargets(), contains("66.66.66.66:20080", "66.66.66.66:20032"));
	}

	private String asString(String resourceUri) throws IOException {
		return StreamUtils.copyToString(
				new DefaultResourceLoader().getResource(resourceUri).getInputStream(), Charset.forName("UTF-8"));
	}
}
