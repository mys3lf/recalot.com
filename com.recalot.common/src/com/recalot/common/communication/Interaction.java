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
