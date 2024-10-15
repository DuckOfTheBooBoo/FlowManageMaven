/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao;

import com.model.pojo.User;
import com.util.HibernateUtil;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author pc
 */
public class UserDAO {
    public UserDAO(){};
    
    public List<User> getAllUsers() {
        List<User> users = null;
        Session session = HibernateUtil.getSessionFactory().openSession();  
        try {  
            session.beginTransaction();  
            users = session.createCriteria(User.class).list();  
            int count = users.size();
            session.getTransaction().commit();  
        } catch (Exception e) {  
            e.printStackTrace();  
            session.getTransaction().rollback();  
        }  
        session.close();  
        return users;  
    }
    
    public User getUserByID(int id) {
        User user = null;
        Session session = HibernateUtil.getSessionFactory().openSession();  
        try {  
            session.beginTransaction();  
            Query qu = session.createQuery("From User U WHERE U.id = :userID");
            qu.setParameter("userID", id);
            user = (User) qu.uniqueResult();
            session.getTransaction().commit();  
        } catch (Exception e    ) {  
            e.printStackTrace();  
            session.getTransaction().rollback();  
        }  
        session.close();  
        return user;  
    }
    
    public User getUserByEmail(String email) {
        User user = null;
        Session session = HibernateUtil.getSessionFactory().openSession();  
        try {
            Query qu = session.createQuery("From User U WHERE U.email = :email");
            qu.setParameter("email", email);
            user = (User) qu.uniqueResult();
        } catch (Exception e    ) {  
            e.printStackTrace();
        }  
        session.close();  
        return user;  
    }
    
    public boolean addUser(User newUser) {
        Session session = HibernateUtil.getSessionFactory().openSession();  
        try {  
            session.beginTransaction();
            session.save(newUser);
            session.getTransaction().commit();
        }  
        catch (Exception e) {  
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        }  
        session.close();
        return true;
    }
}
