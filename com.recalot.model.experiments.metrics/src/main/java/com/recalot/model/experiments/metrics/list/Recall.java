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

import com.recalot.common.Helper;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.interfaces.model.experiment.ListMetric;

import java.util.List;

/**
 * Created by matthaeus.schmedding on 10.04.2015.
 * Precision = (|relevant items| in |retrieved items|) divided by |relevant items|
 */

@Configuration(key = "topN", type = ConfigurationItem.ConfigurationItemType.Integer, value = "10", requirement = ConfigurationItem.ConfigurationItemRequirementType.Optional, description = "")
public class Recall extends ListMetric {

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
        int count = 0;

        if (relevant.size() > 0) {
            List<String> topNretrieved = Helper.applySubList(retrieved, topN);

            for (String item : topNretrieved) {
                if (relevant.contains(item)) count++;
            }

            if (relevant.size() > 0) sum += 1.0 * count / relevant.size();
        }

        testRun++;
    }

    @Override
    public String getDescription() {
        return "Recall = (|relevant items| in |retrieved items|) divided by |relevant items|";
    }
}
