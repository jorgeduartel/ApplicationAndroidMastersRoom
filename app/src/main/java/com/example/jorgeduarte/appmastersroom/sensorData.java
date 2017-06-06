package com.example.jorgeduarte.appmastersroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class sensorData extends AppCompatActivity
{
    private static final String UNAVAILABLE_DATA = "Unavailable";

    public String sensor;
    public ArrayList<String> auxItens =  new ArrayList(); // array aux
    public String nameBackground;
    private int type;
    private int position;
    private String value;
    private ArrayList <String> brightness = new ArrayList();
    private ArrayList <String> date = new ArrayList();
    private double humidity[] = new double[20];
    private double temperature[] = new double[20];
    private double pressure[] = new double[20];
    private double people[] = new double[20];
    private double noise[] = new double[20];
    private double wifiSpeed[] = new double[20];

    private static String url = com.example.jorgeduarte.appmastersroom.url.getUrl()+"getDate/";
    private static String url2= com.example.jorgeduarte.appmastersroom.url.getUrl()+"getday/0";
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
        spinner.setSelection(2); // Para predefinir o spinner no dia "hoje"

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionS, long id) {
                // When the given dropdown item is selected, show its contents in the container view.
                position = positionS+1;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(positionS + 1, nameBackground, sensor, type, value, brightness, date, humidity, temperature, pressure, people, noise, wifiSpeed))
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
                        .replace(R.id.container, PlaceholderFragment.newInstance(position, nameBackground,sensor, type, value, brightness, date, humidity, temperature, pressure, people, noise, wifiSpeed))
                        .commit();

            }else {
                item.setIcon(R.drawable.chart);
                type = 0;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position, nameBackground,sensor, type, value, brightness, date, humidity, temperature, pressure, people, noise, wifiSpeed))
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
            HttpHandler sh2 = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr1 = sh.makeServiceCall(url+"1" , "GET");
            String jsonStr = sh.makeServiceCall(url+"0" , "GET");
            String jsonStr2 = sh.makeServiceCall(url+"2" , "GET");
            String jsonStr3 = sh.makeServiceCall(url+"3" , "GET");
            String jsonStr4 = sh.makeServiceCall(url+"4" , "GET");
            String jsonStr5 = sh.makeServiceCall(url+"5" , "GET");
            String jsonStr6 = sh.makeServiceCall(url+"6" , "GET");
            String jsonStr7 = sh.makeServiceCall(url2 , "GET");



            Log.e(TAG, "Response from url: " + jsonStr7);

            if (jsonStr7 != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject jsonObj1 = new JSONObject(jsonStr1);
                    JSONObject jsonObj2 = new JSONObject(jsonStr2);
                    JSONObject jsonObj3 = new JSONObject(jsonStr3);
                    JSONObject jsonObj4 = new JSONObject(jsonStr4);
                    JSONObject jsonObj5 = new JSONObject(jsonStr5);
                    JSONObject jsonObj6 = new JSONObject(jsonStr6);
                    JSONObject jsonObj7 = new JSONObject(jsonStr7);
                    JSONArray values = jsonObj7.getJSONArray("values");
                    //people =  jsonObj.getString("people");

                    date.add(0, jsonObj.getString("date1"));
                    date.add(1, jsonObj1.getString("date1"));
                    date.add(2, jsonObj2.getString("date1"));
                    date.add(3, jsonObj3.getString("date1"));
                    date.add(4, jsonObj4.getString("date1"));
                    date.add(5, jsonObj5.getString("date1"));
                    date.add(6, jsonObj6.getString("date1"));

                    brightness.add(0, jsonObj.getString("brightAverage"));
                    brightness.add(1, jsonObj1.getString("brightAverage"));
                    brightness.add(2, jsonObj2.getString("brightAverage"));
                    brightness.add(3, jsonObj3.getString("brightAverage"));
                    brightness.add(4, jsonObj4.getString("brightAverage"));
                    brightness.add(5, jsonObj5.getString("brightAverage"));
                    brightness.add(6, jsonObj6.getString("brightAverage"));

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

                    for (int i = 0; i < values.length(); i++) {
                        JSONObject value = values.getJSONObject(i);
                        Log.e(TAG, "Response value: " + (i+7));
                        humidity[(i+7)] = value.getDouble("humidity");
                        temperature[(i+7)] = value.getDouble("rpTemperature");
                        wifiSpeed[(i+7)] = value.getDouble("wifiQuality");
                        noise[(i+7)] = value.getDouble("noise");
                        people[(i+7)] = value.getDouble("nPessoas");
                        pressure[(i+7)] = value.getDouble("pressure");
                        brightness.add((i+7), value.getString("arduinoBrightValue"));
                        date.add((i+7), value.getString("hour"));

                    }



                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, PlaceholderFragment.newInstance(position, nameBackground,sensor, type, value, brightness, date, humidity, temperature, pressure, people, noise, wifiSpeed))
                            .commit();

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

                if(getArguments().getString("value").contains("-9999"))
                {
                    textView.setText(UNAVAILABLE_DATA);
                }
                else
                {
                    textView.setText(("Now "+getArguments().getString("value")));
                }

                TextView textView2 = new TextView(getActivity().getApplicationContext());
                textView2.setText("Date                                    Value");

            if(getArguments().getDoubleArray("Temperature")[0] > 0 )
            {
                switch (sensor) {
                    case "Temperature":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " ºC", 0, 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " ºC", 0, 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " ºC", 7, 19);
                        }
                        break;
                    case "Brightness":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getDataString(sensor, "", 0, 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getDataString(sensor, "", 0, 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getDataString(sensor, "", 7, 19);
                        }
                        break;
                    case "Wi-Fi network speed":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " Mb/s", 0, 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " Mb/s", 0, 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " Mb/s", 7, 19);
                        }
                        break;
                    case "Noise":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " dB", 0, 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " dB", 0, 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " dB", 7, 19);
                        }
                        break;
                    case "Number of people":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " people", 0, 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " people", 0, 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " people", 7, 19);
                        }
                        break;
                    case "Humidity":

                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " %", 0, 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " %", 0, 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " %", 7, 19);
                        }
                        break;
                    case "Pressure":
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                            getData(sensor, " hPa", 0, 6);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                            getData(sensor, " hPa", 0, 2);
                        }else
                        if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                            getData(sensor, " hPa", 7, 19);
                        }
                        break;
                }

                adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, auxItens);
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

                if(getArguments().getString("value").contains("-9999"))
                {
                    textView.setText(UNAVAILABLE_DATA);
                }
                else
                {
                    textView.setText(("Now "+getArguments().getString("value")));
                }

                BarChart barChart = (BarChart) rootView.findViewById(R.id.chart);

                ArrayList<BarEntry> group1 = new ArrayList<>();

                if(getArguments().getDoubleArray("Temperature")[0] > 0) {

                    if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                        if(sensor.contains("Brightness")){
                            getDataChartString(group1, sensor, "",0, 6);
                        }
                        else {
                            getDataChart(group1,sensor, "",0, 6);
                        }

                    } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {

                        if(sensor.contains("Brightness")){
                            getDataChartString(group1,sensor, "",0, 2);
                        }
                        else {
                            getDataChart(group1, sensor, "",0, 2);
                        }

                    } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                        if(sensor.contains("Brightness")){
                            getDataChartString(group1, sensor, "",7,19);
                        }
                        else {
                            getDataChart(group1, sensor, "",7,19);
                        }
                    }

                    BarDataSet barDataSet1 = new BarDataSet(group1, sensor);

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

        public void getDataChart(ArrayList<BarEntry> group1, String sensor, String unit, int startlength, int length) {


            double values[] = getArguments().getDoubleArray(sensor);

            for (int i = startlength; i <= length; i++) {

                if (values[i] >= 0) {
                    if(i > 6) {
                        int aux = i-6;
                        group1.add(new BarEntry((float) values[i], aux));
                    }else {
                        group1.add(new BarEntry((float) values[i], i));
                    }
                }

            }


        }

        public void getDataChartString(ArrayList<BarEntry> group1, String sensor, String unit, int startlength, int length) {


            ArrayList<String>  values = getArguments().getStringArrayList(sensor);
            ArrayList <String> date =  getArguments().getStringArrayList("date");

            float valueChart = -9999;
            for(int i=startlength; i<=length; i++){
                if(i < date.size()) {
                    if (!values.get(i).isEmpty()) {
                        switch (values.get(i)) {
                            case "Dark":
                                valueChart = 1;
                                break;
                            case "Dim":
                                valueChart = 2;
                                break;
                            case "Light":
                                valueChart = 3;
                                break;
                            case "Bright":
                                valueChart = 4;
                                break;
                            case "Very bright":
                                valueChart = 5;
                                break;
                            case "1":
                                valueChart = 1;
                                break;
                            case "2":
                                valueChart = 2;
                                break;
                            case "3":
                                valueChart = 3;
                                break;
                            case "4":
                                valueChart = 4;
                                break;
                            case "5":
                                valueChart = 5;
                                break;
                        }
                    } else {
                        valueChart = -9999;
                    }
                    if (valueChart >= 0) {
                        if(i > 6) {
                            int aux = i-6;
                            group1.add(new BarEntry((float) valueChart, aux));
                        }else {
                            group1.add(new BarEntry((float) valueChart, i));
                        }

                    }
                }
            }
        }

        public void getData(String sensor, String unit, int startlength, int length) {
            double values[] = getArguments().getDoubleArray(sensor);
            ArrayList <String> date =  getArguments().getStringArrayList("date");

            for(int i=startlength; i<=length; i++){
                if(i < date.size()) {
                    if (!Double.isNaN(values[i]) && date.get(i) != null) {

                        if (values[i] < 0) {
                            auxItens.add("" + date.get(i) + "                        " + "Unavailable data");
                        } else {
                            auxItens.add("" + date.get(i) + "                                    " + values[i] + unit);
                        }

                    }
                }
            }

        }



        public void getDataString(String sensor, String unit, int startlength, int length) {
            ArrayList<String>  values = getArguments().getStringArrayList(sensor);
            ArrayList <String> date =  getArguments().getStringArrayList("date");
            String value = null;
            for(int i= startlength; i<=length; i++){

                if(i < date.size()) {
                    if(!values.get(i).isEmpty()){
                        value = values.get(i);
                    }
                    if (value != null && date.get(i) != null) {

                        if (value.contains("-9999")) {
                            auxItens.add("" + date.get(i) + "                        " + "Unavailable data");
                        } else {
                            auxItens.add("" + date.get(i) + "                                    " + value + unit);
                        }
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
                labels.add("8:00");
                labels.add("9:00");
                labels.add("10:00");
                labels.add("11:00");
                labels.add("12:00");
                labels.add("13:00");
                labels.add("14:00");
                labels.add("15:00");
                labels.add("16:00");
                labels.add("17:00");
                labels.add("18:00");
                labels.add("19:00");
                labels.add("20:00");


            }
            return labels;
        }
    }
}