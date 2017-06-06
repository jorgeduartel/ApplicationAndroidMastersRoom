package com.example.jorgeduarte.appmastersroom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private static final int MAXIMUM_NUMBER_OF_PEOPLE = 20;
    private static final int MAXIMUM_NOISE_LEVEL = 140;
    private static final String UNAVAILABLE_DATA = "Unavailable";

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private String ArduinoBright = "-9999";
    private double ArduinoTemperature = -9999;
    private double humidity= -9999;
    private double pressure = -9999;
    private String temperature;
    private int people = -9999;
    private int speedWifi = -9999;
    private double noise = -9999;
    private String backgroundTemperature;
    private String backgroundBrightnes;
    private String backgroundSpeedWifi;
    private String backgroundNoise;
    private String backgroundPeople;
    private String backgroundHumidity;
    private String backgroundPressure;

    // URL to get contacts JSON
    private static String url = com.example.jorgeduarte.appmastersroom.url.getUrl()+"all";

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),Activity_PeopleRoom.class));

            }
        });

        Button buttonSettings = (Button) findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivityChart.class));

            }
        });

        new GetData().execute();

        update();
    }

    public void getnotification(){

       NotificationManager notificationmgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, Activity_PeopleRoom.class);
        PendingIntent pintent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //   PendingIntent pintent = PendingIntent.getActivities(this,(int)System.currentTimeMillis(),intent, 0);

        Notification notif = new Notification.Builder(this)
                .setSmallIcon(R.drawable.team)
                .setContentTitle("Study Room")
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
                    people = jsonObj.getInt("People");
                    ArduinoBright = jsonObj.getString("ArduinoBright");
                    ArduinoTemperature = jsonObj.getDouble("ArduinoTemperature");
                    humidity = jsonObj.getDouble("Humidity");
                    pressure = jsonObj.getDouble("Pressure");
                    temperature = jsonObj.getString("Temperature");
                    noise = jsonObj.getDouble("Noise");
                    speedWifi = jsonObj.getInt("WifiQuality");

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
                                "No server connection!",
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
            pDialog.setCancelable(true);
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

        Button buttonBrightness = (Button) findViewById(R.id.buttonBrightness);
        Button buttonThermometer = (Button) findViewById(R.id.buttonThermometer);
        Button buttonSpeedWifi = (Button) findViewById(R.id.buttonSpeedWifi);
        Button buttonNoise = (Button) findViewById(R.id.buttonNoise);
        Button buttonPeople = (Button) findViewById(R.id.buttonPeople);
        Button buttonPressure = (Button) findViewById(R.id.buttonPressure);
        Button buttonHumidity = (Button) findViewById(R.id.buttonHumidity);

        String aux_ArduinoBright = ArduinoBright.toLowerCase();

        if(aux_ArduinoBright.contains("dark"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_dark);
            backgroundBrightnes = "brightness_dark";
        }
        else if(aux_ArduinoBright.contains("dim"))
        {
                buttonBrightness.setBackgroundResource(R.drawable.brightness_dim);
                backgroundBrightnes = "brightness_dim";
        }
        else if(aux_ArduinoBright.contains("light"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_light);
            backgroundBrightnes = "brightness_light";
        }
        else if(aux_ArduinoBright.contains("bright"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_bright);
            backgroundBrightnes = "brightness_bright";
        }
        else if(aux_ArduinoBright.contains("very bright"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_verybright);
            backgroundBrightnes = "brightness_verybright";
        }
        else
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness);
            backgroundBrightnes = "brightness";
        }

        if (ArduinoTemperature < 5)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_1_5);
            backgroundTemperature = "thermometer_1_5";
        }
        else if (ArduinoTemperature >= 5 && ArduinoTemperature < 10)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_5_10);
            backgroundTemperature = "thermometer_5_10";
        }
        else if (ArduinoTemperature >= 10 && ArduinoTemperature < 15)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_10_15);
            backgroundTemperature = "thermometer_10_15";
        }
        else if (ArduinoTemperature >= 15 && ArduinoTemperature < 20)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_15_20);
            backgroundTemperature = "thermometer_15_20";
        }
        else if (ArduinoTemperature >= 20 && ArduinoTemperature < 25)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_20_25);
            backgroundTemperature = "thermometer_20_25";
        }
        else if (ArduinoTemperature >= 25 && ArduinoTemperature < 30)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_25_30);
            backgroundTemperature = "thermometer_25_30";
        }
        else if (ArduinoTemperature >= 30 && ArduinoTemperature < 35)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_30_35);
            backgroundTemperature = "thermometer_30_35";
        }
        else if (ArduinoTemperature >= 35 && ArduinoTemperature < 40)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_35_40);
            backgroundTemperature = "thermometer_35_40";
        }
        else if (ArduinoTemperature >= 40 && ArduinoTemperature < 45)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_40_45);
            backgroundTemperature = "thermometer_40_45";
        }
        else if (ArduinoTemperature >= 45)
        {
            buttonThermometer.setBackgroundResource(R.drawable.thermometer_45_50);
            backgroundTemperature = "thermometer_45_50";
        }

        if (speedWifi < 2)
        {
            buttonSpeedWifi.setBackgroundResource(R.drawable.wifi_red);
            backgroundSpeedWifi = "wifi_red";
        }
        else if (speedWifi > 2 && speedWifi < 11)
        {
            buttonSpeedWifi.setBackgroundResource(R.drawable.wifi_orange);
            backgroundSpeedWifi = "wifi_orange";
        }
        else if (speedWifi > 10 && speedWifi < 21)
        {
            buttonSpeedWifi.setBackgroundResource(R.drawable.wifi_yellow);
            backgroundSpeedWifi = "wifi_yellow";
        }
        else if (speedWifi > 20)
        {
            buttonSpeedWifi.setBackgroundResource(R.drawable.wifi_green2);
            backgroundSpeedWifi = "wifi_green2";
        }

        if (noise < 3)
        {
            buttonNoise.setBackgroundResource(R.drawable.noise_green);
            backgroundNoise = "noise_green";
        }
        else if (noise > 2 && noise < 11)
        {
            buttonNoise.setBackgroundResource(R.drawable.noise_orange);
            backgroundNoise = "noise_orange";
        }
        else if (noise > 10 )
        {
            buttonNoise.setBackgroundResource(R.drawable.noise_red);
            backgroundNoise = "noise_red";
        }

        if (people < 2)
        {
            buttonPeople.setBackgroundResource(R.drawable.people_green);
            backgroundPeople = "people_green";
        }else if (people >= 2 && people < 11)
        {
            buttonPeople.setBackgroundResource(R.drawable.people_yellow);
            backgroundPeople = "people_yellow";
        }else if (people > 10)
        {
            buttonPeople.setBackgroundResource(R.drawable.team);
            backgroundPeople = "team";
        }

        ArduinoTemperature = Math.round(ArduinoTemperature);
        humidity = Math.round(humidity);
        int pressureInt = (int) pressure;

        if(pressure < 990)
        {
            buttonPressure.setBackgroundResource(R.drawable.pressure1_1);
            backgroundPressure = "pressure1_1";
        }
        else if(pressure >= 990 && pressure < 995)
        {
            buttonPressure.setBackgroundResource(R.drawable.pressure2_1);
            backgroundPressure = "pressure2_1";
        }
        else if(pressure >= 995 && pressure < 1000)
        {
            buttonPressure.setBackgroundResource(R.drawable.pressure3_1);
            backgroundPressure = "pressure3_1";
        }
        else if(pressure >= 1000 && pressure < 1005)
        {
            buttonPressure.setBackgroundResource(R.drawable.pressure4_1);
            backgroundPressure = "pressure4_1";
        }
        else if(pressure >= 1005 && pressure < 1010)
        {
            buttonPressure.setBackgroundResource(R.drawable.pressure5_1);
            backgroundPressure = "pressure5_1";
        }
        else if(pressure >= 1010 && pressure < 1015)
        {
            buttonPressure.setBackgroundResource(R.drawable.pressure6_1);
            backgroundPressure = "pressure6_1";
        }
        else if(pressure >= 1015)
        {
            buttonPressure.setBackgroundResource(R.drawable.pressure7_1);
            backgroundPressure = "pressure7_1";
        }

        if(humidity < 25)
        {
            buttonHumidity.setBackgroundResource(R.drawable.humidity1);
            backgroundHumidity = "humidity1";
        }
        else if(humidity >= 25 && humidity < 50)
        {
            buttonHumidity.setBackgroundResource(R.drawable.humidity2);
            backgroundHumidity = "humidity2";
        }
        else if(humidity >= 50 && humidity < 75)
        {
            buttonHumidity.setBackgroundResource(R.drawable.humidity3);
            backgroundHumidity = "humidity3";
        }
        else if(humidity >= 75 && humidity < 100)
        {
            buttonHumidity.setBackgroundResource(R.drawable.humidity4);
            backgroundHumidity = "humidity4";
        }

        if(ArduinoBright.contains("-9999"))
        {
            brightOutput.setText(UNAVAILABLE_DATA);
        }
        else
        {
            brightOutput.setText(ArduinoBright);
        }

        if(ArduinoTemperature >= 0)
        {
            temperatureOutput.setText((ArduinoTemperature + " ºC"));
        }
        else
        {
            temperatureOutput.setText(UNAVAILABLE_DATA);
        }

        if(humidity >= 0)
        {
            humidityOutput.setText((humidity + " %"));
        }
        else
        {
            humidityOutput.setText(UNAVAILABLE_DATA);
        }

        if(pressureInt >= 0)
        {
            pressureOutput.setText((pressureInt+" hPa"));
        }
        else
        {
            pressureOutput.setText(UNAVAILABLE_DATA);
        }

        if(speedWifi >= 0)
        {
            speedWifiOutput.setText((Integer.toString(speedWifi) + " Mb/s"));
        }
        else
        {
            speedWifiOutput.setText(UNAVAILABLE_DATA);
        }

        if(noise >= 0)
        {
            noiseOutput.setText(noise + "/" + MAXIMUM_NOISE_LEVEL + " dB");
        }
        else
        {
            noiseOutput.setText(UNAVAILABLE_DATA);
        }

        if(people >= 0)
        {
            if(people == 1)
            {
                peopleOutput.setText(people + "/" + MAXIMUM_NUMBER_OF_PEOPLE + " person");
            }
            else
            {
                peopleOutput.setText(people + "/" + MAXIMUM_NUMBER_OF_PEOPLE + " people");
            }
        }
        else
        {
            peopleOutput.setText(UNAVAILABLE_DATA);
        }
    }

    public void sensorFactory(View view){
        switch (view.getId()) {
            case R.id.buttonThermometer:
                startActivity(new Intent(getApplicationContext(),sensorData.class).putExtra("sensor", "Temperature").putExtra("background",  backgroundTemperature).putExtra("value", ArduinoTemperature+" ºC"));
                break;
            case R.id.buttonBrightness:
                startActivity(new Intent(getApplicationContext(),sensorData.class).putExtra("sensor", "Brightness").putExtra("background",  backgroundBrightnes).putExtra("value", ArduinoBright));
                break;
            case R.id.buttonSpeedWifi:
                startActivity(new Intent(getApplicationContext(),sensorData.class).putExtra("sensor", "Wi-Fi network speed").putExtra("background",  backgroundSpeedWifi).putExtra("value", (Integer.toString(speedWifi)+" Mb/s")));
                break;
            case R.id.buttonNoise:
                noise = Math.round(noise);
                startActivity(new Intent(getApplicationContext(),sensorData.class).putExtra("sensor", "Noise").putExtra("background",  backgroundNoise).putExtra("value", noise + "/" + MAXIMUM_NOISE_LEVEL + " dB"));
                break;
            case R.id.buttonPeople:
                if(people == 1)
                {
                    startActivity(new Intent(getApplicationContext(),sensorData.class).putExtra("sensor", "Number of people").putExtra("background",  backgroundPeople).putExtra("value", people + "/" + MAXIMUM_NUMBER_OF_PEOPLE + " person"));
                }
                else
                {
                    startActivity(new Intent(getApplicationContext(),sensorData.class).putExtra("sensor", "Number of people").putExtra("background",  backgroundPeople).putExtra("value", people + "/" + MAXIMUM_NUMBER_OF_PEOPLE + " people"));
                }
                break;
            case R.id.buttonHumidity:
                startActivity(new Intent(getApplicationContext(),sensorData.class).putExtra("sensor", "Humidity").putExtra("background",  backgroundHumidity).putExtra("value", (humidity+" %")));
                break;
            case R.id.buttonPressure:
                int pressureInt = (int) pressure;
                startActivity(new Intent(getApplicationContext(),sensorData.class).putExtra("sensor", "Pressure").putExtra("background",  backgroundPressure).putExtra("value", (pressureInt+" hPa")));
                break;
        }
    }
}