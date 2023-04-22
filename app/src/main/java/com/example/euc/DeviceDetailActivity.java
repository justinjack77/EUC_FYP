package com.example.euc;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeviceDetailActivity extends AppCompatActivity {

    private TextView deviceNameTextView, deviceTypeTextView, uidTextView, gpioPinTextView,ESP_IP;
    private Switch deviceSwitch;
    private androidx.appcompat.widget.Toolbar deleteButton, editButton, backButton;
    private DatabaseReference deviceRef;
    private String deviceId;
    private FirebaseAuth mAuth;
    private String ESP_IP_ADDRESS = "default_ip_address";
    private Context context;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        deviceNameTextView = findViewById(R.id.device_name_text_view);
        deviceTypeTextView = findViewById(R.id.device_type_text_view);
        uidTextView = findViewById(R.id.uid_text_view);
        gpioPinTextView = findViewById(R.id.gpio_pin_spinner);
        deviceSwitch = findViewById(R.id.device_switch);
        deleteButton = findViewById(R.id.delete_device_button);
        editButton = findViewById(R.id.edit_device_button);
        backButton = findViewById(R.id.back_device_button);
        ESP_IP = findViewById(R.id.detail_esp_ip);
//        ESP_IP_ADDRESS = ESP_IP.toString().trim();


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        deviceId = getIntent().getStringExtra("deviceId");
        deviceRef = FirebaseDatabase.getInstance().getReference("UsersList").child(user.getUid()).child("DeviceList").child(deviceId);

        deviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Device device = snapshot.getValue(Device.class);
                    if (device != null) {
                        deviceNameTextView.setText(device.getName());
                        deviceTypeTextView.setText(device.getType());
                        uidTextView.setText(device.getUid());
                        gpioPinTextView.setText(device.getGpioPin());
                        ESP_IP.setText(device.getESP_IP());
                        ESP_IP_ADDRESS = device.getESP_IP(); // move this line here
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeviceDetailActivity.this, "Failed to read device", Toast.LENGTH_SHORT).show();
            }
        });

        deviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            deviceRef.child("on").setValue(isChecked);
            String gpioPin = gpioPinTextView.getText().toString();
            GPIO gpio = new GPIO(gpioPin.trim());
//            String uid = uidTextView.getText().toString().trim();
//            String uid = Auth.current
//            String userPath = "UsersList/" + user.getUid() + "/DeviceList/" + deviceId;
            String userPath = user.getUid();

            if (isChecked) {
                // Turn on the GPIO
                Esp8266Client.turnOnGpioPin(ESP_IP_ADDRESS,userPath, gpio.getPIN());
                Toast.makeText(DeviceDetailActivity.this, "GPIO " + gpioPin + " turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Turn off the GPIO
                Esp8266Client.turnOffGpioPin(ESP_IP_ADDRESS, userPath, gpio.getPIN());
                Toast.makeText(DeviceDetailActivity.this, "GPIO " + gpioPin + " turned off", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Device");
            builder.setMessage("Are you sure you want to delete this device?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                deviceRef.removeValue();
                Toast.makeText(DeviceDetailActivity.this, "Device deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
            builder.setNegativeButton("No", null);
            builder.show();
        });

        editButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceDetailActivity.this);
            builder.setTitle("Edit Device");

            // Inflate the dialog layout
            View view = LayoutInflater.from(DeviceDetailActivity.this).inflate(R.layout.dialog_edit_device, null);

            // Get a reference to the input fields
            EditText nameEditText = view.findViewById(R.id.name_edit_text);
            Spinner typeSpinner = view.findViewById(R.id.type_spinner);
            EditText uidEditText = view.findViewById(R.id.uid_edit_text);
            Spinner gpioSpinner = view.findViewById(R.id.gpio_spinner);
            EditText espIpEditText = view.findViewById(R.id.esp_ip_edit_text);

           // Set the input fields with the current device details
            nameEditText.setText(deviceNameTextView.getText());
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) typeSpinner.getAdapter();
            typeSpinner.setSelection(adapter.getPosition(deviceTypeTextView.getText().toString()));
            uidEditText.setText(uidTextView.getText());

            // Create an adapter for the GPIO pin spinner and set it to the spinner
            ArrayAdapter<CharSequence> gpioAdapter = ArrayAdapter.createFromResource(DeviceDetailActivity.this, R.array.gpio_pins, android.R.layout.simple_spinner_item);
            gpioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gpioSpinner.setAdapter(gpioAdapter);
            gpioSpinner.setSelection(gpioAdapter.getPosition(gpioPinTextView.getText().toString()));

            espIpEditText.setText(ESP_IP.getText());

            // Set the dialog layout
            builder.setView(view);

            // Set the dialog buttons
            builder.setPositiveButton("Save", (dialog, which) -> {
                // Get the new device details from the input fields
                String name = nameEditText.getText().toString().trim();
                String type = typeSpinner.getSelectedItem().toString();
                String uid = uidEditText.getText().toString().trim();
                String espIp = espIpEditText.getText().toString().trim();
                String gpioPin = gpioSpinner.getSelectedItem().toString();

                // Update the device in Firebase
                Device newDevice = new Device(deviceId, name, type, uid,true, gpioPin, espIp);

                deviceRef.setValue(newDevice);

                // Show a toast message
                Toast.makeText(DeviceDetailActivity.this, "Device updated successfully", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Cancel", null);

            // Show the dialog
            builder.show();
        });



        backButton.setOnClickListener(v -> finish());
    }

    private void saveDeviceList(Device device) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("UsersList").child(user.getEmail().replace(".","_")).child("DeviceList");
            if (device.getKey() != null) {
                devicesRef.child(device.getKey()).setValue(device);
            } else {
                String key = devicesRef.push().getKey();
                device.setKey(key);
                devicesRef.child(key).setValue(device);
            }
        }
    }

    private void deleteDeviceList(Device device) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && device.getKey() != null) {
            DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("UsersList").child(user.getUid()).child("DeviceList");
            devicesRef.child(device.getKey()).removeValue();
        }
    }
}
