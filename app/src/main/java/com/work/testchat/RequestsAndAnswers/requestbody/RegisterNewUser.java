package com.work.testchat.RequestsAndAnswers.requestbody;

public class RegisterNewUser extends RequestBody {
    public String login;
    public String email;
    public String pass;
    public String repass;

    public RegisterNewUser(String login, String email, String pass, String repass) {
        this.login = login;
        this.email = email;
        this.pass = pass;
        this.repass = repass;
    }
}
