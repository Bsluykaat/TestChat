package com.work.testchat.RequestsAndAnswers.requestbody;

public class SendMessage extends RequestBody{
    String chatId;
    String message;

    public SendMessage() {
    }

    public SendMessage(String chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }
}
