package com.example.euc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText emailEditText, passwordEditText;
    private TextView userDetailsTextView;

    Context context;
//    FirebaseApp.initializeApp(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        //
//        SharedPreferences preferences = getSharedPreferences("my_app", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("auth_token", authToken);
//        editor.apply();

        //

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("UsersList");
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        userDetailsTextView = findViewById(R.id.userDetailsTextView);


        Button loginButton = findViewById(R.id.loginButton);
        TextView registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = HashUtils.hashPassword(password);

        mAuth.signInWithEmailAndPassword(email, hashedPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        getUserData(firebaseUser.getUid());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getUserData(String uid) {
        DatabaseReference userRef = mDatabase.child(uid.replace(".", "_"));
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userDetailsTextView.setText("User email: " + user.getEmail());
                } else {
                    Toast.makeText(LoginActivity.this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}