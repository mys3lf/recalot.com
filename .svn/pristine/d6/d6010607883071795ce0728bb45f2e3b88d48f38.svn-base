package com.recalot.model.experiments.metrics.list;

import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.ListMetric;
import com.recalot.common.interfaces.model.experiment.Metric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matthaeus.schmedding on 10.04.2015.
 */
public class Coverage extends ListMetric {
    private double testRun;
    private double sum;

    @Override
    public double getResult() {
        if (testRun == 0) return 0;
        return sum / testRun;
    }

    @Override
    public void addList(List<String> relevant, List<String> retrieved) {

        if(retrieved.size() > 0) sum += 1;

        testRun++;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
