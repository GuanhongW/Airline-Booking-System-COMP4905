package com.guanhong.airlinebookingsystem.model;

import java.io.Serializable;

public class UserLoginResponse {

    private String username;
    private long accountId;
    private final String jwttoken;

    public UserLoginResponse(String username, long accountId, String jwttoken) {
        this.username = username;
        this.accountId = accountId;
        this.jwttoken = jwttoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getJwttoken() {
        return jwttoken;
    }

}
