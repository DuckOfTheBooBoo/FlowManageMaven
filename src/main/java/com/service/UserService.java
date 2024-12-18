/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service;

import com.model.pojo.User;
import com.util.Generated;
import com.util.HashUtil;
import com.dao.UserDAO;

/**
 *
 * @author pc
 */
public class UserService {
    private UserDAO userDAO;
    private HashUtil hashUtil;

    public UserService() {
        this(new UserDAO(), new HashUtil());
    }

    public UserService(UserDAO userDAO, HashUtil hashUtil) {
        this.userDAO = userDAO;
        this.hashUtil = hashUtil;
    }

    public boolean createUser(String firstName, String lastName, String email, String password) {
        try {
            String hashedPassword = getHashUtil().hash(password);
            User newUser = new User(firstName, lastName, email, hashedPassword);
            boolean isSuccess = getUserDAO().addUser(newUser);
            if (!isSuccess) {
                throw new Exception("Failed to create new user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public User getUserByEmail(String email) {
        User user = null;
        try {
            user = userDAO.getUserByEmail(email);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return user;
    }

    @Generated
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Generated
    public HashUtil getHashUtil() {
        return hashUtil;
    }

    @Generated
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Generated
    public void setHashUtil(HashUtil hashUtil) {
        this.hashUtil = hashUtil;
    }
}
