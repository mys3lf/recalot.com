package com.recalot.common.interfaces.model.rec;

import com.recalot.common.Helper;
import com.recalot.common.communication.DataSet;
import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Item;
import com.recalot.common.communication.RecommendationResult;
import com.recalot.common.configuration.Configurable;
import com.recalot.common.exceptions.BaseException;

import java.util.*;

/**
 * @author Matthaeus.schmedding
 */
public abstract class Recommender extends Configurable implements RecommenderInformation {
    private String dataSourceId;
    private String recommenderId;
    private RecommenderState state;
    private String key;
    private DataSet dataSet;

    @Override
    public RecommenderState getState() {
        return state;
    }

    public void setState(RecommenderState state){
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

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public void setDataSet(DataSet dataSet){
        this.dataSet = dataSet;
    }
    public DataSet getDataSet(){
        return this.dataSet;
    }

    public abstract void train() throws BaseException;

    public RecommendationResult recommend(String userId) {
        return recommend(userId, null);
    }

    public RecommendationResult recommend(String userId, Map<String, String> param){
        return recommend(userId, null, param);
    }
    public abstract RecommendationResult recommend(String userId, Context context, Map<String, String> param);

    public Double predict(String userId, String itemId){
        return predict(userId, itemId, null);
    }

    public Double predict(String userId, String itemId, Map<String, String> param){
        return predict(userId, itemId, null, param);
    }

    public abstract Double predict(String userId, String itemId, Context context, Map<String, String> param);


    /**
     * A general method for ranking items according to their rating prediction.
     * The method should be overwritten in case the recommender cannot make
     * rating predictions or when a better heuristic is needed, which for
     * example takes the popularity of the recommendations into account.
     * @param userId the user for which a recommendation is sought
     * @return the ranked list of items
     */
    public List<String> recommendItemsByRatingPrediction(String userId) throws BaseException {
        List<String> result = new ArrayList<String>();

        // If there are no ratings for the user in the test set,
        // there is no point of making a recommendation.
        Interaction[] interactions = getDataSet().getInteractions(userId);
        // If we have no ratings...
        if (interactions == null || interactions.length == 0) {
            return Collections.emptyList();
        }

        List<Interaction> interactionList = Arrays.asList(interactions);

        // Calculate rating predictions for all items we know
        Map<String, Double> predictions = new HashMap<>();
        double pred = Float.NaN;
        // Go through all the items
        for (Item item : getDataSet().getItems()) {

            // check if we have seen the item already
            if(interactionList.stream().anyMatch(i -> i.getItemId().equals(item.getId()))) {
                // make a prediction and remember it in case the recommender
                // could make one

                pred = predict(userId, item.getId());
                if (!Double.isNaN(pred)) {
                    predictions.put(item.getId(), pred);
                }
            }
        }

        predictions = Helper.sortByValueDescending(predictions);

        for (String item : predictions.keySet()) {
            result.add(item);
        }

        return result;
    }

}
