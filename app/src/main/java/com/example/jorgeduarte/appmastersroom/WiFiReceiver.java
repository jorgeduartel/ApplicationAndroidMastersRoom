package com.example.jorgeduarte.appmastersroom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jorgeduarte on 06/05/17.
 */

class WifiReceiver extends BroadcastReceiver {

    private double amplitudeDb;
    private double amplitudeDbF;
    private MediaRecorder recorder;
    private double noise = -9999;
    private int speed  = -9999;
    private ProgressDialog pDialog;
    private volatile Thread verifyNoise;
    private static String url = com.example.jorgeduarte.appmastersroom.url.getUrl();


    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID().toLowerCase();
        int rssi = wifiInfo.getRssi();

        if (name.contains("wifi")) {

            speed = wifiInfo.getLinkSpeed();
            Log.d("WifiReceiver", "Don't have Wi-Fi connection" + name);


            checkSpeedWifi(context);
            sendNotification(context);



            start();
            while (noise<0) {
              double s =  getAmplitude();
                Log.d("Noise","  Noise" + noise);

            }
            stop();

            new GetData().execute();

        }
    }

    public void start() {
        if (recorder == null) {
            recorder = new MediaRecorder();
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
        }
    }

    public void stop() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }


    public double getAmplitude() {
        if (recorder != null) {


            int amplitude = recorder.getMaxAmplitude();
            amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));

            if(amplitudeDb>=0) {
                amplitudeDbF =  amplitudeDb;
                noise = amplitudeDb;
                return (recorder.getMaxAmplitude() / 2700.0);
            }else {
                return -9999;
            }
        }
        else
            return -9999;
    }




    public void sendNotification(Context context){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, Activity_PeopleRoom.class);
        PendingIntent pintent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.team)
                        .setContentTitle("Study Room")
                        .setContentText("Please, count the number of people in the room.")
                        .setContentIntent(pintent);

        mNotificationManager.notify(1, mBuilder.build());
    }

    public void checkSpeedWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int rssi = wifiInfo.getRssi();
        int speed = wifiInfo.getLinkSpeed();

        Log.d("WifiReceiver", "Wi-Fi connection  "+rssi+ "  speed"+speed);
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall((url+"sendNoise/"+(int)noise) , "POST");
            String jsonStr2 = sh.makeServiceCall((url+"sendWifiQuality/"+(int)speed) , "POST");


            Log.d("WifiReceiver", (url+"sendNoise/"+noise));

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                } catch (final JSONException e) {

                }
            } else {


            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog

            /**
             * Updating parsed JSON data into ListView
             * */
        }
    }
}