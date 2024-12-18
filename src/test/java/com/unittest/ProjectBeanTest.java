package com.unittest;

import com.controller.bean.AuthBean;
import com.controller.bean.ProjectBean;
import com.model.pojo.*;
import com.service.ProjectService;
import com.service.StatusService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.*;

public class ProjectBeanTest {
    @Mock
    private AuthBean mockAuthBean;

    @Mock
    private StatusService mockStatusService;

    @Mock
    private ProjectService mockProjectService;

    private ProjectBean projectBean;

    private User testUser;
    private User testUser2;
    private User testUser3;

    private Set<Project> projects;

    private Project testProject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        List<Status> statusList = new ArrayList<Status>();
        Status onGoing = new Status("on-going");
        onGoing.setId(1);
        statusList.add(onGoing);
        Status done = new Status("done");
        done.setId(2);
        statusList.add(done);

        when(mockStatusService.getAllStatus()).thenReturn(statusList);

        testUser = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        testUser.setId(1);

        testUser2 = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        testUser2.setId(2);

        testUser3 = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        testUser3.setId(3);

        projectBean = new ProjectBean(mockAuthBean, mockProjectService, mockStatusService);

        // Dummy project init
        projects = new HashSet<Project>();
        testProject = new Project(onGoing, "test project", "test project description", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), 1);
        testProject.setId(1);

        ProjectWorkerId pwId = new ProjectWorkerId(testUser.getId(), testProject.getId());
        ProjectWorker pw = new ProjectWorker(pwId, testProject, testUser, "manager");
        ProjectWorkerId pwId2 = new ProjectWorkerId(testUser2.getId(), testProject.getId());
        ProjectWorker pw2 = new ProjectWorker(pwId2, testProject, testUser2, "worker");
        Set<ProjectWorker> pws = new HashSet<ProjectWorker>();
        pws.add(pw);
        pws.add(pw2);
        testProject.setProjectWorkers(pws);

        projects.add(testProject);

        projectBean.init();
    }

    @After
    public void tearDown() throws Exception {
        Mockito.reset(mockAuthBean, mockProjectService, mockStatusService);
    }

    @Test
    public void testGetProjectListNotLoggedIn() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(null);
        List<Project> projects = projectBean.getProjectList();
        verify(mockProjectService, never()).getProjects(null);
    }

    @Test
    public void testGetProjectListLoggedInNoProject() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        List<Project> projects = projectBean.getProjectList();
        verify(mockProjectService).getProjects(testUser);
        assertEquals(0, projects.size());
    }

    @Test
    public void testGetProjectListLoggedInHasProjects() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        when(mockProjectService.getProjects(testUser)).thenReturn(projects);
        List<Project> projectList = projectBean.getProjectList();

        assertEquals(1, projectList.size());
        assertTrue(projectList.get(0).getId() > 0);
    }

    @Test
    public void testIsAuthorized() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        assertTrue(projectBean.isAuthorized(testProject));
    }

    @Test
    public void testNotIsAuthorized() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser2);
        assertFalse(projectBean.isAuthorized(testProject));

        when(mockAuthBean.getLoggedInUser()).thenReturn(null);
        assertFalse(projectBean.isAuthorized(testProject));
    }

    @Test
    public void testManagerUpdateProject() {
        String successful = "projects?face-redirect=true";

        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockProjectService.updateProject(any(Project.class))).thenReturn(true);

        projectBean.setProjectId(testProject.getId());
        projectBean.init();
        assertNotNull(projectBean.getProject());

        verify(mockProjectService).getProjectById(testUser, testProject.getId());

        // Update title
        projectBean.setTitle("updated title");
        String redirectVal = projectBean.updateProject(testProject.getId());
        verify(mockProjectService).updateProject(testProject);
        assertEquals(successful, redirectVal);

        projectBean.setDescription("updated overview");
        redirectVal = projectBean.updateProject(testProject.getId());
        assertEquals(successful, redirectVal);

        projectBean.setPriority("1");
        redirectVal = projectBean.updateProject(testProject.getId());
        assertEquals(successful, redirectVal);

        projectBean.setDeadline(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        redirectVal = projectBean.updateProject(testProject.getId());
        assertEquals(successful, redirectVal);

        projectBean.setStatusId(mockStatusService.getAllStatus().get(1).getId());
        redirectVal = projectBean.updateProject(testProject.getId());
        assertEquals(successful, redirectVal);

        // Call update from saveProject by supplying project's id
        redirectVal = projectBean.saveProject(testProject.getId());
        assertEquals(successful, redirectVal);
    }

    @Test
    public void testNotManagerUpdateProject() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser2);
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockProjectService.updateProject(any(Project.class))).thenReturn(true);

        projectBean.setProjectId(testProject.getId());
        projectBean.init();
        assertNotNull(projectBean.getProject());
        verify(mockProjectService).getProjectById(testUser2, testProject.getId());

        // Update title
        projectBean.setTitle("updated title");
        String redirectVal = projectBean.updateProject(testProject.getId());
        assertNull(redirectVal);
    }

    @Test
    public void testSaveNewProject() {
        String title = "new project";
        String description = "new project description";
        String priority = "3";
        Date deadline = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Project expectedProject = new Project(mockStatusService.getAllStatus().get(0), title, description, deadline, Integer.parseInt(priority));
        expectedProject.setId(1);

        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        when(mockProjectService.addProject(anyString(), anyString(), anyInt(), any(Date.class), any(User.class))).thenReturn(expectedProject);
        when(mockProjectService.addUserToProject(any(Project.class), any(User.class), anyString())).thenReturn(true);

        projectBean.setTitle(title);
        projectBean.setDescription(description);
        projectBean.setPriority(priority);
        projectBean.setDeadline(deadline);

        String redirectVal = projectBean.saveProject(null);
        verify(mockProjectService).addProject(title, description, Integer.parseInt(priority), deadline, testUser);
        verify(mockProjectService).addUserToProject(expectedProject, testUser, "manager");

        assertEquals("projects?face-redirect=true", redirectVal);
    }

    @Test
    public void testSaveNewProjectNoAuth() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(null);

        String redirectVal = projectBean.saveProject(null);
        assertNull(redirectVal);
    }

    @Test(expected = NumberFormatException.class)
    public void testSaveNewProjectNullInput() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        when(mockProjectService.addProject(isNull(), isNull(), anyInt(), isNull(), any(User.class))).thenReturn(null);
        String redirectVal = projectBean.saveProject(null);
        assertNull(redirectVal);

        projectBean.setTitle("");
        projectBean.setDescription("");
        projectBean.setDeadline(null);
        projectBean.setPriority("");

        when(mockProjectService.addProject(anyString(), anyString(), anyInt(), any(Date.class), any(User.class))).thenReturn(null);

        redirectVal = projectBean.saveProject(null);
        assertNull(redirectVal);
    }


    @Test
    public void testSaveNewProjectFailAddUser() {
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        when(mockProjectService.addUserToProject(any(Project.class), any(User.class), anyString())).thenReturn(false);

        String title = "new project";
        String description = "new project description";
        String priority = "3";
        Date deadline = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        projectBean.setTitle(title);
        projectBean.setDescription(description);
        projectBean.setPriority(priority);
        projectBean.setDeadline(deadline);

        String redirectVal = projectBean.saveProject(null);
        assertNull(redirectVal);
    }
}