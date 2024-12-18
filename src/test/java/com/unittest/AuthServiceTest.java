package com.unittest;

import com.dao.UserDAO;
import com.model.pojo.*;
import com.service.AuthService;
import com.util.HashUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private AuthService authService;

    @Mock
    private UserDAO mockUserDAO;

    @Mock
    private HashUtil mockHashUtil;

    public AuthServiceTest() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        authService = spy(new AuthService());
        when(authService.getUserDAO()).thenReturn(mockUserDAO);
        when(authService.getHashUtil()).thenReturn(mockHashUtil);
    }

    @After
    public void tearDown() {
        Mockito.reset(mockUserDAO, mockHashUtil);
    }

    @Test
    public void testAuthenticateSuccessful() {
        String email = TestVariables.EMAIL;
        String password = TestVariables.PASSWORD;

        User mockUser = mock(User.class);
        when(mockUserDAO.getUserByEmail(anyString())).thenReturn(mockUser);

        when(mockUser.getPassword()).thenReturn("hashedpassword");

        when(mockHashUtil.authenticate(anyString(), eq("hashedpassword"))).thenReturn(true);

        User user = authService.authenticate(email, password);
        verify(mockHashUtil).authenticate(anyString(), anyString());

        assertNotNull(user);
    }

    @Test
    public void testAuthenticateFail() {
        String email = TestVariables.EMAIL;
        String password = TestVariables.PASSWORD;

        User mockUser = mock(User.class);
        when(mockUserDAO.getUserByEmail(eq(TestVariables.EMAIL))).thenReturn(mockUser);

        when(mockUser.getPassword()).thenReturn("hashedpassword");

        when(mockHashUtil.authenticate(eq(password), eq("hashedpassword"))).thenReturn(false);

        User user = authService.authenticate(email, password);
        verify(mockHashUtil).authenticate(anyString(), anyString());

        assertNull(user);
    }

    @Test
    public void testAuthenticateThrowsException() {
        String email = TestVariables.EMAIL;
        String password = TestVariables.PASSWORD;

        User mockUser = mock(User.class);
        when(mockUserDAO.getUserByEmail(eq(TestVariables.EMAIL))).thenReturn(mockUser);

        when(mockUser.getPassword()).thenReturn("hashedpassword");

        when(mockHashUtil.authenticate(anyString(), eq("hashedpassword"))).thenThrow(new IllegalArgumentException());

        User user = authService.authenticate(email, password);
        verify(mockHashUtil).authenticate(anyString(), anyString());

        assertNull(user);
    }
}
