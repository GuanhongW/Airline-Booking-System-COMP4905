package com.guanhong.airlinebookingsystem.model;

public class CreateUserResponse {
    private String username;
    private long accountId;

    public CreateUserResponse(String username, long accountId) {
        this.username = username;
        this.accountId = accountId;
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
}
