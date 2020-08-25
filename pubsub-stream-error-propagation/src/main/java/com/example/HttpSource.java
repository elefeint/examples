package com.example;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableBinding(Source.class)
@RestController
public class HttpSource {

	@Autowired
	private Source source;

	@GetMapping("pubsub-error")
	public void triggerPubsubError() {
		String message = "cause client library to fail on assert [" + (new Random()).nextInt() + "]";
		System.out.println("Publishing " + message);

		try {
			this.source.output().send(
					new GenericMessage<String>(message, ImmutableMap.of("trigger-error", true)));
		} catch (Exception e) {
			System.out.println("*** As expected, the error was propagated back to sender: " + e.getMessage());
		}

	}


	@GetMapping("pubsub-success")
	public void pubsubPublishSuccess() {
		String message = "happy path message [" + (new Random()).nextInt() + "]";
		System.out.println("Publishing " + message);

		try {
			this.source.output().send(
					new GenericMessage<String>(message));
		} catch (Exception e) {
			System.out.println("*** This exception should not happen (unless you turned internet off)! "
					+ e.getMessage());
		}

	}


}
