package com.example.distributedapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MQTTCallback {


    public String mqttusermessage = "1699652067002_236";
    private static final String serverUri = "tcp://192.168.56.1:1883";
    private static final String CLIENT_ID = "your_client_id";
    public String incommingMessageVar;
    TextView explain, TVusdatat, TVusdata, textView3, TVavdata;
    private EditText value1Field, value2Field, value3Field, value4Field, value5Field,
            value6Field, value7Field, value8Field, value9Field, value10Field;
    private Button Bsenddata;

    private MqttHandler mqttHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextViews
        explain = findViewById(R.id.explain);
        TVusdatat = findViewById(R.id.TVusdatat);
        TVusdata = findViewById(R.id.TVusdata);
        textView3 = findViewById(R.id.textView3);
        TVavdata = findViewById(R.id.TVavdata);

        // EditText
        value1Field = findViewById(R.id.value1Field);
        value2Field = findViewById(R.id.value2Field);
        value3Field = findViewById(R.id.value3Field);
        value4Field = findViewById(R.id.value4Field);
        value5Field = findViewById(R.id.value5Field);
        value6Field = findViewById(R.id.value6Field);
        value7Field = findViewById(R.id.value7Field);
        value8Field = findViewById(R.id.value8Field);
        value9Field = findViewById(R.id.value9Field);
        value10Field = findViewById(R.id.value10Field);

        // Button
        Bsenddata = findViewById(R.id.Bsenddata);

        explain.setText("Please insert you CO² consumption in kg of today.");


        mqttHandler = new MqttHandler();
        mqttHandler.setCallback(this);
        mqttHandler.init(getApplicationContext(), new Runnable() {
            @Override
            public void run() {
                // This block will be executed when the MQTT client successfully connects

            }
        });

        Bsenddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    publishuserdata();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Log.d("TAG_mqtt", "TextView change");
    }

    void publishuserdata() throws InterruptedException {

        int value1 = Integer.parseInt(value1Field.getText().toString().isEmpty() ? "0" : value1Field.getText().toString());
        int value2 = Integer.parseInt(value2Field.getText().toString().isEmpty() ? "0" : value2Field.getText().toString());
        int value3 = Integer.parseInt(value3Field.getText().toString().isEmpty() ? "0" : value3Field.getText().toString());
        int value4 = Integer.parseInt(value4Field.getText().toString().isEmpty() ? "0" : value4Field.getText().toString());
        int value5 = Integer.parseInt(value5Field.getText().toString().isEmpty() ? "0" : value5Field.getText().toString());
        int value6 = Integer.parseInt(value6Field.getText().toString().isEmpty() ? "0" : value6Field.getText().toString());
        int value7 = Integer.parseInt(value7Field.getText().toString().isEmpty() ? "0" : value7Field.getText().toString());
        int value8 = Integer.parseInt(value8Field.getText().toString().isEmpty() ? "0" : value8Field.getText().toString());
        int value9 = Integer.parseInt(value9Field.getText().toString().isEmpty() ? "0" : value9Field.getText().toString());
        int value10 = Integer.parseInt(value10Field.getText().toString().isEmpty() ? "0" : value10Field.getText().toString());


        float beefConsumption = (float) (value1 * 16.88);
        float porkConsumption = (float) (value2 * 6.92);
        float chickenConsumption = (float) (value3 * 2.79);
        float fishConsumption = (float) (value4 * 5.14);
        float butterConsumption = (float) (value5 * 12.11);
        float dairyProductsConsumption = (float) (value6 * 5.89);
        float carConsumption = value7 * 171;
        float pTransportConsumption = value8 * 67;
        float planeConsumption = value9 * 365;
        float appliancesConsumption = value10 * 750;

        float totalConsumption = beefConsumption + porkConsumption + chickenConsumption + fishConsumption +
                butterConsumption + dairyProductsConsumption + carConsumption + pTransportConsumption +
                planeConsumption + appliancesConsumption;


        String stco2 = String.format("%.3f", totalConsumption);

        String timestamp = getCurrentTimestamp();


        mqttusermessage = timestamp + "_" + stco2;

        TVusdata.setText(stco2);

        try {

            // mqttHandler.publishMessage(mqttusermessage);
            Context context = this;
            mqttHandler.publishMessage(mqttusermessage);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMessageArrived(String message) {
        runOnUiThread(() -> TVavdata.setText(message));
    }

    private String getCurrentTimestamp() {
        long timestamp = System.currentTimeMillis();
        String timestampString = String.valueOf(timestamp);

        // Stelle sicher, dass der String genau 13 Stellen hat
        while (timestampString.length() < 13) {
            timestampString = "0" + timestampString;
        }

       // Log.d("TAG_mqtt", "timestampString: " + timestampString);
        return timestampString;
    }

}