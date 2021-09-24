package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// That's just a random POJO (plain old Java object) to show that Spring beans can be of any type.
public class SomeClass {

  private String name;

  private List<String> whosDoneIt = new ArrayList<>();

  public SomeClass(String name) {
    this.name = name;
  }

  public void addMeddler(String meddler) {
    this.whosDoneIt.add(meddler);
  }

  public String toString() {
    return this.name + ": " +
        "<br/>&nbsp;&nbsp;" + this.whosDoneIt.stream().collect(Collectors.joining(",<br/>&nbsp;&nbsp;"))
        + "<br/>";
  }

}
