package com.work.testchat.localDb;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ApplicationData extends RealmObject {
    public RealmList<User> users;
    public User lastLogged;
}
