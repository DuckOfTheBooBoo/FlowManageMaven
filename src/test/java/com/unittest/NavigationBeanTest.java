package com.unittest;

import com.controller.bean.NavigationBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NavigationBeanTest {

    private NavigationBean navigationBean;

    @Before
    public void setUp() throws Exception {
        navigationBean = new NavigationBean();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testShowPageNull() {
        navigationBean.setPage(null);
        assertEquals("dashboard?faces-redirect=true", navigationBean.showPage());
    }

    @Test
    public void testShowPageProject() {
        navigationBean.setPage("project");
        assertEquals("project?faces-redirect=true&amp;includeViewParams=true", navigationBean.showPage());
    }

    @Test
    public void testShowPageProjects() {
        navigationBean.setPage("projects");
        assertEquals("projects?faces-redirect=true", navigationBean.showPage());
    }

    @Test
    public void testShowPageDashboard() {
        navigationBean.setPage("dashboard");
        assertEquals("dashboard?faces-redirect=true", navigationBean.showPage());
    }

    @Test
    public void testShowPageProjectForm() {
        navigationBean.setPage("project-form");
        assertEquals("project-form?faces-redirect=true", navigationBean.showPage());
    }

    @Test
    public void testShowPageContactForm() {
        navigationBean.setPage("contact-form");
        assertEquals("contact-form?faces-redirect=true&amp;includeViewParams=true", navigationBean.showPage());
    }

    @Test
    public void testShowPageTaskForm() {
        navigationBean.setPage("task-form");
        assertEquals("task-form?faces-redirect=true&amp;includeViewParams=true", navigationBean.showPage());
    }

    @Test
    public void testShowPageLogin() {
        navigationBean.setPage("login");
        assertEquals("login?faces-redirect=true", navigationBean.showPage());
    }

    @Test
    public void testShowPageInvalidPage() {
        navigationBean.setPage("invalid-page");
        assertNull(navigationBean.showPage());
    }
}