package com.example;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

  private List<SomeClass> allInstancesOfSomeClass;

  private SomeClass oneSpecificInstance;

  // when there is only one constructor, it will be called by Spring with the correct
  // arguments.
  // bonus: sometimes you want Just This One, Specific bean. Use @Qualifier.
  public WebController(
      List<SomeClass> allInstancesOfSomeClass,
      @Qualifier("someClassObjectFromConfig") SomeClass someClass) {
    this.allInstancesOfSomeClass = allInstancesOfSomeClass;
    this.oneSpecificInstance = someClass;
  }

  @GetMapping("/constructor")
  public String findAllInstancesOfSomeClass() {
    return allInstancesOfSomeClass.stream()
        .map(someClass -> someClass.toString())
        .collect(Collectors.joining("\n<br/>\n"));
  }

  @GetMapping("/constructor-by-name")
  public String findOnlyBeanFromController() {
    return oneSpecificInstance.toString();
  }

  // Notice the `static` here: without it, `object2` will not be part of the
  // findAllInstancesOfSomeClass() output because of a chicken and egg problem: member (non-static)
  // beans will only be discovered after `WebController` object itself is instantiated, and by then
  // the constructor already executed, and it's too late to add another object to the list.
  @Bean
  public static SomeClass someClassObjectFromWebController() {
    return new SomeClass("object2 from web controller");
  }

}
