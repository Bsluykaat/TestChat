package com.work.testchat.RequestsAndAnswers.requestbody;

public class LogInUser extends RequestBody {
    public String login;
    public String pass;

    public LogInUser() {
    }

    public LogInUser(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }
}
