package com.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

// Any beans returned by methods inside of any class marked with `@SpringBootApplication`
// will be registered with Spring context.
// You would not do this in production (you'd have dedicated `@Configuration` classes), but we
// often do this in sample applications where only a couple of extra beans are needed to demonstrate
// a pattern.
@SpringBootApplication
public class SampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(SampleApplication.class, args);
  }

  @Bean
  public SomeClass someClassObjectFromSpringBootApplicationAnnotation() {
    return new SomeClass("object1 from @SpringBootAnnotation");
  }

  /*
   * Order:
   * postProcessBeanDefinitionRegistry
   * postProcessBeanFactory
   */
  @Bean
  public CustomBeanDefinitionRegistryPostProcessor customBeanDefinitionRegistryPostProcessor() {
    return new CustomBeanDefinitionRegistryPostProcessor();
  }

  @Bean
  public BeanPostProcessor customBeanPostProcessor() {
    return new CustomBeanPostProcessor();
  }

}
