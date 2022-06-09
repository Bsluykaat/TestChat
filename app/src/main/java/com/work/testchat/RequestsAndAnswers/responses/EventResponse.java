package com.work.testchat.RequestsAndAnswers.responses;

import com.work.testchat.RequestsAndAnswers.Meta;
import com.work.testchat.RequestsAndAnswers.responseBody.ResponseBody;

public class EventResponse {
    public String event;
    public ResponseBody data;

    public EventResponse() {
    }

    public EventResponse(int status, String message) {
        this.data = new ResponseBody();
        this.data.meta = new Meta();
        this.data.meta.status = status;
        this.data.meta.message = message;
    }
}
