package com.example.jorgeduarte.appmastersroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.R.attr.x;
import static android.R.attr.y;
import static com.example.jorgeduarte.appmastersroom.R.id.imageView;

public class sensorData extends AppCompatActivity {

    public String sensor;
    public ArrayList<String> auxItens =  new ArrayList(); // array aux
    public String nameBackground;
    public ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private int type;
    private int position;
    private String value;
    private String[] brightness;
   // private double humidity= 2.551124572753906;
   // private double pressure = 102.260498046875;
    //private ArrayList <Double> temperature;
    private ArrayList <String> brightness2 =  new ArrayList();
    private ArrayList <String> date =  new ArrayList();
    private double humidity[] =  new double[20];
    private double temperature[] =  new double[20];
    private double pressure[] =  new double[20];
    private double people[] =  new double[20];
    private double noise[] =  new double[20];
    private double wifiSpeed[] =  new double[20];



    private static String url = com.example.jorgeduarte.appmastersroom.url.getUrl()+"getDate/";
    private static String url2= com.example.jorgeduarte.appmastersroom.url.getUrl()+"getday/3";
    private String TAG = sensorData.class.getSimpleName();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        sensor = bundle.getString("sensor");
        nameBackground = bundle.getString("background");
        value =  bundle.getString("value");

        new GetData().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        type = 0;


        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        sensor+" (Week)",
                        sensor+" (Last three days)",
                        sensor+" (Today)",
                }));


        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionS, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                position = positionS+1;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(positionS + 1, nameBackground, sensor, type, value, brightness2, date, humidity, temperature, pressure, people, noise, wifiSpeed))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        if (android.os.Build.VERSION.SDK_INT >= 21) { // Jorge
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.gray));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.dataType) {

            if(type == 0) {
                item.setIcon(R.drawable.grid);

                type = 1;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position, nameBackground,sensor, type, value, brightness2, date, humidity, temperature, pressure, people, noise, wifiSpeed))
                        .commit();

            }else {
                item.setIcon(R.drawable.chart);
                type = 0;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position, nameBackground,sensor, type, value, brightness2, date, humidity, temperature, pressure, people, noise, wifiSpeed))
                        .commit();

            }
        }

        return super.onOptionsItemSelected(item);
    }




    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url+"0" , "GET");
            String jsonStr1 = sh.makeServiceCall(url+"1" , "GET");
            String jsonStr2 = sh.makeServiceCall(url+"2" , "GET");
            String jsonStr3 = sh.makeServiceCall(url+"3" , "GET");
            String jsonStr4 = sh.makeServiceCall(url+"4" , "GET");
            String jsonStr5 = sh.makeServiceCall(url+"5" , "GET");
            String jsonStr6 = sh.makeServiceCall(url+"6" , "GET");

            //String jsonStr7 = sh.makeServiceCall(url2 , "GET");

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null && jsonStr1 != null && jsonStr2 != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject jsonObj1 = new JSONObject(jsonStr1);
                    JSONObject jsonObj2 = new JSONObject(jsonStr2);
                    JSONObject jsonObj3 = new JSONObject(jsonStr3);
                    JSONObject jsonObj4 = new JSONObject(jsonStr4);
                    JSONObject jsonObj5 = new JSONObject(jsonStr5);
                    JSONObject jsonObj6 = new JSONObject(jsonStr6);
                    //JSONObject jsonObj7 = new JSONObject(jsonStr7);

                    //people =  jsonObj.getString("people");



                    date.add(0, jsonObj.getString("date1"));
                    date.add(1, jsonObj1.getString("date1"));
                    date.add(2, jsonObj2.getString("date1"));
                    date.add(3, jsonObj3.getString("date1"));
                    date.add(4, jsonObj4.getString("date1"));
                    date.add(5, jsonObj5.getString("date1"));
                    date.add(6, jsonObj6.getString("date1"));


                    brightness2.add(0, jsonObj.getString("brightAverage"));
                    brightness2.add(1, jsonObj1.getString("brightAverage"));
                    brightness2.add(2, jsonObj2.getString("brightAverage"));
                    brightness2.add(3, jsonObj3.getString("brightAverage"));
                    brightness2.add(4, jsonObj4.getString("brightAverage"));
                    brightness2.add(5, jsonObj5.getString("brightAverage"));
                    brightness2.add(6, jsonObj6.getString("brightAverage"));


                    humidity[0] = jsonObj.getDouble("humidityAverage");
                    humidity[1] = jsonObj1.getDouble("humidityAverage");
                    humidity[2] = jsonObj2.getDouble("humidityAverage");
                    humidity[3] = jsonObj3.getDouble("humidityAverage");
                    humidity[4] = jsonObj4.getDouble("humidityAverage");
                    humidity[5] = jsonObj5.getDouble("humidityAverage");
                    humidity[6] = jsonObj6.getDouble("humidityAverage");


                    temperature[0] = jsonObj.getDouble("temperatureAverage");
                    temperature[1] = jsonObj1.getDouble("temperatureAverage");
                    temperature[2] = jsonObj2.getDouble("temperatureAverage");
                    temperature[3] = jsonObj3.getDouble("temperatureAverage");
                    temperature[4] = jsonObj4.getDouble("temperatureAverage");
                    temperature[5] = jsonObj5.getDouble("temperatureAverage");
                    temperature[6] = jsonObj6.getDouble("temperatureAverage");

                    pressure[0] = jsonObj.getDouble("pressureAverage");
                    pressure[1] = jsonObj1.getDouble("pressureAverage");
                    pressure[2] = jsonObj2.getDouble("pressureAverage");
                    pressure[3] = jsonObj3.getDouble("pressureAverage");
                    pressure[4] = jsonObj4.getDouble("pressureAverage");
                    pressure[5] = jsonObj5.getDouble("pressureAverage");
                    pressure[6] = jsonObj6.getDouble("pressureAverage");

                    people[0] = jsonObj.getDouble("peopleAverage");
                    people[1] = jsonObj1.getDouble("peopleAverage");
                    people[2] = jsonObj2.getDouble("peopleAverage");
                    people[3] = jsonObj3.getDouble("peopleAverage");
                    people[4] = jsonObj4.getDouble("peopleAverage");
                    people[5] = jsonObj5.getDouble("peopleAverage");
                    people[6] = jsonObj6.getDouble("peopleAverage");

                    noise[0] = jsonObj.getDouble("noiseAverage");
                    noise[1] = jsonObj1.getDouble("noiseAverage");
                    noise[2] = jsonObj2.getDouble("noiseAverage");
                    noise[3] = jsonObj3.getDouble("noiseAverage");
                    noise[4] = jsonObj4.getDouble("noiseAverage");
                    noise[5] = jsonObj5.getDouble("noiseAverage");
                    noise[6] = jsonObj6.getDouble("noiseAverage");

                    wifiSpeed[0] = jsonObj.getDouble("wifiAverage");
                    wifiSpeed[1] = jsonObj1.getDouble("wifiAverage");
                    wifiSpeed[2] = jsonObj2.getDouble("wifiAverage");
                    wifiSpeed[3] = jsonObj3.getDouble("wifiAverage");
                    wifiSpeed[4] = jsonObj4.getDouble("wifiAverage");
                    wifiSpeed[5] = jsonObj5.getDouble("wifiAverage");
                    wifiSpeed[6] = jsonObj6.getDouble("wifiAverage");


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
            pDialog = new ProgressDialog(sensorData.this);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public ArrayList<String> auxItens =  new ArrayList(); // array aux
        public ListView listView;
        private ArrayList<String> arrayList;
        private ArrayAdapter<String> adapter;


        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String nameBackground, String sensor, int type, String value, ArrayList <String> brightness, ArrayList <String> date, double humidity[], double temperature[], double pressure[], double people[], double noise[], double wifiSpeed[]) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("nameBackground", nameBackground);
            args.putString("sensor", sensor);
            args.putInt("type", type);
            args.putString("value", value);
            args.putDoubleArray("Humidity", humidity);
            args.putDoubleArray("Temperature", temperature);
            args.putDoubleArray("Pressure", pressure);
            args.putDoubleArray("Number of people", people);
            args.putDoubleArray("Noise", noise);
            args.putDoubleArray("Wi-Fi network speed", wifiSpeed);
            args.putStringArrayList("date", date);
            args.putStringArrayList("Brightness", brightness);

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle bundle = this.getArguments();
            int type = bundle.getInt("type");
            String sensor = bundle.getString("sensor");

            if (type==0) {
                View rootView = inflater.inflate(R.layout.fragment_sensor_data, container, false);


                TextView textView = (TextView) rootView.findViewById(R.id.textValue);

                textView.setText(("Now "+getArguments().getString("value")));

                TextView textView2 = new TextView(getActivity().getApplicationContext());
                textView2.setText("Date                                    Value");

            if(getArguments().getDoubleArray("Temperature")[0] > 0 )
            {
                switch (sensor) {
                    case "Temperature":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " ºc", 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " ºc", 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " ºc", 0);
                        }

                        break;
                    case "Brightness":
                        for (String value: getArguments().getStringArrayList(sensor)) {
                            for (String date: getArguments().getStringArrayList("date")) {

                                auxItens.add(""+date +"                                    "+value);
                            }
                        }
                        break;
                    case "Wi-Fi network speed":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " mbps", 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " mbps", 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " mbps", 0);
                        }
                        break;
                    case "Noise":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " db", 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " db", 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " db", 0);
                        }
                        break;
                    case "Number of people":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " un", 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " un", 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " un", 0);
                        }
                        break;
                    case "Humidity":

                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " %", 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " %", 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " %", 0);
                        }
                        break;
                    case "Pressure":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " Br", 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " Br", 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " Br", 0);
                        }
                        break;
                }

                adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, auxItens);
                listView = (ListView) rootView.findViewById(R.id.dataList);
                listView.setAdapter(adapter);
                listView.addHeaderView(textView2);
            }
                String nameBackground = bundle.getString("nameBackground");

                ImageView imageSensor = (ImageView) rootView.findViewById(R.id.imageSensor);


                ImageView colorView = (ImageView) rootView.findViewById(R.id.colorView);
                Drawable d = getDrawable(nameBackground);
                imageSensor.setBackground(d);

                return rootView;
            }else if (type == 1){
                View rootView = inflater.inflate(R.layout.fragment_sensor_chart, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.textValue);

                textView.setText(("Now "+getArguments().getString("value")));

                BarChart barChart = (BarChart) rootView.findViewById(R.id.chart);

                ArrayList<BarEntry> group1 = new ArrayList<>();

                if(getArguments().getDoubleArray("Temperature")[0] > 0) {

                    if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                        double values[] = getArguments().getDoubleArray(sensor);

                        for (int i = 0; i <= 6; i++) {

                            if (values[i] >= 0) {
                                group1.add(new BarEntry((float) values[i], i));
                            }
                        }

                    } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {

                        double values[] = getArguments().getDoubleArray(sensor);
                        for (int i = 0; i <= 2; i++) {

                            if (values[i] >= 0) {
                                group1.add(new BarEntry((float) values[i], i));
                            }

                        }
                    } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                        double values[] = getArguments().getDoubleArray(sensor);
                        for (int i = 0; i <= 2; i++) {

                            if (values[i] >= 0) {
                                group1.add(new BarEntry((float) values[i], i));
                            }

                        }
                    }

                    BarDataSet barDataSet1 = new BarDataSet(group1, "Group 1");
                    //barDataSet1.setColor(Color.rgb(0, 155, 0));
                    barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);


                    ArrayList<BarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(barDataSet1);

                    BarData data = new BarData(getXAxisValues(), dataSets);
                    barChart.setData(data);
                    //   barChart.setDescription("My Grouped Bar Chart");
                    barChart.animateXY(2000, 2000);
                    barChart.invalidate();
                }
                String nameBackground = bundle.getString("nameBackground");

                ImageView imageSensor = (ImageView) rootView.findViewById(R.id.imageSensor);

                ImageView colorView = (ImageView) rootView.findViewById(R.id.colorView);
                Drawable d = getDrawable(nameBackground);
                imageSensor.setBackground(d);

                return rootView;

            }
            else {
                View rootView = inflater.inflate(R.layout.fragment_sensor_data, container, false);
                return rootView;
            }
        }

        public Drawable getDrawable(String name) {
            Context context = getActivity().getApplicationContext();
            int resourceId = context.getResources().getIdentifier(name, "drawable", getActivity().getApplicationContext().getPackageName());
            return context.getResources().getDrawable(resourceId);
        }

        public void getData(String sensor, String unit, int length) {
            double values[] = getArguments().getDoubleArray(sensor);
            ArrayList <String> date =  getArguments().getStringArrayList("date");
            for(int i=0; i<=length; i++){
                if(!Double.isNaN(values[i]) && date.get(i) != null){

                    if(values[i] < 0){
                        auxItens.add("" + date.get(i) + "                        " + "Unavailable data");
                    }else {
                        auxItens.add("" + date.get(i) + "                                    " + values[i] + unit);
                    }
                }
            }

        }

        private ArrayList<String> getXAxisValues() {
            ArrayList<String> labels = new ArrayList<>();
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
            labels.add("Su");
            labels.add("Mo");
            labels.add("Tu");
            labels.add("We");
            labels.add("Th");
            labels.add("Fr");
            labels.add("Sa");
           }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                labels.add("Before yesterday");
                labels.add("Yesterday");
                labels.add("Today");
            }else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                labels.add("Before yesterday");
                labels.add("Yesterday");
                labels.add("Today");
            }
            return labels;
        }
    }
}