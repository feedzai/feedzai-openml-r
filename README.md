# Feedzai OpenML Provider for R
Implementations of the Feedzai OpenML API to allow support for machine
learning models in the [R programming language](https://www.r-project.org/)
using [RServe](https://www.rforge.net/Rserve/doc.html). 

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


## Prerequisites for running tests

To use these providers you need to have [R Project](https://www.r-project.org/) installed on your environment.
After installing R, you need to install the R packages that are being used by the provider. The easiest way is to install them from CRAN.

It should be taken into account that this section only describes the known prerequisites that are common to any model generated in R.
Before importing a model you need to ensure the required packages for that model are also installed.

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

