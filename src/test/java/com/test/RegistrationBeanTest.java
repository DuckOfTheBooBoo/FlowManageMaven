package com.test;

import com.controller.bean.RegistrationBean;
import com.service.UserService;
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
public class RegistrationBeanTest {
    
    @Mock
    private UserService mockUserService;
    
    private RegistrationBean registrationBean;
    
    public RegistrationBeanTest() {
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
        registrationBean = new RegistrationBean(mockUserService);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testRegistration() {
        registrationBean.setFirstName(TestVariables.FIRST_NAME);
        registrationBean.setLastName(TestVariables.LAST_NAME);
        registrationBean.setEmail(TestVariables.EMAIL);
        registrationBean.setPassword(TestVariables.PASSWORD);
        
        when(mockUserService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        
        String expectedRedirectionString = "login?faces-redirect=true";
        String redirectVal = registrationBean.register();
        
        verify(mockUserService).createUser(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, TestVariables.PASSWORD);
        
        assertNotNull(redirectVal);
        assertEquals(redirectVal, expectedRedirectionString);
    }
    
    @Test
    public void testRegistrationEmptyLastName() {
        registrationBean.setFirstName(TestVariables.FIRST_NAME);
        registrationBean.setLastName("");
        registrationBean.setEmail(TestVariables.EMAIL);
        registrationBean.setPassword(TestVariables.PASSWORD);
        
        when(mockUserService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        
        String expectedRedirectionString = "login?faces-redirect=true";
        String redirectVal = registrationBean.register();
        
        verify(mockUserService).createUser(TestVariables.FIRST_NAME, "", TestVariables.EMAIL, TestVariables.PASSWORD);
        
        assertNotNull(redirectVal);
        assertEquals(redirectVal, expectedRedirectionString);
    }
    
    @Test
    public void testRegistrationInvalidEmail() {
        
        registrationBean.setFirstName(TestVariables.FIRST_NAME);
        registrationBean.setLastName(TestVariables.LAST_NAME);
        registrationBean.setEmail("notavalidemail");
        registrationBean.setPassword(TestVariables.PASSWORD);
        
        when(mockUserService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        
        String redirectVal = registrationBean.register();
        
        verify(mockUserService).createUser(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, "notavalidemail", TestVariables.PASSWORD);
        
        assertNull(redirectVal);
    }
    
    @Test
    public void testRegistrationEmptyPassword() {
        
        registrationBean.setFirstName(TestVariables.FIRST_NAME);
        registrationBean.setLastName(TestVariables.LAST_NAME);
        registrationBean.setEmail(TestVariables.EMAIL);
        registrationBean.setPassword("");
        
        when(mockUserService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        String redirectVal = registrationBean.register();
        
        verify(mockUserService).createUser(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, "");
        
        assertNull(redirectVal);
        
        registrationBean.setPassword(null);
        redirectVal = registrationBean.register();
        verify(mockUserService).createUser(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, TestVariables.EMAIL, null);
        
        assertNull(redirectVal);

    }
    
    @Test
    public void testRegistrationEmailExists() {
        String existingEmail = "arajdianaltaf123@gmail.com";
        registrationBean.setFirstName(TestVariables.FIRST_NAME);
        registrationBean.setLastName(TestVariables.LAST_NAME);
        registrationBean.setEmail(existingEmail);
        registrationBean.setPassword(TestVariables.PASSWORD);
        
        when(mockUserService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        
        String redirectVal = registrationBean.register();
        
        verify(mockUserService).createUser(TestVariables.FIRST_NAME, TestVariables.LAST_NAME, existingEmail, TestVariables.PASSWORD);
        
        assertNull(redirectVal);
    }
    
    @Test
    public void testRegistrationEmptyString() {
        registrationBean.setFirstName("");
        registrationBean.setLastName("");
        registrationBean.setEmail("");
        registrationBean.setPassword("");
        when(mockUserService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        String redirectVal = registrationBean.register();
        assertNull(redirectVal);
    }
    
    @Test
    public void testRegistrationNullInput() {
        registrationBean.setFirstName(null);
        registrationBean.setLastName(null);
        registrationBean.setEmail(null);
        registrationBean.setPassword(null);
        when(mockUserService.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(false);

        String redirectVal = registrationBean.register();
        assertNull(redirectVal);
    }
}
