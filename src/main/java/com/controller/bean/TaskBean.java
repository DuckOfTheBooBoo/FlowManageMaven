/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller.bean;

import com.model.pojo.Priority;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import com.model.pojo.Project;
import com.model.pojo.ProjectWorker;
import com.model.pojo.Task;
import com.model.pojo.User;
import com.service.ProjectService;
import com.service.TaskService;
import com.util.Generated;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author pc
 */
@Named(value = "taskBean")
@ViewScoped
public class TaskBean implements java.io.Serializable {

    private Project project;
    private Integer projectId;
    private Integer taskId;
    private Task task;
    private String taskTitle;
    private String taskDescription;
    private Date deadline;
    private Integer assigneeId;
    private Integer priority;
    private List<Object> priorityList;

    @Inject
    private transient ProjectService ps;

    @Inject
    private transient TaskService ts;

    @Inject
    private transient AuthBean authbean;

    private FacesContext facesContext; // only used in testing

    public TaskBean(AuthBean authbean, TaskService ts, ProjectService ps, FacesContext fc) {
        this.authbean = authbean;
        this.ts = ts;
        this.ps = ps;
        this.facesContext = fc;
    }

    /**
     * Creates a new instance of TaskBean
     */
    @Generated
    public TaskBean() {
    }
    
    @PostConstruct
    public void init() {
        this.priorityList = new ArrayList<>();
        priorityList.add(new Priority("Low", 1));
        priorityList.add(new Priority("Medium", 2));
        priorityList.add(new Priority("High", 3));

        if (projectId != null) {
            this.project = ps.getProjectById(authbean.getLoggedInUser(), projectId);
        }
        
        if (taskId != null) {
            this.task = ts.getTaskById(taskId);
            taskTitle = this.task.getTitle();
            taskDescription = this.task.getDescription();
            deadline = this.task.getDeadline();
            priority = this.task.getPriority();
            this.assigneeId = this.task.getProjectWorker().getUser().getId();
        }
    }

    @Generated
    private FacesContext facesContextWrapper() {
        if (facesContext != null) {
            return this.facesContext;
        }
        return FacesContext.getCurrentInstance();
    }

    @Generated
    public Integer getTaskId() {
        return taskId;
    }

    @Generated
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    @Generated
    public Task getTask() {
        return task;
    }

    @Generated
    public void setTask(Task task) {
        this.task = task;
    }



    @Generated
    public Project getProject() {
        return project;
    }

    @Generated
    public Integer getPriority() {
        return priority;
    }

    @Generated
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Generated
    public void setProject(Project project) {
        this.project = project;
    }

    @Generated
    public String getTaskDescription() {
        return taskDescription;
    }

    @Generated
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Generated
    public String getTaskTitle() {
        return taskTitle;
    }

    @Generated
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    @Generated
    public Date getDeadline() {
        return deadline;
    }

    @Generated
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
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
    public TaskService getTs() {
        return ts;
    }

    @Generated
    public void setTs(TaskService ts) {
        this.ts = ts;
    }

    @Generated
    public AuthBean getAuthbean() {
        return authbean;
    }

    @Generated
    public void setAuthbean(AuthBean authbean) {
        this.authbean = authbean;
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
    public List<Object> getPriorityList() {
        return priorityList;
    }

    @Generated
    public void setPriorityList(List<Object> priorityList) {
        this.priorityList = priorityList;
    }

    public String addNewTask() {
        if(taskId != null) {
            return saveTask();
        }
        
        Date currentDate = new Date();
        if(!deadline.after(currentDate)) {
            facesContextWrapper().addMessage("projectDeadline", new FacesMessage("Deadline must be in the future"));
            return null;
        }
        
        User assignee = getProjectUsers().stream().filter(u -> Objects.equals(u.getId(), assigneeId)).findFirst().orElse(null);
        
        if (assignee != null && assignee.getId() != null) {
            boolean isSuccessful = ts.addNewTask(assignee, project, taskTitle, taskDescription, deadline, priority);
            if (isSuccessful) {
                return "project?faces-redirect=true&amp;project_id="+projectId;
            }
        }
        
        facesContextWrapper().addMessage("task-form", new FacesMessage("Failed to add new task"));
        return null;
    }
    
    public String saveTask() {
        User assignee = null;
        for (User user : getProjectUsers()) {            
            if (user.getId() == assigneeId) {
                assignee = user;
                break;
            }
        }
        if (assignee != null || assignee.getId() != null) {
            boolean isSuccessful = ts.saveTask(taskId, assignee, project, taskTitle, taskDescription, deadline, priority);
            if (isSuccessful) {
                return "project?faces-redirect=true&amp;project_id="+projectId;
            }
        }
        
        facesContextWrapper().addMessage("task-form", new FacesMessage("Failed to add new task"));
        return null;
    }
    
    public List<User> getProjectUsers() {
        List<User> workers = new ArrayList<>();
        if (project != null) {
            Set<ProjectWorker> pw = this.project.getProjectWorkers();
        
            pw.stream().forEach((worker) -> {
                workers.add(worker.getUser());
            });
        }
        
        return workers;
    }

    @Generated
    public Integer getAssigneeId() {
        return assigneeId;
    }

    @Generated
    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }
    
    
}
