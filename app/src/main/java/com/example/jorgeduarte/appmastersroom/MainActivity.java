package com.example.jorgeduarte.appmastersroom;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    private String ArduinoBright = "Very bright";
    private double ArduinoTemperature = 51.15;
    private double humidity= 39.551124572753906;
    private double pressure = 1002.260498046875;
    private String temperature;
    private Context context;
    private int people = 15;
    private int speedWifi;
    private double noise = 1;

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


        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID();
        int rssi = wifiInfo.getRssi();
        int speed = wifiInfo.getLinkSpeed();
        speedWifi = 21;

        Log.d("WifiReceiver", "Don't have Wifi Connection"+name+"  speed"+speed);





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(getApplicationContext(),Activity_PeopleRoom.class));

            }
        });

        //  new GetData().execute();

        update();

    }

    public void getnotification(){




       NotificationManager notificationmgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, Activity_PeopleRoom.class);
        PendingIntent pintent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //   PendingIntent pintent = PendingIntent.getActivities(this,(int)System.currentTimeMillis(),intent, 0);


        Notification notif = new Notification.Builder(this)
                .setSmallIcon(R.drawable.team)
                .setContentTitle("Masters Room")
                .setContentText("Please count the number of people in the room ")
                .setContentIntent(pintent)
                .build();


        notificationmgr.notify(0,notif);





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
                    ArduinoTemperature =  jsonObj.getDouble("ArduinoTemperature");
                    humidity =  jsonObj.getDouble("humidity");
                    pressure =  jsonObj.getDouble("pressure");
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

    public void update(){
        TextView brightOutput = (TextView) findViewById(R.id.textArduinoBright);
        TextView temperatureOutput = (TextView) findViewById(R.id.textTemperature);
        TextView humidityOutput = (TextView) findViewById(R.id.textHumidity);
        TextView pressureOutput = (TextView) findViewById(R.id.textPressure);
        TextView speedWifiOutput = (TextView) findViewById(R.id.textSpeedWifi);
        TextView noiseOutput = (TextView) findViewById(R.id.textNoise);
        TextView peopleOutput = (TextView) findViewById(R.id.textPeople);

        ImageView imageBrightness = (ImageView) findViewById(R.id.imageBrightness);
        ImageView imageThermometer = (ImageView) findViewById(R.id.imageThermometer);
        ImageView imageSpeedWifi = (ImageView) findViewById(R.id.imageSpeedWifi);
        ImageView imageNoise = (ImageView) findViewById(R.id.imageNoise);
        ImageView imagePeople = (ImageView) findViewById(R.id.imagePeople);


        switch (ArduinoBright){
            case "Dark":
                imageBrightness.setImageResource(R.drawable.brightness_dark);
                break;
            case "Dim":
                imageBrightness.setImageResource(R.drawable.brightness_dim);
                break;
            case "Light":
                imageBrightness.setImageResource(R.drawable.brightness_light);
                break;
            case "Bright":
                imageBrightness.setImageResource(R.drawable.brightness_bright);
                break;
            case "Very bright":
                imageBrightness.setImageResource(R.drawable.brightness_verybright);
                break;
        }

        if (ArduinoTemperature <10){

            imageSpeedWifi.setImageResource(R.drawable.thermometer_1_10);
        }else if (ArduinoTemperature >9 && ArduinoTemperature <16){
            imageThermometer.setImageResource(R.drawable.thermometer_10_15);
        }else if (ArduinoTemperature >15 && ArduinoTemperature <21){
            imageThermometer.setImageResource(R.drawable.thermometer_15_20);
        }else if (ArduinoTemperature >20 && ArduinoTemperature <26){
            imageThermometer.setImageResource(R.drawable.thermometer_20_25);
        }else if (ArduinoTemperature >25 && ArduinoTemperature <31){
            imageThermometer.setImageResource(R.drawable.thermometer_25_30);
        }else if (ArduinoTemperature >30 && ArduinoTemperature <36){
            imageThermometer.setImageResource(R.drawable.thermometer_30_35);
        }else if (ArduinoTemperature >35 && ArduinoTemperature <41){
            imageThermometer.setImageResource(R.drawable.thermometer_35_40);
        }else if (ArduinoTemperature >40 && ArduinoTemperature <46){
            imageThermometer.setImageResource(R.drawable.thermometer_40_45);
        }else if (ArduinoTemperature >45 && ArduinoTemperature <51){
            imageThermometer.setImageResource(R.drawable.thermometer_45_50);
        }else if (ArduinoTemperature >50){
            imageThermometer.setImageResource(R.drawable.thermometer_50_55);
        }

        if (speedWifi <2){
            imageSpeedWifi.setImageResource(R.drawable.wifi_red);
        }else if (speedWifi >2 && speedWifi <11){
            imageSpeedWifi.setImageResource(R.drawable.wifi_orange);
        }else if (speedWifi >10 && speedWifi <21){
            imageSpeedWifi.setImageResource(R.drawable.wifi_yellow);
        }else if (speedWifi >20){
            imageSpeedWifi.setImageResource(R.drawable.wifi_green2);
        }


        if (noise <3){
            imageNoise.setImageResource(R.drawable.noise_green);
        }else if (noise >2 && noise <11){
            imageNoise.setImageResource(R.drawable.noise_orange);
        }else if (noise >10 ){
            imageNoise.setImageResource(R.drawable.noise_red);
        }

        if (people <2){
            imagePeople.setImageResource(R.drawable.people_green);
        }else if (people >2 && people <11){
            imagePeople.setImageResource(R.drawable.people_yellow);
        }else if (people >10 ){
            imagePeople.setImageResource(R.drawable.team);
        }


        ArduinoTemperature = Math.round(ArduinoTemperature);
        humidity = Math.round(humidity);
        int pressureInt = (int) pressure;



        brightOutput.setText(ArduinoBright);
        temperatureOutput.setText((ArduinoTemperature+" ºc"));
        humidityOutput.setText((humidity+" %"));
        pressureOutput.setText((pressureInt+" br"));
        speedWifiOutput.setText((Integer.toString(speedWifi)+" mbps"));
        noiseOutput.setText(noise+" db");
        peopleOutput.setText(people+" un");

    }





}


