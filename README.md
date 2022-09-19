# rc-geometry-designer

### App Description

WIP

### Built With

Java 14, JavaFX 14, FXML, CSS

### Project Configuration

Using Java 14 SDK and Java 14 as a project language level.

### Tested With

JUnit 5, TestFx, AssertJ, Mockito

### Deployment

The project is built using Gradle 6.3 and using [JLink](https://github.com/beryx/badass-jlink-plugin) v.2.1.7 plugin to deploy the application - jpackage and application image.

To build the application - fat jar creation:

```groovy
gradle build
```

Application image creation:

```groovy
gradle jlink
```

To run the application image:

```
build\image\bin\java.exe -m com.radsoltan/com.radsoltan.EntryPoint
```

Installation file creation (uses incubator modules jdk.incubator.jpackage):

```groovy
gradle jpackage
```

### Authors

Radoslaw Soltan