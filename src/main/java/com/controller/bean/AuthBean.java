package com.controller.bean;

import com.model.pojo.User;
import com.service.AuthService;
import com.util.Generated;

import java.io.Serializable;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author pc
 */
@Named(value = "authBean")
@SessionScoped
public class AuthBean implements Serializable {

    private String email;
    private String password;
    private User loggedInUser;
    private FacesContext facesContext;

    @Inject
    private transient AuthService authService;

    public AuthBean(AuthService authService, FacesContext fc) {
        this.authService = authService;
        this.facesContext = fc;
    }

    @Generated
    public AuthBean() {}

    @Generated
    public String getEmail() {
        return email;
    }

    @Generated
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated
    public String getPassword() {
        return password;
    }

    @Generated
    public void setPassword(String password) {
        this.password = password;
    }

    @Generated
    public User getLoggedInUser() {
        return loggedInUser;
    }

    @Generated
    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @Generated
    public FacesContext facesContextWrapper() {
        if (this.facesContext != null) { // only happen in testing
            return this.facesContext;
        }
        
        return FacesContext.getCurrentInstance();
    }

    public String login() {
        Optional<String> optEmail = Optional.ofNullable(email);
        Optional<String> optPassword = Optional.ofNullable(password);
        
        // Validate user credentials
        if (!optEmail.isPresent()) {
            FacesMessage message = new FacesMessage("Email is required");
            facesContextWrapper().addMessage("form:email", message);
            return null;
        }

        if (!optPassword.isPresent()) {
            FacesMessage message = new FacesMessage("Password is required");
            facesContextWrapper().addMessage("form:password", message);
            return null;
        }
        
        loggedInUser = authService.authenticate(email, password);
        
        if (loggedInUser != null) {
            // Successful login, redirect to the dashboard
            return "dashboard?faces-redirect=true";
        } else {
            facesContextWrapper().addMessage("login-form", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid credentials.", null));
            return null;
        }
    }

    public String logout() {
        // Invalidate session
        facesContextWrapper().getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }

    @Generated
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
}