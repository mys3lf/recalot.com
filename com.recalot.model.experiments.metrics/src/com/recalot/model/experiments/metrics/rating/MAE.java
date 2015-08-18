package com.recalot.model.experiments.metrics.rating;

import com.recalot.common.interfaces.model.experiment.RatingMetric;

/**
 * Implements mean absolute error (MAE)
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class MAE extends RatingMetric {

    private int count;
    private double sum;

    public MAE() {
        count = 0;
        sum = 0;
    }

    @Override
    public void addRating(double actual, double retrieved) {
        count++;
        sum += Math.abs(actual -  retrieved);
    }

    @Override
    public double getResult() {
        return count > 0 ? sum / count : Double.MAX_VALUE;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
