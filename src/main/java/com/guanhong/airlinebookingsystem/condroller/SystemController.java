package com.guanhong.airlinebookingsystem.condroller;

import com.guanhong.airlinebookingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guanhong.airlinebookingsystem.entity.User;

@RestController

public class SystemController {
    @Autowired
    UserService userService;

    @RequestMapping("/get/{id}")
    public User startSpringBoot(@PathVariable("id") int id) {
        try{
            System.out.println(userService.getUserById(id).toString());
            return userService.getUserById(id);
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }

    }

}
