package com.example.jorgeduarte.appmastersroom;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

class HttpHandler
{
    private static final String TAG = HttpHandler.class.getSimpleName();

    HttpHandler() { }

    String makeServiceCall(String reqURL, String requestMethod)
    {
        String response = null;

        try
        {
            URL URL = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
            conn.setRequestMethod(requestMethod);

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        }
        catch (MalformedURLException e)
        {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }
        catch (ProtocolException e)
        {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        return response;
    }

    private String convertStreamToString(InputStream is)
    {
        BufferedReader reader;
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try
        {
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}