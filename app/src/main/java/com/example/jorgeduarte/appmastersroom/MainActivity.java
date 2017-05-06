package com.example.jorgeduarte.appmastersroom;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    private String ArduinoBright;
    private String ArduinoTemperature;
    private String humidity;
    private String pressure;
    private MediaRecorder recorder;
    private String temperature;
    private Context context;
    private String people;
    private TextView tvNoiseOutput;
    private double amplitudeDb;
    private double amplitudeDbF;
    private volatile Thread verifyNoise;

    // URL to get contacts JSON
    private static String url = "http://192.168.201.100:8080/all";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (android.os.Build.VERSION.SDK_INT >= 21) { // Jorge
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.gray));
        }



       /* WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        Log.e(TAG, "Wifi: " + level);*/

       /* GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Log.e(TAG, "ArduinoBright: " + ArduinoBright);

            }
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Activity_PeopleRoom.class));

            }
        });

        //  new GetData().execute();
        // recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null");
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        verifyNoiseThread();



    }


    public void onReceive(WifiManager wifiManager) {
        int numberOfLevels=5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level=WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        System.out.println("Bars =" +level);
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url , "GET");

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //people =  jsonObj.getString("people");
                    ArduinoBright = jsonObj.getString("ArduinoBright");
                    ArduinoTemperature =  jsonObj.getString("ArduinoTemperature");
                    humidity =  jsonObj.getString("humidity");
                    pressure =  jsonObj.getString("pressure");
                    temperature =  jsonObj.getString("temperature");


                    Log.e(TAG, "ArduinoBright: " + ArduinoBright);
                    update();

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

        }


    }

    public void verifyNoiseThread(){
        this.verifyNoise = new Thread(new Runnable() {
            @Override
            public void run () {
                while(true){
                    try {
                        int amplitude = recorder.getMaxAmplitude();
                        amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
                        if(amplitudeDb>=0) {
                            amplitudeDbF =  amplitudeDb;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        verifyNoise.start();
    }

    public void update(){
        TextView brightOutput = (TextView) findViewById(R.id.textArduinoBright);
        TextView temperatureOutput = (TextView) findViewById(R.id.textTemperature);
        TextView humidityOutput = (TextView) findViewById(R.id.textHumidity);
        TextView pressureOutput = (TextView) findViewById(R.id.textPressure);
        tvNoiseOutput = (TextView) findViewById(R.id.tvNoise);

        brightOutput.append(ArduinoBright);
        temperatureOutput.append(ArduinoTemperature);
        humidityOutput.append(humidity);
        pressureOutput.append(pressure);
        tvNoiseOutput.setText("Noise: " +amplitudeDbF);
    }




}

