package com.guanhong.airlinebookingsystem.entity;

import javax.persistence.*;

@Entity
@Table(name= "user")
public class User {

    public User(){

    }

    public User(String username, String password, String role, String jwttoken){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }


    public User(String userName, String password){
        this.username = userName;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



    @Override
    public String toString(){
        return "username: " + this.username + "role: " + this.role;
    }
}
