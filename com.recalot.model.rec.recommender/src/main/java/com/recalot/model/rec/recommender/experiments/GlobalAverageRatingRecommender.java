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

package com.recalot.model.rec.recommender.experiments;

import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.*;

/**
 * This class is used for comparison in experiments and provides a global average as prediction.
 * The recommend method provides a empty list.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class GlobalAverageRatingRecommender extends Recommender {
    private String ratingType = "rating".intern();
    private double prediction;
    private RecommendationResult result;

    @Override
    public void train() throws BaseException {

        int count = 0;
        double sum = 0.0;
        for (Interaction interaction : getDataSet().getInteractions()) {
            if(interaction.getType().toLowerCase().equals(ratingType)) {
                Integer value = Integer.parseInt(interaction.getValue());
                if(value != null) {
                    count++;
                    sum += value;
                }
            }
        }

        result = new RecommendationResult(getKey(), new ArrayList<>());
        prediction = count > 0 ? sum / count : Double.NaN;
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) {
        return result;
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) {
        return prediction;
    }
}
