/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller.bean;

import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import java.io.Serializable;

/**
 *
 * @author pc
 */
@Named(value = "userBean")
@RequestScoped
public class UserBean implements Serializable {
    
    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
    }
    
}
