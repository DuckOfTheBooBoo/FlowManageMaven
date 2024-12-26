/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller.bean;

import com.util.Generated;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;

/**
 *
 * @author pc
 */
@ManagedBean(name = "navigationBean", eager = true)
@RequestScoped
public class NavigationBean implements Serializable {

    /**
     * Creates a new instance of NavigationBean
     */
    public NavigationBean() {
    }
    
    @ManagedProperty(value = "#{param.page}")
    private String page;
    
    @ManagedProperty(value = "#{param.project_id}")
    private Integer projectId;
    
    @ManagedProperty(value = "#{param.task_id}")
    private Integer taskId;
    
    @ManagedProperty(value = "#{param.state}")
    private String state;

    @Generated
    public String getState() {
        return state;
    }

    @Generated
    public void setState(String state) {
        this.state = state;
    }

    @Generated
    public String getPage() {
        return this.page;
    }

    @Generated
    public void setPage(String page) {
        this.page = page;
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
    public Integer getTaskId() {
        return taskId;
    }

    @Generated
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
    
    public String showPage() {
        if(page == null) {
            return "dashboard?faces-redirect=true";
        }
        
        if("project".equals(page)) {
            return "project?faces-redirect=true&amp;includeViewParams=true";
        }
        
        if("projects".equals(page)) {
            return "projects?faces-redirect=true";
        }
        
        if ("dashboard".equals(page)) {
            return "dashboard?faces-redirect=true";
        }
        
        if("project-form".equals(page)) {
            return "project-form?faces-redirect=true";
        }
        
        if("contact-form".equals(page)) {
            return "contact-form?faces-redirect=true&amp;includeViewParams=true";
        }
        
        if("task-form".equals(page)) {
            return "task-form?faces-redirect=true&amp;includeViewParams=true";
        }
        
        if("login".equals(page)) {
            return "login?faces-redirect=true";
        }
        
        return null;
    }
}
