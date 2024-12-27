package com.controller.bean;

import com.service.UserService;
import com.util.Generated;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.regex.Pattern;

@Named
@RequestScoped
public class RegistrationBean implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserService userService;
    private FacesContext facesContext;

    @Inject
    public RegistrationBean(UserService userService, FacesContext facesContext) {
        this.userService = userService;
        this.facesContext = facesContext;
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

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            facesContextWrapper().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "All fields are required", null));
            return null;
        }

        if (userService.getUserByEmail(email) != null) {
            facesContextWrapper().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "User with this email already exists", null));
            return null;
        }

        // Check if email is valid
        boolean isValid = Pattern.compile("^(.+)@(\\\\S+)$").matcher(email).matches();
        if (!isValid) {
            facesContextWrapper().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email", null));
            return null;
        }

        boolean isSuccessful = userService.createUser(firstName, lastName, email, password);

        if (isSuccessful) {
            return "login?faces-redirect=true";
        }

        return null;

    }

    @Generated
    private FacesContext facesContextWrapper() {
        if (facesContext != null) {
            return this.facesContext;
        }
        return FacesContext.getCurrentInstance();
    }
}
