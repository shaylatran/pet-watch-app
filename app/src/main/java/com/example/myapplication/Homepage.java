package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Homepage extends AppCompatActivity{

    private LineChart chart;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    ArrayList<Double> xValues = new ArrayList<>();
    ArrayList<Double> yValues = new ArrayList<>();
    ArrayList<Double> zValues = new ArrayList<>();
    ArrayList<String> AcTime = new ArrayList<>();
    ArrayList<Long> AcTimeinLong = new ArrayList<>();
    ArrayList<Entry> result = new ArrayList<>();
    ArrayList<Double> trackX = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        chart = findViewById(R.id.petChart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users/" + userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot acxSnapshot = dataSnapshot.child("AcX");
                DataSnapshot acySnapshot = dataSnapshot.child("AcY");
                DataSnapshot aczSnapshot = dataSnapshot.child("AcZ");
                DataSnapshot acTimeSnapshot = dataSnapshot.child("AcTime");

                long convert = 1000L;
                float counter = 0;
                float counter1 = 0;
                float counter2 = 0;
                float counter3 = 0;

                for (DataSnapshot valueSnapshot : acxSnapshot.getChildren())
                {
                    counter1+=1;
                    xValues.add((valueSnapshot.getValue(Double.class)/convert));
                }

                for (DataSnapshot valueSnapshot : acySnapshot.getChildren())
                {
                    counter2+=1;
                    yValues.add((valueSnapshot.getValue(Double.class))/convert);
                }

                for (DataSnapshot valueSnapshot : aczSnapshot.getChildren())
                {
                    counter3+=1;
                    zValues.add((valueSnapshot.getValue(Double.class))/convert);
                }

                for (DataSnapshot valueSnapshot : acTimeSnapshot.getChildren())
                {

                    AcTime.add(valueSnapshot.getValue(String.class));
                }

                for (int i = 0; i < yValues.size(); i++)
                {
                    counter += 1;
                    double sqrtResult = Math.sqrt((xValues.get(i)*xValues.get(i)) + ((yValues.get(i)*yValues.get(i))) + ((zValues.get(i)*zValues.get(i))));
                    trackX.add(sqrtResult);
                    result.add(new Entry(counter, (float)sqrtResult));
//                    System.out.println(result.get(i));
                }

                LineDataSet set1 = new LineDataSet(result, "Magnitude of Accelerometer Data");

                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                chart.setDrawGridBackground(false);
                chart.setHighlightPerDragEnabled(true);

                chart.setBackgroundColor(Color.WHITE);
                chart.setViewPortOffsets(0f, 0f, 0f, 0f);

                set1.setFillAlpha(110);
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.getHoloBlue());
                set1.setValueTextColor(ColorTemplate.getHoloBlue());
                set1.setLineWidth(1.5f);
                set1.setDrawCircles(true);
                set1.setDrawValues(true);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(true);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                LineData data2 = new LineData(dataSets);

                chart.setData(data2);

//                Legend l = chart.getLegend();
//
//                l.setForm(Legend.LegendForm.LINE);
//                Typeface tfLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");;
//                l.setTypeface(tfLight);
//                l.setTextColor(Color.WHITE);
//                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//                l.setDrawInside(false);
//                l.setXEntrySpace(2f);
//                l.setYEntrySpace(0f);
//                l.setYOffset(0f);

                double dMinX = Collections.min(trackX);
                System.out.println(dMinX);
                int fMinX = (int) dMinX;

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);
                xAxis.setAxisMinimum(0);
                xAxis.setAxisMaximum(100);
                xAxis.setTextColor(Color.rgb(255, 192, 56));
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(1f);

                xAxis.setValueFormatter(new IndexAxisValueFormatter() {

                    private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {

                        long millis = TimeUnit.HOURS.toMillis((long) value);
                        return mFormat.format(new Date(millis));
                    }
                });

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                leftAxis.setTextColor(ColorTemplate.getHoloBlue());
                leftAxis.setDrawGridLines(false);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setAxisMinimum(fMinX);
//                leftAxis.setAxisMaximum(fMinX);
                leftAxis.setYOffset(0f);
                leftAxis.setTextColor(Color.rgb(255, 192, 56));

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);

                chart.invalidate();


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

}