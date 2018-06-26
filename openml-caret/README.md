OpenML Provider for caret models
------------------------------------------------------------

This module contains an [OpenML provider](https://github.com/feedzai/feedzai-openml) for loading models trained with the [Caret package](http://caret.r-forge.r-project.org/).

The implemented "Classifier" should define the following methods: 

```caret

# loads a model. This can be a no-op if there's no need to save state  
loadModel <- function() {
    stop("This must be implemented by a concrete provider")
}

# score the instance stored in the 'instance' global var and returns an array with the probability for each of the classes
getClassDistribution <- function() {
    stop("This must be implemented by a concrete provider")
}

# returns the predicted class of the instance stored in the 'instance' global var
classify <- function() {
    stop("This must be implemented by a concrete provider")
}
``` 

## Usage

When the user imports a model to the Feedzai platform using this provider, the import assumes a folder called ```model``` containing the code in an .rds file.
The import assumes a serialized R model.

    .
    └── random-forest
        └── model
            └── classifier.rds
