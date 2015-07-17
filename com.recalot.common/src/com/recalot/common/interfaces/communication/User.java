package com.recalot.common.interfaces.communication;

import java.util.HashMap;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public interface User {
    String getId();
    HashMap<String, Object> getAll();
    Object getValue(String key);
}
