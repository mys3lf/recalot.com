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
