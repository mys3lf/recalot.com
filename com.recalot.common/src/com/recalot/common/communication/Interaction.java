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

import java.util.Date;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Interaction {

    private String id;
    private String userId;
    private String itemId;
    private Date timeStamp;
    private String type;
    private Map<String, String> content;
    private String value;

    public Interaction(String id, String userId, String itemId, Date timeStamp, String type, String value, Map<String, String> content) {
        this.value = value;
        this.id = id.intern();
        this.userId = userId.intern();
        this.itemId = itemId.intern();
        this.timeStamp = timeStamp;
        this.type = type.intern();
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getItemId() {
        return itemId;
    }

    public Date getTimeStamp() {
        return timeStamp;
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

    public String getValue() {
        return value;
    }
}
