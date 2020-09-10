package com.guanhong.airlinebookingsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.guanhong.airlinebookingsystem.entity.Gender;
import com.guanhong.airlinebookingsystem.entity.Role;

import java.util.Date;

public class AccountInfo {
    private String username;
    private String password;
    private String name;
    private Role role;
    private Gender gender;
    @JsonFormat(pattern="yyyy-MM-dd")
    private String birthDate;

    public AccountInfo(){

    }

    public AccountInfo(String username, String password, Role role, String name, Gender gender, String birthDate) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public AccountInfo(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
