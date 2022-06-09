package com.work.testchat.ui.chats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.work.testchat.R;
import com.work.testchat.databinding.FragmentOtherMessageBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OtherMessageFragment extends Fragment {
    FragmentOtherMessageBinding binder;
    View v;
    SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, kk : mm", Locale.getDefault());
    public String message, userName;
    public long time;

    public OtherMessageFragment(String message, long time, String userName) {
        this.message = message;
        this.time = time;
        this.userName = userName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentOtherMessageBinding.inflate(inflater, container, false);
        v = binder.getRoot();
        Valuation();
        return v;
    }

    void Valuation() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        binder.messageTextView.setText(message);
        binder.timeTextView.setText(format.format(calendar.getTime()));
        binder.userNameTextView.setText(userName);
    }
}