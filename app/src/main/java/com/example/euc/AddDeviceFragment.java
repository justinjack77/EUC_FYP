package com.example.euc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceFragment extends Fragment {

    public static DatabaseReference mDatabase;

    public static FirebaseAuth mAuth;
    private TextView userDetailsTextView;
    private RecyclerView deviceRecyclerView;
    private DeviceAdapter deviceAdapter;

    private List<Device> deviceList = new ArrayList<>();

    // Add search input EditText field
    private EditText searchInput;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        deviceRecyclerView = view.findViewById(R.id.device_recycler_view);
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceAdapter = new DeviceAdapter(getContext(), deviceList);
        // ... (rest of the code)
        deviceRecyclerView.setAdapter(deviceAdapter);

        // Add SpacingItemDecoration to your RecyclerView
        int spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        deviceRecyclerView.addItemDecoration(new DeviceAdapter.SpacingItemDecoration(spacing));

        // Initialize search input EditText
        searchInput = view.findViewById(R.id.searchInput);

        androidx.appcompat.widget.Toolbar backButton = view.findViewById(R.id.back_device_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Add TextWatcher to search input
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                deviceAdapter.filter(s.toString());
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //Add new device to Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("UsersList").child(user.getEmail().replace(".", "_")).child("DeviceList");
        userDetailsTextView = view.findViewById(R.id.userDetailsTextView);

        deviceRecyclerView = view.findViewById(R.id.device_recycler_view);
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceAdapter = new DeviceAdapter(getContext(), deviceList);
        deviceAdapter.setItemClickListener(new DeviceAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Device device = deviceList.get(position);
                Intent intent = new Intent(getContext(), DeviceDetailActivity.class);
                intent.putExtra("deviceId", device.getKey());
                startActivity(intent);
            }
        });
        deviceRecyclerView.setAdapter(deviceAdapter);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deviceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Device device = snapshot.getValue(Device.class);
                    device.setKey(snapshot.getKey());
                    device.setDeviceRef(snapshot.getRef());
                    deviceList.add(device);
                }
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load devices.", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(valueEventListener);

        Button addDeviceButton = view.findViewById(R.id.new_device_button);
        addDeviceButton.setOnClickListener(v -> showAddDeviceDialog());

        if (user != null) {
            String uid = user.getUid();
            userDetailsTextView.setText("Manage Your Devices \n Logged in as: \n" + user.getEmail() + "\n" +
                    "UID:\n" + uid);
        }
    }

    private void showAddDeviceDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_device, null);
        EditText deviceNameEditText = view.findViewById(R.id.dialog_device_name_edit_text);
        EditText ESP_IP_TEXT_VIEW = view.findViewById(R.id.dialog_esp_text_view);
        Spinner deviceTypeSpinner = view.findViewById(R.id.dialog_device_type_spinner);
        ArrayAdapter<CharSequence> deviceTypeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.device_types, android.R.layout.simple_spinner_item);
        deviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceTypeSpinner.setAdapter(deviceTypeAdapter);

        // GPIO pins spinner
        Spinner gpioPinSpinner = view.findViewById(R.id.dialog_device_gpio_pin_spinner);
        ArrayAdapter<CharSequence> gpioPinAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gpio_pins, android.R.layout.simple_spinner_item);
        gpioPinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gpioPinSpinner.setAdapter(gpioPinAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Add Device")
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String deviceName = deviceNameEditText.getText().toString().trim();

                        String deviceType = deviceTypeSpinner.getSelectedItem().toString().trim();

                        String gpioPin = gpioPinSpinner.getSelectedItem().toString().trim();
                        String ESP_IP = ESP_IP_TEXT_VIEW.getText().toString().trim();
                        addDevice(deviceName, deviceType, gpioPin, ESP_IP);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void addDevice(String deviceName, String deviceType, String gpioPin, String ESP_IP) {
        if (deviceName.isEmpty() || deviceType.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in both fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = mDatabase.push().getKey();
        Device device = new Device(key, deviceName, deviceType, mAuth.getCurrentUser().getUid(), false, gpioPin, ESP_IP);
        mDatabase.child(key).setValue(device)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Device added successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add device.", Toast.LENGTH_SHORT).show());
    }
}
