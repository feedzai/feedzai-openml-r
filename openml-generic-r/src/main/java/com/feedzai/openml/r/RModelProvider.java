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

import com.feedzai.openml.model.MachineLearningModel;
import com.feedzai.openml.provider.MachineLearningProvider;
import com.feedzai.openml.provider.descriptor.MLAlgorithmDescriptor;
import com.feedzai.openml.util.algorithm.GenericAlgorithm;
import com.feedzai.openml.util.algorithm.MLAlgorithmEnum;
import com.google.auto.service.AutoService;

import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the {@link MachineLearningProvider service provider}.
 * <p>
 * This class is a service responsible for providing {@link MachineLearningModel} that were generated in R.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
@AutoService(MachineLearningProvider.class)
public class RModelProvider implements MachineLearningProvider<GenericRModelLoader> {

    /**
     * Name of the service that provides {@link MachineLearningModel} generated in R.
     */
    private static final String PROVIDER_NAME = "Generic R";

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public Set<MLAlgorithmDescriptor> getAlgorithms() {
        return MLAlgorithmEnum.getDescriptors(GenericAlgorithm.values());
    }

    @Override
    public Optional<GenericRModelLoader> getModelCreator(final String algorithmName) {

        return MLAlgorithmEnum.getByName(GenericAlgorithm.values(), algorithmName)
                .map(algorithm -> new GenericRModelLoader());

    }
}
