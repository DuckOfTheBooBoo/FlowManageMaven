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
    private ProjectService ps;
    private TaskService ts;
    private Integer priority;
    
    @Inject
    private AuthBean authbean;
    private List<Object> priorityList;
    
    /**
     * Creates a new instance of TaskBean
     */
    public TaskBean() {
    }
    
    @PostConstruct
    public void init() {
        this.priorityList = new ArrayList<>();
        priorityList.add(new Priority("Low", 1));
        priorityList.add(new Priority("Medium", 2));
        priorityList.add(new Priority("High", 3));
        ps = new ProjectService();
        ts = new TaskService();
        
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

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    
    
    public Project getProject() {
        return project;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public ProjectService getPs() {
        return ps;
    }

    public void setPs(ProjectService ps) {
        this.ps = ps;
    }

    public TaskService getTs() {
        return ts;
    }

    public void setTs(TaskService ts) {
        this.ts = ts;
    }

    public AuthBean getAuthbean() {
        return authbean;
    }

    public void setAuthbean(AuthBean authbean) {
        this.authbean = authbean;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public List<Object> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<Object> priorityList) {
        this.priorityList = priorityList;
    }
    
    public String addNewTask() {
        if(taskId != null) {
            return saveTask();
        }
        
        FacesContext ctx = FacesContext.getCurrentInstance();
        
        Date currentDate = new Date();
        if(!deadline.after(currentDate)) {
            ctx.addMessage("projectDeadline", new FacesMessage("Deadline must be in the future"));
            return null;
        }
        
        User assignee = getProjectUsers().stream().filter(u -> Objects.equals(u.getId(), assigneeId)).findFirst().orElse(null);
        
        if (assignee != null && assignee.getId() != null) {
            boolean isSuccessful = ts.addNewTask(assignee, project, taskTitle, taskDescription, deadline, priority);
            if (isSuccessful) {
                return "project?faces-redirect=true&amp;project_id="+projectId;
            }
        }
        
        ctx.addMessage("task-form", new FacesMessage("Failed to add new task"));
        return null;
    }
    
    public String saveTask() {
        FacesContext ctx = FacesContext.getCurrentInstance();
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
        
        ctx.addMessage("task-form", new FacesMessage("Failed to add new task"));
        return null;
    }
    
    public List<User> getProjectUsers() {
        Set<ProjectWorker> pw = this.project.getProjectWorkers();
        List<User> workers = new ArrayList<>();
        pw.stream().forEach((worker) -> {
            workers.add(worker.getUser());
        });
        
        return workers;
    }

    public Integer getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }
    
    
}
