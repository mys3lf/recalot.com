package com.recalot.common.interfaces.communication;

import java.util.List;

/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public interface Interactions {
    public Interaction getInteraction(String userId, String itemId);
    public List<Interaction> getAll();
}
