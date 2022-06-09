package com.work.testchat;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.work.testchat.localDb.User;

import java.net.URI;
import java.net.URISyntaxException;

import tech.gusavila92.websocketclient.WebSocketClient;

public class GlobalObjects {
    public static Handler handler = new Handler(Looper.getMainLooper());
    public static WebSocketClient socket;
    public static User user;

    public Context context;

    public static Runnable checkResult, onReconnection;
    public static String recievedMessage;

    public static void createWebSocketConnection() {
        URI uri;
        try {
            uri = new URI("ws://74.208.18.242:8081/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        socket = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                handler.post(() -> {
                    try {
                        onReconnection.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onTextReceived(String message) {
                recievedMessage = message;
                handler.post(() -> {
                    try {
                        checkResult.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {

            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onCloseReceived() {

            }
        };
        socket.setConnectTimeout(5000);
        socket.enableAutomaticReconnection(5000);
        socket.connect();
    }
}
