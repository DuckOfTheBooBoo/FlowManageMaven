package com.controller.bean;

import com.service.UserService;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@Named
@RequestScoped
public class RegistrationBean {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserService userService;

    @Inject
    public RegistrationBean(UserService userService) {
        this.userService = userService;
    }
    
    public RegistrationBean() {}
    
    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    // Registration action method
    public String register() {
        boolean isSuccessful = userService.createUser(firstName, lastName, email, password);
        
        if (isSuccessful) {
            return "login?faces-redirect=true";
        }
        
        return null;
        
    }
}
