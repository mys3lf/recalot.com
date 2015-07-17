package com.recalot.common.interfaces.model;

import com.recalot.common.interfaces.communication.*;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Matthaeus.schmedding
 */
public interface ItemDataAccess {
    public Items getItems();
    public Item getItem(String itemId);
    public void updateItem(String itemId, HashMap<String, Object> content);
    public String createItem(HashMap<String, Object> content);
}
