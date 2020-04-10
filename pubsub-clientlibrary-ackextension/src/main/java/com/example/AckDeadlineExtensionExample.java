package com.example;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.threeten.bp.Duration;

public class AckDeadlineExtensionExample {


  static String PROJECT_NAME = ServiceOptions.getDefaultProjectId();
  static String SUB_NAME = "projects/" + PROJECT_NAME + "/subscriptions/exampleSubscriptionAckDeadline";
  static String TOPIC_NAME = "projects/" + PROJECT_NAME + "/topics/exampleTopicAckDeadline";

  public static void main(String[] args) throws Exception {
    TopicAdminClient topicAdminClient = TopicAdminClient.create();
    SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create();

    // set up logging to show "Sending 1 receipts" message from
    // MessageDispatcher.processOutstandingAckOperations()
    Logger logger = Logger.getLogger("com.google.cloud.pubsub");
    Handler ch = new ConsoleHandler();
    ch.setLevel(Level.FINEST);
    logger.addHandler(ch);
    logger.setLevel(Level.FINEST);

    try {

      topicAdminClient.createTopic(TOPIC_NAME);
      System.out.println("Created topic");

      // Publish a single message
      Publisher publisher = Publisher.newBuilder(TOPIC_NAME).build();
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
          .setData(ByteString.copyFromUtf8("don't extend my deadline"))
          .build();

      // Subscription ack deadline is 30 seconds, so expected redelivery in under a minute.
      int ackDeadlineSeconds = 30;
      subscriptionAdminClient
          .createSubscription(SUB_NAME, TOPIC_NAME, PushConfig.newBuilder().build(),
              ackDeadlineSeconds);
      System.out.println("Created subscription");

      // Publish test message
      publisher.publish(pubsubMessage);

      // Keep test running until 2 messages are received (initial message + redelivery attempt).
      CountDownLatch latch = new CountDownLatch(2);
      long[] timestamps = new long[2];

      Subscriber subscriber = Subscriber
          .newBuilder(SUB_NAME, (msg, ackReplyConsumer) -> {
            long timestamp = System.currentTimeMillis();
            System.out.println("Received message: " + new String(msg.getData().toByteArray())
                + " - " + timestamp);
            timestamps[2 - (int)latch.getCount()] = timestamp;

            // ack message on the second delivery.
            if (latch.getCount() == 1) {
              ackReplyConsumer.ack();
            }
            latch.countDown();
          })
          //.setMaxAckExtensionPeriod(Duration.ofMillis(1L))
          .setMaxAckExtensionPeriod(Duration.ZERO)
          .build();
      subscriber.startAsync();

      // allow the application to shut down after 5 minutes even if redelivery does not happen
      // (it will though; only timing is in question).
      latch.await(300, TimeUnit.SECONDS);
      //subscriber.stopAsync();

      System.out.println("Redelivery took " + ((timestamps[1] - timestamps[0]) / 1000) + " seconds");
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      subscriptionAdminClient.deleteSubscription(SUB_NAME);
      System.out.println("Deleted subscription");

      topicAdminClient.deleteTopic(TOPIC_NAME);
      System.out.println("Deleted topic");
    }
  }
}
