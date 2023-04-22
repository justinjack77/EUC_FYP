package com.example.euc;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseDatabase mDatabase1;
    private DatabaseReference mMessagesRef;
    private String receiverId;
    private String receiverEmail;

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private EditText messageEditText;
    private ImageView sendButton;

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        androidx.appcompat.widget.Toolbar backButton = findViewById(R.id.back_device_button);
        backButton.setOnClickListener(v -> finish());

        if (getIntent().hasExtra("receiverId")) {
            receiverId = getIntent().getStringExtra("receiverId");
        } else {
            finish();
            return;
        }

        String currentUserId = mAuth.getCurrentUser().getEmail().replace(".","_");
        DatabaseReference currentUserRef = mDatabase.getReference("UsersList").child(currentUserId);

        mMessagesRef = currentUserRef.child("AddUsers").child(receiverId.replace(".", "_")).child("Messages");

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter = new MessagesAdapter(currentUserId);
        messagesRecyclerView.setAdapter(messagesAdapter);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(view -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(messageText)) {
                sendMessage(messageText);
                messageEditText.setText("");
            }
        });

        listenForMessages();
    }



    private void sendMessage(String messageText) {
        String currentUserId = mAuth.getCurrentUser().getEmail().replace(".","_");
        Message message = new Message(currentUserId, receiverId, messageText, System.currentTimeMillis());


        DatabaseReference currentUserAddUsersRef = mDatabase.getReference("UsersList").child(currentUserId).child("AddUsers").child(receiverId.replace(".", "_"));
        DatabaseReference receiverUserAddUsersRef = mDatabase.getReference("UsersList").child(receiverId.replace(".", "_")).child("AddUsers").child(currentUserId.replace(".", "_"));

        DatabaseReference currentUserMessagesRef = currentUserAddUsersRef.child("Messages");
        DatabaseReference receiverMessagesRef = receiverUserAddUsersRef.child("Messages");

        // Push message to current user's Messages and receiver's Messages nodes
        currentUserMessagesRef.push().setValue(message);
        receiverMessagesRef.push().setValue(message);

        // Add the receiver's email to current user's AddUsers node if not already present
        DatabaseReference receiverUserRef = mDatabase.getReference("UsersList").child(receiverId.replace(".", "_"));
        DatabaseReference currentUserRef = mDatabase.getReference("UsersList").child(currentUserId);

        receiverUserRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String receiverEmail = snapshot.getValue(String.class);
                    currentUserRef.child("AddUsers").child(receiverId.replace(".", "_")).child("email").setValue(receiverEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        // Add the current user's email to receiver's AddUsers node if not already present
        currentUserRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String currentUserEmail = snapshot.getValue(String.class);
                    DatabaseReference receiverUserAddUsersRef = mDatabase.getReference("UsersList").child(receiverId.replace(".", "_")).child("AddUsers").child(currentUserId.replace(".", "_"));
                    receiverUserAddUsersRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                receiverUserAddUsersRef.child("email").setValue(currentUserEmail);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private void listenForMessages() {
        mMessagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    messagesAdapter.addMessage(message);
                    messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference senderMessageRef = FirebaseDatabase.getInstance()
                .getReference("UsersList")
                .child(receiverId.replace(".", "_"))
                .child("AddUsers")
                .child(mAuth.getCurrentUser().getUid())
                .child("Messages");

        senderMessageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    messagesAdapter.addMessage(message);
                    messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}

