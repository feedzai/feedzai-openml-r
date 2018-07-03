/*
 * Copyright 2018 Feedzai
 *
 * This software is licensed under the Apache License, Version 2.0 (the "Apache License") or the GNU
 * Lesser General Public License version 3 (the "GPL License"). You may choose either license to govern
 * your use of this software only upon the condition that you accept all of the terms of either the Apache
 * License or the LGPL License.
 *
 * You may obtain a copy of the Apache License and the LGPL License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Apache License
 * or the LGPL License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the Apache License and the LGPL License for the specific language governing
 * permissions and limitations under the Apache License and the LGPL License.
 *
 */

package com.feedzai.openml.r;

import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.model.MachineLearningModel;
import com.feedzai.openml.provider.descriptor.fieldtype.ParamValidationError;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.openml.provider.model.MachineLearningModelLoader;
import com.feedzai.openml.util.load.LoadSchemaUtils;
import com.feedzai.openml.util.validate.ClassificationValidationUtils;
import com.feedzai.openml.util.validate.ValidationUtils;
import com.google.common.collect.ImmutableList;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link MachineLearningModelLoader}.
 * <p>
 * This class is responsible for the initialization of a {@link MachineLearningModel} that was generated in R. These
 * instances receive an instance of {@link RConnection} that is initialized by this class. That object is being used to
 * connect to a Rserve server that allows to execute R code in a separate workspace for each connection. This connection
 * should be closed when the {@link MachineLearningModel model} is closed.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
public class GenericRModelLoader implements MachineLearningModelLoader<ClassificationGenericRModel> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(GenericRModelLoader.class);

    /**
     * Relative path that is expected to contain a R script with the objects to be loaded in the R workspace.
     */
    private static final String SCRIPT_PATH = File.separator + "scripts" + File.separator + "classifier.R";

    @Override
    public ClassificationGenericRModel loadModel(final Path modelPath,
                                                 final DatasetSchema schema) throws ModelLoadingException {

        logger.info(String.format("Trying to load a model in path [%s]...", modelPath));

        final RConnection rConnection;

        rConnection = createConnection();
        loadScriptFile(rConnection, modelPath);
        validateRWorkspace(rConnection);

        loadModel(rConnection);

        final ClassificationGenericRModel classificationGenericRModel = new ClassificationGenericRModel(rConnection, schema);
        ClassificationValidationUtils.validateClassificationModel(schema, classificationGenericRModel);

        logger.info("Model loaded successfully.");
        return classificationGenericRModel;
    }

    @Override
    public DatasetSchema loadSchema(final Path modelPath) throws ModelLoadingException {
        return LoadSchemaUtils.datasetSchemaFromJson(modelPath);
    }

    @Override
    public List<ParamValidationError> validateForLoad(final Path modelPath,
                                                      final DatasetSchema schema,
                                                      final Map<String, String> params) {
        return ValidationUtils.baseLoadValidations(schema, params);
    }

    /**
     * Performs the necessary steps to create a connection to RServe to be used to load a model and classify events.
     *
     * @return a {@link RConnection connection} to RServe.
     * @throws ModelLoadingException If anything goes wrong.
     */
    private RConnection createConnection() throws ModelLoadingException {
        final RConnection rConnection;
        try {
            // Creates a connection to RServe, by default it assumes that there is a local instance running on port 6311
            rConnection = new RConnection();
        } catch (final RserveException e) {
            logger.error("Could not connect to RServe.");
            throw new ModelLoadingException("Could not connect to RServe", e);
        }

        logger.info("Connection to R established.");
        return rConnection;
    }

    /**
     * Loads the objects defined in {@link #SCRIPT_PATH} to the R workspace created by {@link RConnection}.
     *
     * @param rConnection   {@link RConnection connection} to RServe.
     * @param modelFilePath Path of the directory of the model.
     * @throws ModelLoadingException If anything goes wrong.
     */
    protected void loadScriptFile(final RConnection rConnection,
                                  final Path modelFilePath) throws ModelLoadingException {
        try {
            rConnection.voidEval(String.format("source('%s%s')", modelFilePath.toAbsolutePath().toString(), SCRIPT_PATH));
        } catch (final RserveException e) {
            logger.error("Could not load the script file. Error found: " + rConnection.getLastError(), e);
            rConnection.close();
            throw new ModelLoadingException("An error was found during the load of the script file", e);
        }
    }

    /**
     * Validates that the R workspace contains the required functions, in order to the provider be able to load a model
     * and classify events.
     *
     * @param rConnection {@link RConnection connection} to RServe.
     * @throws ModelLoadingException If anything goes wrong.
     */
    private void validateRWorkspace(final RConnection rConnection) throws ModelLoadingException {
        final List<String> requiredFunctions = ImmutableList.<String>builder()
                .add(ProviderRObject.LOAD_MODEL_FN.getName())
                .add(ProviderRObject.CLASS_DISTRIBUTION_FN.getName())
                .add(ProviderRObject.CLASSIFICATION_FN.getName())
                .build();

        for (final String requiredFunction : requiredFunctions) {
            try {
                final byte functionExists = rConnection.eval(String.format(
                        "exists('%s', mode = 'function')",
                        requiredFunction
                )).asBytes()[0];

                if (functionExists == 0) {
                    final String errorMsg = String.format(
                            "Could not found the function [%s] in the R workspace.",
                            requiredFunction
                    );
                    logger.error(errorMsg);
                    rConnection.close();
                    throw new ModelLoadingException(errorMsg);
                }
            } catch (final REXPMismatchException | RserveException e) {
                final String errorMsg = "An error was found during the validation of the workspace";
                logger.error(errorMsg + " Error found: " + rConnection.getLastError(), e);
                rConnection.close();
                throw new ModelLoadingException(errorMsg, e);
            }
        }
    }

    /**
     * Executes the R function to load a model in the R workspace.
     *
     * @param rConnection {@link RConnection connection} to RServe.
     * @throws ModelLoadingException If there is an error during the load of the model.
     */
    private void loadModel(final RConnection rConnection) throws ModelLoadingException {
        try {
            rConnection.voidEval(String.format("%s <- %s()",
                                               ProviderRObject.MODEL_VARIABLE.getName(),
                                               ProviderRObject.LOAD_MODEL_FN.getName()));
        } catch (final RserveException e) {
            logger.error("Could not load the model. Error found: " + rConnection.getLastError(), e);
            rConnection.close();
            throw new ModelLoadingException("An error was found during the import of the model", e);
        }
    }
}
