/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller.bean;

import com.model.pojo.User;
import com.service.AuthService;
import java.io.Serializable;
import java.util.Iterator;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.component.UIMessage;
import javax.faces.context.FacesContext;

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

    private AuthService authService = new AuthService();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public String login() {
        // Validate user credentials
        if (email == null || email.isEmpty()) {
            FacesMessage message = new FacesMessage("Email is required");
            FacesContext.getCurrentInstance().addMessage("form:email", message);
            return null;
        }

        if (password == null || password.isEmpty()) {
            FacesMessage message = new FacesMessage("Password is required");
            FacesContext.getCurrentInstance().addMessage("form:password", message);
            return null;
        }
        
        loggedInUser = authService.authenticate(email, password);
        
        if (loggedInUser != null) {
            // Successful login, redirect to the dashboard
            return "dashboard?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage("login-form", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid credentials.", null));
            return null;
        }
    }

    public String logout() {
        // Invalidate session
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
}