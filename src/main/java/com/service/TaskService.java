/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service;

import com.dao.StatusDAO;
import com.dao.TaskDAO;
import com.model.pojo.Project;
import com.model.pojo.ProjectWorker;
import com.model.pojo.Status;
import com.model.pojo.Task;
import com.model.pojo.User;
import com.util.Generated;

import java.util.Date;

/**
 *
 * @author pc
 */
public class TaskService {
    private TaskDAO taskDAO = new TaskDAO();
    private StatusDAO statusDAO = new StatusDAO();
    
    public boolean addNewTask(User user, Project project, String title, String description, Date deadline, int priority) {
        Status onGoingStat = getStatusDAO().getStatusById(1);
        ProjectWorker pw = user.getProjectWorkers().stream().filter(pws -> pws.getProject().getId() == project.getId() && pws.getUser().getId() == user.getId()).findFirst().orElse(null);
        Task newTask = new Task(pw, onGoingStat, title, description, priority, deadline);
        return getTaskDAO().addTask(newTask);
    }
    
    public boolean saveTask(Integer taskId, User user, Project project, String title, String description, Date deadline, int priority) {
        Status onGoingStat = getStatusDAO().getStatusById(1);
        Task targetTask = getTaskDAO().getTaskById(taskId);
        ProjectWorker tpw = user.getProjectWorkers().stream().filter(pw -> pw.getProject().getId() == project.getId()).findFirst().orElse(null);
        targetTask.setProjectWorker(tpw);
        targetTask.setDeadline(deadline);
        targetTask.setTitle(title);
        targetTask.setPriority(priority);
        targetTask.setDescription(description);
        targetTask.setStatus(onGoingStat);
        
        return getTaskDAO().updateTask(targetTask);
    }

    @Generated
    public Task getTaskById(Integer taskId) {
        return getTaskDAO().getTaskById(taskId);
    }

    @Generated
    public boolean updateTask(Task updatedTask) {
        return getTaskDAO().updateTask(updatedTask);
    }
    
    public boolean completeTask(Task updatedTask) {
        Status doneStat = getStatusDAO().getStatusById(2);
        updatedTask.setStatus(doneStat);
        return getTaskDAO().updateTask(updatedTask);
    }

    @Generated
    public boolean deleteTask(Task targetTask) {
        return getTaskDAO().deleteTask(targetTask);
    }

    @Generated
    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    @Generated
    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
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
