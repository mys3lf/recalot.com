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

import com.recalot.common.Helper;

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
    private final Map<Integer, String> content;

    /**
     * Id
     */
    private int id;

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
        this.id = InnerIds.getNextId(id, Helper.Keys.ItemId);
        this.content = new HashMap<>();

        for (String key : content.keySet()) {
            int contentId = InnerIds.getNextId(key, Helper.Keys.Content);
            this.content.put(contentId, content.get(key));
        }
    }

    /**
     * Get the item id
     * @return item id
     */
    public String getId() {
        return  InnerIds.getId(id, Helper.Keys.ItemId);
    }

    /**
     * Get the complete item content
     * @return complete item content
     */
    public Map<String, String> getContent() {

        HashMap<String, String> content = new HashMap<>();

        for (Integer key : this.content.keySet()) {
            String contentKey = InnerIds.getId(key, Helper.Keys.Content);
            content.put(contentKey, this.content.get(key));
        }

        return content;
    }

    /**
     * Get the item content with the given key
     * @param key key of the item content
     * @return item content with the given key
     */
    public String getValue(String key) {
        return content.get(InnerIds.getId(key, Helper.Keys.Content));
    }
}
