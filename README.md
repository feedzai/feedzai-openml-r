# Feedzai OpenML Provider for R
[![Build Status](https://travis-ci.com/feedzai/feedzai-openml-r.svg?branch=hf-0.4.X)](https://travis-ci.com/feedzai/feedzai-openml-r)
[![codecov](https://codecov.io/gh/feedzai/feedzai-openml-r/branch/hf-0.4.X/graph/badge.svg)](https://codecov.io/gh/feedzai/feedzai-openml-r)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/206f6d793ad44c06a3039a185a30a8a3?branch=hf-0.4.X)](https://www.codacy.com/app/feedzai/feedzai-openml-r?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=feedzai/feedzai-openml-r&amp;utm_campaign=Badge_Grade)

Implementations of the Feedzai OpenML API to allow support for machine
learning models in the [R programming language](https://www.r-project.org/)
using [RServe](https://www.rforge.net/Rserve/doc.html). 

## Modules

### Generic R
[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/feedzai/openml-generic-r/maven-metadata.xml.svg)](https://mvnrepository.com/artifact/com.feedzai/openml-generic-r)

The `openml-generic-r` module contains a provider that allows
developers to load R code that conforms to a simple API.
This is the most powerful approach (yet more cumbersome) since models
can actually hold state.

The provider can be pulled from Maven Central:
```xml
<dependency>
  <groupId>com.feedzai</groupId>
  <artifactId>openml-generic-r</artifactId>
  <!-- See project tags for latest version -->
  <version>0.4.0</version>
</dependency>
```

### Caret
[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/feedzai/openml-caret/maven-metadata.xml.svg)](https://mvnrepository.com/artifact/com.feedzai/openml-caret)

The implementation in the `openml-caret` module adds support for models built with
[Caret](https://topepo.github.io/caret/index.html).

This module can be pulled from Maven Central:
```xml
<dependency>
  <groupId>com.feedzai</groupId>
  <artifactId>openml-caret</artifactId>
  <!-- See project tags for latest version -->
  <version>0.4.0</version>
</dependency>
```

## Building
This is a Maven project which you can build using
```bash
mvn clean install
```


## Prerequisites for running tests

To use these providers you need to have [R Project](https://www.r-project.org/) installed in your environment.
After installing R, you need to install the R packages that the provider uses. The easiest way is to install them from [CRAN](https://cran.r-project.org/).

Note that this section only describes the known prerequisites that are common to any model generated in R.
Before importing a model you need to ensure that the required packages for that model are also installed.

Finally you must install Rserve.


### Example in CentOS7:

Execute the following bash commands:

```bash
# repo that has R
yum -y install epel-release;

# needed for R dependencies
yum -y install libcurl-devel openssl-devel gsl-devel libwebp-devel librsvg2-devel R;

# start R
R
```

Execute the following R instructions:
```
# Load caret
install.packages("caret", dependencies=TRUE, repos = "http://cran.radicaldevelop.com/")

# Load all classification model implementations
# https://topepo.github.io/caret/available-models.html
# https://github.com/tobigithub/caret-machine-learning/wiki/caret-ml-setup
library(caret)
modNames <- unique(modelLookup()[modelLookup()\$forClass,c(1)])
install.packages(modNames, dependencies=TRUE, repos = "http://cran.radicaldevelop.com/")

# Load Rserve (needed for Pulse <-> R communication)
install.packages("Rserve", dependencies=TRUE, repos = "http://cran.radicaldevelop.com/"})
```

### Docker 
Feedzai has built a helpful docker image for testing, [available on docker hub](https://hub.docker.com/r/feedzai/rserve-caret/),
that is being used in this repository's continuous integration. See the [travis-ci configuration](.travis.yml) commands
on how to use it.
