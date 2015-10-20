package com.recalot.common.communication;

import com.recalot.common.exceptions.BaseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
     * Relation list
     */
    private final ArrayList<Relation> relations;

    /**
     * Default constructor
     *
     * Initializes the lists
     */
    public FillableDataSet() {
        this.items = new ArrayList<>();
        this.users = new ArrayList<>();
        this.interactions = new ArrayList<>();
        this.relations = new ArrayList<>();
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
    public Relation[] getRelations() throws BaseException {
        return relations.toArray(new Relation[relations.size()]);
    }

    @Override
    public Relation getRelation(String relationId) throws BaseException {
        Optional<Relation> item = relations.stream().filter(i -> i.getId().equals(relationId)).findFirst();

        return item.isPresent() ? item.get() : null;
    }

    @Override
    public Relation[] getRelations(String fromId, String toId) throws BaseException {
        if(fromId != null && toId == null) {
            return relations.stream().filter(i -> i.getFromId().equals(fromId) ).toArray(s -> new Relation[s]);
        } else if(fromId == null && toId != null){
            return relations.stream().filter(i -> i.getToId().equals(toId)).toArray(s -> new Relation[s]);
        } else {
            return relations.stream().filter(i -> i.getFromId().equals(fromId) && i.getToId().equals(toId)).toArray(s -> new Relation[s]);
        }
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

    @Override
    public int getRelationCount() {
        return 0;
    }

    public static DataSet createDataSet(DataSet dataSet, Interaction[] omitInteractions) throws BaseException {
        //create empty dataset
        FillableDataSet temp = new FillableDataSet();

        //save interaction ids that should be omitted
        Map<String, Boolean> interactionMap = new HashMap<>();

        for(Interaction i: omitInteractions) {
            interactionMap.put(i.getId(), true);
        }

        //add all interaction except the interactions that should be omitted
        for (Interaction i : dataSet.getInteractions()) {
            if(!interactionMap.containsKey(i.getId())) {
                temp.addInteraction(i);
            }
        }

        User[] allUsers = dataSet.getUsers();
        Item[] allItems = dataSet.getItems();

        //copy all users
        for(User user: allUsers) {
            temp.addUser(user);
        }

        //copy all items
        for(Item item: allItems) {
            temp.addItem(item);
        }

        return temp;
    }
}
