package com.orion.pos_crushty_android.databases.login;

public class LoginModel {
    private String userId;
    private String apiKey;

    public LoginModel(String userId, String apiKey) {
        this.userId = userId;
        this.apiKey = apiKey;
    }

    public LoginModel() {
        this.userId = "";
        this.apiKey = "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
