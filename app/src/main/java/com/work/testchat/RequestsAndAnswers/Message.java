package com.work.testchat.RequestsAndAnswers;

public class Message {
    public String chatId;
    public String userId;
    public String message;
    public long timestamp;

    public Message(String chatId, String userId, String message, long timestamp) {
        this.chatId = chatId;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }
}
