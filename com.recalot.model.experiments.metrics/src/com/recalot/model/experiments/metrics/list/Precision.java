package com.recalot.model.experiments.metrics.list;

import com.recalot.common.Helper;
import com.recalot.common.interfaces.model.experiment.ListMetric;

import java.util.List;

/**
 * Created by matthaeus.schmedding on 10.04.2015.
 * Precision = (|relevant items| in |retrieved items|) divided by |retrieved items|
 */
public class Precision extends ListMetric {

    private double testRun = 0;
    private double sum = 0;

    private int topN = 0;

    public void setTopN(int topN) {
        this.topN = topN;
    }

    @Override
    public double getResult() {
        if (testRun == 0) return 0;
        return sum / testRun;
    }

    @Override
    public void addList(List<String> relevant, List<String> retrieved) {

        if (retrieved.size() > 0) {
            List<String> topNretrieved = Helper.applySubList(retrieved, topN);
            int count = 0;

            for (String item : topNretrieved) {
                if (relevant.contains(item)) count++;
            }

            sum += 1.0 * count / retrieved.size();
        }

        testRun++;
    }

    @Override
    public String getDescription() {
        return "Precision = (|relevant items| in |retrieved items|) divided by |retrieved items|";
    }
}
