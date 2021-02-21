package com.example.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;

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

//        tvTitle = findViewById(R.id.tvTitle);
        chart = findViewById(R.id.petChart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        LineData data = new LineData();
        chart.setData(data);

        LocalDateTime dt = LocalDateTime.of(2021, Month.FEBRUARY, 20, 0, 0, 0, 0);
        LocalDateTime dt2 = LocalDateTime.of(2021, Month.FEBRUARY, 21, 23, 59, 59, 59);

        long startTime = dt.toEpochSecond(ZoneOffset.UTC);
        System.out.println("startTime:" + startTime + " " + dt);
        long endTime = dt2.toEpochSecond(ZoneOffset.UTC);
        System.out.println("endTime:" + endTime + " " + dt2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users/" + userID + "/Data");
        Query query = reference.orderByKey().startAt(String.valueOf(startTime)).endAt(String.valueOf(endTime));

        System.out.println("Printing query:" + query);

        System.out.println("Printing reference:" + reference);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                chart.setDrawGridBackground(false);
                chart.setHighlightPerDragEnabled(true);
                XAxis xl = chart.getXAxis();
                xl.setTextColor(Color.WHITE);
                xl.setDrawGridLines(false);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setTextColor(Color.WHITE);
                leftAxis.setAxisMaximum(100f);
                leftAxis.setAxisMinimum(0f);
                leftAxis.setDrawGridLines(true);

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);

                LineData data = chart.getData();

                System.out.println("dataChart: " + data);

                if (data != null)
                {
                    ILineDataSet set = data.getDataSetByIndex(0);

                    if (set == null)
                    {
                        System.out.println("set is null: " + set);
                        set = createSet();

                        data.addDataSet(set);

                        for (DataSnapshot valueSnapshot : dataSnapshot.getChildren())
                        {
                            long acValue = Long.parseLong((valueSnapshot.getValue(String.class)));
                            long acEpoch = Long.parseLong((valueSnapshot.getKey()));

                            data.addEntry(new Entry(set.getEntryCount(), (float)acValue), 0);

                            System.out.println("data.addEntry: " + data.getYMax());
                        }
                    }

/*                    System.out.println("set is not null: " + set);
                    for (DataSnapshot valueSnapshot : dataSnapshot.getChildren())
                    {
                        long acValue = Long.parseLong((valueSnapshot.getValue(String.class)));
                        long acEpoch = Long.parseLong((valueSnapshot.getKey()));

                        data.addEntry(new Entry((float) acEpoch, (float)acValue), 0);

                        System.out.println("data.addEntry: " + data.getYMax());
                    }*/

                    chart.setData(data);
                    data.notifyDataChanged();
                    chart.notifyDataSetChanged();

                    chart.moveViewToX(data.getEntryCount());
                    chart.invalidate();
                    System.out.println("set is not null: " + set);
                }

                System.out.println("dataChart: " + data);


            }

            private ILineDataSet createSet() {
                LineDataSet set = new LineDataSet(null, "Dynamic Data");
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setColor(ColorTemplate.getHoloBlue());
                set.setCircleColor(Color.WHITE);
                set.setLineWidth(2f);
                set.setCircleRadius(4f);
                set.setFillAlpha(65);
                set.setFillColor(ColorTemplate.getHoloBlue());
                set.setHighLightColor(Color.rgb(244, 117, 117));
                set.setValueTextColor(Color.WHITE);
                set.setValueTextSize(9f);
                set.setDrawValues(false);
                return set;
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

}