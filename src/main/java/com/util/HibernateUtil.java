package com.util;  
import org.hibernate.SessionFactory;  
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;  
import org.hibernate.cfg.Configuration;  
import org.hibernate.service.ServiceRegistry;  

public class HibernateUtil {  
    //Annotation based configuration  
    private static SessionFactory sessionFactory;  
    private static SessionFactory buildSessionFactory() {  
        try {           
            Configuration configure = new Configuration();
            configure.configure("hibernate.cfg.xml");
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(configure.getProperties()).build();
            return configure.buildSessionFactory(registry);
        }  
        catch (Throwable ex)  
        {  
            // Make sure you log the exception, as it might be swallowed  
            System.err.println("Initial SessionFactory creation failed." + ex);  
            throw new ExceptionInInitializerError(ex);  
        }  
    }  
    public static SessionFactory getSessionFactory()  
    {  
        if (sessionFactory == null) sessionFactory = buildSessionFactory();  
        return sessionFactory;  
    }  
    public static void shutdown()  
    {  
        // Close caches and connection pools  
        sessionFactory.close();  
    }  
}  