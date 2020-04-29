package com.unfyd.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.unfyd.unfydChatBot.Activity.MainFragment;
import com.unfyd.unfydChatBot.Service.ChatBotService;
import com.unfyd.unfydChatBot.Service.ChatService;
import com.unfyd.unfydChatBot.preferences.UserPreferences;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
Button btnopenChat;
    public static String pkgName = null;
    public static Socket mSocket = null;
    public static String apiUrl = null;
    private Boolean isConnected = false;
    public static String mUsername = "android-mobile";
    EditText meditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        meditText=findViewById(R.id.editText);
       // FirebaseApp.initializeApp(getApplicationContext());
    }

    public void OpenChat(View v)
    {
        try {

            UserPreferences.setPreferences(this,"colorCode",meditText.getText().toString());
            ChatService.instance().start(this,  false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
