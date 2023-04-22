package com.example.euc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingFragment extends Fragment {


    private String TAG="Form Setting: ";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView emailTextView;
    private TextView uidTextView;
    private Button logoutButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("UsersList");
        emailTextView = view.findViewById(R.id.emailDetailView);
        uidTextView = view.findViewById(R.id.uidDetailView);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Find the ImageViews by their IDs
        ImageView facebookIcon = view.findViewById(R.id.facebook_icon);
        ImageView twitterIcon = view.findViewById(R.id.twitter_icon);
        ImageView instagramIcon = view.findViewById(R.id.instagram_icon);
        ImageView whatsappIcon = view.findViewById(R.id.whatsapp_icon);

// Set onClick listeners for each ImageView
        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Facebook app or website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                startActivity(intent);
            }
        });

        twitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Twitter app or website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com"));
                startActivity(intent);
            }
        });

        instagramIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Instagram app or website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"));
                startActivity(intent);
            }
        });

        whatsappIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open WhatsApp app or website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.whatsapp.com"));
                startActivity(intent);
            }
        });


        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            getUserData(firebaseUser.getUid());
            emailTextView.setText("Email: " + firebaseUser.getEmail());
            uidTextView.setText("UID: " + firebaseUser.getUid());

            logoutButton.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            });
        } else {
            Toast.makeText(getActivity(), "No user logged in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        androidx.appcompat.widget.Toolbar backButton = view.findViewById(R.id.back_device_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getUserData(String uid) {
        DatabaseReference userRef = mDatabase.child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // Do something with the user data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error retrieving user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
