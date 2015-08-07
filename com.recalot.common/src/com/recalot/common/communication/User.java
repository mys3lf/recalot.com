package com.recalot.common.communication;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data set user
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class User {

    /**
     * User ID
     */
    private String id;

    /**
     * User content map
     */
    private Map<String, String> map;

    /**
     * Constructor with id and content map as parameters
     * @param id user id
     * @param map content map
     */
    public User(String id, Map<String, String> map){
        this.id = id;
        this.map = map;
    }

    /**
     * Constructor with id as parameter
     * @param id user id
     */
    public User(String id){
        this.id = id;
        this.map = new HashMap<>();
    }

    /**
     *
     * @return user id
     */
    public String getId() {
        return id;
    }

    /**
     * Get all available user content
     * @return all available user conent
     */
    public Map<String, String> getContent() {
        return map;
    }

    /**
     * Get user content for a specific key
     * @param key content key
     * @return user content for a specific key
     */
    public String getValue(String key) {
        return map.get(key);
    }
}
