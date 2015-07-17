package com.recalot.common.communication;

import com.recalot.common.exceptions.BaseException;
import com.recalot.common.interfaces.model.data.DataSource;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Matthaeus.schmedding
 */
public class FillableDataSet implements DataSet {
    private final ArrayList<Interaction> interactions;
    private final ArrayList<User> users;
    private final ArrayList<Item> items;

    public FillableDataSet() {
        this.items = new ArrayList<Item>();
        this.users = new ArrayList<User>();
        this.interactions = new ArrayList<Interaction>();
    }

    @Override
    public Interaction[] getInteractions() throws BaseException {
        return interactions.toArray(new Interaction[interactions.size()]);
    }

    @Override
    public Interaction[] getInteractions(String userId) throws BaseException {
        return interactions.stream().filter( i -> i.getUserId().equals(userId)).toArray(size -> new Interaction[size]);
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

    public void addInteraction(Interaction interaction) throws BaseException {
        interactions.add(interaction);
    }

    public void addItem(Item item) throws BaseException {
        items.add(item);
    }

    public void addUser(User user) throws BaseException {
        users.add(user);
    }


    public int getItemsCount() {
        return items.size();
    }

    public int getUsersCount() {
        return users.size();
    }

    public int getInteractionsCount() {
        return interactions.size();
    }
}
