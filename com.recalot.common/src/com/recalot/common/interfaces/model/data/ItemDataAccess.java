package com.recalot.common.interfaces.model.data;

import com.recalot.common.communication.Item;
import com.recalot.common.communication.Message;
import com.recalot.common.exceptions.BaseException;

import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public interface ItemDataAccess {
    public Item[] getItems() throws BaseException;
    public int getItemsCount();

    public Item getItem(String itemId) throws BaseException;
    public Item tryGetItem(String itemId) throws BaseException;
    public Item updateItem(String itemId, Map<String, String> content) throws BaseException;
    public Item createItem(Map<String, String> content) throws BaseException;
}
