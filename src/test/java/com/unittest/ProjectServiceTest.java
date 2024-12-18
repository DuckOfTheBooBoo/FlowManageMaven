package com.unittest;

import com.dao.ProjectDAO;
import com.dao.StatusDAO;
import com.model.pojo.*;
import com.service.ProjectService;
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

public class ProjectServiceTest {


    @Mock
    private ProjectDAO mockProjectDAO;

    @Mock
    private StatusDAO mockStatusDAO;

    private ProjectService projectService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectService = new ProjectService();
        projectService.setProjectDAO(mockProjectDAO);
        projectService.setStatusDAO(mockStatusDAO);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreateProject() {
        Status onGoing = mock(Status.class);
        when(onGoing.getStatus()).thenReturn("on-going");
        when(onGoing.getId()).thenReturn(1);

        when(mockStatusDAO.getStatusById(1)).thenReturn(onGoing);

        User mockUser = mock(User.class);

        Project project = new Project(onGoing, "test project", "test project description", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), 1);

        when(mockProjectDAO.addProject(any(Project.class), any(User.class))).thenReturn(project);

        Project newProject = projectService.addProject(project.getTitle(), project.getOverview(), project.getPriority(), project.getDeadline(), mockUser);

        verify(mockProjectDAO).addProject(any(Project.class), eq(mockUser));

        assertNotNull(newProject);
    }

    @Test
    public void testCreateProjectDBFailed() {
        Status onGoing = mock(Status.class);
        when(onGoing.getStatus()).thenReturn("on-going");
        when(onGoing.getId()).thenReturn(1);

        when(mockStatusDAO.getStatusById(1)).thenReturn(onGoing);

        User mockUser = mock(User.class);

        Project project = new Project(onGoing, "test project", "test project description", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), 1);

        when(mockProjectDAO.addProject(any(Project.class), any(User.class))).thenReturn(null);

        Project newProject = projectService.addProject(project.getTitle(), project.getOverview(), project.getPriority(), project.getDeadline(), mockUser);

        verify(mockProjectDAO).addProject(any(Project.class), eq(mockUser));

        assertNull(newProject);
    }

    @Test
    public void testGetProjectsOfUser() {
        User mockUser = mock(User.class);
        List<Project> mockProjects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Project mockProject = mock(Project.class);
            when(mockProject.getId()).thenReturn(i);
            when(mockProject.getTitle()).thenReturn("test project " + i);
            mockProjects.add(mockProject);
        }

        when(mockProjectDAO.getAllProjects(eq(mockUser))).thenReturn(new HashSet<>(mockProjects));

        Set<Project> projects = projectService.getProjects(mockUser);

        assertNotNull(projects);
        assertEquals(5, projects.size());
    }

    @Test
    public void testGetProjectsOfUserNone() {
        User mockUser = mock(User.class);
        List<Project> mockProjects = new ArrayList<>();

        when(mockProjectDAO.getAllProjects(eq(mockUser))).thenReturn(new HashSet<>(mockProjects));

        Set<Project> projects = projectService.getProjects(mockUser);

        assertNotNull(projects);
        assertEquals(0, projects.size());
    }

    @Test
    public void testDeleteProjectSuccesfulDeletion() {
        // Arrange
        User user = mock(User.class);
        Integer projectId = 1;
        Project mockProject = mock(Project.class);
        when(mockProject.getId()).thenReturn(projectId);
        when(projectService.getProjectById(user, projectId)).thenReturn(mockProject);
        when(mockProjectDAO.deleteProject(mockProject)).thenReturn(true);

        // Act
        boolean result = projectService.deleteProject(user, projectId);

        // Assert
        assertTrue(result);
        verify(mockProjectDAO).deleteProject(any(Project.class));
    }

    @Test
    public void testDeleteProjectProjectNotFound() {
        // Arrange
        User user = mock(User.class);
        Integer projectId = 1;
        when(projectService.getProjectById(user, projectId)).thenReturn(null);

        // Act
        boolean result = projectService.deleteProject(user, projectId);

        // Assert
        assertFalse(result);
        verify(mockProjectDAO, never()).deleteProject(any(Project.class));
    }

    @Test
    public void testDeleteProjectDeletionFails() {
        // Arrange
        User user = mock(User.class);
        Integer projectId = 1;
        Project project = mock(Project.class);
        when(projectService.getProjectById(user, projectId)).thenReturn(project);
        when(mockProjectDAO.deleteProject(project)).thenReturn(false);

        // Act
        boolean result = projectService.deleteProject(user, projectId);

        // Assert
        assertFalse(result);
        verify(mockProjectDAO).deleteProject(project);
    }
}
