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

package com.recalot.model.data.connections.mysql;

import com.recalot.common.Helper;
import com.recalot.common.communication.*;
import com.recalot.common.configuration.Configuration;
import com.recalot.common.exceptions.AlreadyExistsException;
import com.recalot.common.exceptions.BaseException;
import com.recalot.common.exceptions.NotFoundException;
import com.recalot.common.interfaces.model.data.DataSource;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author matthaeus.schmedding
 */

@Configuration(key = "sqlServer")
@Configuration(key = "sqlUsername")
@Configuration(key = "sqlPassword")
@Configuration(key = "sqlDatabase")
public class MySQLDataSource extends DataSource {

    private boolean initialized;
    private DataSet dataSet;

    private String sqlServer;
    private String sqlUsername;
    private String sqlPassword;
    private String sqlDatabase;

    private String connectionPlaceHolder = "jdbc:%s/%s?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
    private Connection connection;

    public ConcurrentHashMap<String, User> users;
    public ConcurrentHashMap<String, Item> items;
    public ConcurrentHashMap<String, Interaction> interactions;
    public ConcurrentHashMap<String, Relation> relations;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private IdComputation idComputation;

    public MySQLDataSource() {
        users = new ConcurrentHashMap<>();
        items = new ConcurrentHashMap<>();
        interactions = new ConcurrentHashMap<>();
        relations = new ConcurrentHashMap<>();
        this.initialized = false;
    }


    public String getSqlServer() {
        return sqlServer;
    }

    public void setSqlServer(String sqlServer) {
        this.sqlServer = sqlServer;
    }

    public String getSqlUsername() {
        return sqlUsername;
    }

    public void setSqlUsername(String sqlUsername) {
        this.sqlUsername = sqlUsername;
    }

    public String getSqlPassword() {
        return sqlPassword;
    }

    public void setSqlPassword(String sqlPassword) {
        this.sqlPassword = sqlPassword;
    }

    public String getSqlDatabase() {
        return sqlDatabase;
    }

    public void setSqlDatabase(String sqlDatabase) {
        this.sqlDatabase = sqlDatabase;
    }

    public Connection getNewConnection() {
        try {
           if(this.connection != null) this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("getNewConnection");
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(String.format(connectionPlaceHolder, sqlServer, sqlDatabase), sqlUsername, sqlPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void connect() throws BaseException {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.connection = DriverManager.getConnection(String.format(connectionPlaceHolder, sqlServer, sqlDatabase), sqlUsername, sqlPassword);
            this.dataSet = new DataSourceDataSet(this);

            if (!structureAvailable()) {
                setInfo("Create DB structure");
                createDBstructure();
            }

            setInfo("Read ID table");
            readIdComputation();
            setInfo("Read Users");
            readUsers();
            setInfo("Read Items");
            readItems();
            setInfo("Read Interactions");
            readInteractions();
            setInfo("Read Relations");
            readRelations();

            setInfo("Done");

            this.initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("SQL Server not found");
        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readIdComputation() {
        ResultSet result = null;
        PreparedStatement statement = null;
        this.idComputation = new IdComputation();

        try {
            statement = connection.prepareStatement("SELECT * FROM " + sqlDatabase + ".idcomputation");
            result = statement.executeQuery();

            while (result.next()) {
                try {
                    String type = result.getString(1);
                    String next = result.getString(2);
                    int parsed = Integer.parseInt(next);
                    if (parsed > 0) {
                        this.idComputation.setNextID(type, parsed);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (result != null) try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class IdComputation {
        private final HashMap<String, Integer> nextDict;

        private IdComputation() {
            this.nextDict = new HashMap<>();
        }

        private int getNextID(String type) {
            int next = 1;
            if (nextDict.containsKey(type)) {
                next = nextDict.get(type);
                next++;
            }

            nextDict.put(type, next);

            return next;
        }

        private void setNextID(String type, int next) {
            nextDict.put(type, next);
        }

        public Set<String> getTypes() {
            return nextDict.keySet();
        }

        public int getCurrent(String key) {
            return nextDict.get(key);
        }
    }

    public boolean structureAvailable() throws SQLException {

        ResultSet result = null;
        try {
            DatabaseMetaData md = connection.getMetaData();
            result = md.getTables(null, null, "%", null);

            ArrayList<String> existingTables = new ArrayList<>();
            while (result.next()) {
                existingTables.add(result.getString(3).toLowerCase());

                // System.out.println(result.getString(3).toLowerCase());
            }

            ArrayList<String> necessaryTables = new ArrayList<>();
            necessaryTables.add("users");
            necessaryTables.add("items");
            necessaryTables.add("interactions");
            necessaryTables.add("relations");
            necessaryTables.add("idcomputation");

            for (String t : necessaryTables) {
                if (!existingTables.contains(t)) return false;
            }

            return true;
        } catch (Exception e) {
            return false;  //To change body of created methods use File | Settings | File Templates.
        } finally {
            if (result != null) result.close();
        }
    }

    public void createDBstructure() throws SQLException {

        Statement statement = null;
        ResultSet result = null;

        try {

            DatabaseMetaData md = connection.getMetaData();
            result = md.getTables(null, null, "%", null);

            ArrayList<String> existingTables = new ArrayList<>();
            while (result.next()) {
                existingTables.add(result.getString(3).toLowerCase());
            }


            statement = connection.createStatement();

            if (!existingTables.contains("users")) {
                //    statement.execute("DROP TABLE IF EXISTS users");
                statement.execute("CREATE TABLE users (" +
                        "id VARCHAR(128) NOT NULL PRIMARY KEY," +
                        "content TEXT NOT NULL)");
            }

            if (!existingTables.contains("items")) {
                //     statement.execute("DROP TABLE IF EXISTS items");
                statement.execute("CREATE TABLE items (" +
                        "id VARCHAR(128) NOT NULL PRIMARY KEY," +
                        "content TEXT NOT NULL)");

            }

            if (!existingTables.contains("interactions")) {
                //    statement.execute("DROP TABLE IF EXISTS interactions");
                statement.execute("CREATE TABLE interactions (" +
                        "id VARCHAR(128) NOT NULL PRIMARY KEY," +
                        "userId VARCHAR(128) NOT NULL," +
                        "itemId VARCHAR(128) NOT NULL," +
                        "timeStamp TIMESTAMP NOT NULL," +
                        "type VARCHAR(128) NOT NULL," +
                        "value VARCHAR(128) NOT NULL," +
                        "content TEXT NOT NULL)");

            }

            if (!existingTables.contains("relations")) {
                statement.execute("CREATE TABLE relations (" +
                        "id VARCHAR(128) NOT NULL PRIMARY KEY," +
                        "fromId VARCHAR(128) NOT NULL," +
                        "toId VARCHAR(128) NOT NULL," +
                        "type VARCHAR(128) NOT NULL," +
                        "content TEXT NOT NULL)");
            }

            if (!existingTables.contains("idcomputation")) {
                //     statement.execute("DROP TABLE IF EXISTS idcomputaion");
                statement.execute("CREATE TABLE idcomputation (" +
                        "id VARCHAR(128) NOT NULL PRIMARY KEY," +
                        "next INT  NOT NULL)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (result != null) result.close();
        }
    }

    private void readInteractions() {
        ResultSet result = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT * FROM " + sqlDatabase + ".interactions");
            result = statement.executeQuery();

            while (result.next()) {
                try {
                    String id = result.getString(1);
                    String userId = result.getString(2);
                    String itemId = result.getString(3);
                    Date timeStamp;

                    try{
                        timeStamp = new Date(result.getTimestamp(4).getTime());
                    } catch (Exception e) {
                        timeStamp = new Date(0);
                    }

                    String type = result.getString(5);
                    String value = result.getString(6);

                    HashMap map = new JSONDeserializer<HashMap>().deserialize(result.getString(7));

                    interactions.put(id, new Interaction(id, userId, itemId, timeStamp, type, value, map));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (result != null) try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readRelations() {
        ResultSet result = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT * FROM " + sqlDatabase + ".relations");
            result = statement.executeQuery();

            while (result.next()) {
                try {
                    String id = result.getString(1);
                    String fromId = result.getString(2);
                    String toId = result.getString(3);
                    String type = result.getString(4);

                    HashMap map = new JSONDeserializer<HashMap>().deserialize(result.getString(5));

                    relations.put(id, new Relation(id, fromId, toId, type, map));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (result != null) try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readItems() {
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + sqlDatabase + ".items");
            result = statement.executeQuery();

            while (result.next()) {
                try {
                    String id = result.getString(1);
                    String content = result.getString(2);

                    HashMap map = new JSONDeserializer<HashMap>().deserialize(content);

                    items.put(id, new Item(id, map));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (result != null) try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readUsers() {
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + sqlDatabase + ".users");
            result = statement.executeQuery();

            while (result.next()) {
                try {
                    String id = result.getString(1);
                    String content = result.getString(2);
                    HashMap map = new JSONDeserializer<HashMap>().deserialize(content);

                    users.put(id, new User(id, map));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public DataSet getDataSet() {
        return this.dataSet;
    }


    @Override
    public void close() throws IOException {
        if (this.connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getRelationCount() {
        return relations.size();
    }

    @Override
    public Relation[] getRelations() throws BaseException {
        return relations.values().toArray(new Relation[relations.size()]);
    }

    @Override
    public Relation getRelation(String relationId) throws BaseException {
        return relations.get(relationId);
    }
    @Override
    public Relation[] getRelations(String fromId, String toId) throws BaseException {
        if(fromId != null && toId == null) {
            return relations.values().stream().filter(i -> i.getFromId().equals(fromId) ).toArray(s -> new Relation[s]);
        } else if(fromId == null && toId != null){
            return relations.values().stream().filter(i -> i.getToId().equals(toId)).toArray(s -> new Relation[s]);
        } else {
            return relations.values().stream().filter(i -> i.getFromId().equals(fromId) && i.getToId().equals(toId)).toArray(s -> new Relation[s]);
        }
    }

    @Override
    public Relation[] getRelationsFor(String fromId) throws BaseException {
        return relations.values().stream().filter(i -> i.getFromId().equals(fromId)).toArray(s -> new Relation[s]);
    }

    @Override
    public Relation updateRelation(String relationId, String fromId, String toId, String type, Map<String, String> content) throws BaseException {
        if (relations.containsKey(relationId)) {
            Relation relation = new Relation(relationId, fromId, toId,  type, filterParams(content, Helper.Keys.FromId, Helper.Keys.ToId, Helper.Keys.Type));

            relations.replace(relationId, relation);

            //  new Thread() {
            //      public void run() {
            updateAtSql(relation);
            //       }
            //  }.start();

            return relation;

        } else {
            throw new NotFoundException("An relationId with the id '%s' could not be found", relationId);
        }
    }

    @Override
    public Relation createRelation(String fromId, String toId, String type, Map<String, String> content) throws BaseException {
        Relation relation = new Relation("" + this.idComputation.getNextID("relation"), fromId, toId,  type, filterParams(content, Helper.Keys.FromId, Helper.Keys.ToId, Helper.Keys.Type));
        relations.put(relation.getId(), relation);

        putToSql(relation);

        return relation;
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
    public Message addInteraction(String itemId, String userId, Date timestamp, String type, String value, Map<String, String> content) throws BaseException {
        Interaction interaction = new Interaction("" + this.idComputation.getNextID("interactions"), userId, itemId, timestamp, type, value, filterParams(content, Helper.Keys.TimeStamp, Helper.Keys.UserId, Helper.Keys.UserId, Helper.Keys.ItemId));
        interactions.put(interaction.getId(), interaction);

        //  new Thread() {
        //    public void run() {
        putToSql(interaction);
        //    }
        // }.start();

        return new Message("Interaction successful saved", "Interaction successful saved", Message.Status.INFO);
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
    public Item tryGetItem(String itemId) throws BaseException {
        return items.get(itemId);
    }

    @Override
    public Item updateItem(String itemId, Map<String, String> content) throws BaseException {
        if (items.containsKey(itemId)) {
            Item item = new Item(itemId, filterParams(content));
            items.replace(itemId, item);

            //  new Thread() {
            //      public void run() {
            updateAtSql(item);
            //       }
            //  }.start();

            return item;

        } else {
            throw new NotFoundException("An item with the id '%s' could not be found", itemId);
        }
        //      return new Message("Item successful updated", "Item successful updated", Message.Status.INFO);
    }

    @Override
    public Item createItem(Map<String, String> content) throws BaseException {
        String wishedItemId = content.get(Helper.Keys.ItemId);

        if (wishedItemId != null && !wishedItemId.isEmpty()) {
            if (users.containsKey(wishedItemId))
                throw new AlreadyExistsException("An item with the id %s already exists.", wishedItemId);
        } else {
            wishedItemId = "" + this.idComputation.getNextID("items");
        }

        Item item = new Item(wishedItemId, filterParams(content));
        items.put(item.getId(), item);


        // new Thread() {
        //    public void run() {

        putToSql(item);
        //     }
        //  }.start();

        return item;

        //    return new Message("Item successful saved", "Item successful saved", Message.Status.INFO);
    }

    @Override
    public Message deleteItem(String itemId) throws BaseException {

        if(items.containsKey(itemId)) {
            items.remove(itemId);
            removeItemFromSql(itemId);

            return new Message("Item successful removed", "Item with id='" + itemId + "' successful deleted", Message.Status.INFO);
        } else {
            throw new NotFoundException("Item with id %s cannot be found.", "" + itemId);
        }
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
    public User tryGetUser(String userId) throws BaseException {
        return users.get(userId);
    }

    @Override
    public User updateUser(String userId, Map<String, String> content) throws BaseException {
        if (users.containsKey(userId)) {
            User user = new User(userId, filterParams(content));
            users.replace(userId, user);

            //  new Thread() {
            //     public void run() {

            updateAtSql(user);
            //     }
            //  }.start();


            return user;
        } else {
            throw new NotFoundException("A user with the id '%s' could not be found", userId);
        }

        // return new Message("User successful updated", "User successful updated", Message.Status.INFO);
    }


    @Override
    public User createUser(Map<String, String> content) throws BaseException {
        String wishedUserId = content.get(Helper.Keys.UserId);

        if (wishedUserId != null && !wishedUserId.isEmpty()) {
            if (users.containsKey(wishedUserId))
                throw new AlreadyExistsException("A user with the id %s already exists.", wishedUserId);
        } else {
            wishedUserId = "" + this.idComputation.getNextID("users");
        }

        User user = new User(wishedUserId, filterParams(content));
        users.put(user.getId(), user);

        //new Thread() {
        //     public void run() {
        putToSql(user);
        //       }
        //  }.start();

        return user;

//        return new Message("User successful saved", "User successful saved", Message.Status.INFO);
    }

    private void putToSql(User user) {

        PreparedStatement statement = null;
        try {
            writeLock.lock();

            Map<String, String> contentMap = filterParams(user.getContent());
            String content = new JSONSerializer().serialize(contentMap);

            if (connection == null || connection.isClosed() || !connection.isValid(2)) connection = getNewConnection();

            // PreparedStatements can use variables and are more efficient
            statement = connection.prepareStatement("INSERT IGNORE INTO users (id, content) VALUES (?, ?)");

            statement.setString(1, user.getId());    //user_id
            statement.setString(2, content);    //content

            statement.executeUpdate();

            putToSql(idComputation);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            writeLock.unlock();
        }
    }

    private void putToSql(IdComputation idComputation) {

        PreparedStatement statement = null;
        try {

            // PreparedStatements can use variables and are more efficient

            statement = connection.prepareStatement("REPLACE INTO idcomputation (id, next) VALUES (?, ?)");

            for (String key : idComputation.getTypes()) {

                statement.setString(1, key);    //user_id
                statement.setInt(2, idComputation.getCurrent(key));    //content

                statement.executeUpdate();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }
        }
    }

    private void putToSql(Item item) {

        PreparedStatement statement = null;
        try {
            writeLock.lock();

            Map<String, String> contentMap = filterParams(item.getContent());

            String content = new JSONSerializer().serialize(contentMap);

            if (connection == null || connection.isClosed() || !connection.isValid(2)) connection = getNewConnection();

            // PreparedStatements can use variables and are more efficient
            statement = connection.prepareStatement("INSERT IGNORE INTO items (id, content) VALUES (?, ?)");

            statement.setString(1, item.getId());    //user_id
            statement.setString(2, content);    //content

            statement.executeUpdate();

            putToSql(idComputation);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            writeLock.unlock();
        }
    }

    private void putToSql(Interaction interaction) {
        PreparedStatement statement = null;
        try {
            writeLock.lock();

            Map<String, String> contentMap = filterParams(interaction.getContent(), "type", "value");

            String content = new JSONSerializer().serialize(contentMap);

            if (connection == null || connection.isClosed() || !connection.isValid(2)) connection = getNewConnection();

            // PreparedStatements can use variables and are more efficient
            statement = connection.prepareStatement("INSERT IGNORE INTO interactions (id, userId, itemId, timeStamp, type, value, content) VALUES (?, ?, ?, ?, ? , ? , ?)");

            statement.setString(1, interaction.getId());    //user_id
            statement.setString(2, interaction.getUserId());    //user_id
            statement.setString(3, interaction.getItemId());    //user_id
            statement.setTimestamp(4, new java.sql.Timestamp(interaction.getTimeStamp().getTime()));    //user_id
            statement.setString(5, interaction.getType());    //user_id
            statement.setString(6, interaction.getValue());    //content
            statement.setString(7, content);    //content

            statement.executeUpdate();

            putToSql(idComputation);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            writeLock.unlock();
        }
    }

    private void putToSql(Relation relation) {
        PreparedStatement statement = null;
        try {
            writeLock.lock();

            Map<String, String> contentMap = filterParams(relation.getContent(), "type", "value");

            String content = new JSONSerializer().serialize(contentMap);

            if (connection == null || connection.isClosed() || !connection.isValid(2)) connection = getNewConnection();

            // PreparedStatements can use variables and are more efficient
            statement = connection.prepareStatement("INSERT IGNORE INTO relations (id, fromId, toId, type, content) VALUES (?, ?, ?, ?, ?)");

            statement.setString(1, relation.getId());    //user_id
            statement.setString(2, relation.getFromId());    //user_id
            statement.setString(3, relation.getToId());    //user_id
            statement.setString(4, relation.getType());    //user_id
            statement.setString(5, content);    //content

            statement.executeUpdate();

            putToSql(idComputation);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            writeLock.unlock();
        }
    }

    private void updateAtSql(Relation relation) {
        PreparedStatement statement = null;
        try {
            writeLock.lock();

            Map<String, String> contentMap = filterParams(relation.getContent(), "type", "value");

            String content = new JSONSerializer().serialize(contentMap);

            if (connection == null || connection.isClosed() || !connection.isValid(2)) connection = getNewConnection();

            // PreparedStatements can use variables and are more efficient
            statement = connection.prepareStatement("UPDATE relations SET fromId=?, toId=?, type=?, content=? WHERE id=?");

            statement.setString(5, relation.getId());    //user_id
            statement.setString(1, relation.getFromId());    //user_id
            statement.setString(2, relation.getToId());    //user_id
            statement.setString(3, relation.getType());    //user_id
            statement.setString(4, content);    //content

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            writeLock.unlock();
        }
    }

    private void updateAtSql(User user) {
        PreparedStatement statement = null;
        try {
            writeLock.lock();

            Map<String, String> contentMap = filterParams(user.getContent());

            String content = new JSONSerializer().serialize(contentMap);

            if (connection == null || connection.isClosed() || !connection.isValid(2)) connection = getNewConnection();

            // PreparedStatements can use variables and are more efficient
            statement = connection.prepareStatement("UPDATE users SET content=? WHERE id=?");

            statement.setString(2, user.getId());    //user_id
            statement.setString(1, content);    //content

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            writeLock.unlock();
        }
    }

    private void updateAtSql(Item item) {
        PreparedStatement statement = null;
        try {
            writeLock.lock();

            Map<String, String> contentMap = filterParams(item.getContent());
            String content = new JSONSerializer().serialize(contentMap);

            if (connection == null || connection.isClosed() || !connection.isValid(2)) connection = getNewConnection();

            // PreparedStatements can use variables and are more efficient
            statement = connection.prepareStatement("UPDATE items SET content=? WHERE id=?");

            statement.setString(2, item.getId());    //user_id
            statement.setString(1, content);    //content

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            writeLock.unlock();
        }
    }

    private void removeItemFromSql(String itemId) {
        PreparedStatement statement = null;
        try {
            writeLock.lock();

            if (connection == null || connection.isClosed() || !connection.isValid(2)) connection = getNewConnection();

            // PreparedStatements can use variables and are more efficient
            statement = connection.prepareStatement("DELETE FROM items WHERE id=?");

            statement.setString(1, itemId);    //item id

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            writeLock.unlock();
        }
    }


    private Map<String, String> filterParams(Map<String, String> content, String... ignore) {
        Map<String, String> map = new HashMap<>();

        for (String key : content.keySet()) {
            boolean contains = false;
            if (key.equals(Helper.Keys.SourceId)) {
                contains = true;
            } else if (key.equals(Helper.Keys.ID)) {
                contains = true;
            } else if (key.equals(Helper.Keys.UserId)) {
                contains = true;
            } else if (key.equals(Helper.Keys.ItemId)) {
                contains = true;
            } else if (ignore != null && ignore.length > 0) {
                for (String test : ignore) {
                    if (test.equals(key)) {
                        contains = true;
                        break;
                    }
                }
            }
            if (!contains) {
                map.put(key, content.get(key));
            }
        }

        return map;
    }
}
