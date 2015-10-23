// Copyright (C) 2015 Matthäus Schmedding
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
