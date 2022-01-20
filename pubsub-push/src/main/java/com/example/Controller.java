package com.example;

import brave.Span;
import brave.Span.Kind;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContextOrSamplingFlags;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.publisher.PubSubPublisherTemplate;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  @Autowired
  PubSubPublisherTemplate publisherOperations;

  @Autowired
  private ObjectMapper jacksonObjectMapper;

  @Autowired
  private Tracer tracer;

  @Autowired
  private Tracing tracing;

  @Value("${topicName}")
  private String topicName;

  // Step 1. User triggers flow via HTTP; message is sent to Pub/Sub
  @PostMapping("sendMessage")
  public void sendMessage(@RequestBody String message) {
    System.out.println("*** Publishing: " + message);
    // Spring Cloud GCP pubsub+trace integration enriches the headers with trace ID.
    publisherOperations.publish(topicName, message);

  }

  // Step 2. Pub/Sub pushes the message over HTTP to the receiving endpoint
  @PostMapping("frompubsub")
  public void getMessage(@RequestBody String body) {

    // Extract trace context from the POST body.
    TraceContextOrSamplingFlags flags = tracing.propagation()
          .extractor((String message, String key) -> parseTraceFromPubSubBody(message))
          .extract(body);

    // Create a span with context flags from the previously sampled /sendMessage user HTTP request.
    // The fact that `/frompubsub` had sampling turned off is no longer relevant.
    Span newSpan = tracer.nextSpan(flags);
    newSpan.name("got-message-from-pubsub-push");
    newSpan.kind(Kind.CONSUMER);
    long timestamp = tracing.clock(newSpan.context()).currentTimeMicroseconds();
    newSpan.start(timestamp);

    System.out.println("*** Got message from Pub/Sub push; flags = " + flags);

    // simulate doing work
    try {
      Thread.sleep(300);
    } catch(InterruptedException e) {
      // wake up
    }

    // complete span; send to Cloud Trace.
    newSpan.finish();

  }

  private String parseTraceFromPubSubBody(String message) {
    // example of what arrives:
    //  {"subscription":".../pushsubscription",
    //   "message":{"data":"dGVzdCttZXNzYWdlKzE9","messageId":"1",
    //     "attributes":{"b3":"61e98ba6abdecbfb47188242d3f9398e-ec36907be1b2501e-1"}}}
    try {
      JsonNode root = jacksonObjectMapper.readTree(message);
      return root.at("/message/attributes/b3").textValue();
    } catch (IOException e) {
      System.out.println("Parsing trace context failed");
      e.printStackTrace();
      return null;
    }
  }

}
