package com.recalot.common.interfaces.model.experiment;

import com.recalot.common.communication.RecommendationResult;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class OnlineExperimentRecommendation {
    private String experimentId;
    private String userId;
    private String itemId;
    private RecommendationResult result;

    public OnlineExperimentRecommendation(String experimentId, String userId, String itemId, RecommendationResult result) {
        this.experimentId = experimentId;
        this.userId = userId;
        this.itemId = itemId;
        this.result = result;
    }

    public String getId() {
        return experimentId;
    }

    public void setId(String experimentId) {
        this.experimentId = experimentId;
    }

    public RecommendationResult getResult() {
        return result;
    }

    public void setResult(RecommendationResult result) {
        this.result = result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
