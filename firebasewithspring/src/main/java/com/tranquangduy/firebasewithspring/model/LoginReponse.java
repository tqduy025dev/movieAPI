package com.tranquangduy.firebasewithspring.model;

public class LoginReponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public LoginReponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
