# Feedzai OpenML Provider for R
Implementations of the Feedzai OpenML API to allow support for R using
[RServe](https://www.rforge.net/Rserve/doc.html). 

## Modules

### Generic R

You can find in the `openml-generic-r` module a provider that allows
users to load in code that confirms to a simple API.
This is the most powerful approach (yet more cumbersome) since models
can actually hold state.

The provider can be pulled from Maven Central:
```xml
<dependency>
  <groupId>com.feedzai</groupId>
  <artifactId>openml-generic-r</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Caret
Another implementation, available in module `openml-caret` adds support for models built with
[Caret](https://topepo.github.io/caret/index.html).

This module can be pulled from Maven Central:
```xml
<dependency>
  <groupId>com.feedzai</groupId>
  <artifactId>openml-caret</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Building
This is a maven project which you can build using
```bash
mvn clean install
```
