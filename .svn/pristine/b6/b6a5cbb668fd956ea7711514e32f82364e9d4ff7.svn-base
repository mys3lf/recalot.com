package com.recalot.common.interfaces.model;

import com.recalot.common.interfaces.communication.DataSet;
import com.recalot.common.interfaces.communication.RecommendationResult;

import java.util.HashMap;

/**
 * @author Matthaeus.schmedding
 */
public interface Recommender {
    public void train(DataSet dataSet);

    public RecommendationResult recommend();
    public RecommendationResult recommend(Object context);
    public RecommendationResult recommend(HashMap<String, Object> configOrContext);
    public RecommendationResult recommend(String user);
    public RecommendationResult recommend(String user, Object context);
    public RecommendationResult recommend(String user, HashMap<String, Object> configOrContext);
}
