This sample demonstrates the impact of a (simulated) server-side GOAWAY response on the client library and the reactive layer built on it.

## Setup

Standard gcloud installation with default credentials is the easiest.

## With Spring Cloud GCP version 2.0.5, a new property `retryableCodes` accepts a custom list of retriable codes. 
This allows the client library to retry automatically.

```
spring.cloud.gcp.pubsub.subscriber.retryableCodes=UNKNOWN,ABORTED,UNAVAILABLE,INTERNAL
```
## How was GOAWAY simulated?

GOAWAY handover from Netty to gRPC happens [here](https://github.com/grpc/grpc-java/blob/735b85fb335238354042c2dd01688bec6105b824/netty/src/main/java/io/grpc/netty/NettyClientHandler.java#L280).

`PubsubGrpcOccasionallyFailingInterceptor` simulates GOAWAY by creating a similar error message and allowing it to bubble up through regular gRPC mechanisms.

```
Status.INTERNAL
                .withDescription("http2 exception")
                .withCause(new Http2Exception(Http2Error.STREAM_CLOSED, "Stream closed before write could take place"))
```

You can change the number of successful pulls by changing the constructor parameter for `PubsubGrpcOccasionallyFailingInterceptor` -- the interceptor will always return N successful results from actual server-side Pub/Sub, followed by N+1st failure simulating GOAWAY server response.
It will then continue returning another batch of N successful responses, if this is requested.

## To test the Pub/Sub client library directly

In this directory, run:

```
mvn test -Dgcp.project=[YOUR_PROJECT] -Dpubsub.subscription=[YOUR_SUBSCRIPTION]
```

Observe that when `.setRetryableCodes(StatusCode.Code.INTERNAL)` is present, the retries work correctly.

If you remove `.setRetryableCodes(StatusCode.Code.INTERNAL)` and rerun the app, even though retry settings are set up programmatically in `PubSubClientLibraryTest`, the test fails on the GOAWAY message.

```
RetrySettings retrySettings = RetrySettings.newBuilder()
        .setMaxAttempts(maxRetryAttempts)
        .setTotalTimeout(Duration.ofMinutes(10))
        .build();
```


## To test Spring Boot application with Reactive Pub/Sub

In this directory, run:

```
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dpubsub.subscription=[YOUR_SUBSCRIPTION]"
```

The application can be terminated by pressing any key on the terminal.

Observe that despite total timeout and max attempts properties being provided through `application.properties`, the pulling stops as soon as GOAWAY error equivalent is received.
As written, the example reactive poll will NOT retry, so it will stop after the first failure (N+1st attempt). Try different reactive retry strategies to see what works best for your application.

You can experiment with other [Spring Cloud GCP Pub/Sub settings]() or with reactor retries here -- in a production application, some form of retry strategy should be provided.

