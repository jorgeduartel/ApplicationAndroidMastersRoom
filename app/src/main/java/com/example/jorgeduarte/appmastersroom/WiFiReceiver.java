package com.example.jorgeduarte.appmastersroom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationManager;
/**
 * Created by jorgeduarte on 06/05/17.
 */

class WifiReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID();
        int rssi = wifiInfo.getRssi();
        int speed = wifiInfo.getLinkSpeed();

        if (name.contains("e-MEI")){


            Log.d("WifiReceiver", "Don't have Wifi Connection"+name+"  speed"+speed);

            checkSpeedWifi(context);
            sendNotification(context);

        }

    }

    public void sendNotification(Context context){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, Activity_PeopleRoom.class);
        PendingIntent pintent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.team)
                        .setContentTitle("Masters Room")
                        .setContentText("Please count the number of people in the room")
                        .setContentIntent(pintent);




        mNotificationManager.notify(1, mBuilder.build());
    }

    public void checkSpeedWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int rssi = wifiInfo.getRssi();
        int speed = wifiInfo.getLinkSpeed();

        Log.d("WifiReceiver", "Wifi Connection  "+rssi+ "  speed"+speed);
    }

};