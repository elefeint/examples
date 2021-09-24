package com.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class CustomBeanPostProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
   if (bean instanceof SomeClass) {
      System.out.println("***** hi there before init: " + beanName);
      ((SomeClass)bean).addMeddler("CustomBeanPostProcessor.postProcessBeforeInitialization");
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    if (bean instanceof SomeClass) {
      System.out.println("***** hi there after init: " + beanName);
      ((SomeClass)bean).addMeddler("CustomBeanPostProcessor.postProcessAfterInitialization");
    }
    return bean;
  }
}
