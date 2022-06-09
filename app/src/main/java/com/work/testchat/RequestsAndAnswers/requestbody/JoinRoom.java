package com.work.testchat.RequestsAndAnswers.requestbody;

public class JoinRoom extends RequestBody{
    String roomId;

    public JoinRoom() {
    }

    public JoinRoom(String roomId) {
        this.roomId = roomId;
    }
}
