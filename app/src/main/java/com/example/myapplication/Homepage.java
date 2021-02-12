package com.example.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Homepage extends AppCompatActivity{

    private LineChart chart;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private TextView tvTitle;
    long reference_timestamp;

    ArrayList<Long> acTime = new ArrayList<>();
    ArrayList<Long> newAcTime = new ArrayList<>();
    ArrayList<Long> acValues = new ArrayList<>();
    ArrayList<Entry> result = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        tvTitle = findViewById(R.id.tvTitle);
        chart = findViewById(R.id.petChart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        LocalDateTime dt = LocalDateTime.of(2021, Month.FEBRUARY, 8, 0, 0, 0, 0);
        LocalDateTime dt2 = LocalDateTime.of(2021, Month.FEBRUARY, 8, 23, 59, 59, 59);

        ZonedDateTime zdt = dt.atZone(ZoneId.systemDefault());
        ZonedDateTime zdt2 = dt2.atZone(ZoneId.systemDefault());

        long startTime = zdt.toInstant().toEpochMilli()/1000;
        System.out.println("startTime:" + startTime);
        long endTime = zdt2.toInstant().toEpochMilli()/1000;
        System.out.println("endTime:" + endTime);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users/" + userID + "/Data");
        Query query = reference.orderByKey().startAt(String.valueOf(startTime)).endAt(String.valueOf(endTime));

        System.out.println("Printing query:" + query);

        System.out.println("Printing reference:" + reference);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("getting accelerometer values");
                for (DataSnapshot valueSnapshot : dataSnapshot.getChildren())
                {
                    acValues.add(Long.parseLong((valueSnapshot.getValue(String.class))));
                    acTime.add(Long.parseLong((valueSnapshot.getKey())));
                }

                System.out.println(acValues.size());

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

                System.out.println("setting results array");
                for (int i = 0; i < acTime.size(); i++)
                {
                    result.add(new Entry(newAcTime.get(i), acValues.get(i)));
                }

                System.out.println("setting dataset");

                Date date = new Date(acTime.get(0)*1000);
                DateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                String formatted = format.format(date);

                tvTitle.setText("Accelerometer Data for " + formatted);




                LineDataSet set1 = new LineDataSet(result, "Magnitude of Accelerometer Data");

                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                chart.setDrawGridBackground(false);
                chart.setHighlightPerDragEnabled(true);

                chart.setBackgroundColor(Color.WHITE);

                chart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        chart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int offset = (chart.getHeight() - chart.getWidth()) / 100;

                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) chart.getLayoutParams();
                        layoutParams.width = chart.getHeight();
                        layoutParams.height = chart.getWidth();
                        chart.setLayoutParams(layoutParams);

                        chart.setTranslationX(offset);
                        chart.setTranslationY(offset);

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
                        IMarker marker = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            marker = new YourMarkerView(getApplicationContext(), R.layout.marker, reference_timestamp);
                        }
                        chart.setMarker(marker);
                        chart.setExtraOffsets(10, 10, 10, 10);



                        Legend l = chart.getLegend();

                        l.setForm(Legend.LegendForm.LINE);
                        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                        l.setDrawInside(false);


                        XAxis xAxis = chart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextSize(16f);
                        xAxis.setValueFormatter(new XAxisValueFormatter(reference_timestamp));
                        xAxis.setDrawGridLines(false);
                        xAxis.setCenterAxisLabels(true);
                        xAxis.setLabelCount(5);
                        xAxis.setCenterAxisLabels(true);

                        YAxis leftAxis = chart.getAxisLeft();
                        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        leftAxis.setDrawGridLines(false);
                        leftAxis.setGranularityEnabled(true);
                        leftAxis.setLabelCount(5);
                        leftAxis.setTextSize(16f);
                        leftAxis.setTextColor(Color.BLACK);

                        YAxis rightAxis = chart.getAxisRight();
                        rightAxis.setEnabled(false);

                        System.out.println("notifying data sets changed");
                        chart.notifyDataSetChanged();

                        System.out.println("invalidate");
                        chart.invalidate();
                    }
                });


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

}