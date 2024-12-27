package com.unittest;

import com.controller.bean.AuthBean;
import com.controller.bean.TaskBean;
import com.model.pojo.Project;
import com.model.pojo.ProjectWorker;
import com.model.pojo.Task;
import com.model.pojo.User;
import com.service.ProjectService;
import com.service.TaskService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.*;

public class TaskBeanTest {

    @Mock
    private ProjectService mockProjectService;

    @Mock
    private TaskService mockTaskService;

    @Mock
    private AuthBean mockAuthBean;

    @Mock
    private FacesContext mockFacesContext;

    private TaskBean taskBean;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        taskBean = spy(new TaskBean(mockAuthBean, mockTaskService, mockProjectService, mockFacesContext));
    }

    @After
    public void tearDown() throws Exception {
        Mockito.reset(mockProjectService, mockTaskService, mockAuthBean);
    }

    @Test
    public void testTaskBeanInit() {
        Project mockProject = mock(Project.class);
        when(mockProject.getId()).thenReturn(1);

        User mockUser = mock(User.class);
        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);

        when(mockProjectService.getProjectById(any(User.class), eq(1))).thenReturn(mockProject);

        taskBean.setProjectId(1);
        taskBean.init();
        assertNotNull(taskBean.getProject());
        assertNull(taskBean.getTask());
    }

    @Test
    public void testTaskBeanInitNoProject() {
        when(mockProjectService.getProjectById(any(User.class), anyInt())).thenReturn(null);

        taskBean.setProjectId(1);
        taskBean.init();
        assertNull(taskBean.getProject());
    }


    @Test
    public void testTaskBeanInitTaskId() {
        Project mockProject = mock(Project.class);
        when(mockProject.getId()).thenReturn(1);

        User mockUser = mock(User.class);
        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);

        when(mockProjectService.getProjectById(any(User.class), eq(1))).thenReturn(mockProject);

        Task mockTask = mock(Task.class);
        when(mockTask.getId()).thenReturn(1);
        when(mockTask.getTitle()).thenReturn("Test Task");
        when(mockTask.getDescription()).thenReturn("This is a test task");
        when(mockTask.getDeadline()).thenReturn(new Date());
        when(mockTask.getPriority()).thenReturn(1);

        when(mockUser.getId()).thenReturn(1);

        ProjectWorker mockProjectWorker = mock(ProjectWorker.class);
        when(mockProjectWorker.getUser()).thenReturn(mockUser);

        when(mockTask.getProjectWorker()).thenReturn(mockProjectWorker);
        when(mockTaskService.getTaskById(1)).thenReturn(mockTask);

        taskBean.setProjectId(1);
        taskBean.setTaskId(1);
        taskBean.init();
        assertNotNull(taskBean.getTask());
        assertNotNull(taskBean.getProject());
        assertNotNull(taskBean.getTaskTitle());
        assertNotNull(taskBean.getTaskDescription());
        assertNotNull(taskBean.getDeadline());
        assertNotNull(taskBean.getPriority());
    }

    @Test
    public void testTaskBeanInitTaskIdNotExist() {
        Project mockProject = mock(Project.class);
        when(mockProject.getId()).thenReturn(1);

        User mockUser = mock(User.class);
        when(mockAuthBean.getLoggedInUser()).thenReturn(mockUser);

        when(mockProjectService.getProjectById(any(User.class), eq(1))).thenReturn(mockProject);

        when(mockTaskService.getTaskById(1)).thenReturn(null);

        taskBean.setProjectId(1);
        taskBean.setTaskId(1);
        taskBean.init();
        assertNull(taskBean.getTask());
        assertNull(taskBean.getTaskTitle());
        assertNull(taskBean.getTaskDescription());
        assertNull(taskBean.getDeadline());
        assertNull(taskBean.getPriority());
    }

    @Test
    public void testGetProjectUsers() {
        Project mockProject = mock(Project.class);

        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);

        ProjectWorker mockProjectWorker1 = mock(ProjectWorker.class);
        ProjectWorker mockProjectWorker2 = mock(ProjectWorker.class);

        when(mockProjectWorker1.getUser()).thenReturn(mockUser1);
        when(mockProjectWorker2.getUser()).thenReturn(mockUser2);
        when(mockProject.getProjectWorkers()).thenReturn(new HashSet(Arrays.asList(mockProjectWorker1, mockProjectWorker2)));

        taskBean.setProject(mockProject);
        List<User> users = taskBean.getProjectUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(mockUser1));
        assertTrue(users.contains(mockUser2));
    }

    @Test
    public void testAddNewTask() {

        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        when(mockUser1.getId()).thenReturn(1);
        when(mockUser2.getId()).thenReturn(2);

        taskBean.setAssigneeId(2);


        when(taskBean.getProjectUsers()).thenReturn(Arrays.asList(mockUser1, mockUser2));

        when(mockTaskService.addNewTask(any(User.class), any(Project.class), anyString(), anyString(), any(Date.class), anyInt())).thenReturn(true);

        int projectId = 1;
        taskBean.setProjectId(projectId);
        taskBean.setTaskTitle("Test Task");
        taskBean.setTaskDescription("This is a test task");
        taskBean.setDeadline(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        taskBean.setPriority(1);

        Project mockProject = mock(Project.class);
        taskBean.setProject(mockProject);

        String expectedRedirect = "project?faces-redirect=true&amp;project_id="+projectId;
        String redirectVal = taskBean.addNewTask();


        assertEquals(expectedRedirect, redirectVal);
        assertEquals(2, taskBean.getAssigneeId().intValue());
    }

    @Test
    public void testAddNewTaskInvalidDate() {
        LocalDate twoDaysBeforeToday = LocalDate.now().minusDays(2);
        taskBean.setDeadline(Date.from(twoDaysBeforeToday.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        String redirectVal = taskBean.addNewTask();
        verify(mockFacesContext).addMessage(eq("projectDeadline"), any(FacesMessage.class));
        assertNull(redirectVal);
    }

    @Test
    public void testAddNewTaskInvalidAssignee() {
        taskBean.setAssigneeId(0);
        taskBean.setDeadline(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(taskBean.getProjectUsers()).thenReturn(Collections.emptyList());

        String redirectVal = taskBean.addNewTask();
        verify(mockFacesContext).addMessage(eq("task-form"), any(FacesMessage.class));
        assertNull(redirectVal);
    }

    @Test
    public void testSaveTask() {
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        when(mockUser1.getId()).thenReturn(1);
        when(mockUser2.getId()).thenReturn(2);
        when(taskBean.getProjectUsers()).thenReturn(Arrays.asList(mockUser1, mockUser2));
        taskBean.setAssigneeId(2);
        taskBean.setTaskId(1);
        taskBean.setTaskTitle("Test Task");
        taskBean.setTaskDescription("This is a test task");
        taskBean.setDeadline(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        taskBean.setPriority(1);
        when(mockTaskService.saveTask(anyInt(), any(User.class), any(Project.class), anyString(), anyString(), any(Date.class), anyInt())).thenReturn(true);

        Project mockProject = mock(Project.class);

        taskBean.setProject(mockProject);
        taskBean.setProjectId(1);

        String expectedRedirect = "project?faces-redirect=true&amp;project_id="+1;
        String redirectVal = taskBean.saveTask();

        assertEquals(expectedRedirect, redirectVal);
    }

    @Test
    public void testSaveTaskNoAssignee() {
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        when(mockUser1.getId()).thenReturn(1);
        when(mockUser2.getId()).thenReturn(2);
        when(taskBean.getProjectUsers()).thenReturn(Arrays.asList(mockUser1, mockUser2));
        taskBean.setAssigneeId(0);
        String redirectVal = taskBean.saveTask();
        verify(mockTaskService, never()).saveTask(anyInt(), any(User.class), any(Project.class), anyString(), anyString(), any(Date.class), anyInt());

        verify(mockFacesContext).addMessage(eq("task-form"), any(FacesMessage.class));
        assertNull(redirectVal);
    }

    @Test(expected = NullPointerException.class)
    public void testSaveTaskServiceFail() {
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        when(mockUser1.getId()).thenReturn(1);
        when(mockUser2.getId()).thenReturn(2);
        when(taskBean.getProjectUsers()).thenReturn(Arrays.asList(mockUser1, mockUser2));
        taskBean.setAssigneeId(2);
        when(mockTaskService.saveTask(anyInt(), any(User.class), any(Project.class), anyString(), anyString(), any(Date.class), anyInt())).thenReturn(false);
        String redirectVal = taskBean.saveTask();

        verify(mockFacesContext).addMessage(eq("task-form"), any(FacesMessage.class));
        assertNull(redirectVal);
    }
}