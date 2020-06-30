/*
 * Copyright (c) 2018 Feedzai
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

package com.feedzai.openml.caret;

import com.feedzai.openml.provider.descriptor.MLAlgorithmDescriptor;
import com.feedzai.openml.provider.descriptor.MachineLearningAlgorithmType;
import com.feedzai.openml.util.algorithm.MLAlgorithmEnum;

import java.util.Collections;

import static com.feedzai.openml.util.algorithm.MLAlgorithmEnum.createDescriptor;

/**
 * Specifies the algorithms of the models generated in Caret that are supported by Pulse to be imported.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 * @see <a href="https://topepo.github.io/caret/available-models.html">https://topepo.github.io/caret/available-models.html</a>
 * to find more information about the algorithms supported by Caret and how to create models with those algorithms.
 */
public enum CaretAlgorithm implements MLAlgorithmEnum {

    /**
     * An implementation of extensions to Freund and Schapire's AdaBoost algorithm and Friedman's gradient boosting
     * machine.
     */
    GBM(createDescriptor(
            "Stochastic Gradient Boosting",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/gbm/index.html"
    )),
    /**
     * Multivariate regression methods Partial Least Squares Regression (PLSR), Principal Component Regression (PCR) and
     * Canonical Powered Partial Least Squares (CPPLS).
     */
    PLS(createDescriptor(
            "Partial Least Squares",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/pls/index.html"
    )),
    /**
     * Recursive partitioning for classification, regression and survival trees. An implementation of most of the
     * functionality of the 1984 book by Breiman, Friedman, Olshen and Stone.
     */
    CART(createDescriptor(
            "Classification And Regression Trees",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/rpart/index.html"
    )),
    /**
     * Computes a nearest shrunken centroid for gene expression (microarray) data.
     */
    PAM(createDescriptor(
            "Nearest Shrunken Centroids",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/pamr/index.html"
    )),
    /**
     * Classification and regression based on a forest of trees using random inputs, based on Breiman (2001).
     */
    RF(createDescriptor(
            "Random Forest",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/randomForest/index.html"
    )),
    /**
     * Mixture and flexible discriminant analysis.
     */
    MDA(createDescriptor(
            "Mixture Discriminant Analysis",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/mda/index.html"
    )),
    /**
     * Generalized additive (mixed) models, some of their extensions and other generalized ridge regression with
     * multiple smoothing parameter estimation by (Restricted) Marginal Likelihood, Generalized Cross Validation and
     * similar.
     */
    BAM(createDescriptor(
            "Generalized Additive Model using Splines",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/mgcv/index.html"
    )),
    /**
     * Software for feed-forward neural networks with a single hidden layer, and for multinomial log-linear models.
     */
    NNET(createDescriptor(
            "Neural Network",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/nnet/index.html"
    )),
    /**
     * Functional gradient descent algorithm (boosting) for optimizing general risk functions utilizing component-wise
     * (penalised) least squares estimates or regression trees as base-learners for fitting generalized linear models to
     * potentially high-dimensional data.
     */
    GLMBOOST(createDescriptor(
            "Boosted Generalized Linear Model",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/glmboost/index.html"
    )),
    /**
     * Build regression models using the techniques in Friedman's papers "Fast MARS" and "Multivariate Adaptive
     * Regression Splines".
     */
    EARTH(createDescriptor(
            "Multivariate Adaptive Regression Spline",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/earth/index.html"
    )),
    /**
     * Performs discrete, real, and gentle boost under both exponential and logistic loss on a given data set.
     */
    ADA(createDescriptor(
            "Boosted Classification Trees",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/ada/index.html"
    )),
    /**
     * An implementation of Support Vector Machines based in Kernel machine learning methods.
     */
    SVMLK(createDescriptor(
            "Support Vector Machines with Linear Kernel",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/kernlab/index.html"
    )),
    /**
     * Functions for fitting the entire solution path of the Elastic-Net.
     */
    ELASTICNET(createDescriptor(
            "Elastic Net Regularization",
            Collections.emptySet(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "https://cran.r-project.org/web/packages/elasticnet/index.html"
    ));

    /**
     * {@link MLAlgorithmDescriptor} for this algorithm.
     */
    private final MLAlgorithmDescriptor descriptor;

    /**
     * Constructor.
     *
     * @param descriptor {@link MLAlgorithmDescriptor} for this algorithm.
     */
    CaretAlgorithm(final MLAlgorithmDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public MLAlgorithmDescriptor getAlgorithmDescriptor() {
        return this.descriptor;
    }
}
