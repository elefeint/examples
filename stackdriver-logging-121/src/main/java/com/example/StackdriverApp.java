package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StackdriverApp {

  private static final Log LOGGER = LogFactory.getLog(StackdriverApp.class);

  public static void main(String[] args) throws Exception {
    SpringApplication.run(StackdriverApp.class, args);

    LOGGER.error("This will be logged");

    System.in.read();
  }
}
