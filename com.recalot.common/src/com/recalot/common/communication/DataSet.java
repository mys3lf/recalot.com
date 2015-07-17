package com.recalot.common.communication;

import com.recalot.common.exceptions.BaseException;

/**
 * The Data set contains all information of a connected data source.
 * A data set consists of
 *  -users
 *  -items
 *  -interactions between users/items
 *
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
}
