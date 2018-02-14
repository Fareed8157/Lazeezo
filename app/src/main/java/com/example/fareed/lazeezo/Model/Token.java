package com.example.fareed.lazeezo.Model;

/**
 * Created by fareed on 2/4/2018.
 */

public class Token {
    private String token;
    private boolean isServerToken;


    public Token() {
    }

    public Token(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }
}
