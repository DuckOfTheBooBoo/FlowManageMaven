package com.unittest;

import com.controller.bean.AuthBean;
import com.model.pojo.User;
import com.service.AuthService;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

/**
 *
 * @author pc
 */
public class AuthBeanTest {
    
    @Mock
    private AuthService mockAuthService;
    
    @Mock
    private FacesContext mockFacesContext;
    
    @Mock
    private ExternalContext mockExternalContext;
    
    private AuthBean authBean;
    
    public AuthBeanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockFacesContext.getExternalContext()).thenReturn(mockExternalContext);
        authBean = new AuthBean(mockAuthService, mockFacesContext);
    }
    
    @After
    public void tearDown() {
        authBean = null;
    }
    
    @Test
    public void testAuthenticationValidAccount() {
        
        User mockUser = new User(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        
        authBean.setEmail(TestVariables.EMAIL);
        authBean.setPassword(TestVariables.PASSWORD);
        
        when(mockAuthService.authenticate(anyString(), anyString())).thenReturn(mockUser);
        
        String expectedRedirect = "dashboard?faces-redirect=true";
        String redirectVal = authBean.login();
        
        verify(mockAuthService).authenticate(TestVariables.EMAIL, TestVariables.PASSWORD);
        
        assertEquals(redirectVal, expectedRedirect);
    }
    
    @Test
    public void testAuthenticateNullOrEmptyInput() {
        when(mockAuthService.authenticate(any(), any())).thenReturn(null);
        
        authBean.setEmail(null);
        authBean.setPassword(null);
        
        String redirectVal = authBean.login();
        verify(mockFacesContext).addMessage(eq("form:email"), any(FacesMessage.class));
        assertNull(redirectVal);

        // Partial        
        authBean.setEmail("something@email.com");
        authBean.setPassword(null);
        redirectVal = authBean.login();
        verify(mockFacesContext).addMessage(eq("form:password"), any(FacesMessage.class));
        assertNull(redirectVal);
        
        authBean.setEmail("");
        authBean.setPassword("");
        redirectVal = authBean.login();
        
        assertNull(redirectVal);
    }
    
    @Test
    public void testAuthenticateAccountDoesntExist() {
        String email = "nonexistent@email.com";
        String password = "totallynotexistentpassword";
        authBean.setEmail(email);
        authBean.setPassword(password);
        
        when(mockAuthService.authenticate(email, password)).thenReturn(null);
        
        String redirectVal = authBean.login();
        assertNull(redirectVal);
    }
    
    @Test
    public void testLogout() {
        String result = authBean.logout();
        
        verify(mockExternalContext).invalidateSession();
        assertEquals("login?faces-redirect=true", result);
    }
    
    @Test
    public void testIsNotLoggedIn() {
        assertFalse(authBean.isLoggedIn());
    }
    
    @Test
    public void testIsLoggedIn() {
        authBean.setLoggedInUser(new User());
        assertTrue(authBean.isLoggedIn());
    }
}
