/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller.bean;

import com.model.pojo.Project;
import com.model.pojo.ProjectWorker;
import com.model.pojo.User;
import com.service.ProjectService;
import com.service.UserService;
import com.util.Generated;

import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Named;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author pc
 */
@Named(value = "contactBean")
@ViewScoped
public class ContactBean implements java.io.Serializable {

    private User targetUser;
    private String role;
    private Project project;
    private Integer projectId;
    private Integer userId;
    private ProjectWorker pw;
    private String userEmail;

    private FacesContext facesContext;

    @Inject
    private transient UserService us;

    @Inject
    private transient ProjectService ps;

    @Inject
    private transient AuthBean authBean;
    
    /**
     * Creates a new instance of ContactBean
     */
    @Generated
    public ContactBean() {
    }

    // Test constructor
    public ContactBean(FacesContext facesContext, UserService us, ProjectService ps, AuthBean authBean) {
        this.facesContext = facesContext;
        this.us = us;
        this.ps = ps;
        this.authBean = authBean;
    }

    @PostConstruct
    public void init() {
        if (projectId != null) {
            this.project = ps.getProjectById(authBean.getLoggedInUser(), projectId);
            
            if (userId != null) {
                pw = this.project.getProjectWorkers().stream().filter(p -> Objects.equals(p.getUser().getId(), userId)).findFirst().orElse(null);
                if (pw != null) {
                    role = pw.getRole();
                    userEmail = pw.getUser().getEmail();
                }
            }
        }
    }

    @Generated
    public Integer getUserId() {
        return userId;
    }

    @Generated
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Generated
    public ProjectWorker getPw() {
        return pw;
    }

    @Generated
    public void setPw(ProjectWorker pw) {
        this.pw = pw;
    }

    @Generated
    public User getTargetUser() {
        return targetUser;
    }

    @Generated
    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    @Generated
    public String getRole() {
        return role;
    }

    @Generated
    public void setRole(String role) {
        this.role = role;
    }

    @Generated
    public Integer getProjectId() {
        return projectId;
    }

    @Generated
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Generated
    public FacesContext facesContextWrapper() {
        if (this.facesContext != null) { // only happen in testing
            return this.facesContext;
        }

        return FacesContext.getCurrentInstance();
    }

    public String addUserToProject() {
        if(userEmail == null || userEmail.isEmpty()) {
            facesContextWrapper().addMessage("contact-form", new FacesMessage(FacesMessage.SEVERITY_ERROR, "E-mail field is empty.", null));
            return null;
        }

        User currentUser = authBean.getLoggedInUser();
        // Check if user has the authority to add user to a project
        ProjectWorker currentUserPw = this.project.getProjectWorkers().stream().filter(p -> Objects.equals(p.getUser().getId(), currentUser.getId())).findFirst().orElse(null);
        if (currentUserPw == null || !currentUserPw.getRole().equals("manager")) {
            facesContextWrapper().addMessage("contact-form", new FacesMessage(FacesMessage.SEVERITY_ERROR, "You don't have the authority to add user to a project.", null));
            return null;
        }
        
        if (userId != null) {
            return updateWorker();
        }
        
        User searchUser = us.getUserByEmail(userEmail);
        
        if (searchUser != null) {
            for (ProjectWorker pw : project.getProjectWorkers()) {
                if(pw.getUser().equals(searchUser)) {
                    facesContextWrapper().addMessage("contact-form", new FacesMessage(FacesMessage.SEVERITY_ERROR, "User already existed in the project.", null));
                    return null;
                }
            }
            
            this.targetUser = searchUser;
        } else {
            facesContextWrapper().addMessage("contact-form", new FacesMessage(FacesMessage.SEVERITY_ERROR, "User doesn't exists.", null));
            return null;
        }
        
        boolean isSuccessful = ps.addUserToProject(project, searchUser, role);
        if (isSuccessful) {
            return "project?faces-redirect=true&amp;project_id="+projectId;
        }
        facesContextWrapper().addMessage("contact-form", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to add user to the project.", null));
        return null;
    }
    
    private String updateWorker() {
        pw.setRole(role);
        boolean isSuccessful = ps.updateProjectWorker(pw);
        if (!isSuccessful) {
            facesContextWrapper().addMessage("contact-form", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to add user to the project.", null));
            return null;
        }
        
        return "project?faces-redirect=true&amp;project_id="+projectId;
    }

    @Generated
    public String getUserEmail() {
        return userEmail;
    }

    @Generated
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Generated
    public Project getProject() {
        return project;
    }

    @Generated
    public void setProject(Project project) {
        this.project = project;
    }

    @Generated
    public ProjectService getPs() {
        return ps;
    }

    @Generated
    public void setPs(ProjectService ps) {
        this.ps = ps;
    }

    @Generated
    public UserService getUs() {
        return us;
    }

    @Generated
    public void setUs(UserService us) {
        this.us = us;
    }

    @Generated
    public AuthBean getAuthBean() {
        return authBean;
    }

    @Generated
    public void setAuthBean(AuthBean authBean) {
        this.authBean = authBean;
    }

    @Generated
    public FacesContext getFacesContext() {
        return facesContext;
    }

    @Generated
    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }


}
