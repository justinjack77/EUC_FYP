package com.example.euc;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText regEmailEditText, regPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("UsersList");
//        mDatabase.setValue("userlist");
        regEmailEditText = findViewById(R.id.regEmailEditText);
        regPasswordEditText = findViewById(R.id.regPasswordEditText);

        Button registerNewUserButton = findViewById(R.id.registerNewUserButton);
        registerNewUserButton.setOnClickListener(v -> registerUser());


        TextView loginButton = findViewById(R.id.LoginText);


        loginButton.setOnClickListener(v -> {
            Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(LoginIntent);
        });

    }

    private void registerUser() {
        String email = regEmailEditText.getText().toString().trim();
        String password = HashUtils.hashPassword(regPasswordEditText.getText().toString().trim());

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uidWithEmail = email.replace(".", "_");
                        User user = new User(email, password);
                        mDatabase.child(uidWithEmail).setValue(user);
                        Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


