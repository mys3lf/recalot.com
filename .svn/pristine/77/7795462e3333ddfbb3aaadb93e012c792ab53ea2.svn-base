package com.recalot.model.rec.recommender.helper;

import com.recalot.common.Helper;
import com.recalot.common.communication.DataSet;
import com.recalot.common.communication.Interaction;
import com.recalot.common.exceptions.BaseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthaeus.schmedding on 11.06.2015.
 */
public class RecommenderHelper {


    /**
     * Returns a map with user averages given a set of ratings
     */
    public static Map<String, Double> getUserAverageRatings(DataSet dataSet) {
        Map<String, Double> result = new HashMap<>();
        Map<String, Integer> counters = new HashMap<>();

        try {
            for (Interaction r : dataSet.getInteractions()) {
                Double userAvg = result.get(r.getItemId());

                if(Helper.isIntegerRegex(r.getValue())){
                    Integer value = Integer.parseInt(r.getValue());
                    if (userAvg == null) {
                        userAvg = new Double(value);
                        result.put(r.getId(), userAvg);
                        counters.put(r.getUserId(), 1);
                    } else {
                        counters.put(r.getUserId(), counters.get(r.getUserId()) + 1);
                        result.put(r.getUserId(), result.get(r.getUserId()) + value);
                    }
                }

            }
        } catch (BaseException e) {
            e.printStackTrace();
        }
        // Divide by number of ratings
        for (String user : result.keySet()) {
            result.put(user, result.get(user) / (float) counters.get(user));
        }

        return result;
    }

    // =====================================================================================


    /**
     * A method that calculates the overall rating average
     *
     * @param dataSet
     * @return the average rating
     */
    public static double getGlobalRatingAverage(DataSet dataSet) {
        int cnt = 0;
        int total = 0;
        try {
            for (Interaction rating : dataSet.getInteractions()) {
                if(Helper.isIntegerRegex(rating.getValue())){
                    total += Integer.parseInt(rating.getValue());
                    cnt++;
                }
            }
        } catch (BaseException e) {
            e.printStackTrace();
        }
        return total / (double) cnt;
    }


}
