/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gcp.core.GcpScope;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * An example Spring Boot application that reads and writes files stored in Google Cloud
 * Storage (GCS) using the Spring Resource abstraction and the gs: protocol prefix.
 *
 * @author Mike Eltsufin
 */
@SpringBootApplication
@EnableScheduling
public class GcsApplication {

	private static final Log LOGGER = LogFactory.getLog(GcsApplication.class);

	@Value("${private-key-location}")
	private String privateKeyLocation;

	@Autowired
	org.springframework.cloud.context.scope.refresh.RefreshScope refresh;

	public static void main(String[] args) {
		SpringApplication.run(GcsApplication.class, args);
	}

	// every 10 seconds, expire the RefreshScoped credentials bean
	@Scheduled (fixedDelay = 10000)
	public void refreshConfiguration() {
		LOGGER.warn("***** REFRESHING");
		this.refresh.refresh("refreshableCredentials");

	}

	@Bean
	@RefreshScope
	Credentials refreshableCredentials() throws Exception {
		// refresh scoped beans are lazy; this will only happen when credentials attempt to be used.
		LOGGER.warn("***** ASKED FOR NEW CREDENTIALS");

		List<String> scopes = Collections.singletonList(GcpScope.STORAGE_READ_WRITE.getUrl());

		return GoogleCredentials.fromStream(
				// You'd be reading from Vault here, not from local file.
				new FileInputStream(this.privateKeyLocation))
				.createScoped(scopes);
	}

	@Bean
	CredentialsProvider refreshableCredentialsProvider(Credentials refreshableCredentials) throws Exception {
		// credentials provider is not a refresh scoped bean, so this only happens on startup!
		LOGGER.warn("***** ASKED FOR NEW PROVIDER");

		return FixedCredentialsProvider.create(refreshableCredentials);
	}
}
