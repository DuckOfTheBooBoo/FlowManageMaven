package com.controller.bean;

import com.model.pojo.Project;
import com.model.pojo.ProjectWorker;
import com.model.pojo.Status;
import com.model.pojo.Task;
import com.model.pojo.User;
import com.service.ProjectService;
import com.service.TaskService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author pc
 */
@Named(value = "viewProjectBean")
@ViewScoped
public class ViewProjectBean implements java.io.Serializable {
    private Integer projectId;
    private Project project;
    private List<Task> taskList;
    private FacesContext facesContext;

    @Inject
    private ProjectService projectService;

    @Inject
    private TaskService taskService;


    @Inject
    private AuthBean authBean;

    // Test only
    public ViewProjectBean(AuthBean authBean, TaskService taskService, ProjectService projectService, FacesContext fc) {
        this.authBean = authBean;
        this.taskService = taskService;
        this.projectService = projectService;
        this.facesContext = fc;
    }

    /**
     * Creates a new instance of ViewProjectBean
     */
    public ViewProjectBean() {
    }
    
    @PostConstruct
    public void init() {
        Map<String, String> params = facesContextWrapper().getExternalContext().getRequestParameterMap();
        String projectIdParam = params.get("project_id");
        
        if (projectIdParam != null) {
            this.projectId = Integer.parseInt(projectIdParam);
            loadProject();
        }
    }
    
    public void loadProject() {
        if (authBean.getLoggedInUser() == null) {
            facesContextWrapper().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "You're not logged in", null));
        }
        
        if(projectId != null) {
            this.project = projectService.getProjectById(authBean.getLoggedInUser(), projectId);
            if (this.project == null) {
                facesContextWrapper().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Project not found", null));
            }
            
            this.taskList = getTaskList();
        }
    }

    public String getManager() {
        if (project == null) {
            facesContextWrapper().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Project is null", null));
            return null;
        }
        
        Set<ProjectWorker> pw = this.project.getProjectWorkers();
        for (ProjectWorker worker : pw) {
            if (worker.getRole().equals("manager")) {
                return worker.getUser().getFirstName() + " " + worker.getUser().getLastName();
            }
        }
        
        return null; // Project doesn't have a manager?
    }

    public String loggedInRole() {
        if (authBean.getLoggedInUser() == null) {
            return "";
        }
        
        if (project == null) {
            return "";
        }
        
        Set<ProjectWorker> pws = this.project.getProjectWorkers();
        ProjectWorker pw = pws.stream().filter(p -> Objects.equals(p.getUser().getId(), authBean.getLoggedInUser().getId())).findFirst().orElse(null);
        
        if (pw == null) {
            return "";
        }
        
        return pw.getRole();
    }

    public List<Task> getTaskList() {
        List<Task> tasks = null;
        if (this.project == null) {
            tasks = new ArrayList<>();
            return tasks;
        }
        
        tasks = new ArrayList<>();
        for(ProjectWorker pw : this.project.getProjectWorkers()) {
            Set<Task> pwTasks = pw.getTasks();
            for(Task task : pwTasks) {
                tasks.add(task);
            }
        }
        
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task2.getPriority() - task1.getPriority();
            }
        });
        
        return tasks;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
    
    
    public List<Task> getUserOnGoingTaskList() {
        return (List<Task>) getTaskList().stream().filter(t -> t.getProjectWorker().getUser().getId() == authBean.getLoggedInUser().getId() && t.getStatus().getId() == 1).collect(Collectors.toList());
    }
    
    public AuthBean getAuthBean() {
        return authBean;
    }

    public void setAuthBean(AuthBean authBean) {
        this.authBean = authBean;
    }
    
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    public Integer countOnGoingTask() {
        List<Task> ts = getTaskList().stream().filter(t -> t.getStatus().getId() == 1).collect(Collectors.toList());
        if (ts == null) {
            return 0;
        }
        
        return ts.size();
    }
    
    public Integer countFinishedTask() {
        List<Task> ts = getTaskList().stream().filter(t -> t.getStatus().getId() == 2).collect(Collectors.toList());
        if (ts == null) {
            return 0;
        }
        
        return ts.size();
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
    
    public void refreshTask() {
        this.taskList = getTaskList();
    }
    
    public void updateTask(Task task) {
        
    }
    
    public void completeTask(Integer taskId) {
        Task targetTask = this.taskList.stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
        if (targetTask != null) {
            boolean isSuccessful;
            isSuccessful = taskService.completeTask(targetTask);
            System.err.println(isSuccessful);
            refreshTask();
        }
    }
    
    public void deleteTask(Integer taskId) {
        Task targetTask = this.taskList.stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
        if(targetTask != null) {
            boolean isSuccessful;
            isSuccessful = taskService.deleteTask(targetTask);
            if(isSuccessful) {
                refreshTask();
            }
        }
    }
    
    public void deleteMember(Integer userId) {
        ProjectWorker pwu = this.project.getProjectWorkers().stream().filter(pw -> pw.getUser().getId() == userId).findFirst().orElse(null);
        if (pwu != null) {
            boolean isSuccessful = projectService.deleteUserFromProject(pwu);           
        }        
    }

    public FacesContext facesContextWrapper() {
        if (facesContext != null) {
            return this.facesContext;
        }

        return FacesContext.getCurrentInstance();
    }

    public FacesContext getFacesContext() {
        return facesContext;
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }
}
