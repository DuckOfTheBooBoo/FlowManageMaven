package com.test;

import com.controller.bean.AuthBean;
import com.controller.bean.ViewProjectBean;
import com.model.pojo.*;
import com.service.ProjectService;
import com.service.TaskService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ViewProjectBeanTest {
    private ViewProjectBean viewProjectBean;

    @Mock
    private FacesContext mockFacesContext;
    @Mock
    private ExternalContext mockExternalContext;
    @Mock
    private ProjectService mockProjectService;
    @Mock
    private TaskService mockTaskService;
    @Mock
    private AuthBean mockAuthBean;

    private User testUser;
    private Project testProject;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        viewProjectBean = spy(new ViewProjectBean(mockAuthBean, mockTaskService, mockProjectService, mockFacesContext));
        when(mockFacesContext.getExternalContext()).thenReturn(mockExternalContext);

        Status onGoing = new Status("on-going");
        onGoing.setId(1);

        testProject = new Project(onGoing, "test project", "test project description", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), 1);
        testProject.setId(1);

        testUser = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        testUser.setId(1);
    }

    @After
    public void tearDown() throws Exception {
        Mockito.reset(mockFacesContext, mockExternalContext, mockProjectService, mockTaskService, mockAuthBean);
    }

    @Test
    public void testViewProjectBeanInit() {
        Map<String, String> params = new HashMap<String, String>() {{
            put("project_id", String.valueOf(testProject.getId()));
        }};
        when(mockExternalContext.getRequestParameterMap()).thenReturn(params);

        viewProjectBean.init();
        assertNotNull(viewProjectBean.getProjectId());

    }

    @Test
    public void testViewProjectBeanInitNoProjectId() {
        when(mockExternalContext.getRequestParameterMap()).thenReturn(new HashMap<String, String>());
        viewProjectBean.init();
        verify(viewProjectBean, never()).loadProject();
    }

    @Test(expected = NumberFormatException.class)
    public void testViewProjectBeanInitInvalidProjectId() {
        when(mockExternalContext.getRequestParameterMap()).thenReturn(new HashMap<String, String>() {{
            put("project_id", "invalid");
        }});
        ViewProjectBean spyViewProjectBean = spy(new ViewProjectBean(mockAuthBean, mockTaskService, mockProjectService, mockFacesContext));
        spyViewProjectBean.init();
        verify(spyViewProjectBean, never()).loadProject();
    }

    @Test
    public void testLoadProjectUserNotLoggedIn() {
        // Arrange
        when(mockAuthBean.getLoggedInUser()).thenReturn(null);

        // Act
        viewProjectBean.loadProject();

        // Assert
        verify(mockFacesContext).addMessage(eq(null), argThat(message ->
                message.getSeverity() == FacesMessage.SEVERITY_ERROR &&
                        message.getSummary().equals("You're not logged in")
        ));
        assertNull(viewProjectBean.getProject());
        assertTrue(viewProjectBean.getTaskList().isEmpty());
    }

    @Test
    public void testLoadProjectNullProjectId() {
        // Arrange
        User mockUser = mock(User.class);
        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);
        viewProjectBean.setProjectId(null);

        // Act
        viewProjectBean.loadProject();

        // Assert
        verify(mockProjectService, never()).getProjectById(any(User.class), anyInt());
        assertNull(viewProjectBean.getProject());
        assertTrue(viewProjectBean.getTaskList().isEmpty());
    }

    @Test
    public void testLoadProjectProjectNotFound() {
        // Arrange
        User mockUser = mock(User.class);
        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);
        viewProjectBean.setProjectId(1);
        when(mockProjectService.getProjectById(mockUser, 1)).thenReturn(null);

        // Act
        viewProjectBean.loadProject();

        // Assert
        verify(mockFacesContext).addMessage(eq(null), argThat(message ->
                message.getSeverity() == FacesMessage.SEVERITY_ERROR &&
                        message.getSummary().equals("Project not found")
        ));
        assertNull(viewProjectBean.getProject());
        assertTrue(viewProjectBean.getTaskList().isEmpty());
    }

    @Test
    public void testLoadProjectProjectFoundLoadProjectAndTasks() {
        // Arrange
        User mockUser = mock(User.class);
        Project mockProject = mock(Project.class);
        List<Task> mockTasks = Arrays.asList(mock(Task.class), mock(Task.class));

        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);
        viewProjectBean.setProjectId(1);
        when(mockProjectService.getProjectById(mockUser, 1)).thenReturn(mockProject);

        // Mock getTaskList method
        doReturn(mockTasks).when(viewProjectBean).getTaskList();

        // Act
        viewProjectBean.loadProject();

        // Assert
        assertEquals(mockProject, viewProjectBean.getProject());
        assertEquals(mockTasks, viewProjectBean.getTaskList());
        verify(mockFacesContext, never()).addMessage(any(), any());
    }

    @Test
    public void testGetProjectManagerValid() {
        Project mockProject = mock(Project.class);
        User mockUser = mock(User.class);
        ProjectWorker mockPw = mock(ProjectWorker.class);
        Set<ProjectWorker> mockPws = new HashSet<ProjectWorker>() {{
            add(mockPw);
        }};
        viewProjectBean.setProject(mockProject);
        when(mockProject.getProjectWorkers()).thenReturn(mockPws);
        when(mockPw.getUser()).thenReturn(mockUser);
        when(mockUser.getFirstName()).thenReturn(TestVariables.FIRST_NAME);
        when(mockUser.getLastName()).thenReturn(TestVariables.LAST_NAME);
        when(mockPw.getRole()).thenReturn("manager");

        String manager = viewProjectBean.getManager();
        assertEquals(TestVariables.FIRST_NAME + " " + TestVariables.LAST_NAME, manager);
    }

    @Test
    public void testGetManagerNoProject() {
        String manager = viewProjectBean.getManager();
        verify(mockFacesContext).addMessage(eq(null), any(FacesMessage.class));
        assertNull(manager);
    }

    @Test
    public void testGetNoManagerInProject() {
        Project mockProject = mock(Project.class);
        viewProjectBean.setProject(mockProject);
        when(mockProject.getProjectWorkers()).thenReturn(null);
        ProjectWorker mockPw = mock(ProjectWorker.class);
        Set<ProjectWorker> mockPws = new HashSet<ProjectWorker>() {{
            add(mockPw);
        }};
        when(mockProject.getProjectWorkers()).thenReturn(mockPws);

        when(mockPw.getRole()).thenReturn("worker");

        String manager = viewProjectBean.getManager();
        assertNull(manager);
    }

    @Test
    public void testGetLoggedInRole() {
        Project mockProject = mock(Project.class);
        User mockUser = mock(User.class);
        ProjectWorker mockPw = mock(ProjectWorker.class);
        Set<ProjectWorker> mockPws = new HashSet<ProjectWorker>() {{
            add(mockPw);
        }};

        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);

        viewProjectBean.setProject(mockProject);
        when(mockProject.getProjectWorkers()).thenReturn(mockPws);
        when(mockPw.getUser()).thenReturn(mockUser);
        when(mockPw.getRole()).thenReturn("worker");

        String role = viewProjectBean.loggedInRole();
        assertEquals("worker", role);
    }

    @Test
    public void testGetLoggedInRoleInvalid() {
        String role = viewProjectBean.loggedInRole();
        assertTrue(role.isEmpty());

        when(mockAuthBean.getLoggedInUser()).thenReturn(mock(User.class));

        role = viewProjectBean.loggedInRole();
        assertTrue(role.isEmpty());
    }

    @Test
    public void testGetTaskList() {
        Project mockProject = mock(Project.class);
        viewProjectBean.setProject(mockProject);
        ProjectWorker mockPw = mock(ProjectWorker.class);
        Set<ProjectWorker> mockPws = new HashSet<ProjectWorker>() {{
            add(mockPw);
        }};

        when(mockProject.getProjectWorkers()).thenReturn(mockPws);
        Task mockTask1 = mock(Task.class);
        Task mockTask2 = mock(Task.class);

        when(mockTask1.getPriority()).thenReturn(1);
        when(mockTask2.getPriority()).thenReturn(3);

        when(mockPw.getTasks()).thenReturn(new HashSet<>(Arrays.asList(mockTask1, mockTask2)));

        List<Task> tasks = viewProjectBean.getTaskList();
        assertTrue(!tasks.isEmpty());
        assertEquals(Arrays.asList(mockTask2, mockTask1), tasks);
    }

    @Test
    public void testGetTaskListNoProject() {
        List<Task> tasks = viewProjectBean.getTaskList();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testUserOnGoingTaskList() {

        Task mockTask1 = mock(Task.class);
        Task mockTask2 = mock(Task.class);
        Task mockTask3 = mock(Task.class);
        Task mockTask4 = mock(Task.class);

        Status mockOngoing = mock(Status.class);
        Status mockDone = mock(Status.class);

        when(mockOngoing.getId()).thenReturn(1);
        when(mockDone.getId()).thenReturn(2);

        User mockUser = mock(User.class);
        User mockUser2 = mock(User.class);

        ProjectWorker mockPw = mock(ProjectWorker.class);
        ProjectWorker mockPw2 = mock(ProjectWorker.class);

        when(mockTask1.getId()).thenReturn(1);
        when(mockTask2.getId()).thenReturn(1);
        when(mockTask3.getId()).thenReturn(2);
        when(mockTask4.getId()).thenReturn(2);

        when(mockTask1.getStatus()).thenReturn(mockOngoing);
        when(mockTask2.getStatus()).thenReturn(mockOngoing);
        when(mockTask3.getStatus()).thenReturn(mockDone);
        when(mockTask4.getStatus()).thenReturn(mockDone);

        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(1);

        when(mockPw.getUser()).thenReturn(mockUser);
        when(mockPw2.getUser()).thenReturn(mockUser2);
        // Mock Tasks
        when(mockTask1.getProjectWorker()).thenReturn(mockPw);
        when(mockTask2.getProjectWorker()).thenReturn(mockPw);
        when(mockTask3.getProjectWorker()).thenReturn(mockPw);
        when(mockTask4.getProjectWorker()).thenReturn(mockPw2);

        List<Task> mockTasks = new ArrayList<>(Arrays.asList(mockTask1, mockTask2, mockTask3, mockTask4));

        when(viewProjectBean.getTaskList()).thenReturn(mockTasks);

        List<Task> onGoingTasks = viewProjectBean.getUserOnGoingTaskList();

        assertNotNull(onGoingTasks);
        assertEquals(2, onGoingTasks.size());
        assertEquals(Arrays.asList(mockTask1, mockTask2), onGoingTasks);
    }

    @Test
    public void testUserNoOnGoingTaskList() {
        User mockUser = mock(User.class);
        User mockUser2 = mock(User.class);

        ProjectWorker mockPw = mock(ProjectWorker.class);

        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(1);

        when(mockPw.getUser()).thenReturn(mockUser);

        when(viewProjectBean.getTaskList()).thenReturn(new ArrayList<>());

        List<Task> onGoingTasks = viewProjectBean.getUserOnGoingTaskList();

        assertNotNull(onGoingTasks);
        assertTrue(onGoingTasks.isEmpty());
    }

    @Test
    public void testCountFinishedTask() {
        Task mockTask1 = mock(Task.class);
        Task mockTask2 = mock(Task.class);
        Task mockTask3 = mock(Task.class);

        Status onGoing = mock(Status.class);
        Status done = mock(Status.class);

        onGoing.setId(1);
        done.setId(2);

        when(mockTask1.getStatus()).thenReturn(onGoing);
        when(mockTask2.getStatus()).thenReturn(onGoing);
        when(mockTask3.getStatus()).thenReturn(done);

        List<Task> mockTask = new ArrayList<>(Arrays.asList(mockTask1, mockTask2, mockTask3));

        when(viewProjectBean.getTaskList()).thenReturn(mockTask);

        Integer count = viewProjectBean.countFinishedTask();
        assertEquals(1, (int) count);
    }

    @Test
    public void testCountOnGoingTask() {
        Task mockTask1 = mock(Task.class);
        Task mockTask2 = mock(Task.class);
        Task mockTask3 = mock(Task.class);

        Status onGoing = mock(Status.class);
        Status done = mock(Status.class);

        onGoing.setId(1);
        done.setId(2);

        when(mockTask1.getStatus()).thenReturn(onGoing);
        when(mockTask2.getStatus()).thenReturn(onGoing);
        when(mockTask3.getStatus()).thenReturn(done);

        List<Task> mockTask = new ArrayList<>(Arrays.asList(mockTask1, mockTask2, mockTask3));

        when(viewProjectBean.getTaskList()).thenReturn(mockTask);

        Integer count = viewProjectBean.countOnGoingTask();
        assertEquals(2, (int) count);
    }

    @Test
    public void testCompleteTask() {

        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1);

        List<Task> taskList = new ArrayList<>(Arrays.asList(mockTask));

        // Arrange
        when(mockTaskService.completeTask(mockTask)).thenReturn(true);
        int taskId = 1;

        viewProjectBean.setTaskList(taskList);
        // Act
        viewProjectBean.completeTask(taskId);

        // Assert
        verify(mockTaskService).completeTask(mockTask);
        verify(viewProjectBean).refreshTask();
    }

    @Test
    public void testDeleteTask() {
        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(2);

        List<Task> mockTasks = new ArrayList<>(Arrays.asList(mockTask));

        // Arrange
        when(mockTaskService.deleteTask(mockTask)).thenReturn(true);
        int taskId = 2;

        viewProjectBean.setTaskList(mockTasks);

        // Act
        viewProjectBean.deleteTask(taskId);

        // Assert
        verify(mockTaskService).deleteTask(mockTask);
        verify(viewProjectBean).refreshTask();
    }

    @Test
    public void testDeleteMember() {

        ProjectWorker projectWorker1 = mock(ProjectWorker.class);
        Project mockProject = mock(Project.class);
        User mockUser = mock(User.class);
        when(projectWorker1.getUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(1);

        Set<ProjectWorker> projectWorkers = new HashSet<>(Arrays.asList(projectWorker1));

        when(mockProject.getProjectWorkers()).thenReturn(projectWorkers);
        // Arrange
        when(mockProjectService.deleteUserFromProject(projectWorker1)).thenReturn(true);
        int userId = 1;

        viewProjectBean.setProject(mockProject);
        // Act
        viewProjectBean.deleteMember(userId);

        // Assert
        verify(mockProjectService).deleteUserFromProject(projectWorker1);
    }
}