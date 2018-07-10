Reusable Python Logic for OpenML
------------------------------------------------------------

This module contains classes and logic related with the import of R models to the Feedzai platform.

* __ClassificationGenericRModel__: classification object used to represent a model generated in R. It is responsible for the interaction with a `MachineLearningModel` that was generated in R.

* __GenericRModelLoader__: implementation of the `MachineLearningModelLoader` class. It is responsible for the initialization of a `MachineLearningModel` that was generated in R.

* __ProviderRObject__: enumeration with the name of the objects created in a workspace of R by the generic R provider
