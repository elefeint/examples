# Example of running DevAppServer with Java 17

*Make sure you are running Java 17.*

1. First, set up the Cloud SDK / Cloud CLI location

```
export GCP_CLI=[ACTUAL_LOCATION]
```

2. Then, build the app.
```
mvn clean install
```

2. Then, try running dev server as is

    ```
    $GCP_CLI/bin/java_dev_appserver.sh ./target/springboot-helloworld-j17-0.0.1-SNAPSHOT
    ```

    This results in JPMS errors:
    ```asciidoc
    java.lang.RuntimeException: Unable to create a DevAppServer
        at com.google.appengine.tools.development.DevAppServerFactory.doCreateDevAppServer(DevAppServerFactory.java:378)
        at com.google.appengine.tools.development.DevAppServerFactory.createDevAppServer(DevAppServerFactory.java:310)
        at com.google.appengine.tools.development.DevAppServerMain$StartAction.apply(DevAppServerMain.java:384)
        at com.google.appengine.tools.util.Parser$ParseResult.applyArgs(Parser.java:58)
        at com.google.appengine.tools.development.DevAppServerMain.run(DevAppServerMain.java:258)
        at com.google.appengine.tools.development.DevAppServerMain.main(DevAppServerMain.java:249)
    Caused by: java.lang.ExceptionInInitializerError
        at com.google.appengine.tools.development.DevAppServerImpl.<init>(DevAppServerImpl.java:135)
        at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
        at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:77)
        at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
        at java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:499)
        at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:480)
        at com.google.appengine.tools.development.DevAppServerFactory.doCreateDevAppServer(DevAppServerFactory.java:363)
        ... 5 more
    Caused by: java.lang.reflect.InaccessibleObjectException: Unable to make static java.net.URLStreamHandler java.net.URL.getURLStreamHandler(java.lang.String) accessible: module java.base does not "opens java.net" to unnamed module @2e222612
        at java.base/java.lang.reflect.AccessibleObject.checkCanSetAccessible(AccessibleObject.java:354)
        at java.base/java.lang.reflect.AccessibleObject.checkCanSetAccessible(AccessibleObject.java:297)
        at java.base/java.lang.reflect.Method.checkCanSetAccessible(Method.java:199)
        at java.base/java.lang.reflect.Method.setAccessible(Method.java:193)
        at com.google.appengine.tools.development.StreamHandlerFactory.<clinit>(StreamHandlerFactory.java:52)
        ... 12 more
    
    ```


3. Adding the necessary access overrides gets us past JPMS.

    ```asciidoc
   $GCP_CLI/bin/java_dev_appserver.sh --jvm_flag="--add-opens" --jvm_flag="java.base/java.net=ALL-UNNAMED" \
       --jvm_flag="--add-opens" --jvm_flag="java.base/sun.net.www.protocol.http=ALL-UNNAMED"  \
       --jvm_flag="--add-opens" --jvm_flag="java.base/sun.net.www.protocol.https=ALL-UNNAMED" \ 
       ./target/springboot-helloworld-j17-0.0.1-SNAPSHOT
    ```

   But now there is an NPE somewhere

   ```asciidoc
    java.lang.NullPointerException
    at java.base/java.util.concurrent.ConcurrentHashMap.putVal(ConcurrentHashMap.java:1011)
    at java.base/java.util.concurrent.ConcurrentHashMap.put(ConcurrentHashMap.java:1006)
    at java.base/java.util.Properties.put(Properties.java:1301)
    at java.base/java.util.Collections$CheckedMap.put(Collections.java:3739)
    at com.google.appengine.tools.development.SharedMain.setTimeZone(SharedMain.java:197)
    at com.google.appengine.tools.development.SharedMain.postServerActions(SharedMain.java:166)
    at com.google.appengine.tools.development.DevAppServerMain$StartAction.apply(DevAppServerMain.java:399)
    at com.google.appengine.tools.util.Parser$ParseResult.applyArgs(Parser.java:58)
    at com.google.appengine.tools.development.DevAppServerMain.run(DevAppServerMain.java:258)
    at com.google.appengine.tools.development.DevAppServerMain.main(DevAppServerMain.java:249)
    
    ```

And now I am well and truly stuck.


# Original instructions

## Spring Boot Application Google App Engine Standard with Java 11

This sample shows how to deploy a [Spring Boot](https://spring.io/projects/spring-boot)
application to Google App Engine standard.

See the [Quickstart for Java in the App Engine Standard Environment][ae-docs] for more
detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/standard/java11/quickstart

## Setup

See [Prerequisites](../README.md#Prerequisites).

## Deploying

```bash
gcloud app deploy
```

To view your app, use command:
```
gcloud app browse
```
Or navigate to `https://<your-project-id>.appspot.com`.
