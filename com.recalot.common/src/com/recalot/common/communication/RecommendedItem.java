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
    private final Item item;
    /**
     * Item id
     */
    private String itemId;
    /**
     * Confidence
     */
    private double confidence;

    /**
     * Constructor with the item id and the confidence as parameters
     * @param itemId item id
     * @param confidence confidence
     */
    public RecommendedItem(String itemId, double confidence) {
        this.itemId = itemId;
        this.confidence = confidence;
        this.item = null;
    }
    /**
     * Constructor with the item object and the confidence as parameters
     * @param item item
     * @param confidence confidence
     */
    public RecommendedItem(Item item, double confidence) {
        this.itemId = item.getId();
        this.confidence = confidence;
        this.item = item;
    }

    /**
     * Get the confidence
     * @return confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * Get the item id
     * @return item id
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Get the item
     * @return item
     */
    public Item getItem() {
        return item;
    }
}
