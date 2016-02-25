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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents a relation. E.g. befriended user
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Relation implements Serializable {

    private int fromId;
    private int toId;
    private int id;
    private int type;
    private Map<Integer, String> content;

    public Relation(String id, String fromId, String toId, String type) {
        this(id, fromId, toId, type, new HashMap<>());
    }

    public Relation(String id, String fromId, String toId, String type, Map<String, String> content) {
        this.id = InnerIds.getNextId(id);
        this.fromId = InnerIds.getNextId(fromId);
        this.toId = InnerIds.getNextId(toId);
        this.type = InnerIds.getNextId(type);

        this.content = new HashMap<>();

        for (String key : content.keySet()) {
            int contentId = InnerIds.getNextId(key);
            this.content.put(contentId, content.get(key));
        }
    }

    public String getId() {
        return InnerIds.getId(id);
    }

    public String getType() {
        return InnerIds.getId(type);
    }

    public Map<String, String> getContent() {
        HashMap<String, String> content = new HashMap<>();

        for (Integer key : this.content.keySet()) {
            String contentKey = InnerIds.getId(key);
            content.put(contentKey, this.content.get(key));
        }

        return content;
    }

    public String getValue(String key) {
        return content.get(InnerIds.getId(key));
    }

    public String getToId() {
        return InnerIds.getId(toId);
    }

    public String getFromId() {
        return InnerIds.getId(fromId);
    }
}
