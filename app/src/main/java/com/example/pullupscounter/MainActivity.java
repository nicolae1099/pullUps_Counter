package com.example.pullupscounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private float lastX = 0, lastY = 0, lastZ = 0;
    private float deltaX = 0, deltaY = 0, deltaZ = 0;

    private TextView currentX, currentY, currentZ, recordText_TextView, record_TextView;
    private TextView pullUpsText_TextView, pullUps_TextView;
    private Button newSet_Button;

    private long startTime = 0;
    private long last_pullUp_time;

    private int count_pullUps = 0;
    private int count_record = 0;
    private boolean is_at_bar_level = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentX = findViewById(R.id.currentX_textView);
        currentY = findViewById(R.id.currentY_textView);
        currentZ = findViewById(R.id.currentZ_textView);

        pullUpsText_TextView = findViewById(R.id.pullUpsText_TextView);
        pullUps_TextView = findViewById(R.id.pullups_textView);
        pullUps_TextView.setText(String.valueOf(0));

        recordText_TextView = findViewById(R.id.recordText_textView);
        record_TextView = findViewById(R.id.record_TextView);
        record_TextView.setText(String.valueOf(0));

        newSet_Button = findViewById(R.id.newSet_button);
        newSet_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count_pullUps = 0;

            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        startTime = System.currentTimeMillis();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {

            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.d("PullUps", "Nu exista acclerometru pe telefon");
        }
    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis() - startTime;


        deltaX = event.values[0] - lastX;
        deltaY = event.values[1] - lastY;
        deltaZ = event.values[2] - lastZ;
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        if (deltaY > 1.25) {
            is_at_bar_level = true;
        }
        if (is_at_bar_level == true && deltaY < -1.25 && (currentTime - 1200 > last_pullUp_time)) {
            is_at_bar_level = false;
            count_pullUps++;
            last_pullUp_time = System.currentTimeMillis() - startTime;
            pullUps_TextView.setText(String.valueOf(count_pullUps));
        }

        displayCurrentValues();

        if (count_pullUps > count_record) {
            count_record = count_pullUps;
            record_TextView.setText(String.valueOf(count_record));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

}
