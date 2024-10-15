/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service;

import com.dao.StatusDAO;
import com.model.pojo.Status;
import java.util.List;

/**
 *
 * @author pc
 */
public class StatusService {
    private StatusDAO statusDAO = new StatusDAO();
    public List<Status> getAllStatus() {
        return statusDAO.getAllStatus();
    }
}
