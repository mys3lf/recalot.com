package com.recalot.model.rec.recommender.funksvd.helper;

import java.util.Random;


/**
 * Calculates the p and q matrices based on gradient descent.
 * Adapted from previous Apache Mahout implementation (0.4)
 */

public final class GradientDescentSVD {

    private static final Random random = RandomUtils.getRandom();

    private static final double LEARNING_RATE = 0.005;
    /**
     * Parameter used to prevent overfitting. 0.02 is a good value.
     */
    private static final double K = 0.02;
    /**
     * Random noise applied to starting values.
     */
    private static final double r = 0.005;

    private final int m;
    private final int n;
    private final int k;

    /**
     * User singular vector.
     */
    private final double[][] leftVector;

    /**
     * Item singular vector.
     */
    private final double[][] rightVector;

    /**
     * @param m            number of columns
     * @param n            number of rows
     * @param k            number of features
     * @param defaultValue default starting values for the SVD vectors
     */
    public GradientDescentSVD(int m, int n, int k, double defaultValue) {
        this(m, n, k, defaultValue, r);
    }

    public GradientDescentSVD(int m, int n, int k, double defaultValue, double noise) {
        this.m = m;
        this.n = n;
        this.k = k;

        leftVector = new double[m][k];
        rightVector = new double[n][k];

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; j++) {
                leftVector[j][i] = defaultValue + (GradientDescentSVD.random.nextDouble() - 0.5) * noise;
            }
            for (int j = 0; j < n; j++) {
                rightVector[j][i] = defaultValue + (GradientDescentSVD.random.nextDouble() - 0.5) * noise;
            }
        }
    }

    public double getDotProduct(int i, int j) {
        double result = 1.0;
        double[] leftVectorI = leftVector[i];
        double[] rightVectorJ = rightVector[j];
        for (int k = 0; k < this.k; k++) {
            result += leftVectorI[k] * rightVectorJ[k];
        }
        return result;
    }

    /**
     * Training iteration
     *
     * @param i
     * @param j
     * @param k
     * @param value
     */
    public void train(int i, int j, int k, double value) {
        double err = value - getDotProduct(i, j);
        double[] leftVectorI = leftVector[i];
        double[] rightVectorJ = rightVector[j];
        leftVectorI[k] += LEARNING_RATE
                * (err * rightVectorJ[k] - K * leftVectorI[k]);
        rightVectorJ[k] += LEARNING_RATE
                * (err * leftVectorI[k] - K * rightVectorJ[k]);

     }

    int getM() {
        return m;
    }

    int getN() {
        return n;
    }

    int getK() {
        return k;
    }


    /**
     * Returns the left vector (user vector)
     *
     * @param user
     * @return the latent vector weights
     */
    public double[] getLeftVector(int user) {
        return this.leftVector[user];
    }

}

