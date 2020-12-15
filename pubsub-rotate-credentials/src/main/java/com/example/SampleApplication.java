package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SampleApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SampleApplication.class, args);
    System.out.println("Press any key to exit");
    System.in.read();
  }

}
