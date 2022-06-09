package com.work.testchat.RequestsAndAnswers.requestbody;

public class GetChatUsers extends RequestBody{
    String chatId;

    public GetChatUsers() {
    }

    public GetChatUsers(String chatId) {
        this.chatId = chatId;
    }
}
