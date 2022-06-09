package com.work.testchat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.work.testchat.RequestsAndAnswers.Message;
import com.work.testchat.RequestsAndAnswers.Request;
import com.work.testchat.RequestsAndAnswers.requestbody.GetChatUsers;
import com.work.testchat.RequestsAndAnswers.requestbody.GetMessages;
import com.work.testchat.RequestsAndAnswers.requestbody.JoinRoom;
import com.work.testchat.RequestsAndAnswers.requestbody.SendMessage;
import com.work.testchat.RequestsAndAnswers.requestbody.TokenLogInUser;
import com.work.testchat.RequestsAndAnswers.responses.EventResponse;
import com.work.testchat.RequestsAndAnswers.responses.GetChatUsersResponse;
import com.work.testchat.RequestsAndAnswers.responses.GetMessagesResponse;
import com.work.testchat.RequestsAndAnswers.responses.SendMessageResponse;
import com.work.testchat.RequestsAndAnswers.responses.TokenLogInResponse;
import com.work.testchat.databinding.ActivityChatBinding;
import com.work.testchat.localDb.ApplicationData;
import com.work.testchat.localDb.User;
import com.work.testchat.ui.chats.MyMessageFragment;
import com.work.testchat.ui.chats.OtherMessageFragment;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;

import io.realm.Realm;
import tech.gusavila92.websocketclient.WebSocketClient;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binder;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    WebSocketClient socket = GlobalObjects.socket;
    String id, withWhom, token;
    Gson gson = new Gson();
    ApplicationData applicationData;
    User user;
    boolean isFirstTime = true, connectedToRoom = false;
    com.work.testchat.RequestsAndAnswers.User[] users;
    Message[] messages;

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
        binder = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        GlobalObjects.checkResult = this::checkResult;
        GlobalObjects.onReconnection = this::onReconnection;
        withWhom = getIntent().getStringExtra("chatName");
        id = getIntent().getStringExtra("chatId");
        applicationData = Realm.getDefaultInstance().where(ApplicationData.class).findFirst();
        user = applicationData.lastLogged;
        token = user.userToken;
        getSupportActionBar().setTitle(getString(R.string.chat_with, withWhom));
        fragmentManager = getSupportFragmentManager();
        binder.sendButton.setOnClickListener(view -> {
            if (isConnectedToInternet() && connectedToRoom) {
                sendMessage();
            } else {
                Toast.makeText(this, R.string.conection_required, Toast.LENGTH_SHORT).show();
            }
        });
        binder.scrollDownFab.setOnClickListener(view -> {
            binder.chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        });
        joinChat();
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) ChatActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        connectedToRoom = false;
        return false;
    }

    void joinChat() {
        JoinRoom data = new JoinRoom(id);
        Request request = new Request("room-join-request", data);
        socket.send(gson.toJson(request));
    }

    void getChatUsers() {
        GetChatUsers data = new GetChatUsers(id);
        Request request = new Request("get-chat-users-request", data);
        socket.send(gson.toJson(request));
    }

    void getMessages() {
        GetMessages data = new GetMessages(id);
        Request request = new Request("get-chat-messages-request", data);
        socket.send(gson.toJson(request));
    }

    void restoreMessages(GetMessagesResponse response) {
        messages = response.data.data.messages;
        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            Fragment fragment;
            if (message.userId.equals(user.userId)) {
                fragment = new MyMessageFragment(message.message, message.timestamp * 1000, user.userName);
            } else {
                fragment = new OtherMessageFragment(message.message, message.timestamp * 1000, getUserName(message.userId));
            }
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.MessagesContainer, fragment);
            fragmentTransaction.commitNowAllowingStateLoss();
        }
        binder.chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    void updateMessages(GetMessagesResponse response) {
        Message[] newMessages = response.data.data.messages;
        for (int i = messages.length; i < newMessages.length; i++) {
            Message message = newMessages[i];
            Fragment fragment;
            if (message.userId.equals(user.userId)) {
                fragment = new MyMessageFragment(message.message, message.timestamp * 1000, user.userName);
            } else {
                fragment = new OtherMessageFragment(message.message, message.timestamp * 1000, getUserName(message.userId));
            }
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.MessagesContainer, fragment);
            fragmentTransaction.commitNowAllowingStateLoss();
        }
        messages = newMessages;
        binder.chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    String getUserName(String id) {
        for (int i = 0; i < users.length; i++) {
            if (users[i].id.equals(id)) {
                return users[i].name;
            }
        }
        return getString(R.string.unknown);
    }

    void readMessage(SendMessageResponse response) {
        Message message = response.data.data.message;
        Fragment fragment;
        if (message.chatId.equals(id)) {
            fragment = new OtherMessageFragment(message.message, message.timestamp * 1000, getUserName(message.userId));
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.MessagesContainer, fragment);
            fragmentTransaction.commitNowAllowingStateLoss();
            binder.chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
        Message[] newMessages = Arrays.copyOf(messages, messages.length + 1);
        newMessages[messages.length] = message;
        messages = newMessages;
    }

    void sendMessage() {
        SendMessage data = new SendMessage(id, binder.messageEditText.getText().toString());
        Request request = new Request("send-message-request", data);
        socket.send(gson.toJson(request));
        MyMessageFragment myMessageFragment = new MyMessageFragment(binder.messageEditText.getText().toString(), Calendar.getInstance().getTimeInMillis(), user.userName);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.MessagesContainer, myMessageFragment);
        fragmentTransaction.commitNowAllowingStateLoss();
        binder.messageEditText.setText(null);
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        binder.chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        Message[] newMessages = Arrays.copyOf(messages, messages.length + 1);
        newMessages[messages.length] = new Message(id, user.userId, binder.messageEditText.getText().toString(),
                Calendar.getInstance().getTimeInMillis() / 1000);
        messages = newMessages;
    }

    void checkResult() {
        EventResponse response;
        try {
            response = gson.fromJson(GlobalObjects.recievedMessage, EventResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            response = new EventResponse(400, "something went wrong");
        }
        if (response.data.meta.status == 200) {
            if (response.event.equals("room-join-response")) {
                getChatUsers();
                connectedToRoom = true;
            } else if (response.event.equals("get-chat-messages-response")) {
                GetMessagesResponse getMessagesResponse = gson.fromJson(GlobalObjects.recievedMessage, GetMessagesResponse.class);
                if (isFirstTime) {
                    restoreMessages(getMessagesResponse);
                    isFirstTime = false;
                } else {
                    updateMessages(getMessagesResponse);
                }
            } else if (response.event.equals("send-message-response")) {
                readMessage(gson.fromJson(GlobalObjects.recievedMessage, SendMessageResponse.class));
            } else if (response.event.equals("get-chat-users-response")) {
                GetChatUsersResponse getChatUsersResponse = gson.fromJson(GlobalObjects.recievedMessage, GetChatUsersResponse.class);
                users = getChatUsersResponse.data.data.users;
                getMessages();
            } else if (response.event.equals("auth-token-response")) {
                TokenLogInResponse tokenLogInResponse = gson.fromJson(GlobalObjects.recievedMessage, TokenLogInResponse.class);
                if (tokenLogInResponse.data.data.auth) {
                    joinChat();
                }
            }
        } else {
            binder.exceptionTextView.setVisibility(View.VISIBLE);
            binder.messageLayout.setVisibility(View.GONE);
            binder.scrollDownFab.setVisibility(View.GONE);
            Toast.makeText(this, response.data.meta.message, Toast.LENGTH_SHORT).show();
        }
    }

    void tokenLogIn() {
        TokenLogInUser data = new TokenLogInUser(token);
        Request request = new Request("auth-token-request", data);
        socket.send(gson.toJson(request));
    }

    void onReconnection() {
        Toast.makeText(this, R.string.reconnected, Toast.LENGTH_SHORT).show();
        tokenLogIn();
    }
}