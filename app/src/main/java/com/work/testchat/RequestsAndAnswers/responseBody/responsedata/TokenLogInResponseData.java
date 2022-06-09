package com.work.testchat.RequestsAndAnswers.responseBody.responsedata;

import com.work.testchat.RequestsAndAnswers.User;

public class TokenLogInResponseData extends ResponseData{
    public boolean auth;
    public User user;

    public TokenLogInResponseData() {
    }

    public TokenLogInResponseData(boolean auth, User user) {
        this.auth = auth;
        this.user = user;
    }
}
