package com.work.testchat.interfaces;

import com.work.testchat.localDb.Chat;
import com.work.testchat.ui.chats.ChatFragment;

public interface ChatListener {
    void onChatRemove (ChatFragment chatFragment, Chat chat);
}
