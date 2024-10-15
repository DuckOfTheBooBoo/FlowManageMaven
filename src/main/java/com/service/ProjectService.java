/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service;

import com.dao.ProjectDAO;
import com.dao.StatusDAO;
import com.model.pojo.Project;
import com.model.pojo.ProjectWorker;
import com.model.pojo.User;
import com.model.pojo.Status;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pc
 */
public class ProjectService {
    private ProjectDAO projectDAO = new ProjectDAO();
    private StatusDAO statusDAO = new StatusDAO();
    
    public Project addProject(String title, String description, int priority, Date deadline, User managerUser) {
        Status onGoing = statusDAO.getStatusById(1);
        Project newProject = new Project(onGoing, title, description, deadline, priority);
        Project project = projectDAO.addProject(newProject, managerUser);
        return project;
    }
    
    public boolean addUserToProject(Project project, User user, String role) {
        return projectDAO.addUserToProject(project, user, role);
    }
    
    public boolean updateProjectWorker(ProjectWorker pw) {
        return projectDAO.updateUserInProject(pw);
    }

    public boolean updateProject(Project project) {
        return projectDAO.updateProject(project);
    }
    
    public Set<Project> getProjects(User user) {
        Set<Project> projects = new HashSet<>();
        projects = projectDAO.getAllProjects(user);
        
        return projects;
    }
    
    public Project getProjectById(User user, int projectId) {
        return projectDAO.getProjectByID(projectId, user);
    }
    
    public boolean deleteUserFromProject(ProjectWorker pw) {
        return projectDAO.deleteUserFromProject(pw);
    }
    
    public boolean deleteProject(User user, Integer projectId) {
        Project targetProject = getProjectById(user, projectId);
        return projectDAO.deleteProject(targetProject);
    }
}
