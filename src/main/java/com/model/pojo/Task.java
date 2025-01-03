package com.model.pojo;
// Generated Sep 30, 2024 4:01:08 PM by Hibernate Tools 4.3.1


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Task generated by hbm2java
 */
@Entity
@Table(name="task"
    ,catalog="flow_manage"
)
public class Task  implements java.io.Serializable {


     private int id;
     private ProjectWorker projectWorker;
     private Status status;
     private String title;
     private String description;
     private int priority;
     private Date deadline;

    public Task() {
    }

    public Task(ProjectWorker projectWorker, Status status, String title, String description, int priority, Date deadline) {
       this.projectWorker = projectWorker;
       this.status = status;
       this.title = title;
       this.description = description;
       this.priority = priority;
       this.deadline = deadline;
    }
    
    public Task(int id, ProjectWorker projectWorker, Status status, String title, String description, int priority, Date deadline) {
       this.id = id;
       this.projectWorker = projectWorker;
       this.status = status;
       this.title = title;
       this.description = description;
       this.priority = priority;
       this.deadline = deadline;
    }
   
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.EAGER, targetEntity = ProjectWorker.class)
    @JoinColumns( { 
        @JoinColumn(name="user_id", referencedColumnName="user_id", nullable=false, insertable=true, updatable=true), 
        @JoinColumn(name="project_id", referencedColumnName="project_id", nullable=false, insertable=true, updatable=true) } )
    public ProjectWorker getProjectWorker() {
        return this.projectWorker;
    }
    
    public void setProjectWorker(ProjectWorker projectWorker) {
        this.projectWorker = projectWorker;
    }

@ManyToOne(fetch=FetchType.LAZY, targetEntity = Status.class)
    @JoinColumn(name="status_id", nullable=false)
    public Status getStatus() {
        return this.status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    
    @Column(name="title", nullable=false)
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    
    @Column(name="priority", nullable=false)
    public int getPriority() {
        return this.priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="deadline", nullable=false, length=10)
    public Date getDeadline() {
        return this.deadline;
    }
    
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    
    @Column(name="description", nullable=true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


