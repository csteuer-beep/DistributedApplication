package com.example.distributedapplication;

public interface MQTTCallback {
    void onMessageArrived(String message);
}
