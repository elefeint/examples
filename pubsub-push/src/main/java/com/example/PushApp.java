package com.example;

import brave.http.HttpTracingCustomizer;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PushApp {

  @Value("${topicName}")
  private String topicName;

  @Value("${subscriptionName}")
  private String subscriptionName;

  @Autowired
  private PubSubAdmin pubSubAdmin;

  public static void main(String[] args) {
    SpringApplication.run(PushApp.class, args);
  }

  @Bean
  HttpTracingCustomizer pubSubHttpTracingCustomizer() {
    return httpTracingBuilder -> {
      httpTracingBuilder.serverSampler(request -> {
        if (request.path().equals("/frompubsub")) {
          System.out.println("Turning off sampling for Pub/Sub push endpoint");
          return false;
        }
        return true;
      });

      // In principle, instead of turning off sampling for /frompubsub,
      // one could use httpTracingBuilder.serverRequestParser() to get trace
      // context from the HttpRequest body and into Brave context. But this is inconvenient because
      // servlet request body is streaming and can only be read once. You can twiddle Spring Web's
      // filters into caching the content, but it's cumbersome.
    };
  }

  @Bean
  public ApplicationRunner createTopicAndSubscription() throws Exception {
    return args -> {
      pubSubAdmin.createTopic(this.topicName);
      pubSubAdmin.createSubscription(this.subscriptionName, this.topicName, 10, "http://localhost:8080/frompubsub");

      System.out.println("*** Finished creating resources");
    };
  }

  @PreDestroy
  public void cleanupResources() {
    pubSubAdmin.deleteSubscription(this.subscriptionName);
    pubSubAdmin.deleteTopic(this.topicName);

    System.out.println("*** Finished deleting resources");
  }

}
