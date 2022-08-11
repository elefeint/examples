Running `./gradlew jib --dry-run` with `afterEvaluate` section does not list `:jar` in the list of tasks:

```
:compileJava SKIPPED
:processResources SKIPPED
:classes SKIPPED
:jib SKIPPED

```

But if you remove `afterEvaluate`, then Jib behaves as designed, and adds the `:jar` task.

```
:compileJava SKIPPED
:processResources SKIPPED
:classes SKIPPED
:jar SKIPPED
:jib SKIPPED
```
