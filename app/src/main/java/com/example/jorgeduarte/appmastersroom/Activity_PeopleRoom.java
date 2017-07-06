package com.example.jorgeduarte.appmastersroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity_PeopleRoom extends AppCompatActivity
{
    // URL to get contacts JSON
    private String TAG = MainActivity.class.getSimpleName();
    private static String baseURL = URL.getURL() + "sendNumberOfPeople/";
    private static String url;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextView fab = (TextView) findViewById(R.id.peopleLable);
                Log.e(TAG, "SEND: " + fab.getText().toString());
                url = baseURL+fab.getText().toString();
                Log.e(TAG, "URL: " + url);

                 new GetData().execute(); // Quando o servidor está a funcionar
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    /**
     * Async task class to get JSON by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();

            // Making a request to URL and getting response
            String jsonStr = sh.makeServiceCall(url, "POST");

            Log.e(TAG, "Response from URL: " + jsonStr);

            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String response = jsonObj.getString("NumberOfPeople");

                    Log.e(TAG, "Response POST people: " + response);

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
            progressDialog = new ProgressDialog(Activity_PeopleRoom.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
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
              Updating parsed JSON data into ListView
              */
        }
    }
}