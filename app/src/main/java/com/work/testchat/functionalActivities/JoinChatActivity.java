package com.work.testchat.functionalActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.work.testchat.R;
import com.work.testchat.databinding.ActivityJoinChatBinding;

public class JoinChatActivity extends AppCompatActivity {
    ActivityJoinChatBinding binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = ActivityJoinChatBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
        binder.cancelButton.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        binder.addButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("chatName", binder.userNameEditText.getText().toString());
            intent.putExtra("chatId", binder.userIdEditText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}