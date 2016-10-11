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

/**
 * An recommended item consists of the item id and a confidence of the recommendation.
 * So far most recommender algorithms set 0.0 as confidence.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class RecommendedItem {

    /**
     * Item content
     */
    private Item item;
    /**
     * Item id
     */
    private int itemId;
    /**
     * Confidence
     */
    private double confidence;

    /**
     * Constructor with the item id and the confidence as parameters
     *
     * @param itemId     item id
     * @param confidence confidence
     */
    public RecommendedItem(String itemId, double confidence) {
        this.itemId = InnerIds.getId(itemId);
        this.confidence = confidence;
        this.item = null;
    }

    /**
     * Constructor with the item object and the confidence as parameters
     *
     * @param item       item
     * @param confidence confidence
     */
    public RecommendedItem(Item item, double confidence) {
        this.itemId = InnerIds.getId(item.getId());
        this.confidence = confidence;
        this.item = item;
    }

    /**
     * Get the confidence
     *
     * @return confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * Get the item id
     *
     * @return item id
     */
    public String getItemId() {
        return InnerIds.getId(itemId);
    }

    /**
     * Get the item
     *
     * @return item
     */
    public Item getItem() {
        return item;
    }

    /**
     * set the item
     * @param item
     */
    public void setItem(Item item) {
        this.item = item;
    }

}
