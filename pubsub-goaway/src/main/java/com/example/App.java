package com.example;

import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.cloud.spring.pubsub.reactive.PubSubReactiveFactory;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import java.io.IOException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.threeten.bp.Duration;
import reactor.core.publisher.Flux;

/**
 * Spring Boot application demonstrating impact of a server-side GOAWAY on reactive Pub/Sub layer.
 *
 * Experiment with high-level retries by changing the reactive pipeline in {@link #subscribeToPubsub()}
 */
@SpringBootApplication
public class App {

  private static String subscription = System.getProperty("pubsub.subscription");

  @Autowired
  private PubSubReactiveFactory reactiveFactory;

  public static void main(String[] args) throws IOException {
    if (subscription == null || "".equals(subscription)) {
      System.err.println("Please provide `pubsub.subscription` property");
      return;
    }

    SpringApplication.run(App.class, args);
    System.out.println("Press any key to terminate...");
    System.in.read();
  }

  @Bean
  public TransportChannelProvider subscriberTransportChannelProvider() {
    return SubscriberStubSettings.defaultGrpcTransportProviderBuilder()
        // match default keepalive
        .setKeepAliveTime(Duration.ofMinutes(5))
        .setInterceptorProvider(
            () -> Collections.singletonList(new PubsubGrpcOccasionallyFailingInterceptor(3))
        )
        .build();
  }

  @Bean
  ApplicationRunner subscribeToPubsub() {
    return args -> {
      Flux<AcknowledgeablePubsubMessage> msgStream =
          reactiveFactory.poll(this.subscription, 100);
          // no high level retry because the goal is to test low-level GAX retry options,
          // but this is where you would test reactive retries.

      msgStream.subscribe(
          msg -> System.out.println("****** Got message: " + new String(msg.getPubsubMessage().getData().toByteArray())),
          error -> System.out.println("****** Got error: " + error)
      );

    };
  }

}
