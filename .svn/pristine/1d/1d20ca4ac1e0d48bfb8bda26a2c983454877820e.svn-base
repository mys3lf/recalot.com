package com.recalot.model.experiments.metrics.list;

import com.recalot.common.interfaces.model.experiment.ListMetric;

import java.util.List;

/**
 * Created by matthaeus.schmedding on 10.04.2015.
 * Precision = (|relevant items| in |retrieved items|) divided by |relevant items|
 */
public class Recall extends ListMetric {
    private double testRun = 0;
    private double sum = 0;

    @Override
    public double getResult() {
        if (testRun == 0) return 0;
        return sum / testRun;
    }

    @Override
    public void addList(List<String> relevant, List<String> retrieved) {
        int count = 0;

        for (String item : retrieved) {
            if (relevant.contains(item)) count++;
        }

        sum += 1.0 * count / relevant.size();

        testRun++;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
