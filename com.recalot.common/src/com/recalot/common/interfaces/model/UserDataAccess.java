package com.recalot.common.interfaces.model;

import com.recalot.common.interfaces.communication.User;
import com.recalot.common.interfaces.communication.Users;

import java.util.HashMap;

/**
 * @author Matthaeus.schmedding
 */
public interface UserDataAccess {
    public Users getUsers();
    public User getUser(String userId);
    public void updateUser(String userId, HashMap<String, Object> content);
    public String createUser(HashMap<String, Object> content);
}
