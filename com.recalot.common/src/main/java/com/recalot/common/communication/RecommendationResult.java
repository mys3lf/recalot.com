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
     * Experiment Id
     */
    private String experimentId;
    /**
     * Recommended items
     */
    private List<RecommendedItem> items;
    private String resultId;

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
     * Constructor with the recommender id, the recommended items and the experiment id as parameters
     *
     * @param recommender Recommender ID
     * @param items Recommended items
     */
    public RecommendationResult(String recommender, String experimentId, List<RecommendedItem> items) {
        this.recommenderKey = recommender;
        this.experimentId = experimentId;
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

    /**
     * Returns the experiment id (can be null)
     * @return experiment id
     */
    public String getExperimentId() {
        return experimentId;
    }

    /**
     * Sets the experiment id
     */
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    /**
     * Sets the result id
     * @param resultId result id
     */
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    /**
     * Return the result id
     * @return  resultId result id
     */
    public String getResultId() {
        return resultId;
    }
}
