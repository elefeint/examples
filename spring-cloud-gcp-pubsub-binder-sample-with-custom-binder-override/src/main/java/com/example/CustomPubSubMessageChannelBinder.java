/*
 * Copyright 2017-2020 the original author or authors.
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

import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubMessageSource;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.cloud.gcp.stream.binder.pubsub.PubSubMessageChannelBinder;
import org.springframework.cloud.gcp.stream.binder.pubsub.properties.PubSubConsumerProperties;
import org.springframework.cloud.gcp.stream.binder.pubsub.properties.PubSubExtendedBindingProperties;
import org.springframework.cloud.gcp.stream.binder.pubsub.properties.PubSubProducerProperties;
import org.springframework.cloud.gcp.stream.binder.pubsub.provisioning.PubSubChannelProvisioner;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Custom message channel binder for Pub/Sub overriding async publish to be synchronous.
 */
public class CustomPubSubMessageChannelBinder extends PubSubMessageChannelBinder {

	public CustomPubSubMessageChannelBinder(String[] headersToEmbed,
			PubSubChannelProvisioner provisioningProvider, PubSubTemplate pubSubTemplate,
			PubSubExtendedBindingProperties pubSubExtendedBindingProperties) {

		super(headersToEmbed, provisioningProvider, pubSubTemplate, pubSubExtendedBindingProperties);
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ExtendedProducerProperties<PubSubProducerProperties> producerProperties,
			MessageChannel errorChannel) {

		PubSubMessageHandler defaultPublishHandler =
				(PubSubMessageHandler)super.createProducerMessageHandler(destination, producerProperties, errorChannel);

		// Override to force synchronous execution in order to bubble up exceptions to emitter.
		defaultPublishHandler.setSync(true);

		return defaultPublishHandler;
	}

}
