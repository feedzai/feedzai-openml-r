/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */

package com.feedzai.openml.caret;

import com.feedzai.openml.model.MachineLearningModel;
import com.feedzai.openml.provider.MachineLearningProvider;
import com.feedzai.openml.provider.descriptor.MLAlgorithmDescriptor;
import com.feedzai.openml.util.algorithm.MLAlgorithmEnum;
import com.google.auto.service.AutoService;

import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the {@link MachineLearningProvider service provider}.
 * <p>
 * This class is a service responsible for providing {@link MachineLearningModel} that were generated in Caret.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
@AutoService(MachineLearningProvider.class)
public class CaretModelProvider implements MachineLearningProvider<CaretModelLoader> {

    /**
     * Name of the service that provides {@link MachineLearningModel} generated in Caret.
     */
    public static final String CARET_NAME = "Caret";

    @Override
    public String getName() {
        return CARET_NAME;
    }

    @Override
    public Set<MLAlgorithmDescriptor> getAlgorithms() {
        return MLAlgorithmEnum.getDescriptors(CaretAlgorithm.values());
    }

    @Override
    public Optional<CaretModelLoader> getModelCreator(final String algorithmName) {

        return MLAlgorithmEnum.getByName(CaretAlgorithm.values(), algorithmName)
                .map(algorithm -> new CaretModelLoader());
    }
}
