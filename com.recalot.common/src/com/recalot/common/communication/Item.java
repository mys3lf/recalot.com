package com.recalot.common.communication;

import java.util.HashMap;
import java.util.Map;

/**
 * Data set item
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Item {

    /**
     * Content Map
     */
    private final Map<String, String> content;
    /**
     * Id
     */
    private final String id;

    /**
     * Constructor with id as parameter
     * @param id item id
     */
    public Item(String id){
        this(id, new HashMap<>());
    }

    /**
     * Constructor with id and content map as parameters
     * @param id item id
     * @param content content map
     */
    public Item(String id, Map<String, String> content) {
        this.id = id;
        this.content = content;
    }

    /**
     * Get the item id
     * @return item id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the complete item content
     * @return complete item content
     */
    public Map<String, String> getContent() {
        return content;
    }

    /**
     * Get the item content with the given key
     * @param key key of the item content
     * @return item content with the given key
     */
    public String getValue(String key) {
        return content.get(key);
    }
}
