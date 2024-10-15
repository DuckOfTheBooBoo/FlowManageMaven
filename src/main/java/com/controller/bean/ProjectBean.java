/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller.bean;

import com.model.pojo.Priority;
import com.model.pojo.Project;
import com.model.pojo.ProjectWorker;
import com.model.pojo.Status;
import com.model.pojo.User;
import com.service.ProjectService;
import com.service.StatusService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author pc
 */

@Named(value = "projectBean")
@ViewScoped
public class ProjectBean implements java.io.Serializable {
    
    private Project project;
    private Integer projectId;
    private String title;
    private String description;
    private String priority;
    private Date deadline;
    private Integer statusId;
    private List<User> userList;
    private List<Priority> priorityList;
    private List<Status> statusList;
    private List<Project> projectList;
    private ProjectService ps = new ProjectService();
    private StatusService ss = new StatusService();
    
    @Inject
    private AuthBean authBean;  // Injected session-scoped bean
    /**
     * Creates a new instance of ProjectBean
     */
    public ProjectBean() {
    }
    
    @PostConstruct
    public void init() {
        this.priorityList = new ArrayList<>();
        priorityList.add(new Priority("Low", 1));
        priorityList.add(new Priority("Medium", 2));
        priorityList.add(new Priority("High", 3));
        
        if (this.projectId != null) {
            this.project = ps.getProjectById(authBean.getLoggedInUser(), projectId);
            if (this.project != null) {
                this.title = project.getTitle();
                this.description = project.getOverview();
                this.priority = String.valueOf(project.getPriority());
                this.deadline = project.getDeadline();
                this.statusId = project.getStatus().getId();
                statusList = ss.getAllStatus();
            }
        }
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public StatusService getSs() {
        return ss;
    }

    public void setSs(StatusService ss) {
        this.ss = ss;
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }
    
    

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public ProjectService getPs() {
        return ps;
    }

    public void setPs(ProjectService ps) {
        this.ps = ps;
    }
    
    public List<Project> getProjectList() {
        Set<Project> projects = ps.getProjects(getAuthBean().getLoggedInUser());        
        
        if (projects == null) {
            return new ArrayList<Project>();
        }
        
        return new ArrayList<Project>(projects);
    }
    
    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }
    
    public AuthBean getAuthBean() {
        return authBean;
    }

    public void setAuthBean(AuthBean authBean) {
        this.authBean = authBean;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
    
    public void setPriorityList(List<Priority> pl) {
        this.priorityList = pl;
    }
    
    public List<Priority> getPriorityList() {
        return this.priorityList;
    }
    
    public boolean isAuthorized(Project project) {
        // Get user role in project
        if(authBean.getLoggedInUser() == null) {
            return false;
        }
        
        ProjectWorker pw = project.getProjectWorkers().stream().filter(p -> p.getUser().getId() == authBean.getLoggedInUser().getId()).findFirst().orElse(null);
        if (pw == null) {
            return false;
        }
        
        return "manager".equals(pw.getRole());
    }
    
    public String updateProject(Integer projectId) {
        Project targetProject = ps.getProjectById(authBean.getLoggedInUser(), projectId);
        statusList = ss.getAllStatus();
        Status targetStatus = statusList.stream().filter(s -> Objects.equals(s.getId(), Integer.valueOf(statusId))).findFirst().orElse(null);
        if(targetStatus != null) {
            targetProject.setTitle(title);
            targetProject.setStatus(targetStatus);
            targetProject.setOverview(description);
            targetProject.setPriority(Integer.valueOf(priority));
            targetProject.setDeadline(deadline);

            boolean isSuccessful = ps.updateProject(targetProject);
            if(isSuccessful) {
                return "projects?face-redirect=true";
            }
        }
        
        return null;
    }
    
    public String saveProject(Integer paramProjectId) {
        if(paramProjectId != null) {
            return updateProject(paramProjectId);
        }
        
        User loggedUser = getAuthBean().getLoggedInUser();
        
        if (loggedUser == null) {
            return null;
        }
        
        int priorityInt = Integer.valueOf(priority);
        
        // Transaction are opened and closed in this, project is created in db
        Project newProject = ps.addProject(title, description, priorityInt, deadline, loggedUser);
        
        if (newProject == null) {
            System.err.println("Project is null");
            return null;
        }
        
        System.err.println(newProject);
        
        // And also this, but created project id is not found in this
        boolean isSuccessful = ps.addUserToProject(newProject, loggedUser, "manager");
        
        if (!isSuccessful) {
            System.err.println("Add user to project failed");
            return null;
        }
//        
        return "projects?face-redirect=true";
    }
    
    public void deleteProject(Integer projectId) {
        ps.deleteProject(authBean.getLoggedInUser(), projectId);

    }
}
