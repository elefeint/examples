package com.example;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.cloud.spring.storage.integration.GcsSessionFactory;
import com.google.cloud.spring.storage.integration.outbound.GcsMessageHandler;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@SpringBootApplication
public class DriverApp {

  public static void main(String[] args) throws IOException {
    SpringApplication.run(DriverApp.class, args);

    System.out.println("Press any key to continue...");
    System.in.read();
  }

  @Bean
  public MessageChannel pubsubInputChannel() {
    return new PublishSubscribeChannel();
  }


  //                                 ======================                                             =============================
  // Pubsub -> (inbound adapter)    SI pubsubInputChannel  ---> messageReceiver (service activator) -->  SI writeFilesInputChannel  -> (outbound adapter) -> GCS
  //                                 ======================                                              =============================
  @Bean
  public PubSubInboundChannelAdapter messageChannelAdapter(
      @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
      PubSubTemplate pubsubTemplate) {
    PubSubInboundChannelAdapter adapter =
        new PubSubInboundChannelAdapter(pubsubTemplate, "exampleSubscription");
    adapter.setOutputChannel(inputChannel);
    adapter.setAckMode(AckMode.MANUAL);

    return adapter;
  }


  @Bean
  @ServiceActivator(inputChannel = "pubsubInputChannel")
  public MessageHandler messageReceiver(@Qualifier("writeFiles") MessageChannel fileWritingChannel) {
    return message -> {
      System.out.println("Message arrived! Payload: " + new String((byte[]) message.getPayload()));
      BasicAcknowledgeablePubsubMessage originalMessage =
          message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
      originalMessage.ack();

      fileWritingChannel.send(message);
    };
  }


  // GCS now

  @Bean
  public MessageChannel writeFilesInputChannel() {
    return new PublishSubscribeChannel();
  }


  @Bean
  @ServiceActivator(inputChannel = "writeFilesInputChannel")
  public MessageHandler outboundChannelAdapter(Storage gcs) {
    GcsMessageHandler outboundChannelAdapter = new GcsMessageHandler(new GcsSessionFactory(gcs));
    outboundChannelAdapter.setRemoteDirectoryExpression(new ValueExpression<>("pubsub_and_storage"));

    return outboundChannelAdapter;
  }

}
