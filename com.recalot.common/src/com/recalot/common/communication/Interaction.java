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
import com.recalot.common.exceptions.NotFoundException;

import java.util.Date;
import java.util.Map;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Interaction {

    private int id;
    private int userId;
    private int itemId;
    private Date timeStamp;
    private int type;
    private Map<String, String> content;
    private int value;

    public Interaction(String id, String userId, String itemId, Date timeStamp, String type, String value, Map<String, String> content) {

        this.value = InnerIds.getNextId(value, Helper.Keys.Value);
        this.id = InnerIds.getNextId(id, Helper.Keys.InteractionId);
        this.userId = InnerIds.getNextId(userId, Helper.Keys.UserId);
        this.itemId = InnerIds.getNextId(itemId, Helper.Keys.ItemId);
        this.type = InnerIds.getNextId(type, Helper.Keys.Type);

        this.timeStamp = timeStamp;
        this.content = content;
    }

    public String getId() {
        return InnerIds.getId(id, Helper.Keys.InteractionId);
    }

    public String getUserId() {
        return InnerIds.getId(userId, Helper.Keys.UserId);
    }

    public String getItemId() {
        return InnerIds.getId(itemId, Helper.Keys.ItemId);
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getType() {
        return InnerIds.getId(type, Helper.Keys.Type);
    }

    public Map<String, String> getContent() {
        return content;
    }

    public String getValue(String key) {
        return content.get(key);
    }

    public String getValue() {
        return InnerIds.getId(value, Helper.Keys.Value);
    }
}
