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
 */
public class Gini extends ListMetric {
    private int topN;
    private HashMap<String, Integer> count;

    public void setTopN(Integer topN){
        this.topN = topN;
    }

    @Override
    public double getResult() {
        double sum = 0.0;
        double result = 0.0;

        for(Integer v : count.values()){
            sum+= v;
            result += v * v;
        }

        return result / (sum * sum);
    }

    @Override
    public void addList(List<String> relevant, List<String> retrieved) {
        List<String> topNretrieved = retrieved.subList(0, topN);

        for (String item : topNretrieved) {
            if (relevant.contains(item)){
                Helper.incrementMapValue(count, item);
            }
        }
    }

    @Override
    public String getDescription() {
        return null;
    }
}
