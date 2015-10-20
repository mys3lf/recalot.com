package com.recalot.model.rec.context;

import com.recalot.common.context.Context;

import java.io.IOException;
import java.util.Date;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class LastVisited implements Context {
    @Override
    public Object getContext(String userId) {
        return getContext(userId, null);
    }

    @Override
    public Object getContext(String userId, String itemId) {
        return getContext(userId, null);
    }

    @Override
    public Object getContext(String userId, String itemId, Date timestamp) {
        return getContext(userId, null);
    }

    @Override
    public String getKey() {
        return "last-visited";
    }

    @Override
    public String getDescription() {
        return "Returns the last visited items.";
    }

    @Override
    public void close() throws IOException {

    }
}
