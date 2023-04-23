package com.example.euc;

import com.google.firebase.database.DatabaseReference;

public class Device {
    private String name;
    private String type;
    private boolean isOn;
    private String key;
    private String uid;
    private String gpioPin;
    private String ESP_IP;
    DatabaseReference deviceRef;

    public void setDeviceRef(DatabaseReference deviceRef) {
        this.deviceRef = deviceRef;
    }

    public Device() {
    }

    public Device(String key, String name, String type, String uid, boolean isOn, String gpioPin, String ESP_IP) {
        this.name = name;
        this.type = type;
        this.uid = uid;
        this.isOn = isOn;
        this.key = key;
        this.gpioPin = gpioPin;
        this.ESP_IP = ESP_IP;
    }


    // Add getters and setters for the gpioPin
    public String getGpioPin() {
        return gpioPin;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isOn() {
        return isOn;
    }

    public String getKey() {
        return key;
    }

    public String getUid() {
        return uid;
    }

    public String getESP_IP() {
        return ESP_IP;
    }

    public void setESP_IP(String ESP_IP) {
        this.ESP_IP = ESP_IP;
    }

    public void setGpioPin(String gpioPin) {
        this.gpioPin = gpioPin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
