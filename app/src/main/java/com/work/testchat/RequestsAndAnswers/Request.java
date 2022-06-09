package com.work.testchat.RequestsAndAnswers;

import com.work.testchat.RequestsAndAnswers.requestbody.RequestBody;

public class Request {
    public String event;
    public RequestBody data;

    public Request() {
    }

    public Request(String event, RequestBody object) {
        this.event = event;
        this.data = object;
    }
}
