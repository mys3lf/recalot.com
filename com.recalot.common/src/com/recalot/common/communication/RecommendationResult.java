package com.recalot.common.communication;

import java.util.List;

/**
 * Result of a recommender.
 * The recommendation result consists of an identifier of the recommender that computed the result
 * and of a list with the recommended items.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class RecommendationResult {

    /**
     * Recommender ID
     */
    private String recommenderKey;

    /**
     * Recommended items
     */
    private List<RecommendedItem> items;

    /**
     * Constructor with the recommender id and the recommended items as parameters
     *
     * @param recommender Recommender ID
     * @param items Recommended items
     */
    public RecommendationResult(String recommender, List<RecommendedItem> items) {
        this.recommenderKey = recommender;
        this.items = items;

    }

    /**
     * Get the recommended ID
     * @return recommender ID
     */
    public String getRecommender() {
        return recommenderKey;
    }

    /**
     * Get the computed recommendations
     * @return recommended items
     */
    public List<RecommendedItem> getItems() {
        return items;
    }
}
