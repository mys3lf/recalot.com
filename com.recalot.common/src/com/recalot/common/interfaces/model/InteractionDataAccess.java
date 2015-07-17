package com.recalot.common.interfaces.model;

import com.recalot.common.interfaces.communication.*;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Matthaeus.schmedding
 */
public interface InteractionDataAccess {
    public Interactions getInteractions();
    public Interactions getInteractions(String userId);
    public Interaction getInteraction(String itemId, String userId);
    public void addInteraction(String itemId, String userId, Date timestamp, String type);
    public void addInteraction(String itemId, String userId, Date timestamp, String type, HashMap<String, Object> content);
}
