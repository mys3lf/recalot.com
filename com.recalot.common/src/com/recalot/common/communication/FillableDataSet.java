package com.recalot.common.communication;

import com.recalot.common.exceptions.BaseException;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Fillable data set. This data set can be filled manually.
 *
 * @author Matthaeus.schmedding
 */
public class FillableDataSet implements DataSet {

    /**
     * Interactions list
     */
    private final ArrayList<Interaction> interactions;

    /**
     * Users list
     */
    private final ArrayList<User> users;

    /**
     * Items list
     */
    private final ArrayList<Item> items;

    /**
     * Default constructor
     *
     * Initializes the lists
     */
    public FillableDataSet() {
        this.items = new ArrayList<>();
        this.users = new ArrayList<>();
        this.interactions = new ArrayList<>();
    }

    /**
     * Add interaction to the data set
     * @param interaction interaction that should be added
     * @throws BaseException
     */
    public void addInteraction(Interaction interaction) throws BaseException {
        interactions.add(interaction);
    }

    /**
     * Add item to the data set
     * @param item item that should be added
     * @throws BaseException
     */
    public void addItem(Item item) throws BaseException {
        items.add(item);
    }

    /**
     * Add user to the data set
     * @param user user that should be added
     * @throws BaseException
     */
    public void addUser(User user) throws BaseException {
        users.add(user);
    }

    @Override
    public Interaction[] getInteractions() throws BaseException {
        return interactions.toArray(new Interaction[interactions.size()]);
    }

    @Override
    public Interaction[] getInteractions(String userId) throws BaseException {
        return interactions.stream().filter( i -> i.getUserId().equals(userId)).toArray(Interaction[]::new);
    }

    @Override
    public Item[] getItems() throws BaseException {
        return items.toArray(new Item[items.size()]);
    }

    @Override
    public User[] getUsers() throws BaseException {
        return users.toArray(new User[users.size()]);
    }

    @Override
    public Item getItem(String itemId) throws BaseException {
        Optional<Item> item = items.stream().filter(i -> i.getId().equals(itemId)).findFirst();

        return item.isPresent() ? item.get() : null;
    }

    @Override
    public User getUser(String userId) throws BaseException {
        Optional<User> user = users.stream().filter(u -> u.getId().equals(userId)).findFirst();

        return user.isPresent() ? user.get() : null;
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    @Override
    public int getUsersCount() {
        return users.size();
    }

    @Override
    public int getInteractionsCount() {
        return interactions.size();
    }
}
