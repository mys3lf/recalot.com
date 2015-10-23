// Copyright (C) 2015 Matthäus Schmedding
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
    public Relation[] getRelations() throws BaseException {
        return dataSource.getRelations();
    }

    @Override
    public Relation getRelation(String relationId) throws BaseException {
        return dataSource.getRelation(relationId);
    }

    @Override
    public Relation[] getRelations(String fromId, String toId) throws BaseException {
        return dataSource.getRelations(fromId, toId);
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
    @Override
    public int getRelationCount() {
        return dataSource.getRelationCount();
    }
}
