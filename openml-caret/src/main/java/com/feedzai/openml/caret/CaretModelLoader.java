/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */

package com.feedzai.openml.caret;

import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.provider.descriptor.fieldtype.ParamValidationError;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.openml.provider.model.MachineLearningModelLoader;
import com.feedzai.util.load.LoadModelUtils;
import com.feedzai.util.validate.ValidationUtils;
import com.google.common.collect.ImmutableList;
import com.feedzai.openml.r.GenericRModelLoader;
import com.feedzai.openml.r.ProviderRObject;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.feedzai.openml.r.ProviderRObject.MODEL_VARIABLE;

/**
 * Implementation of the {@link MachineLearningModelLoader}.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 * @see GenericRModelLoader
 */
public class CaretModelLoader extends GenericRModelLoader {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CaretModelLoader.class);

    @Override
    public List<ParamValidationError> validateForLoad(final Path modelPath,
                                                      final DatasetSchema schema,
                                                      final Map<String, String> params) {

        final ImmutableList.Builder<ParamValidationError> errors = ImmutableList.builder();

        errors.addAll(super.validateForLoad(modelPath, schema, params));
        errors.addAll(ValidationUtils.validateModelInDir(modelPath));

        ValidationUtils.validateCategoricalSchema(schema).ifPresent(errors::add);

        return errors.build();
    }

    /**
     * Prepares the workspace created by the {@link RConnection} to be able to load a model and predict instances. To do
     * that it starts by declaring the required package and creates the three requires functions in order to be
     * compatible with {@link GenericRModelLoader}.
     *
     * @param rConnection   {@link RConnection connection} to RServe.
     * @param modelFilePath Path of the model.
     * @throws ModelLoadingException If anything goes wrong.
     */
    @Override
    protected void loadScriptFile(final RConnection rConnection,
                                  final Path modelFilePath) throws ModelLoadingException {
        final String loadModelFn = "%s <- function() {\n" +
                "  temp_Model <- readRDS('%s')\n" +
                "  return(temp_Model)\n" +
                "}";
        final String getClassDistributionFn = "%s <- function(instance) {\n" +
                "  temp_Distribution <- predict(%s, instance, type='prob')\n" +
                "  return(temp_Distribution)\n" +
                "}";
        final String getClassificationFn = "%s <- function(instance) {\n" +
                "  temp_Classification <- predict(%s, instance, type='raw')\n" +
                "  return(temp_Classification)\n" +
                "}";

        try {
            rConnection.voidEval("library(caret)");
            rConnection.voidEval(String.format(loadModelFn,
                                               ProviderRObject.LOAD_MODEL_FN.getName(),
                                               LoadModelUtils.getModelFilePath(modelFilePath)));
            rConnection.voidEval(String.format(getClassDistributionFn,
                                               ProviderRObject.CLASS_DISTRIBUTION_FN.getName(),
                                               MODEL_VARIABLE.getName()));
            rConnection.voidEval(String.format(getClassificationFn,
                                               ProviderRObject.CLASSIFICATION_FN.getName(),
                                               MODEL_VARIABLE.getName()));

        } catch (final RserveException e) {
            logger.error("Unable to prepare the workspace. Error found: " + rConnection.getLastError());
            rConnection.close();
            throw new ModelLoadingException("Unable to prepare the workspace.", e);
        }
    }
}
