package com.work.testchat;

import android.app.Application;

import com.work.testchat.localDb.ApplicationData;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppManager extends Application {
    @Override
    public void onCreate() {
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(configuration);
        //Realm.deleteRealm(Realm.getDefaultConfiguration());
        if (Realm.getDefaultInstance().where(ApplicationData.class).findFirst() == null) {
            Realm.getDefaultInstance().executeTransaction(realm -> Realm.getDefaultInstance().createObject(ApplicationData.class));
        }
        super.onCreate();
    }
}
