package com.work.testchat.functionalActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.work.testchat.GlobalObjects;
import com.work.testchat.R;
import com.work.testchat.RequestsAndAnswers.responses.EventResponse;
import com.work.testchat.RequestsAndAnswers.responses.RegisterNewUserResponse;
import com.work.testchat.RequestsAndAnswers.requestbody.RegisterNewUser;
import com.work.testchat.RequestsAndAnswers.Request;
import com.work.testchat.databinding.ActivityRegistrationBinding;

import java.net.URI;
import java.net.URISyntaxException;

import tech.gusavila92.websocketclient.WebSocketClient;

public class RegistrationActivity extends AppCompatActivity {
    ActivityRegistrationBinding binder;
    WebSocketClient socket = GlobalObjects.socket;
    String loginText, passwordText;
    Gson gson = new Gson();

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        GlobalObjects.checkResult = this::checkResult;
        GlobalObjects.onReconnection = () -> {};
        binder.registerButton.setOnClickListener(view -> {
            if (binder.password.getText().toString().equals(binder.retypePassword.getText().toString())) {
                loginText = binder.login.getText().toString();
                passwordText = binder.password.getText().toString();
                RegisterNewUser data = new RegisterNewUser(loginText,
                        binder.email.getText().toString(),
                        passwordText,
                        binder.retypePassword.getText().toString());
                Request request = new Request("reg-new-account-request", data);
                socket.send(gson.toJson(request));

            } else {
                Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void checkResult() {
        EventResponse response = gson.fromJson(GlobalObjects.recievedMessage, EventResponse.class);
        if (response.data.meta.status == 200) {
            if (response.event.equals("reg-new-account-response")) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("login", loginText);
                resultIntent.putExtra("password", passwordText);
                setResult(RESULT_OK, resultIntent);
                runOnUiThread(() -> Toast.makeText(RegistrationActivity.this, getString(R.string.welcome), Toast.LENGTH_SHORT).show());
                finish();
            }
        } else {
            Toast.makeText(RegistrationActivity.this, response.data.meta.message, Toast.LENGTH_SHORT).show();
        }
    }
}