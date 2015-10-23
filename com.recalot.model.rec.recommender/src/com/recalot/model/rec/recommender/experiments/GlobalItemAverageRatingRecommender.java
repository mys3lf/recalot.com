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

package com.recalot.model.rec.recommender.experiments;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for comparison in experiments and provides a item average as prediction.
 * The recommend method provides a empty list.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class GlobalItemAverageRatingRecommender extends Recommender {
    private String ratingType = "rating".intern();
    private   HashMap<String, Double> prediction;
    private RecommendationResult result;

    @Override
    public void train() throws BaseException {

        HashMap<String, Integer> count = new HashMap<>();
        HashMap<String, Double> sum = new HashMap<>();


        for (Interaction interaction : getDataSet().getInteractions()) {
            if(interaction.getType().toLowerCase().equals(ratingType)) {
                Integer value = Integer.parseInt(interaction.getValue());
                if(value != null) {
                    Helper.incrementMapValue(count, interaction.getItemId());
                    Helper.incrementMapValue(sum, interaction.getItemId(), 1.0 * value);
                }
            }
        }

        result = new RecommendationResult(getKey(), new ArrayList<>());


        prediction = new HashMap<>();

        for(String itemId : count.keySet()) {
            int c = count.get(itemId);
            double s = sum.get(itemId);

            if(c > 0) {
                prediction.put(itemId, s / c);
            }
        }
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) {
        return result;
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) {
        return prediction.containsKey(itemId) ? prediction.get(itemId) : Double.NaN;
    }
}
