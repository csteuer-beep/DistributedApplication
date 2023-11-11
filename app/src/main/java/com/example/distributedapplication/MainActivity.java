package com.example.distributedapplication;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity  implements MQTTCallback{


    public String mqttusermessage = "1699652067002_236";
    private static final String serverUri = "tcp://192.168.56.1:1883";
    private static final String CLIENT_ID = "your_client_id";
    public String incommingMessageVar;
    TextView explain, TVinputCO2, TVusdatat, TVusdata, textView3, TVavdata;
    private EditText ETVCO2;
    private Button Bsenddata;

    private MqttHandler mqttHandler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextViews
        explain = findViewById(R.id.explain);
        TVinputCO2 = findViewById(R.id.TVinputCO2);
        TVusdatat = findViewById(R.id.TVusdatat);
        TVusdata = findViewById(R.id.TVusdata);
        textView3 = findViewById(R.id.textView3);
        TVavdata = findViewById(R.id.TVavdata);

        // EditText
        ETVCO2 = findViewById(R.id.ETVCO2);

        // Button
        Bsenddata = findViewById(R.id.Bsenddata);

        // Optional: Setzen Sie Initialwerte für Ihre Views
        explain.setText("Please insert you CO² consumption in kg of today.");
        //ETVCO2.setText("CO²");

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
                // Hier wird die Methode publishuserdata(View) aufgerufen, wenn der Button geklickt wird
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
      //  TVavdata.setText(mqttHandler.getmessagereceived());



    }

    void publishuserdata() throws InterruptedException {

       String stco2 = String.valueOf(ETVCO2.getText());
       String timestamp = getCurrentTime();

       mqttusermessage = timestamp + "_" + stco2;

        TVusdata.setText(stco2);

        try {

            // mqttHandler.publishMessage(mqttusermessage);
            Context context = this;
            mqttHandler.publishMessage(mqttusermessage);


            /*mqttHandler.init(context, new Runnable() {
                @Override
                public void run() {
                    // This block will be executed when the MQTT client successfully connects
                    mqttHandler.publishMessage("Home Page started"); // Call the method to publish messages here
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace(); // Hier könntest du den Fehler auch loggen oder anderweitig behandeln
        }

      //  TimeUnit.SECONDS.sleep(2);

      //  TVavdata.setText(mqttHandler.getmessagereceived());


    }

    @Override
    public void onMessageArrived(String message) {
        runOnUiThread(()->TVavdata.setText(message));
    }

    public static String getCurrentTime() {
        Instant instant = Instant.now();
        long currentTimeMillis = instant.toEpochMilli();
        //System.out.println("Current time: " + currentTimeMillis + " milliseconds");
        return String.valueOf(currentTimeMillis);
    }





}