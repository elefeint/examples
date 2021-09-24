package com.example;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// All `@Configuration`-annotated classes will be found and registered by Spring Boot.
// Guess what's annotated with @Configuration? Other Spring/SpringBoot annotations, such as
// `@SpringBootApplication` and `@RestController`
@Configuration
public class SomeConfig  implements ApplicationContextAware  {

  @Bean
  public SomeClass someClassObjectFromConfig() {
    return new SomeClass("object3 from @Configuration");
  }


  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    System.out.println("***** " + applicationContext.getBeansOfType(SomeClass.class));
  }
}
