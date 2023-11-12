package com.example.distributedapplication;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttHandler extends AppCompatActivity {
    private MQTTCallback callback;
    private MqttAndroidClient mqttAndroidClient;
    public String message_received = "No message received";
    final String serverUri = "tcp://192.168.56.1:1883"; //"tcp://iot.eclipse.org:1883";
    final String subscriptionTopic = "footprint/average";
    final String publishTopic = "footprint/userdata";

    String clientId = "ExampleAndroidClient";

    public void setCallback(MQTTCallback callback) {
        this.callback = callback;
    }

    public void init(Context applicationContext, final Runnable onConnect) {
        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(applicationContext, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    Log.d("TAG_mqtt", "Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    Log.d("TAG_mqtt", "Connected to: " + serverURI);
                    // Trigger the onConnect callback when connected
                    onConnect.run();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("TAG_mqtt", "The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                message_received = new String(message.getPayload());
                Log.d("TAG_mqtt", "Incoming message: " + new String(message.getPayload()));
                if (callback != null) {
                    callback.onMessageArrived(message_received);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setCleanSession(true);

        Log.d("TAG_mqtt", "Connecting to " + serverUri + "...");

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    // Subscribe inside the connect success block, not directly
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("TAG_mqtt", "Failed to connect to: " + serverUri +
                            ". Cause: " + ((exception.getCause() == null) ?
                            exception.toString() : exception.getCause()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("TAG_mqtt", e.toString());
        }
    }

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("TAG_mqtt", "Subscribed to: " + subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("TAG_mqtt", "Failed to subscribe");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("TAG_mqtt", e.toString());
        }

    }

    public String getmessagereceived() {
        return message_received;
    }

    public void publishMessage(String publishMessage) {
        MqttMessage message = new MqttMessage();
        message.setPayload(publishMessage.getBytes());
        message.setRetained(false);
        message.setQos(1); // Set the QoS level

        if (mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.publish(publishTopic, message, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("TAG_mqtt", "Message Published");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("TAG_mqtt", "Failed to publish message");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Log.d("TAG_mqtt", e.toString());
            }
        } else {
            // If not connected, attempt reconnection before publishing
            Log.d("TAG_mqtt", "Client not connected! Reconnecting...");
            // Attempt reconnection logic here before publishing
            // You might want to add logic to reconnect before publishing
            // For instance, calling the connection method or handling reconnection
        }
    }
}

