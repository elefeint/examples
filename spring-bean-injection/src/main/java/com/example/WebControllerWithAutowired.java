package com.example;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebControllerWithAutowired {

  @Autowired
  private List<SomeClass> allInstancesOfSomeClass;

  // There is no user-defined constructor, so Java will add a no-argument default constructor
  // that does not do anything particularly interesting.

  // But our `allInstancesOfSomeClass` list will still be initialized correctly because of the
  // `@Autowired` annotation on the field itself.
  // This approach is less testable than constructor injection.
  @GetMapping("/autowired")
  public String getAutowiredObjects() {
    return this.allInstancesOfSomeClass.stream()
        .map(someClass -> someClass.toString())
        .collect(Collectors.joining("\n<br/>\n"));
  }
}
