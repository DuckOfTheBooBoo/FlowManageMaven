package com.dao;

import com.model.pojo.Status;
import com.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.util.List;
import org.hibernate.Query;

public class StatusDAO {
    public StatusDAO() {}
    
    public List<Status> getAllStatus() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Status> statusList = null;
        try {
            Query qu = session.createQuery("FROM Status");
            statusList = (List<Status>) qu.list();
        } catch (Exception e) {
            e.printStackTrace();   
        }
        session.close();
        
        return statusList;
    }
    
    // Get a Status by ID
    public Status getStatusById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Status stat = (Status) session.get(Status.class, id);
            return stat;
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            session.close();
        }
        
        return null;
    }
}