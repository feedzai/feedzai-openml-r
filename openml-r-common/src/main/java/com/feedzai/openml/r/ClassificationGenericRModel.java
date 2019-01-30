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

import com.feedzai.openml.util.data.encoding.EncodingHelper;
import com.feedzai.openml.data.Instance;
import com.feedzai.openml.data.schema.AbstractValueSchema;
import com.feedzai.openml.data.schema.CategoricalValueSchema;
import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.data.schema.FieldSchema;
import com.feedzai.openml.model.ClassificationMLModel;
import com.feedzai.openml.model.MachineLearningModel;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

/**
 * Classification object used to represent a {@link MachineLearningModel model} generated in R.
 * <p>
 * This class is responsible for the interaction with a {@link MachineLearningModel} that was generated in R. It
 * receives an instance of {@link RConnection} that is being used to connect to a Rserve server. This connection allows
 * to execute R code in a independent workspace. The {@link RConnection} is not thread-safe and so we should block the
 * access to this instance to be only be accessible to one thread at the time. The connection to R should be closed when
 * the {@link MachineLearningModel model} is closed.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
public class ClassificationGenericRModel implements ClassificationMLModel {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ClassificationGenericRModel.class);

    /**
     * A {@link RConnection connection} to RServe to execute R code. This instance should only be closed when the model
     * is closed, otherwise it will be necessary to load again the model.
     */
    private final RConnection rConnection;

    /**
     * The {@link DatasetSchema} the model uses.
     */
    private final DatasetSchema schema;

    /**
     * Constructor for a {@link ClassificationGenericRModel}.
     *
     * @param rConnection {@link RConnection connection} to RServe. This instance is only being used by this object and
     *                    thus it is responsible to close it.
     * @param schema      The {@link DatasetSchema} the model uses.
     */
    public ClassificationGenericRModel(final RConnection rConnection,
                                       final DatasetSchema schema) {
        Preconditions.checkArgument(schema.getTargetFieldSchema().isPresent(), "R Models require a target variable.");
        this.rConnection = rConnection;
        this.schema = schema;
    }

    @Override
    public double[] getClassDistribution(final Instance instance) {
        try {
            final RList list = evaluateInstance(
                    ProviderRObject.CLASS_DISTRIBUTION_FN.getName(),
                    instance
            ).asList();

            final Set<String> targetValues = getTargetValues();
            final double[] classDistribution = new double[targetValues.size()];
            for (int i = 0; i < classDistribution.length; i++) {
                classDistribution[i] = list.at(i).asDouble();
            }
            return classDistribution;

        } catch (final Exception e) {
            logger.warn("Error during instance evaluation. Error found: " + this.rConnection.getLastError());
            throw new RuntimeException("Error during instance evaluation.", e);
        }
    }

    @Override
    public int classify(final Instance instance) {
        try {
            final String predictedClass = evaluateInstance(
                    ProviderRObject.CLASSIFICATION_FN.getName(),
                    instance
            ).asString();
            return Iterables.indexOf(getTargetValues(), predictedClass::equals);

        } catch (final Exception e) {
            logger.warn("Error during instance evaluation. Error found: " + this.rConnection.getLastError());
            throw new RuntimeException("Error during instance evaluation.", e);
        }
    }

    @Override
    public boolean save(final Path dir, final String name) {
        // R models are only load-able and thus cannot be saved.
        return false;
    }

    @Override
    public DatasetSchema getSchema() {
        return this.schema;
    }

    @Override
    public synchronized void close() {
        this.rConnection.close();
    }

    /**
     * If the target field is categorical then it returns a set with the nominal values, otherwise returns an empty set.
     *
     * @return the values of the target variable.
     */
    private Set<String> getTargetValues() {
        return this.schema.getTargetFieldSchema()
                .map(FieldSchema::getValueSchema)
                .filter(CategoricalValueSchema.class::isInstance)
                .map(CategoricalValueSchema.class::cast)
                .map(CategoricalValueSchema::getNominalValues)
                .orElse(ImmutableSortedSet.of());
    }

    /**
     * Calls a R {@code function} to evaluate an {@code instance}.
     * This function depends on the {@link RConnection connection to Rserve} to run R code. The connection to Rserve
     * isn't thread-safe and so this method needs to be synchronized.
     *
     * @param function Name of the R function to evaluate an instance. It's assumed that this function only receives
     *                 one parameter.
     * @param instance The instance to be evaluated.
     * @return The result of the expression.
     * @throws RserveException If anything goes wrong during the execution of R code.
     * @throws REXPMismatchException If there is an error during the creation of the data frame.
     * @see <a href="http://rforge.net/Rserve/">Rserve</a>
     */
    private synchronized REXP evaluateInstance(final String function,
                                               final Instance instance) throws RserveException, REXPMismatchException {
        final String instanceRVar = "instance";
        this.rConnection.assign(instanceRVar, convertInstanceToDataFrame(instance));
        return this.rConnection.eval(String.format("%s(%s)", function, instanceRVar));
    }

    /**
     * Converts a {@link Instance} to a {@link REXP data frame}. This object will be used by the model to classify the
     * instance.
     *
     * @param instance The instance to classify.
     * @return the object to be classified by a R model.
     * @throws REXPMismatchException If it cannot convert an object to an expected type.
     */
    private REXP convertInstanceToDataFrame(final Instance instance) throws REXPMismatchException {
        final RList rlist = new RList();
        for (int i = 0; i < this.schema.getFieldSchemas().size(); i++) {
            final FieldSchema fieldSchema = this.schema.getFieldSchemas().get(i);
            rlist.put(fieldSchema.getFieldName(), convertFieldValue(instance.getValue(i), fieldSchema.getValueSchema()));
        }
        return REXP.createDataFrame(rlist);
    }

    /**
     * Converts the value of a field to be used by R. If the type of the schema of categorical we will need to get its
     * real value.
     *
     * @param fieldValue  The value of the field.
     * @param fieldSchema The schema of the field.
     * @return the value of the field to use in R code.
     */
    private REXP convertFieldValue(final double fieldValue, final AbstractValueSchema fieldSchema) {
        if (fieldSchema instanceof CategoricalValueSchema) {
            return new REXPString(
                    EncodingHelper.decodeDoubleToCategory(
                            fieldValue,
                            (CategoricalValueSchema) fieldSchema
                    )
            );
        }
        return new REXPDouble(fieldValue);
    }
}
