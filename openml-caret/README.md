OpenML Provider for caret models
------------------------------------------------------------

This module contains an OpenML provider for loading custom code that implements
Feedzai's caret API.

The implemented "Classifier" should define the following methods: 

```caret
# Please add necessary components here.
``` 

## Usage

When the user imports a model to the Feedzai platform using this provider, the import assumes a folder called ```model``` containing the code in an .rds file.
The import assumes a serialized R model.

    .
    └── random-forest
        └── model
            └── classifier.rds
