package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// All `@Configuration`-annotated classes will be found and registered by Spring Boot.
// Guess what's annotated with @Configuration? Other Spring/SpringBoot annotations, such as
// `@SpringBootApplication` and `@RestController`
@Configuration
public class SomeConfig {

  @Bean
  public SomeClass someClassObjectFromConfig() {
    return new SomeClass("object3 from @Configuration");
  }
}
