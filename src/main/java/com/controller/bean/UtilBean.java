/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller.bean;

import com.model.pojo.Project;
import com.model.pojo.Task;
import com.model.pojo.User;
import com.util.Generated;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author pc
 */
@Named(value = "utilBean")
@RequestScoped
public class UtilBean implements Serializable {
    @Generated
    public UtilBean() {
    }
    
    public long getRemainingTime(Date date) {
        Date currentDate = new Date();
        long diffInMil = date.getTime() - currentDate.getTime();
        return TimeUnit.DAYS.convert(diffInMil, TimeUnit.MILLISECONDS);
    }
    
    public String getFullName(User user) {
        if (user == null || user.getFirstName() == null) {
            return "";
        }
        
        if (user.getLastName() != null) {
            return user.getFirstName() + " " + user.getLastName();
        }
        
        return user.getFirstName();
    }
    
    public List<Task> sortTaskPriority(List<Task> tasks) {
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task2.getPriority() - task1.getPriority();
            }
        });
        
        return tasks;
    }
    
    public List<Project> sortProjectPriority(List<Project> projects) {
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project project1, Project project2) {
                return project2.getPriority() - project1.getPriority();
            }
        });
        
        return projects;
    }
}
