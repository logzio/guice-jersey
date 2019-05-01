# Jersey Guice Module
[![Build Status](https://travis-ci.org/logzio/guice-jersey.svg?branch=master)](https://travis-ci.org/logzio/guice-jersey)
[![Coverage Status](https://coveralls.io/repos/logzio/guice-jersey/badge.svg?branch=master)](https://coveralls.io/r/logzio/guice-jersey?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.logz/guice-jersey/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.logz/guice-jersey)

## Introduction
Jersey comes with its own dependency injection framework for instantiating its classes.
If you're using Guice as your dependency injection framework, and you want to inject your own classes into the JAX-RS classes you created - such as Resources and Filters - you need to bridge the gap between the two DI frameworks.
This module aims to do just that by booting Jetty based Jersey server and initializing the bridge between HK2 and Guice.

## Installation

### Gradle

```groovy
compile 'io.logz:guice-jersey:1.0.7'
```

### Maven

```xml
<dependency>
  <groupId>io.logz</groupId>
  <artifactId>guice-jersey</artifactId>
  <version>1.0.7</version>
</dependency>
```
## Usage

### Getting Started

1. Add `JerseyModule` to your Guice Injector
2. Configure packages to scan for resources and a port to expose
3. Get instance of `JerseyServer` and start consuming your Restful resources

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

## Motivation

### Why Jersey?
I was looking for REST library I can drop in place and it will integrate seamlessly with my Guice based project.

My requirements from the rest library were:
- Being able to inject existing services used with Guice
- Integration with Bean Validation
- Serve resources asynchronously

Google search yielded the following:
- Rapidoid - Simple REST framework with async support but no integration with Guice at the time
- RESTEasy - Implementation of the JAX-RS spec. Has examples of usage with guice, writing async resources and Bean Validation support
but no easy way to use with both async resources and Guice.
- Jersey - Reference implementation of the JAX-RS spec, same as RESTEasy there was no easy way to answer both async and Guice requirements together.

After spending roughly 1.5 days fighting with those libraries and not getting what I wanted, I decided to go with Jersey as this is RI of well defined spec.

### Why this module?
I could not find a library which binded the two together: Jersey and Guice. I tried the following:
- https://github.com/Squarespace/jersey2-guice:
When I started to read the getting started and saw the example this solution looked too complex and
I looked for a simple way to configure and also wire the web server for you.
- https://mvnrepository.com/artifact/com.sun.jersey.contribs/jersey-guice:
Works only with version 1.x of Jersey.

Without any working solution, I sat down to write my own.

### Contribution
 - Fork
 - Code
 - ```./mvnw test```
 - Issue a PR :)
