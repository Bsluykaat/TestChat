package com.work.testchat.localDb;

import io.realm.RealmList;
import io.realm.RealmObject;

public class User extends RealmObject {
    public String userName = "";
    public String userId = "";
    public String userToken = "";
    public RealmList<Chat> chats;
}
