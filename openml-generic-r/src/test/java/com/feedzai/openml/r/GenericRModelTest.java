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

import com.feedzai.openml.data.Instance;
import com.feedzai.openml.mocks.MockInstance;
import com.feedzai.openml.data.schema.CategoricalValueSchema;
import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.data.schema.FieldSchema;
import com.feedzai.openml.data.schema.NumericValueSchema;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.util.algorithm.GenericAlgorithm;
import com.feedzai.util.algorithm.MLAlgorithmEnum;
import com.feedzai.util.provider.AbstractProviderModelLoadTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Tests for the {@link RModelProvider}.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
public class GenericRModelTest extends AbstractProviderModelLoadTest<ClassificationGenericRModel,
        GenericRModelLoader, RModelProvider> {

    /**
     * Name of the directory that contains a dummy model to be used in tests.
     */
    private static final String MODEL_0_FILE = "model0";

    /**
     * Name of the directory that contains a dummy model to be used in tests.
     */
    private static final String MODEL_1_FILE = "model1";

    /**
     * Name of the directory that contains a invalid model. The '/scripts/classifier.R' of this model doesn't contain
     * all the required functions.
     */
    private static final String MODEL_NOT_VALID_FILE = "model_not_valid";

    /**
     * Values of the target variable from the models stored in {@link #MODEL_0_FILE} and {@link #MODEL_1_FILE}.
     */
    private static final Set<String> TARGET_VALUES = ImmutableSet.of("a", "b");

    /**
     * Expected exception in tests.
     */
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Tests loading a model that doesn't contain all the required functions.
     *
     * @throws ModelLoadingException Exception that we expect to be thrown.
     */
    @Test
    public void modelLoadingErrorNotAllRequiredFunctions() throws ModelLoadingException {
        this.exception.expect(ModelLoadingException.class);
        this.exception.expectMessage("Could not found the function");
        this.exception.expectMessage("in the R workspace");
        loadModel(getValidAlgorithm(), MODEL_NOT_VALID_FILE, TARGET_VALUES);
    }

    @Override
    public Instance getDummyInstance() {
        return new MockInstance(new double[]{1.0, 0.0});
    }

    @Override
    public Instance getDummyInstanceDifferentResult() {
        return new MockInstance(new double[]{0.0, 1.0});
    }

    @Override
    public RModelProvider getMachineLearningProvider() {
        return new RModelProvider();
    }

    @Override
    public ClassificationGenericRModel getFirstModel() throws ModelLoadingException {
        return loadModel(getValidAlgorithm(), MODEL_0_FILE, TARGET_VALUES);
    }

    @Override
    public ClassificationGenericRModel getSecondModel() throws ModelLoadingException {
        return loadModel(getValidAlgorithm(), MODEL_1_FILE, TARGET_VALUES);
    }

    @Override
    public GenericRModelLoader getFirstMachineLearningModelLoader() {
        return getMachineLearningModelLoader(getValidAlgorithm());
    }

    @Override
    public Set<Integer> getClassifyValuesOfFirstModel() {
        return IntStream.range(0, TARGET_VALUES.size()).boxed().collect(Collectors.toSet());
    }

    @Override
    public Set<Integer> getClassifyValuesOfSecondModel() {
        return IntStream.range(0, TARGET_VALUES.size()).boxed().collect(Collectors.toSet());
    }

    @Override
    public MLAlgorithmEnum getValidAlgorithm() {
        return GenericAlgorithm.GENERIC_CLASSIFICATION;
    }

    @Override
    public String getValidModelDirName() {
        return MODEL_0_FILE;
    }

    @Override
    public Set<String> getFirstModelTargetNominalValues() {
        return TARGET_VALUES;
    }

    @Override
    public DatasetSchema createDatasetSchema(final Set<String> targetValues) {
        return new DatasetSchema(
                1,
                ImmutableList.<FieldSchema>builder()
                        .add(
                                new FieldSchema(
                                        "PassengerId",
                                        0,
                                        new NumericValueSchema(false)
                                )
                        )
                        .add(
                                new FieldSchema(
                                        "Survived",
                                        1,
                                        new CategoricalValueSchema(false, targetValues)
                                )
                        )
                        .build()
        );
    }
}
