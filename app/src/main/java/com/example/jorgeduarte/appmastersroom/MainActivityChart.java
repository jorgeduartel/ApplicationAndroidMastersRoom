package com.example.jorgeduarte.appmastersroom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivityChart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chart);

        BarChart barChart = (BarChart) findViewById(R.id.chart);

        ArrayList<BarEntry> group1 = new ArrayList<>();
        group1.add(new BarEntry(4f, 0));
        group1.add(new BarEntry(8f, 1));
        group1.add(new BarEntry(6f, 2));
        group1.add(new BarEntry(12f, 3));
        group1.add(new BarEntry(18f, 4));
        group1.add(new BarEntry(9f, 5));


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


    private ArrayList<String> getXAxisValues() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Su");
        labels.add("Mo");
        labels.add("Tu");
        labels.add("We");
        labels.add("Th");
        labels.add("Fr");
        labels.add("Sa");
        return labels;
    }
}
