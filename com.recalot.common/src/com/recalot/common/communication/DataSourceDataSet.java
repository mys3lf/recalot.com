package com.recalot.common.communication;

import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;

/**
 * Gets an instance of a connected data source and allows the access of the containing information
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class DataSourceDataSet implements DataSet {
    /**
     * DataSource instance
     */
    private DataSource dataSource;

    /**
     * Constructor
     * @param dataSource data source
     */
    public DataSourceDataSet(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Interaction[] getInteractions()  throws BaseException {
        return dataSource.getInteractions();
    }

    @Override
    public Interaction[] getInteractions(String userId) throws BaseException {
        return dataSource.getInteractions(userId);
    }

    @Override
    public User getUser(String userId) throws BaseException {
        return dataSource.getUser(userId);
    }

    @Override
    public Item getItem(String itemId) throws BaseException {
        return dataSource.getItem(itemId);
    }

    @Override
    public Item[] getItems() throws BaseException {
        return dataSource.getItems();
    }

    @Override
    public User[] getUsers() throws BaseException {
        return dataSource.getUsers();
    }

    @Override
    public int getItemsCount() {
        return dataSource.getItemsCount();
    }

    @Override
    public int getUsersCount() {
        return dataSource.getUsersCount();
    }

    @Override
    public int getInteractionsCount() {
        return dataSource.getInteractionsCount();
    }
}
