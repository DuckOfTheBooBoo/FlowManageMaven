/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service;
import com.dao.UserDAO;
import com.util.HashUtil;
import com.model.pojo.User;
import java.io.Serializable;

/**
 *
 * @author pc
 */
public class AuthService implements Serializable {
    private final UserDAO userDAO = new UserDAO();
    
    public User authenticate(String email, String password) {
        // Compare password
        User dbUser = userDAO.getUserByEmail(email);
        HashUtil hashUtil = new HashUtil();
        
        if (dbUser == null) {
            return null;
        }
        
        try {
            boolean isValid = hashUtil.authenticate(password, dbUser.getPassword());
            if (isValid) {
                return dbUser;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
