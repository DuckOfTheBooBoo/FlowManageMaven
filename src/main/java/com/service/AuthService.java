/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service;
import com.dao.UserDAO;
import com.util.Generated;
import com.util.HashUtil;
import com.model.pojo.User;
import java.io.Serializable;

/**
 *
 * @author pc
 */
public class AuthService implements Serializable {
    private final UserDAO userDAO = new UserDAO();
    private final HashUtil hashUtil = new HashUtil();
    
    public User authenticate(String email, String password) {
        // Compare password
        User dbUser = getUserDAO().getUserByEmail(email);

        if (dbUser == null) {
            return null;
        }
        
        try {
            boolean isValid = getHashUtil().authenticate(password, dbUser.getPassword());
            if (isValid) {
                return dbUser;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    @Generated
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Generated
    public HashUtil getHashUtil() {
        return hashUtil;
    }
}
