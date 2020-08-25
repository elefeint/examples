package com.example;

import com.google.pubsub.v1.PubsubMessage;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.support.converter.PubSubMessageConverter;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ErrorPropagationApp {

	public static void main(String[] args) {
		SpringApplication.run(ErrorPropagationApp.class, args);
	}

	/* This converter triggers a pre-send assertion error in client library
	 if `causeError` header is present */
	@Bean
	public PubSubMessageConverter converter() {
		return new SimplePubSubMessageConverter() {
			@Override
			public PubsubMessage toPubSubMessage(Object payload, Map<String, String> headers) {
				PubsubMessage.Builder builder = super.toPubSubMessage(payload, headers).toBuilder();
				if (headers.containsKey("trigger-error")) {
					builder.setOrderingKey("this-will-fail-client-library-assert");
				}
				return builder.build();
			}
		};
	}

}
