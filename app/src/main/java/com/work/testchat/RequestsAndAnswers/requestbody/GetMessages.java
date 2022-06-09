package com.work.testchat.RequestsAndAnswers.requestbody;

public class GetMessages extends RequestBody{
    String chatId;

    public GetMessages() {
    }

    public GetMessages(String chatId) {
        this.chatId = chatId;
    }
}
