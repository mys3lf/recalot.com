package com.recalot.common.interfaces.model.data;

import com.recalot.common.communication.Interaction;
import com.recalot.common.communication.Message;
import com.recalot.common.exceptions.BaseException;

import java.util.Date;
import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public interface InteractionDataAccess {
    public Interaction[] getInteractions() throws BaseException;
    public int getInteractionsCount();
    public Interaction[] getInteractions(String userId) throws BaseException;
    public Interaction[] getInteractions(String itemId, String userId) throws BaseException;
    public Message addInteraction(String itemId, String userId, Date timestamp, String type, String value, Map<String, String> content)  throws BaseException;
}
