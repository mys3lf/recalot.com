// Copyright (C) 2016 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.model.experiments.metrics.list;

import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.ListMetric;

import java.util.List;

/**
 * Created by matthaeus.schmedding on 10.04.2015.
 * Precision = (|relevant items| in |retrieved items|) divided by |relevant items|
 */

@Configuration(key = "topN", type = ConfigurationItem.ConfigurationItemType.Integer, value = "10", requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional, description = "")
@Configuration(key = "beta", type = ConfigurationItem.ConfigurationItemType.Double, value = "1.0", requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional, description = "")
public class FScore extends ListMetric {
    private double beta;
    private Recall recall;
    private Precision precision;

    public FScore() throws BaseException {
        this.recall = new Recall();
        this.precision = new Precision();
    }

    public void setTopN(int topN){
        this.recall.setTopN(topN);
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
        return "F-Score";
    }
}
