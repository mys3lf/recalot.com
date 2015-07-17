package com.recalot.common.interfaces.model.experiment;

/**
 * @author Matthaeus.schmedding
 */
public abstract class RatingMetric extends Metric {

    public abstract void addRating(double actual, double retrieved);
}

