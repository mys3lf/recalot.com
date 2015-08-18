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
