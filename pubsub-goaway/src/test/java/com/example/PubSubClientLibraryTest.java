package com.example;

import static org.junit.Assert.assertEquals;

import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.threeten.bp.Duration;

public class PubSubClientLibraryTest {

  private String projectId = System.getProperty("gcp.project");
  private String subscription = System.getProperty("pubsub.subscription");

  private int maxRetryAttempts = 5;

  private int numPulls = 10;


  @Test
  public void testRetries() throws IOException {

    SubscriberStubSettings.Builder settingsBuilder = SubscriberStubSettings.newBuilder();

    // simulate occasional GOAWAY
    settingsBuilder.setTransportChannelProvider(createOccasionallyFailingChannelProvider());

    RetrySettings retrySettings = RetrySettings.newBuilder()
        .setMaxAttempts(maxRetryAttempts)
        .setTotalTimeout(Duration.ofMinutes(10))
        .build();
    settingsBuilder.pullSettings().setRetrySettings(retrySettings);
    SubscriberStubSettings settings = settingsBuilder.build();

    // initial stub settings are correct.
    assertEquals(maxRetryAttempts, settings.pullSettings().getRetrySettings().getMaxAttempts());

    try (SubscriberStub subscriberStub = settings.createStub()) {
      PullRequest request = createPullRequest();

      for (int i = 0; i < numPulls; i++) {
        System.out.println("Attempt #" + (i + 1));
        PullResponse pullResponse = subscriberStub.pullCallable().call(request);
        List<ReceivedMessage> messages = pullResponse.getReceivedMessagesList();

        System.out.println("Received "  + messages.size() + " messages - "
            + messages.stream().map(msg -> new String(msg.getMessage().getData().toByteArray())).collect(
            Collectors.joining(", ")));
      }
    }
  }

  private TransportChannelProvider createOccasionallyFailingChannelProvider() {
    return SubscriberStubSettings.defaultGrpcTransportProviderBuilder()
        // match default keepalive
        .setKeepAliveTime(Duration.ofMinutes(5))
        .setInterceptorProvider(
            () -> Collections.singletonList(new PubsubGrpcOccasionallyFailingInterceptor(3))
        )
        .build();
  }

  private PullRequest createPullRequest() {
    String subscriptionName = ProjectSubscriptionName.format(projectId, subscription);
    PullRequest pullRequest =
        PullRequest.newBuilder()
            .setMaxMessages(10)
            .setSubscription(subscriptionName)
            .setReturnImmediately(true)
            .build();
    return pullRequest;
  }

}
