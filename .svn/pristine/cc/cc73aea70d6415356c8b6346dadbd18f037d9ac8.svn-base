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

    public HashMap<String, User> users;
    public HashMap<String, Item> items;
    public HashMap<String, Interaction> interactions;

    private DataSet dataSet;

    public DataSourceBase(){
        this.users = new HashMap<>();
        this.items = new HashMap<>();
        this.interactions = new HashMap<>();

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
        if (!items.containsKey(itemId)) throw new NotFoundException("Item with id %s cannot be found.", "" + itemId);
        return items.get(itemId);
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
        if (!users.containsKey(userId)) throw new NotFoundException("User with id %s cannot be found.", "" + userId);
        return users.get(userId);
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
    public User updateUser(String userId, Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("updateUser");
    }

    @Override
    public User createUser(Map<String, String> content) throws BaseException {
        throw throwNotSupportedException("createUser");
    }

    private NotSupportedException throwNotSupportedException(String createUser) throws NotSupportedException {
        return new NotSupportedException("The method %s is not supported in this data connection. This data connection is a read only data connection.", createUser);
    }
}
