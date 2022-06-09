package com.work.testchat.functionalActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.work.testchat.GlobalObjects;
import com.work.testchat.MainActivity;
import com.work.testchat.R;
import com.work.testchat.RequestsAndAnswers.Request;
import com.work.testchat.RequestsAndAnswers.requestbody.TokenLogInUser;
import com.work.testchat.RequestsAndAnswers.responses.EventResponse;
import com.work.testchat.RequestsAndAnswers.responses.TokenLogInResponse;
import com.work.testchat.localDb.ApplicationData;
import com.work.testchat.localDb.User;

import io.realm.Realm;
import tech.gusavila92.websocketclient.WebSocketClient;

public class SplashActivity extends AppCompatActivity {
    WebSocketClient socket;
    Gson gson = new Gson();;
    User user;
    Realm realm;
    ApplicationData applicationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        GlobalObjects.createWebSocketConnection();

        socket = GlobalObjects.socket;
        GlobalObjects.checkResult = this::checkResult;
        GlobalObjects.onReconnection = () -> {tokenLogIn(user.userToken);};
        realm = Realm.getDefaultInstance();
        applicationData = realm.where(ApplicationData.class).findFirst();
        user = applicationData.lastLogged;

        if (user != null) {
            tokenLogIn(user.userToken);
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    void tokenLogIn(String token) {
        TokenLogInUser data = new TokenLogInUser(token);
        Request request = new Request("auth-token-request", data);
        socket.send(gson.toJson(request));
    }

    void checkResult() {
        EventResponse response = gson.fromJson(GlobalObjects.recievedMessage, EventResponse.class);
        if (response.data.meta.status == 200) {
            if (response.event.equals("auth-token-response")) {
                TokenLogInResponse tokenLogInResponse = gson.fromJson(GlobalObjects.recievedMessage,
                        TokenLogInResponse.class);
                if (tokenLogInResponse.data.data.auth) {
                    GlobalObjects.user = user;
                }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        } else {
            Toast.makeText(this, response.data.meta.message, Toast.LENGTH_SHORT).show();
        }
    }
}