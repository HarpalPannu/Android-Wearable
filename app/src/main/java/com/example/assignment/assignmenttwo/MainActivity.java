package com.example.assignment.assignmenttwo;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity {

    String[] workOutTypes = {"Walking", "Running", "Cycling"};
    TextView timerLabel;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    private boolean started = false;
    private EditText weightInput;
    private EditText distanceInput;
    private String caloriesBurned;
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            timerLabel.setText(String.format( Locale.getDefault(),"%02d:%02d", mins,secs));
            customHandler.postDelayed(this, 0);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner workOutType = findViewById(R.id.workOutType);
        timerLabel = findViewById(R.id.timerLabel);
        distanceInput = findViewById(R.id.distanceInput);
        weightInput = findViewById(R.id.weightInput);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workOutTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workOutType.setAdapter(arrayAdapter);


        workOutType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button startStopBtn = findViewById(R.id.startStopBtn);
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(started){
                    customHandler.removeCallbacksAndMessages(null);
                    startStopBtn.setText(R.string.start);
                    started = false;
                    weightInput.setEnabled(true);
                    distanceInput.setEnabled(true);

                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog);
                    double kilogramweight = Double.parseDouble(weightInput.getText().toString());
                    double kilometerwalkdistance = Double.parseDouble(distanceInput.getText().toString());
                    double hours =  (TimeUnit.MILLISECONDS.toMinutes(updatedTime) + (TimeUnit.MILLISECONDS.toSeconds(updatedTime)/60.0))/(60.0);
                    double kmperhrspeed = kilometerwalkdistance/hours;
                    double kcalperkgperhr  = 0.0215*Math.pow(kmperhrspeed, 3) - 0.1765*Math.pow(kmperhrspeed, 2) + 0.871*kmperhrspeed + 1.4577;
                    if(workOutType.getSelectedItem().toString().equals(workOutTypes[0]) | workOutType.getSelectedItem().toString().equals(workOutTypes[1])){
                        caloriesBurned= String.valueOf(Math.round(kcalperkgperhr*kilogramweight*hours));
                    }else{
                        caloriesBurned= String.valueOf(Math.round(kcalperkgperhr*kilogramweight*hours));
                    }


                  TextView text =  dialog.findViewById(R.id.dialogLabel);
                  text.setText(String.format("Distance : %s Km\nWeight : %sKg \nTime : %s\nExercise : %s\nCalories : %s",
                          distanceInput.getText().toString(), weightInput.getText().toString(), timerLabel.getText().toString(),
                          workOutType.getSelectedItem().toString(), caloriesBurned));
                   ImageView image =  dialog.findViewById(R.id.okImg);
                   image.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialog.dismiss();
                           timerLabel.setText(R.string._00_00);
                       }
                   });
                    dialog.show();

                }else {
                    if(weightInput.getText().toString().isEmpty()){
                        weightInput.requestFocus();
                        return;
                    }else if(distanceInput.getText().toString().isEmpty()){
                        distanceInput.requestFocus();
                        return;
                    }
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                    started = true;
                    startStopBtn.setText(R.string.stop);
                    weightInput.setEnabled(false);
                    distanceInput.setEnabled(false);
                }
            }
        });

        setAmbientEnabled();
    }
}
