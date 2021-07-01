package com.example;

// That's just a random POJO (plain old Java object) to show that Spring beans can be of any type.
public class SomeClass {

  private String name;

  public SomeClass(String name) {
    this.name = name;
  }

  public String toString() {
    return this.name;
  }

}
