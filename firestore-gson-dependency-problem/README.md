1. Put a valid GCP project in `TestFirestore`.
2. Run the app, obeserve failure: `java.lang.ClassNotFoundException: com.google.gson.stream.JsonReader`
3. Update firestore version to 3.3.0, run the app, observe no failure.


Why? Because client library making gson `test` scope blocks what was otherwise a transitive compile dependency from `google-http-java-client`.

Try some variations of `mvn dependency:tree -Dincludes=com.google.code.gson:gson` with 3.3.0 and 3.4.0.

