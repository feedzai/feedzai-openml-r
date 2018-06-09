/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */

package com.feedzai.openml.caret;

import com.feedzai.openml.data.Instance;
import com.feedzai.openml.mocks.MockInstance;
import com.feedzai.openml.data.schema.CategoricalValueSchema;
import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.data.schema.FieldSchema;
import com.feedzai.openml.data.schema.NumericValueSchema;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.util.algorithm.MLAlgorithmEnum;
import com.feedzai.util.provider.AbstractProviderCategoricalTargetTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.feedzai.openml.r.ClassificationGenericRModel;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Tests for the {@link CaretModelProvider}.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
public class CaretModelTest extends AbstractProviderCategoricalTargetTest<ClassificationGenericRModel, CaretModelLoader,
        CaretModelProvider> {

    /**
     * Name of the file that contains a model created with the {@link CaretAlgorithm#PAM pam algorithm}.
     */
    private static final String PAM_FILE_NAME = "pam";

    /**
     * Values of the target variable from the model stored in {@link #PAM_FILE_NAME}.
     */
    private static final Set<String> PAM_TARGET_VALUES = ImmutableSet.of("S", "N");

    /**
     * Name of the file that contains a model created with the {@link CaretAlgorithm#GBM gbm algorithm}.
     */
    private static final String GBM_FILE_NAME = "gbm";

    /**
     * Values of the target variable from the model stored in {@link #GBM_FILE_NAME}.
     */
    private static final Set<String> GBM_TARGET_VALUES = ImmutableSet.of("0", "1");

    @Override
    public Instance getDummyInstance() {
        return new MockInstance(new double[]{1.0, 0.0, 3.0, 1.0, 22.0, 1.0, 0.0, 7.25, 0.0});
    }

    @Override
    public CaretModelProvider getMachineLearningProvider() {
        return new CaretModelProvider();
    }

    @Override
    public ClassificationGenericRModel getFirstModel() throws ModelLoadingException {
        return loadModel(CaretAlgorithm.PAM, PAM_FILE_NAME, PAM_TARGET_VALUES);
    }

    @Override
    public ClassificationGenericRModel getSecondModel() throws ModelLoadingException {
        return loadModel(CaretAlgorithm.GBM, GBM_FILE_NAME, GBM_TARGET_VALUES);
    }

    @Override
    public CaretModelLoader getFirstMachineLearningModelLoader() {
        return getMachineLearningModelLoader(CaretAlgorithm.PAM);
    }

    @Override
    public Set<Integer> getClassifyValuesOfFirstModel() {
        return IntStream.range(0, PAM_TARGET_VALUES.size()).boxed().collect(Collectors.toSet());
    }

    @Override
    public Set<Integer> getClassifyValuesOfSecondModel() {
        return IntStream.range(0, GBM_TARGET_VALUES.size()).boxed().collect(Collectors.toSet());
    }

    @Override
    public MLAlgorithmEnum getValidAlgorithm() {
        return CaretAlgorithm.PAM;
    }

    @Override
    public String getValidModelDirName() {
        return PAM_FILE_NAME;
    }

    @Override
    public Set<String> getFirstModelTargetNominalValues() {
        return PAM_TARGET_VALUES;
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
                        .add(
                                new FieldSchema(
                                        "Pclass",
                                        2,
                                        new NumericValueSchema(false)
                                )
                        )
                        .add(
                                new FieldSchema(
                                        "Sex",
                                        3,
                                        new NumericValueSchema(false)
                                )
                        )
                        .add(
                                new FieldSchema(
                                        "Age",
                                        4,
                                        new NumericValueSchema(false)
                                )
                        )
                        .add(
                                new FieldSchema(
                                        "SibSp",
                                        5,
                                        new NumericValueSchema(false)
                                )
                        )
                        .add(
                                new FieldSchema(
                                        "Parch",
                                        6,
                                        new NumericValueSchema(false)
                                )
                        )
                        .add(
                                new FieldSchema(
                                        "Fare",
                                        7,
                                        new NumericValueSchema(false)
                                )
                        )
                        .add(
                                new FieldSchema(
                                        "Embarked",
                                        8,
                                        new NumericValueSchema(false)
                                )
                        )
                        .build()
        );
    }
}
