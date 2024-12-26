package com.controller.bean;

import com.service.UserService;
import com.util.Generated;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;

@Named
@RequestScoped
public class RegistrationBean implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserService userService;

    @Inject
    public RegistrationBean(UserService userService) {
        this.userService = userService;
    }

    @Generated
    public RegistrationBean() {}
    
    // Getters and Setters
    @Generated
    public String getFirstName() {
        return firstName;
    }

    @Generated
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Generated
    public String getLastName() {
        return lastName;
    }

    @Generated
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    // Registration action method
    public String register() {
        boolean isSuccessful = userService.createUser(firstName, lastName, email, password);
        
        if (isSuccessful) {
            return "login?faces-redirect=true";
        }
        
        return null;
        
    }
}
