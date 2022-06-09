package com.work.testchat.RequestsAndAnswers.requestbody;

public class TokenLogInUser extends RequestBody {
    String token;

    public TokenLogInUser() {
    }

    public TokenLogInUser(String token) {
        this.token = token;
    }
}
