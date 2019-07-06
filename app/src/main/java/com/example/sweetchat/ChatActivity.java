package com.example.sweetchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolBar;
    private ImageButton SendMessageButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth  = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        messageReceiverID = getIntent().getExtras().get("chat_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("chat_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("chat_user_image").toString();

        Initialization();


        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
    }

    private void Initialization() {

        chatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);
    }

    private void SendMessage() {
        String MessageText = MessageInputText.getText().toString();

        if(MessageText.isEmpty()){
            Toast.makeText(this, "First Write a Message...", Toast.LENGTH_SHORT).show();
        }
        else{
            String MessageSenderRef = "Message/" + messageSenderID + "/" +messageReceiverID;
            String MessageReceiverRef = "Message/" + messageReceiverID + "/" +messageSenderID;

            DatabaseReference UserMessageKeyRef = RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();

            String MessagePushID = UserMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", MessageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(MessageSenderRef + "/" + MessagePushID, messageTextBody);
            messageBodyDetails.put(MessageReceiverRef + "/" + MessagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message Send Successfully.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ChatActivity.this, "Error Occurs, Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });


        }

    }
}
