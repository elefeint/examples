
package com.google.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.core.subscriber.PubSubSubscriberTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

/**
 *
 */
@SpringBootApplication
public class ExampleApplication {

	@Autowired
	private PubSubTemplate pubSubTemplate;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ExampleApplication.class, args);

		System.out.println("Press <ENTER> to exit");
		System.in.read();
	}


	@Bean
	public MessageChannel processorChannel() {
		return new DirectChannel();
	}

	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(
			@Qualifier("processorChannel") MessageChannel inputChannel,
			PubSubSubscriberTemplate subscriberTemplate) {

		PubSubInboundChannelAdapter adapter =
				new PubSubInboundChannelAdapter(subscriberTemplate, "exampleSubscription");
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.AUTO);

		return adapter;
	}

	@ServiceActivator(inputChannel = "processorChannel")
	public void processMessage(String receivedMessage, @Header("reply-to-topic") String replyTo) {

		System.out.println("Got message: " + receivedMessage);

		this.pubSubTemplate.publish(replyTo, "I have received your message: "+ receivedMessage);


	}

}
