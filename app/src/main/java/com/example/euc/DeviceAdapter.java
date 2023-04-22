package com.example.euc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
//    private MyFirebaseMessagingService firebaseMessagingService;

    static private String ESP_IP_ADDRESS;
    private Context context;
    private List<Device> deviceList;
    private List<Device> filteredDeviceList;
    private ItemClickListener itemClickListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();


    public DeviceAdapter(Context context, List<Device> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
        this.filteredDeviceList = deviceList;
//        firebaseMessagingService = new MyFirebaseMessagingService();
    }

    public void filter(String text) {
        List<Device> filteredList = new ArrayList<>();

        for (Device device : deviceList) {
            if (device.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(device);
            }
        }

        updateList(filteredList);
    }

    public void updateList(List<Device> newList) {
        filteredDeviceList = new ArrayList<>();
        filteredDeviceList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Device device = filteredDeviceList.get(position);
        holder.deviceNameTextView.setText(device.getName());
        holder.deviceTypeSpinner.setText(device.getType());
        holder.gpioPinSpinner.setText(device.getGpioPin());
        holder.deviceSwitch.setOnCheckedChangeListener(null);
        holder.deviceSwitch.setChecked(device.isOn());
        holder.espIp.setText(device.getESP_IP());
        ESP_IP_ADDRESS = device.getESP_IP();
        device.deviceRef.child("on").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean on = dataSnapshot.getValue(Boolean.class);
                if (on != null) {
                    holder.deviceSwitch.setChecked(on);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Failed to read value: " + databaseError.toException());
            }
        });
        Log.d("From Device Adapter: ", "ESP_IP_ADDRESS: " + ESP_IP_ADDRESS);


        holder.deviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            device.setOn(isChecked);
            String deviceId = device.getKey();
            String uid = device.getUid();
//            String userPath = "UsersList/" + uid + "/DeviceList/" + deviceId;
            String userPath = user.getUid();
            DatabaseReference deviceRef = AddFragment.mDatabase.child(deviceId);
            deviceRef.setValue(device);
            String gpioPin = device.getGpioPin();
            GPIO gpio = new GPIO(device.getGpioPin());

//            MyFirebaseMessagingService notificationService = new MyFirebaseMessagingService();
            if (isChecked) {
                Esp8266Client.turnOnGpioPin(ESP_IP_ADDRESS,userPath, gpio.getPIN());
                Toast.makeText(context, "GPIO " + gpioPin + " turned on", Toast.LENGTH_SHORT).show();
            } else {
                Esp8266Client.turnOffGpioPin(ESP_IP_ADDRESS, userPath, gpio.getPIN());
                Toast.makeText(context, "GPIO " + gpioPin + " turned off", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredDeviceList.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceNameTextView;
        TextView deviceTypeSpinner, espIp;
        TextView gpioPinSpinner;
        SwitchCompat deviceSwitch;
        GPIO gpio;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceNameTextView = itemView.findViewById(R.id.device_name_text_view);
            deviceTypeSpinner = itemView.findViewById(R.id.device_type_spinner);
            gpioPinSpinner = itemView.findViewById(R.id.gpio_pin_spinner);
            deviceSwitch = itemView.findViewById(R.id.device_switch);
            espIp = itemView.findViewById(R.id.device_esp_ip);
            ESP_IP_ADDRESS = espIp.getText().toString().trim();
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // SpacingItemDecoration class
    public static class SpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public SpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.bottom = spacing;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = spacing;
            } else {
                outRect.top = 0;
            }
        }
    }
}
