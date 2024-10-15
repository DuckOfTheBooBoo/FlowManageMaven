/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao;

import com.model.pojo.Project;
import com.model.pojo.ProjectWorker;
import com.model.pojo.ProjectWorkerId;
import com.model.pojo.Task;
import com.model.pojo.User;
import com.util.HibernateUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author pc
 */
public class ProjectDAO {
    public Set<Project> getAllProjects(User user) {
        Set<Project> projects = null;
        Session session = HibernateUtil.getSessionFactory().openSession();  
        try {  
            String hql = "SELECT pw.project FROM ProjectWorker pw WHERE pw.user.id = :user_id";
            Query qu = session.createQuery(hql);
            qu.setParameter("user_id", user.getId());
            
            List<Project> tempProject = (List<Project>) qu.list();
            projects = new HashSet<Project>(tempProject);
        } catch (Exception e) {  
            e.printStackTrace();
        }  
        session.close();  
        return projects;  
    }
    
    public Project getProjectByID(int projectId, User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Project project = null;
        try {
            String hql = "SELECT pw.project FROM ProjectWorker pw WHERE pw.user.id = :user_id AND pw.project.id = :project_id";
            Query qu = session.createQuery(hql);
            qu.setParameter("user_id", user.getId());
            qu.setParameter("project_id", projectId);
            
            project = (Project) qu.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        session.close();
        return project;
    }
    
    public List<Task> getProjectTasks(int projectId) {
        TaskDAO taskDAO = new TaskDAO();
        return taskDAO.getTasks(projectId);
    }
    
    @SuppressWarnings("empty-statement")
    public Project addProject(Project newProject, User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Project createdProject = null;
        String defaultRole = "manager";

        try {
            tx = session.beginTransaction();

            // Save the new project
            session.save(newProject);
            tx.commit();
            createdProject = newProject;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            createdProject = null;
        } finally {
            session.close();
        }

        return createdProject;
    }
    
    public boolean addUserToProject(Project project, User user, String role) {
        if (project == null) {
            return false;
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            ProjectWorkerId pwId = new ProjectWorkerId(user.getId(), project.getId());
            System.err.println(pwId);
            ProjectWorker newProjectWorker = new ProjectWorker(pwId, project, user, role);
            System.err.println(newProjectWorker);

            project.getProjectWorkers().add(newProjectWorker);
            user.getProjectWorkers().add(newProjectWorker);
//            session.saveOrUpdate(user);
            session.saveOrUpdate(project);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateUserInProject(ProjectWorker pw) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.update(pw);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean deleteUserFromProject(ProjectWorker pw) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.delete(pw);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean updateProject(Project updatedProject) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            session.update(updatedProject);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        
        session.close();
        return false;
    }
    
    public boolean deleteProject(Project targetProject) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        boolean isSuccessful = false;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            session.delete(targetProject);
            
            tx.commit();
            isSuccessful = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
            isSuccessful = false;
        } finally {
            session.close();
        }
        return isSuccessful;
    }
}
