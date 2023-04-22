package com.example.euc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;
    private RecyclerView usersRecyclerView;
    private UsersAdapter usersAdapter;
    private EditText searchEditText;
    private ImageView addUserImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("UsersList");

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersAdapter = new UsersAdapter();
        usersRecyclerView.setAdapter(usersAdapter);

        addUserImageView = findViewById(R.id.addUserImageView);
        addUserImageView.setOnClickListener(view -> showAddUserDialog());

        searchEditText = findViewById(R.id.searchEditText);
        androidx.appcompat.widget.Toolbar backButton = findViewById(R.id.back_device_button);
        backButton.setOnClickListener(v -> finish());
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usersAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fetchUsers();

    }

    private void fetchUsers() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getEmail();
            String receiverId = getIntent().getStringExtra("receiverId");
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UsersList").child(currentUserId.replace(".", "_")).child("AddUsers");


            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<User> users = new ArrayList<>();
                    String currentUserEmail = currentUser.getEmail();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String email = userSnapshot.getKey().replace("_", ".");
                        String password = userSnapshot.child("password").getValue(String.class);
                        User user = new User(email, password);
                        if (user != null && user.getEmail() != null && !user.getEmail().equals(currentUserEmail)) {
                            users.add(user);
                        }
                    }
                    usersAdapter.setUsers(users);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(UsersListActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            // Open the search activity or show the search bar
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> implements Filterable {
        private List<User> users = new ArrayList<>();
        private List<User> usersFiltered = new ArrayList<>();

        public void setUsers(List<User> users) {
            this.users = users;
            this.usersFiltered = new ArrayList<>(users);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.user_item, parent, false);
            return new UserViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = usersFiltered.get(position);
            holder.userDisplayName.setText(user.getEmail());
            holder.itemView.setOnClickListener(view -> {
                Intent chatIntent = new Intent(UsersListActivity.this, ChatActivity.class);
                chatIntent.putExtra("receiverId", user.getEmail());
                startActivity(chatIntent);
            });
        }

        @Override
        public int getItemCount() {
            return usersFiltered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String query = constraint.toString().toLowerCase().trim();
                    List<User> filteredUsers = new ArrayList<>();

                    if (query.isEmpty()) {
                        filteredUsers.addAll(users);
                    } else {
                        for (User user : users) {
                            if (user.getEmail().toLowerCase().contains(query)) {
                                filteredUsers.add(user);
                            }
                        }
                    }

                    FilterResults results = new FilterResults();
                    results.values = filteredUsers;
                    return results;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    usersFiltered.clear();
                    usersFiltered.addAll((List<User>) results.values);
                    notifyDataSetChanged();
                }
            };
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView userDisplayName;

            UserViewHolder(@NonNull View itemView) {
                super(itemView);
                userDisplayName = itemView.findViewById(R.id.userDisplayName);
            }
        }
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add User");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Enter user's email");
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();
                if (!email.isEmpty()) {
                    addUserToFirebase(email);
                } else {
                    Toast.makeText(UsersListActivity.this, "Email cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addUserToFirebase(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UsersList").child(user.getEmail().replace(".", "_")).child("AddUsers").child(email.replace(".", "_"));
        DatabaseReference usersRef1 = FirebaseDatabase.getInstance().getReference("UsersList").child(email.replace(".", "_")).child("AddUsers").child(user.getEmail().replace(".", "_"));
//
//        String userId = usersRef.push().getKey();
        String userId = usersRef.getKey();
//        DatabaseReference receiverUserRef = mDatabase.getReference("UsersList").child(receiverId.replace(".", "_"));
        User newUser = new User(email, ""); // Assuming an empty password field, you can update this to store other information.

        String senderEmailKey = user.getEmail().replace(".", "_");
        User newUser1 = new User(email, "");
        String userId1 = usersRef1.getKey();


        if (userId != null) {
            usersRef.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UsersListActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                        // Refresh the users list
                        fetchUsers();
                    } else {
                        Toast.makeText(UsersListActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            usersRef1.child(userId1).setValue(newUser1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UsersListActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                        // Refresh the users list
                        fetchUsers();
                    } else {
                        Toast.makeText(UsersListActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(UsersListActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
        }
    }

}