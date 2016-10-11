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

package com.recalot.common.interfaces.model.rec;

import com.recalot.common.Helper;
import com.recalot.common.communication.DataSet;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.configuration.Configurable;
import com.recalot.common.context.ContextProvider;
import com.recalot.common.exceptions.BaseException;

import java.util.*;

/**
 * @author Matthaeus.schmedding
 */
public abstract class Recommender extends Configurable implements RecommenderInformation {
    protected String dataSourceId;
    protected String recommenderId;

    protected String experimentId;
    protected RecommenderState state;
    protected String key;
    protected DataSet dataSet;

    @Override
    public RecommenderState getState() {
        return state;
    }

    public void setState(RecommenderState state) {
        this.state = state;
    }

    @Override
    public String getId() {
        return recommenderId;
    }

    public void setId(String recommenderId) {
        this.recommenderId = recommenderId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public DataSet getDataSet() {
        return this.dataSet;
    }

    public abstract void train() throws BaseException;

    public RecommendationResult recommend(String userId) throws BaseException {
        return recommend(userId, new HashMap<>());
    }

    public RecommendationResult recommend(String userId, Map<String, String> param) throws BaseException {
        return recommend(userId, null, param);
    }

    public RecommendationResult recommend(String userId, ContextProvider context) throws BaseException {
        return recommend(userId, context, new HashMap<>());
    }

    public abstract RecommendationResult recommend(String userId, ContextProvider context, Map<String, String> param) throws BaseException;

    public Double predict(String userId, String itemId) throws BaseException {
        return predict(userId, itemId, new HashMap<>());
    }

    public Double predict(String userId, String itemId, Map<String, String> param) throws BaseException {
        return predict(userId, itemId, null, param);
    }

    public Double predict(String userId, String itemId, ContextProvider context) throws BaseException {
        return predict(userId, itemId, context, new HashMap<>());
    }

    public abstract Double predict(String userId, String itemId, ContextProvider context, Map<String, String> param) throws BaseException;


    /**
     * A general method for ranking items according to their rating prediction.
     * The method should be overwritten in case the recommender cannot make
     * rating predictions or when a better heuristic is needed, which for
     * example takes the popularity of the recommendations into account.
     *
     * @param userId      the user for which a recommendation is sought
     * @param omitVisited should visited items be omited
     * @return the ranked list of items
     */
    public List<String> recommendItemsByRatingPrediction(String userId, boolean omitVisited) throws BaseException {
        return recommendItemsByRatingPrediction(userId, getDataSet().getItems(), omitVisited);
    }


    /**
     * A general method for ranking items according to their rating prediction.
     * The method should be overwritten in case the recommender cannot make
     * rating predictions or when a better heuristic is needed, which for
     * example takes the popularity of the recommendations into account.
     *
     * @param userId      the user for which a recommendation is sought
     * @param items       the items which a recommender should use
     * @param omitVisited should visited items be omited
     * @return the ranked list of items
     */
    public List<String> recommendItemsByRatingPrediction(String userId, Item[] items, boolean omitVisited) throws BaseException {
        List<String> result = new ArrayList<String>();

        // If there are no ratings for the user in the test set,
        // there is no point of making a recommendation.
        Interaction[] interactions = getDataSet().getInteractions(userId);
        // If we have no ratings...
        if (interactions == null || interactions.length == 0) {
            return Collections.emptyList();
        }

        List<Interaction> interactionList = Arrays.asList(interactions);

        //put visited item into a map. It is faster this way
        Map<String, Boolean> visited = new HashMap<>();
        if (omitVisited) {
            for (Interaction interaction : interactionList) {
                if (!visited.containsKey(interaction.getItemId())) {
                    visited.put(interaction.getItemId(), true);
                }
            }
        }

        // Calculate rating predictions for all items we know
        Map<String, Double> predictions = new HashMap<>();
        double pred;
        // Go through all the items
        for (Item item : items) {

            // check if we have seen the item already
            if (!visited.containsKey(item.getId())) {
                // make a prediction and remember it in case the recommender
                // could make one

                pred = predict(userId, item.getId());
                if (!Double.isNaN(pred)) {
                    predictions.put(item.getId(), pred);
                }
            }
        }
        // Calculate rating predictions for all items we know
        predictions = Helper.sortByValueDescending(predictions);

        for (String item : predictions.keySet()) {
            result.add(item);
        }

        return result;
    }

}
