package com.example;

import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.threeten.bp.Duration;

@SpringBootApplication
public class SampleApplication {

  private static final Log LOGGER = LogFactory.getLog(SampleApplication.class);

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SampleApplication.class, args);
    System.out.println("Press any key to stop");
    System.in.read();
  }

  @Bean
  public MessageChannel pubsubInputChannel() {
    LOGGER.debug("Inside pubsubInputChannel");
    return new DirectChannel();
  }
// end::pubsubInputChannel[]

  // tag::messageChannelAdapter[]
  @Bean
  public PubSubInboundChannelAdapter messageChannelAdapter(@Qualifier("pubsubInputChannel") MessageChannel inputChannel,
      PubSubTemplate pubSubTemplate) {

    PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, "exampleSubscription");

    adapter.setOutputChannel(inputChannel);
    adapter.setAckMode(AckMode.MANUAL);
    adapter.setPayloadType(String.class);



    LOGGER.debug("Inside PubSubInboundChannelAdapter");

    return adapter;
  }
// end::messageChannelAdapter[]

  // tag::messageReceiver[]
  @Bean
  public TransportChannelProvider transportChannelProvider() {
    int customMaxMessageSize = 9999999; // 4MiB is the gRPC default
    return InstantiatingGrpcChannelProvider.newBuilder()
        .setKeepAliveTime(Duration.ofMinutes(10))
        .setMaxInboundMessageSize(customMaxMessageSize)
        .build();
  }

  @Bean
  @ServiceActivator(inputChannel = "pubsubInputChannel")
  public MessageHandler messageReceiver() {
    int Counter = 0;
    LOGGER.debug("Inside messageReceiver");
    return message -> {
      LOGGER.debug("Inside message");
      System.out.println("got message: " + message);
    };
  }
}
