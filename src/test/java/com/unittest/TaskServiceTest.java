package com.unittest;

import com.dao.StatusDAO;
import com.dao.TaskDAO;
import com.model.pojo.*;
import com.service.TaskService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.*;

public class TaskServiceTest {


    @Mock
    private TaskDAO taskDAO;
    @Mock
    private StatusDAO statusDAO;

    private TaskService taskService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        taskService = new TaskService();
        taskService.setTaskDAO(taskDAO);
        taskService.setStatusDAO(statusDAO);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddNewTaskSuccess() {
        User user = mock(User.class);
        ProjectWorker projectWorker = mock(ProjectWorker.class);
        Project project = mock(Project.class);
        // Arrange
        Set<ProjectWorker> projectWorkers = new HashSet<>();
        projectWorkers.add(projectWorker);

        when(user.getProjectWorkers()).thenReturn(projectWorkers);
        when(projectWorker.getProject()).thenReturn(project);
        when(projectWorker.getUser()).thenReturn(user);
        when(taskDAO.addTask(any(Task.class))).thenReturn(true);

        Date deadline = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Act
        boolean result = taskService.addNewTask(user, project, "Test Task", "Test Description", deadline, 1);

        // Assert
        assertTrue(result);
        verify(taskDAO).addTask(any(Task.class));
        verify(statusDAO).getStatusById(1);
    }

    @Test
    public void testAddNewTaskNoMatchingProjectWorker() {
        User user = mock(User.class);
        ProjectWorker projectWorker = mock(ProjectWorker.class);
        Project project = mock(Project.class);
        // Arrange
        Set<ProjectWorker> differentProjectWorkers = new HashSet<>();
        ProjectWorker differentWorker = mock(ProjectWorker.class);
        when(differentWorker.getProject()).thenReturn(mock(Project.class));
        when(differentWorker.getUser()).thenReturn(mock(User.class));
        differentProjectWorkers.add(differentWorker);

        when(user.getProjectWorkers()).thenReturn(differentProjectWorkers);

        Date deadline = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());


        // Act
        boolean result = taskService.addNewTask(user, project, "Test Task", "Test Description", deadline, 1);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddNewTaskDAOFails() {
        User user = mock(User.class);
        ProjectWorker projectWorker = mock(ProjectWorker.class);
        Project project = mock(Project.class);
        // Arrange
        Set<ProjectWorker> projectWorkers = new HashSet<>();
        projectWorkers.add(projectWorker);

        when(user.getProjectWorkers()).thenReturn(projectWorkers);
        when(projectWorker.getProject()).thenReturn(project);
        when(projectWorker.getUser()).thenReturn(user);
        when(taskDAO.addTask(any(Task.class))).thenReturn(false);

        Date deadline = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());


        // Act
        boolean result = taskService.addNewTask(user, project, "Test Task", "Test Description", deadline, 1);

        // Assert
        assertFalse(result);
        verify(taskDAO).addTask(any(Task.class));
    }

    @Test
    public void testSaveTaskSuccess() {
        User user = mock(User.class);
        ProjectWorker projectWorker = mock(ProjectWorker.class);
        Project project = mock(Project.class);
        when(projectWorker.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(1);
        when(statusDAO.getStatusById(anyInt())).thenReturn(mock(Status.class));
        // Arrange
        Set<ProjectWorker> projectWorkers = new HashSet<>();
        projectWorkers.add(projectWorker);
        when(project.getProjectWorkers()).thenReturn(projectWorkers);

        Task task = mock(Task.class);
        when(taskDAO.getTaskById(anyInt())).thenReturn(task);
        when(user.getProjectWorkers()).thenReturn(projectWorkers);
        when(taskDAO.updateTask(any(Task.class))).thenReturn(true);

        boolean isSuccessful = taskService.saveTask(1, user, project, "Test Task", "Test Description", Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()), 1);

        assertTrue(isSuccessful);
    }

    @Test
    public void testSaveTaskNoProjectWorker() {
        User user = mock(User.class);
        ProjectWorker projectWorker = mock(ProjectWorker.class);
        Project project = mock(Project.class);
        Project project2 = mock(Project.class);
        when(project.getId()).thenReturn(1);
        when(project2.getId()).thenReturn(2);
        when(projectWorker.getProject()).thenReturn(project2);
        when(statusDAO.getStatusById(anyInt())).thenReturn(mock(Status.class));
        // Arrange
        Set<ProjectWorker> projectWorkers = new HashSet<>();
        projectWorkers.add(projectWorker);
        when(project.getProjectWorkers()).thenReturn(projectWorkers);

        Task task = mock(Task.class);
        when(taskDAO.getTaskById(anyInt())).thenReturn(task);
        when(user.getProjectWorkers()).thenReturn(projectWorkers);
        when(taskDAO.updateTask(any(Task.class))).thenReturn(false);

        boolean isSuccessful = taskService.saveTask(1, user, project, "Test Task", "Test Description", Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()), 1);

        assertFalse(isSuccessful);
    }

    @Test
    public void testCompleteTask() {
        when(statusDAO.getStatusById(eq(2))).thenReturn(mock(Status.class));

        Task task = mock(Task.class);
        when(taskDAO.updateTask(any(Task.class))).thenReturn(true);

        boolean isSuccessful = taskService.completeTask(task);

        assertTrue(isSuccessful);
    }
}
