Constructor dependency injection documentation is [here](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using.spring-beans-and-dependency-injection).

Relevant bits:
* constructor injection is the best practice
* Adding `@SpringBootApplication` annotation means any beans found in any Spring component-annotated classes will be automatically found and added to Spring context.


How to use this sample:
* Switch into this directory and run `mvn spring-boot:run`.
This will start a web server (because of the org.springframework.boot:spring-boot-starter-web dependency in pom.xml). Fun fact -- the reason you can even invoke the `spring:boot:run` goal is that this sample inherits from `org.springframework.boot:spring-boot-starter-parent` parent POM, which includes the `spring-boot-maven-plugin` plugin configuration.
* Go to http://localhost:8080/constructor; look at `WebController.java`
* Go to http://localhost:8080/autowired; look at `WebControllerWithAutowired.java`
* Go to http://localhost:8080/many-constructors; look at `WebControllerWithMultipleConstructors.java`
* Bonus. Go to http://localhost:8080/constructor-by-name; look at `WebController.java` again!