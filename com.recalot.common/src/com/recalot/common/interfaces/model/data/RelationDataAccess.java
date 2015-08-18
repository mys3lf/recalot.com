package com.recalot.common.interfaces.model.data;

import com.recalot.common.communication.Item;
import com.recalot.common.communication.Message;
import com.recalot.common.communication.Relation;
import com.recalot.common.exceptions.BaseException;

import java.util.Map;

/**
 * @author Matthaeus.schmedding
 */
public interface RelationDataAccess {
    public Relation[] getRelations() throws BaseException;
    public Relation[] getRelations(String fromId, String toId) throws BaseException;
    public int getRelationCount();
    public Relation getRelation(String relationId) throws BaseException;
    public Relation updateRelation(String relationId, String fromId, String toId, String type, Map<String, String> content) throws BaseException;
    public Relation createRelation(String fromId, String toId, String type, Map<String, String> content) throws BaseException;
}
