package com.recalot.model.rec.recommender.experiments;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.rec.Context;
import com.recalot.common.interfaces.model.rec.Recommender;
import flexjson.JSONDeserializer;

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
    public RecommendationResult recommend(String userId, Context context, Map<String, String> param) {
        return result;
    }

    @Override
    public Double predict(String userId, String itemId, Context context, Map<String, String> param) {
        return prediction;
    }
}
