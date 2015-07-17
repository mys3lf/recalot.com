package com.recalot.model.experiments.metrics.list;

import com.recalot.common.Helper;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.ListMetric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matthaeus.schmedding on 10.04.2015.
 * Precision = (|relevant items| in |retrieved items|) divided by |relevant items|
 */
public class FScore extends ListMetric {
    private double beta;
    private Recall recall;
    private Precision precision;

    public FScore() throws BaseException {
        this.recall = new Recall();
        this.precision = new Precision();
    }

    public void setTopN(int topN){
     //   this.recall.setTopN(topN);
        this.precision.setTopN(topN);
    }

    public void setBeta(double beta){
        this.beta = beta;
    }

    @Override
    public double getResult() {
        double resultRecall = recall.getResult();
        double resultPrecision = precision.getResult();

        if (resultRecall == 0 && resultPrecision == 0) return 0;

        return (1 + beta * beta) * (resultPrecision * resultRecall) / ( beta * beta * resultPrecision + resultRecall);
    }

    @Override
    public void addList(List<String> relevant, List<String> retrieved) {
        recall.addList(relevant, retrieved);
        precision.addList(relevant, retrieved);
    }

    @Override
    public String getDescription() {
        return null;
    }
}
