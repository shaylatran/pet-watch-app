package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Homepage extends AppCompatActivity{

    private LineChart chart;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private TextView tvContent;
    long reference_timestamp;

    ArrayList<Long> acTime = new ArrayList<>();
    ArrayList<Long> newAcTime = new ArrayList<>();
    ArrayList<Long> acValues = new ArrayList<>();
    ArrayList<Entry> result = new ArrayList<>();
    ArrayList<Entry> result2 = new ArrayList<>();

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
        System.out.println("Printing reference:" + reference);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                DataSnapshot acxSnapshot = dataSnapshot.child("AcX");
//                DataSnapshot acySnapshot = dataSnapshot.child("AcY");
//                DataSnapshot aczSnapshot = dataSnapshot.child("AcZ");
                DataSnapshot acSnapshot = dataSnapshot.child("Data");
//                DataSnapshot acTimeSnapshot = dataSnapshot.child("AcTime");

                long convert = 1000L;
                float counter = 0;
                float counter1 = 0;
                float counter2 = 0;
                float counter3 = 0;

                System.out.println("getting accelerometer values");
                for (DataSnapshot valueSnapshot : acSnapshot.getChildren())
                {
                    acValues.add(Long.parseLong((valueSnapshot.getValue(String.class))));
                    acTime.add(Long.parseLong((valueSnapshot.getKey())));
                }

//                for (int i = 0; i < acValues.size(); i++)
//                {
//                    System.out.println(acValues.get(i));
//                    System.out.println(acTime.get(i));
//                }

                long temp_time;
                for (int i = 0; i < acTime.size(); i++)
                {
                    reference_timestamp = acTime.get(0);
                    System.out.println("Printing acTime.get(0):" + acTime.get(0));
                    temp_time = acTime.get(i) - reference_timestamp;
                    System.out.println("Printing temp_time:" + temp_time);
                    newAcTime.add(temp_time);
                }

                for (int i = 0; i < newAcTime.size(); i++)
                {
                    System.out.println(newAcTime.get(i));
                }

//
//                for (int i = 0; i < AcTimeandDate.size(); i++)
//                {
////                    parts = AcTimeandDate.get(i).split(" ");
////                    System.out.println("Month: " + parts[0]);
////                    System.out.println("Date: " + parts[1]);
////                    System.out.println("Year: " + parts[2]);
////                    System.out.println("Time: " + parts[3]);
//
//                    try
//                    {
////                        DateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
////                        Date date = format.parse(AcTimeandDate.get(i));
//                        Calendar cal = Calendar.getInstance();
//                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
//                        cal.setTime(sdf.parse(AcTimeandDate.get(i)));
//
//                        Calendar cal2 = Calendar.getInstance();
//                        cal2.set(Calendar.YEAR, 2021);
//                        cal2.set(Calendar.MONTH, Calendar.JANUARY);
//                        cal2.set(Calendar.DAY_OF_MONTH, 29);
//
//                        if (cal.after(cal2))
//                        {
////                            System.out.println("In here");
////                            float fDate = (float) cal2.getTime();
//                            Date date = cal.getTime();
//                            float fDate = (float) date.getTime();
//                            System.out.println("Date:" + date);
//                            fTime.add(fDate);
//                        }
//                    }
//
//                    catch(Exception e)
//                    {
//                        System.out.println(e);
//                    }
//                }

                System.out.println("setting results array");
                for (int i = 0; i < acTime.size(); i++)
                {
                    result.add(new Entry(newAcTime.get(i), acValues.get(i)));
                }

                System.out.println("setting dataset");
                LineDataSet set1 = new LineDataSet(result, "Magnitude of Accelerometer Data");

                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                chart.setDrawGridBackground(false);
                chart.setHighlightPerDragEnabled(true);

                chart.setBackgroundColor(Color.WHITE);
                chart.setViewPortOffsets(0f, 0f, 0f, 0f);


                set1.setDrawValues(false);
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(Color.rgb(0,0,165));
                set1.setLineWidth(1.5f);
                set1.setDrawCircles(true);

                System.out.println("creating arraylist for datasets");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                System.out.println("adding dataset to set1");
                dataSets.add(set1);

                System.out.println("creating LineData data2 for datasets");
                LineData data2 = new LineData(dataSets);



                System.out.println("setting the data");
                chart.setData(data2);


                chart.getDescription().setText("");
                IMarker marker = new YourMarkerView(getApplicationContext(), R.layout.marker, reference_timestamp);
                chart.setMarker(marker);

                Legend l = chart.getLegend();

                l.setForm(Legend.LegendForm.LINE);
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(false);


                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                xAxis.setTextSize(16f);
                xAxis.setValueFormatter(new XAxisValueFormatter(reference_timestamp));
//                xAxis.setTextColor(Color.BLUE);
                xAxis.setDrawGridLines(false);
//                xAxis.setTextColor(Color.rgb(255, 192, 56));
                xAxis.setCenterAxisLabels(true);
                xAxis.setLabelCount(3);
                xAxis.setCenterAxisLabels(true);


                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                leftAxis.setDrawGridLines(false);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setLabelCount(3);
                leftAxis.setTextSize(16f);
                leftAxis.setTextColor(Color.BLACK);

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);

                System.out.println("notifying data sets changed");
                chart.notifyDataSetChanged();

                System.out.println("invalidate");
                chart.invalidate();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

}