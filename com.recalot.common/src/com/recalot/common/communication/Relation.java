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

import java.util.Map;

/**
 * This represents a relation. E.g. befriended user
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Relation {

    private final String fromId;
    private final String toId;
    private String id;
    private String type;
    private Map<String, String> content;

    public Relation(String id, String fromId, String toId, String type, Map<String, String> content) {
        this.id = id.intern();
        this.fromId = fromId.intern();
        this.toId = toId.intern();
        this.type = type.intern();
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getContent() {
        return content;
    }

    public String getValue(String key) {
        return content.get(key);
    }

    public String getToId() {
        return toId;
    }

    public String getFromId() {
        return fromId;
    }
}
