package com.example.jorgeduarte.appmastersroom;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

class WifiReceiver extends BroadcastReceiver
{
    private MediaRecorder recorder;

    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID().toLowerCase();

        if (name.contains("e-mei"))
        {
            Log.d("WifiReceiver", "Don't have Wi-Fi connection" + name);

            checkSpeedWifi(context);
            sendNotification(context);

            start();
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
            } catch (IllegalStateException | IOException e) {
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

    public void checkSpeedWifi(Context context)
    {
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
        protected Void doInBackground(Void... arg0)
        {
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            // Showing progress dialog
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            // Dismiss the progress dialog

            /*
             * Updating parsed JSON data into ListView
             * */
        }
    }
}