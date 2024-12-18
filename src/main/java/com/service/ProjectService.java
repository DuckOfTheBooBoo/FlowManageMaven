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
import com.util.Generated;

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
        Status onGoing = getStatusDAO().getStatusById(1);
        Project newProject = new Project(onGoing, title, description, deadline, priority);
        Project project = getProjectDAO().addProject(newProject, managerUser);
        return project;
    }

    @Generated
    public boolean addUserToProject(Project project, User user, String role) {
        return getProjectDAO().addUserToProject(project, user, role);
    }

    @Generated
    public boolean updateProjectWorker(ProjectWorker pw) {
        return getProjectDAO().updateUserInProject(pw);
    }

    @Generated
    public boolean updateProject(Project project) {
        return getProjectDAO().updateProject(project);
    }
    
    public Set<Project> getProjects(User user) {
        Set<Project> projects = new HashSet<>();
        projects = getProjectDAO().getAllProjects(user);
        
        return projects;
    }

    @Generated
    public Project getProjectById(User user, int projectId) {
        return getProjectDAO().getProjectByID(projectId, user);
    }

    @Generated
    public boolean deleteUserFromProject(ProjectWorker pw) {
        return getProjectDAO().deleteUserFromProject(pw);
    }
    
    public boolean deleteProject(User user, Integer projectId) {
        Project targetProject = getProjectById(user, projectId);
        return getProjectDAO().deleteProject(targetProject);
    }

    @Generated
    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    @Generated
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    @Generated
    public StatusDAO getStatusDAO() {
        return statusDAO;
    }

    @Generated
    public void setStatusDAO(StatusDAO statusDAO) {
        this.statusDAO = statusDAO;
    }
}
