package com.recalot.common.context;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public interface UserContext extends Context {
    public Object getContext(String sourceId, String userId);
    public void processContext(String sourceId, String userId, Object context);
    public void processContext(String sourceId, String userId, Object context, String type);
}

