// Copyright (C) 2015 Matthäus Schmedding
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
import com.recalot.common.configuration.ConfigurationItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.experiment.ListMetric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the classical definition of the gini index
 *
 *  Gini, C. (1912). "Italian: Variabilità e mutabilità" 'Variability and Mutability', C. Cuppini, Bologna, 156 pages. Reprinted in Memorie di metodologica statistica (Ed. Pizetti E, Salvemini, T). Rome: Libreria Eredi Virgilio Veschi (1955).
 *
 * Created by matthaeus.schmedding on 10.04.2015.
 */
public class Gini extends ListMetric {
    private int topN;
    private HashMap<String, Integer> count = new HashMap<>();

    public void setTopN(Integer topN){
        this.topN = topN;
    }

    @Override
    public double getResult() {
        double sum = 0.0;
        double xmean = 0.0;

        for(Integer x1 : count.values()){
            xmean += x1;

            for(Integer x2 : count.values()) {
                sum += Math.abs(x2 - x1);
            }
        }

        int n = count.size();

        if(n > 0) {
            return sum / (n * n * 2 *(xmean / n));
        } else {
            return 0.0;
        }
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
        return "Classical definition of the gini index by Gini, C. (1912)";
    }
}
