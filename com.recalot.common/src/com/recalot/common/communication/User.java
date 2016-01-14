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
