package com.example.jorgeduarte.appmastersroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
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
    private ProgressDialog progressDialog;

    private double temperature = -9999;
    private double humidity = -9999;
    private double pressure = -9999;
    private String brightness = "-9999";
    private double noise = -9999;
    private int people = -9999;
    private double WiFiSpeed = -9999;

    private String backgroundTemperature;
    private String backgroundHumidity;
    private String backgroundPressure;
    private String backgroundBrightness;
    private String backgroundNoise;
    private String backgroundPeople;
    private String backgroundWiFiSpeed;

    // URL to get contacts JSON
    private static String URL = com.example.jorgeduarte.appmastersroom.URL.getURL() + "all";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
        }

        Button buttonSettings = (Button) findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),MainActivityChart.class));
            }
        });

        new GetData().execute();
        update();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();

            // Making a request to URL and getting response
            String jsonStr = sh.makeServiceCall(URL, "GET");

            Log.e(TAG, "Response from URL: " + jsonStr);

            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    temperature = jsonObj.getDouble("Temperature");
                    humidity = jsonObj.getDouble("Humidity");
                    pressure = jsonObj.getDouble("Pressure");
                    brightness = jsonObj.getString("Brightness");
                    noise = jsonObj.getDouble("Noise");
                    people = jsonObj.getInt("NumberOfPeople");
                    WiFiSpeed = jsonObj.getDouble("WiFiSpeed");

                    update();
                }
                catch (final JSONException e)
                {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), "JSON parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
            else
            {
                Log.e(TAG, "Couldn't get JSON from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "No server connection!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();

        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /*
             * Updating parsed JSON data into ListView
             */
        }
    }

    public void update()
    {
        Button buttonTemperature = (Button) findViewById(R.id.buttonTemperature);
        Button buttonHumidity = (Button) findViewById(R.id.buttonHumidity);
        Button buttonPressure = (Button) findViewById(R.id.buttonPressure);
        Button buttonBrightness = (Button) findViewById(R.id.buttonBrightness);
        Button buttonNoise = (Button) findViewById(R.id.buttonNoise);
        Button buttonPeople = (Button) findViewById(R.id.buttonPeople);
        Button buttonWiFiSpeed = (Button) findViewById(R.id.buttonWiFiSpeed);

        TextView temperatureOutput = (TextView) findViewById(R.id.textTemperature);
        TextView humidityOutput = (TextView) findViewById(R.id.textHumidity);
        TextView pressureOutput = (TextView) findViewById(R.id.textPressure);
        TextView brightOutput = (TextView) findViewById(R.id.textBrightness);
        TextView noiseOutput = (TextView) findViewById(R.id.textNoise);
        TextView peopleOutput = (TextView) findViewById(R.id.textPeople);
        TextView WiFiSpeedOutput = (TextView) findViewById(R.id.textWiFiSpeed);

        if (temperature < 5)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_1_5);
            backgroundTemperature = "thermometer_1_5";
        }
        else if (temperature >= 5 && temperature < 10)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_5_10);
            backgroundTemperature = "thermometer_5_10";
        }
        else if (temperature >= 10 && temperature < 15)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_10_15);
            backgroundTemperature = "thermometer_10_15";
        }
        else if (temperature >= 15 && temperature < 20)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_15_20);
            backgroundTemperature = "thermometer_15_20";
        }
        else if (temperature >= 20 && temperature < 25)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_20_25);
            backgroundTemperature = "thermometer_20_25";
        }
        else if (temperature >= 25 && temperature < 30)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_25_30);
            backgroundTemperature = "thermometer_25_30";
        }
        else if (temperature >= 30 && temperature < 35)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_30_35);
            backgroundTemperature = "thermometer_30_35";
        }
        else if (temperature >= 35 && temperature < 40)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_35_40);
            backgroundTemperature = "thermometer_35_40";
        }
        else if (temperature >= 40 && temperature < 45)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_40_45);
            backgroundTemperature = "thermometer_40_45";
        }
        else if (temperature >= 45)
        {
            buttonTemperature.setBackgroundResource(R.drawable.thermometer_45_50);
            backgroundTemperature = "thermometer_45_50";
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
        else if(humidity >= 75 && humidity <= 100)
        {
            buttonHumidity.setBackgroundResource(R.drawable.humidity4);
            backgroundHumidity = "humidity4";
        }

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

        String brightnessLowerCase = brightness.toLowerCase();

        if(brightnessLowerCase.contains("dark"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_dark);
            backgroundBrightness = "brightness_dark";
        }
        else if(brightnessLowerCase.contains("dim"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_dim);
            backgroundBrightness = "brightness_dim";
        }
        else if(brightnessLowerCase.contains("light"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_light);
            backgroundBrightness = "brightness_light";
        }
        else if(brightnessLowerCase.contains("bright"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_bright);
            backgroundBrightness = "brightness_bright";
        }
        else if(brightnessLowerCase.contains("very bright"))
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness_verybright);
            backgroundBrightness = "brightness_verybright";
        }
        else
        {
            buttonBrightness.setBackgroundResource(R.drawable.brightness);
            backgroundBrightness = "brightness";
        }

        if(noise < 30)
        {
            buttonNoise.setBackgroundResource(R.drawable.noise_green);
            backgroundNoise = "noise_green";
        }
        else if(noise >= 30 && noise < 60)
        {
            buttonNoise.setBackgroundResource(R.drawable.noise_orange);
            backgroundNoise = "noise_orange";
        }
        else if(noise >= 60)
        {
            buttonNoise.setBackgroundResource(R.drawable.noise_red);
            backgroundNoise = "noise_red";
        }

        if(people < 8)
        {
            buttonPeople.setBackgroundResource(R.drawable.people_green);
            backgroundPeople = "people_green";
        }
        else if(people >= 8 && people < 15)
        {
            buttonPeople.setBackgroundResource(R.drawable.people_yellow);
            backgroundPeople = "people_yellow";
        }
        else if(people >= 15)
        {
            buttonPeople.setBackgroundResource(R.drawable.team);
            backgroundPeople = "team";
        }

        if(WiFiSpeed < 10)
        {
            buttonWiFiSpeed.setBackgroundResource(R.drawable.wifi_red);
            backgroundWiFiSpeed = "wifi_red";
        }
        else if(WiFiSpeed >= 10 && WiFiSpeed < 20)
        {
            buttonWiFiSpeed.setBackgroundResource(R.drawable.wifi_orange);
            backgroundWiFiSpeed = "wifi_orange";
        }
        else if(WiFiSpeed >= 20 && WiFiSpeed < 40)
        {
            buttonWiFiSpeed.setBackgroundResource(R.drawable.wifi_yellow);
            backgroundWiFiSpeed = "wifi_yellow";
        }
        else if(WiFiSpeed >= 40)
        {
            buttonWiFiSpeed.setBackgroundResource(R.drawable.wifi_green2);
            backgroundWiFiSpeed = "wifi_green2";
        }

        temperature = Math.round(temperature * 100.0) / 100.0; // Arredondar o valor para 2 casas decimais
        humidity = Math.round(humidity * 100.0) / 100.0;
        pressure = Math.round(pressure * 100.0) / 100.0;
        noise = Math.round(noise * 100.0) / 100.0;
        WiFiSpeed = Math.round(WiFiSpeed * 100.0) / 100.0;

        if(temperature >= -50)
        {
            temperatureOutput.setText(temperature + " ºC");
        }
        else
        {
            temperatureOutput.setText(UNAVAILABLE_DATA);
        }

        if(humidity >= 0)
        {
            humidityOutput.setText(humidity + "%");
        }
        else
        {
            humidityOutput.setText(UNAVAILABLE_DATA);
        }

        if(pressure >= 0)
        {
            pressureOutput.setText(pressure + " hPa");
        }
        else
        {
            pressureOutput.setText(UNAVAILABLE_DATA);
        }

        if(brightness.contains("-9999"))
        {
            brightOutput.setText(UNAVAILABLE_DATA);
        }
        else
        {
            brightOutput.setText(brightness);
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

        if(WiFiSpeed >= 0)
        {
            WiFiSpeedOutput.setText(WiFiSpeed + " Mb/s");
        }
        else
        {
            WiFiSpeedOutput.setText(UNAVAILABLE_DATA);
        }
    }

    public void sensorFactory(View view)
    {
        switch (view.getId())
        {
            case R.id.buttonTemperature:
                startActivity(new Intent(getApplicationContext(), SensorData.class).putExtra("sensor", "Temperature").putExtra("background", backgroundTemperature).putExtra("value", temperature + " ºC"));
                break;
            case R.id.buttonHumidity:
                startActivity(new Intent(getApplicationContext(), SensorData.class).putExtra("sensor", "Humidity").putExtra("background", backgroundHumidity).putExtra("value", (humidity + "%")));
                break;
            case R.id.buttonPressure:
                startActivity(new Intent(getApplicationContext(), SensorData.class).putExtra("sensor", "Pressure").putExtra("background", backgroundPressure).putExtra("value", (pressure + " hPa")));
                break;
            case R.id.buttonBrightness:
                startActivity(new Intent(getApplicationContext(), SensorData.class).putExtra("sensor", "Brightness").putExtra("background", backgroundBrightness).putExtra("value", brightness));
                break;
            case R.id.buttonNoise:
                startActivity(new Intent(getApplicationContext(), SensorData.class).putExtra("sensor", "Noise").putExtra("background", backgroundNoise).putExtra("value", noise + "/" + MAXIMUM_NOISE_LEVEL + " dB"));
                break;
            case R.id.buttonPeople:
                if(people == 1)
                {
                    startActivity(new Intent(getApplicationContext(), SensorData.class).putExtra("sensor", "Number of people").putExtra("background", backgroundPeople).putExtra("value", people + "/" + MAXIMUM_NUMBER_OF_PEOPLE + " person"));
                }
                else
                {
                    startActivity(new Intent(getApplicationContext(), SensorData.class).putExtra("sensor", "Number of people").putExtra("background", backgroundPeople).putExtra("value", people + "/" + MAXIMUM_NUMBER_OF_PEOPLE + " people"));
                }
                break;
            case R.id.buttonWiFiSpeed:
                startActivity(new Intent(getApplicationContext(), SensorData.class).putExtra("sensor", "Wi-Fi network speed").putExtra("background", backgroundWiFiSpeed).putExtra("value", (WiFiSpeed + " Mb/s")));
                break;
        }
    }
}