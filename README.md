# Jersey Guice Module

## Introduction
Easy to use Google Guice module for Jersey.

## Installation

### Gradle

```goovy
compile 'io.logz:guice-jersey:1.0.1'
```

### Maven

```xml
<dependency>
  <groupId>io.logz</groupId>
  <artifactId>guice-jersey</artifactId>
  <version>1.0.1</version>
</dependency>
```
## Usage

### Getting Started

```java
public class Main {
    public static void main(String[] args) throws Exception {
        JerseyConfiguration configuration = JerseyConfiguration.builder()
            .addPackage("com.example.resources")
            .addPort(8080)
            .build();
        
        List<Module> modules = new ArrayList<>();        
        modules.add(new JerseyModule(configuration));
        modules.add(new AbstractModule() {
          @Override
          protected void configure() {
            // Your module bindings ...
          }
        });
        
        Guice.createInjector(modules)
          .getInstance(JerseyServer.class).start();
    }
}
```
### Contribution
 - Fork
 - Code
 - ```./mvnw test```
 - Issue a PR :)
