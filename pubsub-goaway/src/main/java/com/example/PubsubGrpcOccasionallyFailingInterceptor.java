package com.example;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2Error;
import io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2Exception;
import java.util.Map;

/**
 * gRPC interceptor that returns N responses from server Pub/Sub,
 * and then fails with GOAWAY on N+1st attempt
 */
class PubsubGrpcOccasionallyFailingInterceptor implements ClientInterceptor {

  private Map<String, Object> cannedResponses;

  private int remainingSuccessful;

  /**
   *
   * @param attemptsBeforeFailing how many pull requests to pass through to Pub/Sub successfully before intentionally failing
   */
  PubsubGrpcOccasionallyFailingInterceptor(int attemptsBeforeFailing) {
    System.out.println("*** Created failing interceptor");
    this.remainingSuccessful = attemptsBeforeFailing;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    System.out.println("*** Received pull request");

    remainingSuccessful--;

    if (remainingSuccessful == 0) {
      System.out.println("*** Ran out of successful attempts; returning GOAWAY");
      remainingSuccessful = 5;

      return new FailingClientCall();
    }

    return next.newCall(method, callOptions);
  }

  /**
   * Client call that always fails with a simulation of server-side GOAWAY
   */
  static class FailingClientCall extends ClientCall {

    @Override
    public void start(Listener listener, Metadata metadata) {

        listener.onClose(
            Status.INTERNAL
                .withDescription("http2 exception")
                .withCause(new Http2Exception(Http2Error.STREAM_CLOSED, "Stream closed before write could take place")), metadata);
    }

    @Override
    public void request(int i) {
    }

    @Override
    public void cancel(String s, Throwable throwable) {
    }

    @Override
    public void halfClose() {
    }

    @Override
    public void sendMessage(Object o) {
    }
  }

}


