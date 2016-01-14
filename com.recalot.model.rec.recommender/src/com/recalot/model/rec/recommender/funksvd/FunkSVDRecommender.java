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

package com.recalot.model.rec.recommender.funksvd;

import java.util.*;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.interfaces.model.rec.Recommender;
import com.recalot.model.rec.recommender.helper.RecommenderHelper;
import com.recalot.model.rec.recommender.funksvd.helper.*;


/**
 * Implements a baseline SVD recommender
 * http://sifter.org/~simon/journal/20061211.html
 * Adapted from previous Apache Mahout implementation (0.4)
 */
public class FunkSVDRecommender extends Recommender {

    // Default parameter settings
    int numFeatures = 50;
    int initialSteps = 50;

    private HashMap<String, Integer> userMap = null;
    private HashMap<String, Integer> itemMap = null;
    private HashMap<String, Integer> interactionValueMap = null;
    private GradientDescentSVD emSvd = null;
    private List<Interaction> cachedPreferences = null;

    public FunkSVDRecommender(){
        setKey("funksvd");
    }

    // SVD-Specific things here
    public void train(int steps) {
        for (int i = 0; i < steps; i++) {
            nextTrainStep();
        }
    }

    // =====================================================================================

    private void nextTrainStep() {
        Collections.shuffle(cachedPreferences, random);

        for (int i = 0; i < numFeatures; i++) {
            for (Interaction rating : cachedPreferences) {

                if(userMap.containsKey(rating.getUserId()) && itemMap.containsKey(rating.getItemId()) && interactionValueMap.containsKey(rating.getValue())) {
                    int useridx = userMap.get(rating.getUserId());
                    int itemidx = itemMap.get(rating.getItemId());
                    int interactionValue = interactionValueMap.get(rating.getValue());
                    // System.out.println("Training useridx: " + useridx + ", itemidx: " + // itemidx);
                    emSvd.train(useridx, itemidx, i, interactionValue);
                }
            }
        }
    }

    // --------------------------------------
    private static final Random random = RandomUtils.getRandom();


    @Override
    public void train() throws BaseException {

        int numUsers = getDataSet().getUsersCount();
        userMap = new HashMap<>(numUsers);
        int idx = 0;

        for (User user : getDataSet().getUsers()) {
            userMap.put(user.getId(), idx++);
        }

        int numItems = getDataSet().getItemsCount();
        itemMap = new HashMap<>(numItems);

        idx = 0;
        for (Item item : getDataSet().getItems()) {
            itemMap.put(item.getId(), idx++);
        }

        interactionValueMap = new HashMap<>();

        for (Interaction item : getDataSet().getInteractions()) {
            if(!interactionValueMap.containsKey(item.getValue())) {
                interactionValueMap.put(item.getValue(), Integer.parseInt(item.getValue()));
            }
        }

        double average = RecommenderHelper.getGlobalRatingAverage(getDataSet());
        double defaultValue = Math.sqrt((average - 1.0) / numFeatures);

        emSvd = new GradientDescentSVD(numUsers, numItems, numFeatures, defaultValue);
        cachedPreferences = new ArrayList<>();

        cachedPreferences.addAll(Arrays.asList(getDataSet().getInteractions()));
        //recachePreferences();

        train(initialSteps);
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
        // return 0;
        Integer useridx = userMap.get(userId);
        Integer itemidx = itemMap.get(itemId);

        //return (float) emSvd.getDotProduct(useridx, itemidx);

        if (useridx != null) {
            return emSvd.getDotProduct(useridx, itemidx);
        } else {
            // This might happen during training test splits for super-sparse (test) data
            return Double.NaN;
        }
    }



    // =====================================================================================

    /**
     * Setter for factory
     *
     * @param n
     */
    public void setNumFeatures(String n) {
        this.numFeatures = Integer.parseInt(n);
    }

    // =====================================================================================

    /**
     * Setter for the initial steps
     *
     * @param n
     */
    public void setInitialSteps(String n) {
        this.initialSteps = Integer.parseInt(n);
    }

}
