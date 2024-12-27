package com.unittest;

import com.controller.bean.UtilBean;
import com.model.pojo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class UtilBeanTest {

    private UtilBean utilBean;

    @Before
    public void setUp() throws Exception {
        utilBean = new UtilBean();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getFullName() {
        User user = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        String expectedFullName = TestVariables.FIRST_NAME + " " + TestVariables.LAST_NAME;

        String fullName = utilBean.getFullName(user);

        assertEquals(expectedFullName, fullName);
    }

    @Test
    public void getFullNameNoLastName() {
        User user = new User(TestVariables.FIRST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        String expectedFullName = TestVariables.FIRST_NAME;

        String fullName = utilBean.getFullName(user);

        assertEquals(expectedFullName, fullName);
    }

    @Test
    public void getFullNameNull() {
        User user = null;
        String expectedFullName = "";
        String fullName = utilBean.getFullName(user);

        assertEquals(expectedFullName, fullName);
    }

    @Test
    public void getRemainingDays() {
        Date date = new GregorianCalendar(2024, Calendar.NOVEMBER, 12).getTime();
        Date currentDate = new Date();

        long remainingDays = utilBean.getRemainingTime(date);
        long expectedRemainingDays = TimeUnit.DAYS.convert(date.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);

        assertEquals(expectedRemainingDays, remainingDays);
    }

    @Test
    public void getRemainingDaysToday() {
        Date date = new Date();

        long remainingDays = utilBean.getRemainingTime(date);
        long expectedRemainingDays = 0L;

        assertEquals(expectedRemainingDays, remainingDays);
    }

    @Test
    public void getRemainingDaysHasPassed() {
        Date date = new GregorianCalendar(2023, Calendar.NOVEMBER, 12).getTime();

        long remainingDays = utilBean.getRemainingTime(date);
        assertTrue(remainingDays < 0);
    }

    @Test
    public void sortTaskPriority() {
        Task task1 = new Task();
        task1.setPriority(1);
        Task task2 = new Task();
        task2.setPriority(2);
        Task task3 = new Task();
        task3.setPriority(3);
        List<Task> tasks = new ArrayList<>(
                Arrays.asList(task1, task3, task2)
        );

        List<Task> sortedTasks = new ArrayList<>(
                Arrays.asList(task3, task2, task1)
        );
        List<Task> output = utilBean.sortTaskPriority(tasks);
        assertEquals(sortedTasks, output);

        tasks = new ArrayList<>(
                Arrays.asList(task3, task1, task2)
        );
        assertEquals(sortedTasks, utilBean.sortTaskPriority(tasks));

        tasks = new ArrayList<>(
                Arrays.asList(task2, task3, task1)
        );
        assertEquals(sortedTasks, utilBean.sortTaskPriority(tasks));
    }

    @Test
    public void sortProjectPriority() {
        Project project1 = new Project();
        project1.setPriority(1);
        Project project2 = new Project();
        project2.setPriority(2);
        Project project3 = new Project();
        project3.setPriority(3);
        List<Project> projects = new ArrayList<>(
                Arrays.asList(project1, project3, project2)
        );

        List<Project> sortedProjects = new ArrayList<>(
                Arrays.asList(project3, project2, project1)
        );

        assertEquals(sortedProjects, utilBean.sortProjectPriority(projects));

        projects = new ArrayList<>(
                Arrays.asList(project3, project1, project2)
        );
        assertEquals(sortedProjects, utilBean.sortProjectPriority(projects));

        projects = new ArrayList<>(
                Arrays.asList(project2, project3, project1)
        );
        assertEquals(sortedProjects, utilBean.sortProjectPriority(projects));
    }
}