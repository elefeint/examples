package com.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

public class CustomBeanDefinitionRegistryPostProcessor implements
    BeanDefinitionRegistryPostProcessor {

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry)
      throws BeansException {
    beanDefinitionRegistry.registerBeanDefinition(
        "someObjectDynamic",
        BeanDefinitionBuilder.genericBeanDefinition(
            SomeClass.class,
            () -> {
              System.out.println("***** dynamic instance of SomeClass being created");
              return new SomeClass("bean registered dynamically by CustomBeanDefinitionRegistryPostProcessor");
            }
        ).getBeanDefinition());
    System.out.println("***** in CustomBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry");
  }

  @Override
  public void postProcessBeanFactory(
      ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    System.out.println("***** in CustomBeanDefinitionRegistryPostProcessor.postProcessBeanFactory");
  }
}
