package com.work.testchat.functionalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.work.testchat.R;
import com.work.testchat.databinding.ActivityAddNewChatBinding;

public class AddNewChatActivity extends AppCompatActivity {
    ActivityAddNewChatBinding binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = ActivityAddNewChatBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
        binder.cancelButton.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        binder.addButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("chatName", binder.userNameEditText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });
    }


}