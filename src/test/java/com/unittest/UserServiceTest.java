package com.unittest;

import com.dao.UserDAO;
import com.model.pojo.*;
import com.service.UserService;
import com.util.HashUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class UserServiceTest {


    @Mock
    private UserDAO mockUserDAO;

    @Mock
    private HashUtil mockHashUtil;

    private UserService userService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService();

        userService.setUserDAO(mockUserDAO);
        userService.setHashUtil(mockHashUtil);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreateUser() {
        when(mockHashUtil.hash(TestVariables.PASSWORD)).thenReturn("hashedpassword");
        when(mockUserDAO.addUser(any(User.class))).thenReturn(true);


      boolean isSuccessful = userService.createUser(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);

      assertTrue(isSuccessful);
    }

    @Test
    public void testCreateUserUnsuccessful() {
        when(mockHashUtil.hash(TestVariables.PASSWORD)).thenReturn("hashedpassword");
        when(mockUserDAO.addUser(any(User.class))).thenReturn(false);


      boolean isSuccessful = userService.createUser(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);

      assertFalse(isSuccessful);
    }

    @Test
    public void testCreateUserExistingEmail() {
        when(mockHashUtil.hash(TestVariables.PASSWORD)).thenReturn("hashedpassword");
        when(mockUserDAO.addUser(any(User.class))).thenReturn(true); // behavior mocked from integration test


        boolean isSuccessful = userService.createUser(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        assertFalse(isSuccessful);
    }

    @Test
    public void testCreateUserNullValues() {
        when(mockHashUtil.hash(TestVariables.PASSWORD)).thenReturn("hashedpassword");
        when(mockUserDAO.addUser(any(User.class))).thenReturn(false);


      boolean isSuccessful = userService.createUser(null, null, null, null);

      assertFalse(isSuccessful);
    }

    @Test
    public void testGetUserByEmail() {
        when(mockUserDAO.getUserByEmail(TestVariables.EMAIL)).thenReturn(mock(User.class));


        User user = userService.getUserByEmail(TestVariables.EMAIL);

        assertNotNull(user);
    }

    @Test
    public void testGetUserByEmailNoUser() {
        when(mockUserDAO.getUserByEmail(TestVariables.EMAIL)).thenReturn(null);

        User user = userService.getUserByEmail(TestVariables.EMAIL);

        assertNull(user);
    }
}
