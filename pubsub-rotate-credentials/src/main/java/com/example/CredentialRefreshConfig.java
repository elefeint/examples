package com.example;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
public class CredentialRefreshConfig {

  /** Reference to adapter stored to enable dynamically restarting upon context refresh. */
  @Autowired
  private PubSubInboundChannelAdapter pubSubAdapter;

  /** Loads credentials from filesystem at context startup and upon each context refresh. */
  @Bean
  @RefreshScope
  public CredentialsProvider gcpPubSubCredentialsProvider(ResourceLoader resourceLoader,
      @Value("${credentials.path}") String credentialsPath) {
    System.out.println("*** Getting credentials from " + credentialsPath);
    try {
      ServiceAccountCredentials credentials = ServiceAccountCredentials
          .fromStream(resourceLoader.getResource(credentialsPath).getInputStream());
      System.out.println("*** Instantiated credentials: " + credentials.getClientEmail() + credentials.getPrivateKeyId());
      return FixedCredentialsProvider.create(credentials);
    } catch (IOException e) {
      throw new RuntimeException(String.format("IOException when reading %s file", credentialsPath),
          e);
    }
  }

  /** Restarts the Spring Integration adapter to reconnect with new credentials. */
  @EventListener(RefreshScopeRefreshedEvent.class)
  public void onRefreshScope(RefreshScopeRefreshedEvent event) {
    System.out.println("*** Restarting Pub/Sub adapter, triggered by event " + event);

    this.pubSubAdapter.stop();
    this.pubSubAdapter.start();
  }

}
