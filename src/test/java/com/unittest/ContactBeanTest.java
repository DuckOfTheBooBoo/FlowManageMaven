package com.unittest;

import com.controller.bean.AuthBean;
import com.controller.bean.ContactBean;
import com.model.pojo.*;
import com.service.ProjectService;
import com.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.mockito.Mockito.*;

public class ContactBeanTest {

    private ContactBean contactBean;

    @Mock
    private FacesContext mockFacesContext;

    @Mock
    private AuthBean mockAuthBean;

    @Mock
    private UserService mockUserService;

    @Mock
    private ProjectService mockProjectService;

    private User testUser;
    private User testUser2;
    private User testUser3;

    private Project testProject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        contactBean = new ContactBean(mockFacesContext, mockUserService, mockProjectService, mockAuthBean);
        testUser = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        testUser.setId(1);

        testUser2 = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        testUser2.setId(2);

        testUser3 = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        testUser3.setId(3);

        testProject = new Project(new Status("on-going"), "test project", "test project description", new Date(), 1);
        testProject.setId(1);

        ProjectWorker pw = new ProjectWorker(
                new ProjectWorkerId(testUser.getId(), testProject.getId()),
                testProject, testUser, "manager");

        ProjectWorker pw2 = new ProjectWorker(
                new ProjectWorkerId(testUser2.getId(), testProject.getId()),
                testProject, testUser2, "worker"
        );

        testProject.setProjectWorkers(
                new HashSet<>(Arrays.asList(pw, pw2))
        );
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testContactBeanInit() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);

        contactBean.setProjectId(testProject.getId());
        contactBean.init();
        verify(mockProjectService).getProjectById(testUser, testProject.getId());
        assertNotNull(contactBean.getProject());
    }

    @Test
    public void testContactBeanInitUnauthorized() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser2);

        contactBean.setProjectId(testProject.getId());
        contactBean.init();
        assertNull(contactBean.getProject());
    }

    @Test
    public void testContactBeanInitUpdateUserExistsInProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);

        contactBean.setProjectId(testProject.getId());
        contactBean.setUserId(testUser2.getId());
        contactBean.init();
        assertNotNull(contactBean.getPw());
        assertEquals("worker", contactBean.getRole());
        assertEquals(TestVariables.EMAIL, contactBean.getUserEmail());
    }

    @Test
    public void testContactBeanInitUpdateUserNotExistsInProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);

        contactBean.setProjectId(testProject.getId());
        contactBean.setUserId(testUser3.getId());
        contactBean.init();
        assertNull(contactBean.getPw());
    }

    @Test
    public void testAddUserToProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockProjectService.addUserToProject(any(Project.class), any(User.class), anyString())).thenReturn(true);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        when(mockUserService.getUserByEmail(anyString())).thenReturn(testUser3);

        contactBean.setProjectId(testProject.getId());
        contactBean.init();

        contactBean.setUserEmail("dududada@gmail.com");
        contactBean.setRole("worker");
        String expectedRedirectVal = "project?faces-redirect=true&amp;project_id="+testProject.getId();
        String redirectVal = contactBean.addUserToProject();
        verify(mockProjectService).addUserToProject(testProject, testUser3, "worker");

        assertEquals(expectedRedirectVal, redirectVal);
    }

    @Test
    public void testAddUserToProjectUnauthorized() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockProjectService.addUserToProject(any(Project.class), any(User.class), anyString())).thenReturn(true);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser2);
        when(mockUserService.getUserByEmail(anyString())).thenReturn(testUser3);


        contactBean.setProjectId(testProject.getId());
        contactBean.init();

        contactBean.setUserEmail("dududada@gmail.com");
        contactBean.setRole("worker");
        String redirectVal = contactBean.addUserToProject();

        assertNull(redirectVal);
    }

    @Test
    public void testAddNonExistentUserToProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockProjectService.addUserToProject(any(Project.class), any(User.class), anyString())).thenReturn(true);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser2);
        when(mockUserService.getUserByEmail(anyString())).thenReturn(null);


        contactBean.setProjectId(testProject.getId());
        contactBean.init();

        contactBean.setUserEmail("dududada@gmail.com");
        contactBean.setRole("worker");
        String redirectVal = contactBean.addUserToProject();
        verify(mockFacesContext).addMessage(anyString(), any(FacesMessage.class));
        assertNull(redirectVal);
    }

    @Test
    public void testAddAlreadyExistsUserToProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser2);
        when(mockUserService.getUserByEmail(anyString())).thenReturn(testUser2);


        contactBean.setProjectId(testProject.getId());
        contactBean.init();

        contactBean.setUserEmail(testUser2.getEmail());
        contactBean.setRole("worker");
        String redirectVal = contactBean.addUserToProject();
        verify(mockFacesContext).addMessage(anyString(), any(FacesMessage.class));
        assertNull(redirectVal);
    }

    @Test
    public void testFailToAddUserToProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockProjectService.addUserToProject(any(Project.class), any(User.class), anyString())).thenReturn(false);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser2);
        when(mockUserService.getUserByEmail(anyString())).thenReturn(testUser2);


        contactBean.setProjectId(testProject.getId());
        contactBean.init();

        contactBean.setUserEmail(testUser2.getEmail());
        contactBean.setRole("worker");
        String redirectVal = contactBean.addUserToProject();
        verify(mockFacesContext).addMessage(anyString(), any(FacesMessage.class));
        assertNull(redirectVal);
    }

    @Test
    public void testUpdateUserInProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        when(mockProjectService.updateProjectWorker(any(ProjectWorker.class))).thenReturn(true);

        contactBean.setUserId(testUser2.getId());
        contactBean.setProjectId(testProject.getId());
        contactBean.init();
        assertNotNull(contactBean.getPw());

        contactBean.setRole("another role");
        String redirectVal = contactBean.addUserToProject();
        String expectedRedirect = "project?faces-redirect=true&amp;project_id="+testProject.getId();

        assertEquals(expectedRedirect, redirectVal);
        assertEquals("another role", contactBean.getPw().getRole());
    }

    @Test
    public void testUpdateNonExistentUserInProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(testProject);
        when(mockAuthBean.getLoggedInUser()).thenReturn(testUser);
        when(mockProjectService.updateProjectWorker(any(ProjectWorker.class))).thenReturn(false); // null role will raise exception in dao

        contactBean.setProjectId(testProject.getId());
        contactBean.setUserId(testUser3.getId());
        contactBean.init();

        assertNull(contactBean.getPw());

        String val = contactBean.addUserToProject();
        assertNull(val);
    }
}