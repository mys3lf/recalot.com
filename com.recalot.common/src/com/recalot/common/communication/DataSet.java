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

/**
 * The Data set contains all information of a connected data source.
 * A data set consists of
 *  -users
 *  -items
 *  -interactions between users/items
 *  -relations
 * @author Matthäus Schmedding (info@recalot.com)
 */
public interface DataSet {

    /**
     * Get all available interactions
     * @return all interactions in the data set
     * @throws BaseException
     */
    public Interaction[] getInteractions()  throws BaseException;

    /**
     * Get all available interactions of a specific user
     * @param userId user id of a specific user
     * @return all interactions in the data set of a user
     * @throws BaseException
     */
    public Interaction[] getInteractions(String userId)  throws BaseException;

    /**
     * Get user information
     * @param userId user id
     * @return user information
     * @throws BaseException
     */
    public User getUser(String userId) throws BaseException;

    /**
     * Get item information
     * @param itemId item id
     * @return item information
     * @throws BaseException
     */
    public Item getItem(String itemId) throws BaseException;

    /**
     * Get all items
     * @return all available items in the data set
     * @throws BaseException
     */
    public Item[] getItems() throws BaseException;

    /**
     * Get all users
     * @return all available users in the data set
     * @throws BaseException
     */
    public User[] getUsers() throws BaseException;

    /**
     * Get all relations
     * @return all available relations in the data set
     * @throws BaseException
     */
    public Relation[] getRelations() throws BaseException;

    /**
     * Get relation information
     * @param relationId relation id
     * @return relation information
     * @throws BaseException
     * */
    public Relation getRelation(String relationId) throws BaseException;

    /**
     * Get relation information
     * @param fromId from id
     * @param toId to id
     * @return relation information
     * @throws BaseException
     * */
    public Relation[] getRelations(String fromId, String toId) throws BaseException;

    /**
     *
     * @return item count
     */
    public int getItemsCount();


    /**
     *
     * @return user count
     */
    public int getUsersCount();

    /**
     *
     * @return interaction count
     */
    public int getInteractionsCount();

    /**
     *
     * @return relation count
     */
    public int getRelationCount();
}
