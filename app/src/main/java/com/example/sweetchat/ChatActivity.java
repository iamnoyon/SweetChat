package com.example.sweetchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID, messageReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReceiverID = getIntent().getExtras().get("chat_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("chat_user_name").toString();


        Toast.makeText(this, messageReceiverID, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, messageReceiverName, Toast.LENGTH_SHORT).show();
    }
}
