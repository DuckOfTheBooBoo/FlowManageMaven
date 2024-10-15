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
import java.util.Date;

/**
 *
 * @author pc
 */
public class TaskService {
    private TaskDAO taskDAO = new TaskDAO();
    private StatusDAO statusDAO = new StatusDAO();
    
    public boolean addNewTask(User user, Project project, String title, String description, Date deadline, int priority) {
        Status onGoingStat = statusDAO.getStatusById(1);
        ProjectWorker pw = user.getProjectWorkers().stream().filter(pws -> pws.getProject().getId() == project.getId() && pws.getUser().getId() == user.getId()).findFirst().orElse(null);
        Task newTask = new Task(pw, onGoingStat, title, description, priority, deadline);
        return taskDAO.addTask(newTask);
    }
    
    public boolean saveTask(Integer taskId, User user, Project project, String title, String description, Date deadline, int priority) {
        Status onGoingStat = statusDAO.getStatusById(1);
        Task targetTask = taskDAO.getTaskById(taskId);
        ProjectWorker tpw = user.getProjectWorkers().stream().filter(pw -> pw.getProject().getId() == project.getId()).findFirst().orElse(null);
        targetTask.setProjectWorker(tpw);
        targetTask.setDeadline(deadline);
        targetTask.setTitle(title);
        targetTask.setPriority(priority);
        targetTask.setDescription(description);
        targetTask.setStatus(onGoingStat);
        
        return taskDAO.updateTask(targetTask);
    }
    
    public Task getTaskById(Integer taskId) {
        return taskDAO.getTaskById(taskId);
    }
    
    public boolean updateTask(Task updatedTask) {
        return taskDAO.updateTask(updatedTask);
    }
    
    public boolean completeTask(Task updatedTask) {
        Status doneStat = statusDAO.getStatusById(2);
        updatedTask.setStatus(doneStat);
        return taskDAO.updateTask(updatedTask);
    }
    
    public boolean deleteTask(Task targetTask) {
        return taskDAO.deleteTask(targetTask);
    }
}
