package com.work.testchat.ui.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.work.testchat.ChatActivity;
import com.work.testchat.R;
import com.work.testchat.databinding.FragmentChatBinding;
import com.work.testchat.interfaces.ChatListener;
import com.work.testchat.localDb.Chat;


public class ChatFragment extends Fragment {
    View v;
    FragmentChatBinding binder;
    public Chat chat;
    ChatListener chatListener;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.chat_main_layout) {

            } else if (view.getId() == R.id.chat_remove_button) {

            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        chatListener = (ChatListener) getParentFragment();
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentChatBinding.inflate(inflater, container, false);
        v = binder.getRoot();
        Valuation();
        return v;
    }

    void Valuation() {
        binder.chatRemoveButton.setOnClickListener(view -> disappear());
        binder.chatMainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatId", chat.id);
            intent.putExtra("chatName", chat.withWhom);
            startActivity(intent);
        });
        binder.nameTextView.setText(getString(R.string.chat_with, chat.withWhom)
                .concat(", id = ")
                .concat(chat.id));
    }

    void disappear() {
        AnimationSet animationSet = new AnimationSet(false);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.disappearing);
        animationSet.addAnimation(animation);
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) binder.chatMainLayout.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        binder.chatMainLayout.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = binder.chatMainLayout.getMeasuredHeight();
        Animation collapseAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                binder.chatMainLayout.getLayoutParams().height = interpolatedTime == 1
                        ? 0
                        : targetHeight - (int) (targetHeight * interpolatedTime);
                binder.chatMainLayout.requestLayout();
            }
        };
        collapseAnimation.setDuration(300);
        collapseAnimation.setStartOffset(300);
        animationSet.addAnimation(collapseAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binder.chatMainLayout.setVisibility(View.GONE);
                chatListener.onChatRemove(ChatFragment.this, chat);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binder.chatMainLayout.startAnimation(animationSet);
    }
}