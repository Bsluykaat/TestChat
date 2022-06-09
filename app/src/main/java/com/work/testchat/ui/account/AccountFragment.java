package com.work.testchat.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.work.testchat.GlobalObjects;
import com.work.testchat.R;
import com.work.testchat.functionalActivities.RegistrationActivity;
import com.work.testchat.RequestsAndAnswers.Request;
import com.work.testchat.RequestsAndAnswers.requestbody.LogInUser;
import com.work.testchat.RequestsAndAnswers.requestbody.TokenLogInUser;
import com.work.testchat.RequestsAndAnswers.responses.EventResponse;
import com.work.testchat.RequestsAndAnswers.responses.LogInUserResponse;
import com.work.testchat.RequestsAndAnswers.responses.Response;
import com.work.testchat.RequestsAndAnswers.responses.TokenLogInResponse;
import com.work.testchat.databinding.FragmentAccountBinding;
import com.work.testchat.interfaces.AccountChanges;
import com.work.testchat.localDb.ApplicationData;
import com.work.testchat.localDb.User;

import java.net.URI;
import java.net.URISyntaxException;

import io.realm.Realm;
import tech.gusavila92.websocketclient.WebSocketClient;

public class AccountFragment extends Fragment {
    FragmentAccountBinding binder;
    View v;
    WebSocketClient socket = GlobalObjects.socket;
    Realm realm;
    boolean isLoggedIn;
    ApplicationData applicationData;
    AccountChanges accountChanges;
    ActivityResultContracts.StartActivityForResult forResult = new ActivityResultContracts.StartActivityForResult();
    ActivityResultLauncher<Intent> registerIntent = registerForActivityResult(forResult, result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            logIn(result.getData().getStringExtra("login"), result.getData().getStringExtra("password"));
        }
    });

    @Override
    public void onAttach(@NonNull Context context) {
        accountChanges = (AccountChanges) getActivity();
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binder = FragmentAccountBinding.inflate(inflater, container, false);
        v = binder.getRoot();
        Valuation();
        return v;
    }

    @Override
    public void onResume() {
        GlobalObjects.checkResult = this::checkResult;
        super.onResume();
    }

    void Valuation() {
        GlobalObjects.checkResult = this::checkResult;
        GlobalObjects.onReconnection = () -> {};
        realm = Realm.getDefaultInstance();
        applicationData = realm.where(ApplicationData.class).findFirst();
        binder.loginButton.setOnClickListener(view -> logIn(binder.userLogin.getText().toString(),
                binder.userPassword.getText().toString()));
        binder.registerButton.setOnClickListener(view -> registerIntent.launch(new Intent(getContext(), RegistrationActivity.class)));
        binder.logoutButton.setOnClickListener(view -> logOut());
        if (isLoggedIn) {
            Authentication();
        } else if (applicationData.lastLogged != null) {
            Authentication();
        }
    }

    void logIn(String login, String password) {
        Gson gson = new Gson();
        LogInUser data = new LogInUser(login, password);
        Request request = new Request("auth-login-request", data);
        socket.send(gson.toJson(request));
    }

    void logOut() {
        realm.executeTransaction(realm -> {
            applicationData.lastLogged = null;
        });
        binder.logoutView.setVisibility(View.GONE);
        binder.loginView.setVisibility(View.VISIBLE);
        GlobalObjects.user = null;
        accountChanges.onAccountChanged();
        isLoggedIn = false;
    }

    void checkResult() {
        Gson gson = new Gson();
        EventResponse response = gson.fromJson(GlobalObjects.recievedMessage, EventResponse.class);
        String metaMessage = response.data.meta.message;
        if (response.data.meta.status == 400) {
            Toast.makeText(getContext(), metaMessage, Toast.LENGTH_SHORT).show();
        } else if (response.data.meta.status == 200) {
            if (response.event.equals("auth-login-response")) {
                LogInUserResponse logInUserResponse = gson.fromJson(GlobalObjects.recievedMessage, LogInUserResponse.class);
                updateInternal(logInUserResponse);
            } else if (response.event.equals("auth-token-response")) {
                TokenLogInResponse tokenLogInResponse = gson.fromJson(GlobalObjects.recievedMessage, TokenLogInResponse.class);
                if (tokenLogInResponse.data.data.auth) {
                    Authentication();
                } else {
                    Toast.makeText(getContext(), metaMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void updateInternal(Response response) {
        realm.executeTransaction(realm -> {
            User user = realm.createObject(User.class);
            user.userToken = ((LogInUserResponse) response).data.data.token;
            user.userId = ((LogInUserResponse) response).data.data.user.id;
            user.userName = ((LogInUserResponse) response).data.data.user.name;
            applicationData.lastLogged = user;
            applicationData.users.add(user);
        });
        Authentication();
    }

    void Authentication() {
        binder.logoutView.setVisibility(View.VISIBLE);
        binder.loginView.setVisibility(View.GONE);
        binder.loggedInUserName.setText(applicationData.lastLogged.userName);
        GlobalObjects.user = applicationData.lastLogged;
        accountChanges.onAccountChanged();
        isLoggedIn = true;
    }
}