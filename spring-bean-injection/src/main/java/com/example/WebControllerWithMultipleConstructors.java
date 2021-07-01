package com.example;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebControllerWithMultipleConstructors {

  private List<SomeClass> allInstancesOfSomeClass;

  public WebControllerWithMultipleConstructors() {
  }

  // Now there are too many constructors for Spring Boot to figure out what's going on, so you have
  // to tell it to use this specific one with @Autowired on the constructor.
  // Try commenting out @Autowired and going to http://localhost:8080/many-constructors -- you'll
  // get an NPE (null pointer exception) in `getAutowiredObjects()` because
  // `allInstancesOfSomeClass` will still be null.
  @Autowired
  public WebControllerWithMultipleConstructors(List<SomeClass> allInstancesOfSomeClass) {
    this.allInstancesOfSomeClass = allInstancesOfSomeClass;
  }

  // Now there are too many constructors for Spring Boot to figure out what's going on, so you have
  // to tell it to use this specific one with @Autowired
  public WebControllerWithMultipleConstructors(
      List<SomeClass> allInstancesOfSomeClass,
      String someUnusedString) {
  }

  @GetMapping("/many-constructors")
  public String getAutowiredObjects() {
    return this.allInstancesOfSomeClass.stream()
        .map(someClass -> someClass.toString())
        .collect(Collectors.joining("\n<br/>\n"));
  }
}
