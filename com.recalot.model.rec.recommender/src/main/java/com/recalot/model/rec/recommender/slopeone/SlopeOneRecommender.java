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

package com.recalot.model.rec.recommender.slopeone;

import com.recalot.common.Helper;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.communication.RecommendedItem;
import com.recalot.common.communication.User;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A weighted slope one recommender. Uses the original implementation of Daniel Lemire
 * (lemire.me/fr/documents/publications/SlopeOne.java)
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class SlopeOneRecommender  extends Recommender {

    public SlopeOneRecommender(){
        setKey("slopeone");
    }

    private HashMap<String, Map<String, Integer>> mFreqMatrix;
    private HashMap<String, Map<String, Float>> mDiffMatrix;


    /**
     * Mainly taken from Lemire's code.
     * Calculates the rating differences
     */
    @Override
    public void train() throws BaseException {
        mDiffMatrix = new HashMap<>();
        mFreqMatrix = new HashMap<>();

        // iterate through all ratings
        for(User user : getDataSet().getUsers()) {
            // then iterate through user data

            for(Interaction interaction1 : getDataSet().getInteractions(user.getId())) {

                if(!mDiffMatrix.containsKey(interaction1.getItemId())) {
                    mDiffMatrix.put(interaction1.getItemId(), new HashMap<>());
                    mFreqMatrix.put(interaction1.getItemId(), new HashMap<>());
                }

                for(Interaction interaction2: getDataSet().getInteractions(user.getId())) {
                    int oldcount = 0;
                    if(mFreqMatrix.get(interaction1.getItemId()).containsKey(interaction2.getItemId())) {
                        oldcount = mFreqMatrix.get(interaction1.getItemId()).get(interaction2.getItemId()).intValue();
                    }
                    float olddiff = 0.0f;
                    if(mDiffMatrix.get(interaction1.getItemId()).containsKey(interaction2.getItemId())) {
                        olddiff = mDiffMatrix.get(interaction1.getItemId()).get(interaction2.getItemId()).floatValue();
                    }
                    float observeddiff = Integer.parseInt(interaction1.getValue()) - Integer.parseInt(interaction2.getValue());
                    mFreqMatrix.get(interaction1.getItemId()).put(interaction2.getItemId(), oldcount + 1);
                    mDiffMatrix.get(interaction1.getItemId()).put(interaction2.getItemId(), olddiff + observeddiff);
                }
            }
        }
        for (String j : mDiffMatrix.keySet()) {
            for (String i : mDiffMatrix.get(j).keySet()) {
                float oldvalue = mDiffMatrix.get(j).get(i).floatValue();
                int count = mFreqMatrix.get(j).get(i).intValue();
                mDiffMatrix.get(j).put(i,oldvalue/count);
            }
        }
    }

    @Override
    public RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) {
        List<RecommendedItem> items =  new ArrayList<>();
        try {
            List<String> rec = recommendItemsByRatingPrediction(userId, true);

            for(String key: rec){
                items.add(new RecommendedItem(key, 0.0));
            }

        } catch (BaseException e) {
            e.printStackTrace();
        }

        items =  Helper.applySubList(items, param, 10);

        return new RecommendationResult(getId(), items);
    }

    @Override
    public Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) {

        double prediction = 0;
        int frequency = 0;

        // Go through all items the user has rated
        try {
            for (Interaction rj : getDataSet().getInteractions(userId)) {
                // get the differences from the rating matrix
                Map<String, Float> diffsForItem = mDiffMatrix.get(itemId);
                if (diffsForItem != null) {
                    Float avgDiff = diffsForItem.get(rj.getItemId());
                    if (avgDiff != null) {
                        int frq = mFreqMatrix.get(itemId).get(rj.getItemId()).intValue();

                        float newval = (avgDiff + Integer.parseInt(rj.getValue()) ) * frq;
                        prediction += newval;
                        frequency += mFreqMatrix.get(itemId).get(rj.getItemId()).intValue();
                    }
                }
            }
        } catch (BaseException e) {
            e.printStackTrace();
            return Double.NaN;
        }
        if (frequency > 0) {
            prediction = prediction / frequency;
        }

        // Default behavior
        if (prediction == 0) {
            return Double.NaN;
        }

        return prediction;
    }


}
