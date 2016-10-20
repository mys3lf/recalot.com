// Copyright (C) 2016 Matthäus Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.model.data.connections.base;

import com.recalot.common.communication.*;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.common.exceptions.NotSupportedException;
import com.recalot.common.interfaces.model.data.DataSource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthaeus.schmedding on 11.06.2015.
 */
public abstract class DataSourceBase extends DataSource {

    public Map<Integer, User> users;
    public Map<Integer, Item> items;
    public Map<Integer, Interaction> interactions;
    public Map<Integer, Relation> relations;

    private DataSet dataSet;

    public DataSourceBase(){
        this.users = new HashMap<>();
        this.items = new HashMap<>();
        this.interactions = new HashMap<>();
        this.relations = new HashMap<>();

        this.dataSet = new DataSourceDataSet(this);
    }

    @Override
    public DataSet getDataSet() {
        return this.dataSet;
    }

    @Override
    public Interaction[] getInteractions() throws BaseException {
        return interactions.values().toArray(new Interaction[interactions.size()]);
    }

    @Override
    public int getInteractionsCount() {
        return interactions.size();
    }

    @Override
    public Interaction[] getInteractions(String userId) throws BaseException {
        return interactions.values().stream().filter(i -> i.getUserId().equals(userId)).toArray(s -> new Interaction[s]);
    }

    @Override
    public Interaction[] getInteractions(String itemId, String userId) throws BaseException {
        return interactions.values().stream().filter(i -> i.getUserId().equals(userId) && i.getItemId().equals(itemId)).toArray(s -> new Interaction[s]);
    }

    @Override
    public Message addInteraction(String itemId, String userId, Date timestamp, String type, String value,  Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("addInteraction");
    }

    @Override
    public Item[] getItems() throws BaseException {
        return items.values().toArray(new Item[items.size()]);
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    @Override
    public Item getItem(String itemId) throws BaseException {
        if (!items.containsKey(InnerIds.getId(itemId))) throw new NotFoundException("Item with id %s cannot be found.", "" + itemId);
        return items.get(InnerIds.getId(itemId));
    }

    @Override
    public Item tryGetItem(String itemId){
        return items.get(InnerIds.getId(itemId));
    }

    @Override
    public User[] getUsers() throws BaseException {
        return users.values().toArray(new User[users.size()]);
    }

    @Override
    public int getUsersCount() {
        return users.size();
    }

    @Override
    public User getUser(String userId) throws BaseException {
        if (!users.containsKey(InnerIds.getId(userId))) throw new NotFoundException("User with id %s cannot be found.", "" + userId);
        return users.get(InnerIds.getId(userId));
    }

    @Override
    public User tryGetUser(String userId) throws BaseException {
        return users.get(InnerIds.getId(userId));
    }

    @Override
    public Item updateItem(String itemId, Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("updateItem");
    }

    @Override
    public Item createItem(Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("createItem");
    }

    @Override
    public Message deleteItem(String itemId) throws BaseException {
        throw throwNotSupportedException("deleteItem");
    }

    @Override
    public User updateUser(String userId, Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("updateUser");
    }

    @Override
    public User createUser(Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("createUser");
    }

    @Override
    public Relation getRelation(String relationId) throws BaseException {
        return relations.get(InnerIds.getId(relationId));
    }

    @Override
    public Relation[] getRelations(String fromId, String toId) throws BaseException {
        return relations.values().stream().filter(i -> i.getFromId().equals(fromId) && i.getToId().equals(toId)).toArray(s -> new Relation[s]);
    }

    @Override
    public Relation[] getRelationsFor(String fromId) throws BaseException {
        return relations.values().stream().filter(i -> i.getFromId().equals(fromId)).toArray(s -> new Relation[s]);
    }

    @Override
    public Relation[] getRelations() throws BaseException {
        return relations.values().toArray(new Relation[relations.size()]);
    }

    @Override
    public int getRelationCount() {
        return relations.size();
    }

    @Override
    public Relation updateRelation(String relationId, String fromId, String toId, String type, Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("updateRelation");
    }

    @Override
    public Relation createRelation(String fromId, String toId, String type, Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("createRelation");
    }

    private NotSupportedException throwNotSupportedException(String createUser) throws NotSupportedException {
        return new NotSupportedException("The method %s is not supported in this data connection. This data connection is a read only data connection.", createUser);
    }
}
