package com.work.testchat.ui.chats;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.work.testchat.RequestsAndAnswers.requestbody.TokenLogInUser;
import com.work.testchat.functionalActivities.AddNewChatActivity;
import com.work.testchat.GlobalObjects;
import com.work.testchat.functionalActivities.JoinChatActivity;
import com.work.testchat.RequestsAndAnswers.Request;
import com.work.testchat.RequestsAndAnswers.requestbody.CreateRoom;
import com.work.testchat.RequestsAndAnswers.responses.CreateRoomResponse;
import com.work.testchat.RequestsAndAnswers.responses.EventResponse;
import com.work.testchat.databinding.FragmentListOfChatsBinding;
import com.work.testchat.interfaces.ChatListener;
import com.work.testchat.R;
import com.work.testchat.localDb.Chat;
import com.work.testchat.localDb.User;

import java.util.List;

import io.realm.Realm;
import tech.gusavila92.websocketclient.WebSocketClient;

public class ListOfChatsFragment extends Fragment implements ChatListener {
    FragmentListOfChatsBinding binder;
    View v;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Realm realm;
    User user;
    WebSocketClient socket = GlobalObjects.socket;
    String withWhom;
    Gson gson = new Gson();
    ActivityResultContracts.StartActivityForResult forResult = new ActivityResultContracts.StartActivityForResult();
    ActivityResultLauncher<Intent> newChatLauncher = registerForActivityResult(forResult, new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                withWhom = result.getData().getStringExtra("chatName");
                CreateRoom data = new CreateRoom();
                Request request = new Request("room-create-request", data);
                socket.send(gson.toJson(request));
            }
        }
    });
    ActivityResultLauncher<Intent> joinChatLauncher = registerForActivityResult(forResult, new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Chat chat = new Chat();
                chat.id = result.getData().getStringExtra("chatId");
                chat.withWhom = result.getData().getStringExtra("chatName");
                realm.executeTransaction(realm -> user.chats.add(chat));
                ChatFragment fragment = new ChatFragment();
                fragment.chat = chat;
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.ChatsContainer, fragment);
                fragmentTransaction.commitNow();
            }
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binder = FragmentListOfChatsBinding.inflate(inflater, container, false);
        v = binder.getRoot();
        Initialisation();
        Valuation();
        return v;
    }

    void Initialisation() {
        fragmentManager = getChildFragmentManager();
    }

    void Valuation() {
        List<Fragment> fragments = fragmentManager.getFragments();
        fragmentTransaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {
            fragmentTransaction.remove(fragments.get(i));
        }
        fragmentTransaction.commitNowAllowingStateLoss();
        GlobalObjects.checkResult = this::checkResult;
        GlobalObjects.onReconnection = this::onReconnection;
        realm = Realm.getDefaultInstance();
        user = GlobalObjects.user;
        if (user == null) {
            binder.createFab.setVisibility(View.GONE);
            binder.joinFab.setVisibility(View.GONE);
        } else {
            binder.textAuthenticationRequired.setVisibility(View.GONE);
            binder.createFab.setOnClickListener(view -> {
                newChatLauncher.launch(new Intent(getContext(), AddNewChatActivity.class));
            });
            binder.joinFab.setOnClickListener(view -> {
                joinChatLauncher.launch(new Intent(getContext(), JoinChatActivity.class));
            });
            restoreChats();
        }
    }

    void checkResult() {
        EventResponse response = gson.fromJson(GlobalObjects.recievedMessage, EventResponse.class);
        if (response.data.meta.status == 200) {
            if (response.event.equals("room-create-response")) {
                createNewChat(gson.fromJson(GlobalObjects.recievedMessage, CreateRoomResponse.class));
            }
        } else {
            Toast.makeText(getContext(), response.data.meta.message, Toast.LENGTH_SHORT).show();
        }
    }

    void restoreChats() {
        for (int i = 0; i < user.chats.size(); i++) {
            ChatFragment fragment = new ChatFragment();
            fragment.chat = user.chats.get(i);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.ChatsContainer, fragment);
            fragmentTransaction.commitNow();
        }
    }

    void createNewChat(CreateRoomResponse response) {
        /* = ;*/
        binder.textAuthenticationRequired.setVisibility(View.VISIBLE);
        binder.textAuthenticationRequired.setText(response.data.meta.message);
        Chat chat = new Chat();
        chat.id = response.data.data.roomId;
        chat.withWhom = withWhom;
        realm.executeTransaction(realm -> user.chats.add(chat));
        ChatFragment fragment = new ChatFragment();
        fragment.chat = chat;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.ChatsContainer, fragment);
        fragmentTransaction.commitNow();
    }

    @Override
    public void onChatRemove(ChatFragment chatFragment, Chat chat) {
        realm.executeTransaction(realm -> user.chats.remove(chat));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(chatFragment);
        fragmentTransaction.commitNow();
    }

    void tokenLogIn() {
        TokenLogInUser data = new TokenLogInUser(GlobalObjects.user.userToken);
        Request request = new Request("auth-token-request", data);
        socket.send(gson.toJson(request));
    }

    void onReconnection () {
        tokenLogIn();
    }
}