package org.example;

import org.example.PubSubApplication.PubsubOutboundGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

// TODO:   // tag::autowireGateway[] should not show up in tutorial

@RestController
public class WebAppController {
  @Autowired
  private PubsubOutboundGateway messagingGateway;

  @PostMapping("/publishMessage")
  public RedirectView publishMessage(@RequestParam("message") String message) {
    messagingGateway.sendToPubsub(message, "custom header value");
    return new RedirectView("/");
  }
}