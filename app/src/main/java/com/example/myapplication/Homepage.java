package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Homepage extends AppCompatActivity{

    private LineChart chart;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private String userID;
    private TextView tvTitle;

    ArrayList<Long> acTime = new ArrayList<>();
    ArrayList<Long> acValues = new ArrayList<>();
    long referenceTimestamp = 0;
    long threshold = 0;

    public String textTitle = "PetWatch Notification";
    public String textContent = "Your pet may be moving!";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        tvTitle = findViewById(R.id.tvTitle);
        chart = findViewById(R.id.petChart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        LineData data = new LineData();
        chart.setData(data);

//         original code for starting the graph over a 24-hour period each at
//         12 am of today's date to 12 am to tomorrow's date
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate tomorrow = today.plusDays(1);

        Instant instant = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        long startTime = instant.toEpochMilli()/1000;
        System.out.println(startTime);

        instant = tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant();
        long endTime = instant.toEpochMilli()/1000;
        System.out.println(endTime);

//        long startTime = 1618847128; // hardcoded times for April 19, 2021 at 4:45pm
//        long endTime = 1618962325; // April 20, 2021 at 4:45 pm for a 24-hour test

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users/" + userID + "/Data");
        reference2 = FirebaseDatabase.getInstance().getReference("Users/" + userID);
        Query query = reference.orderByKey().startAt(String.valueOf(startTime)).endAt(String.valueOf(endTime));
        createNotificationChannel();

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
                chart.getDescription().setText("");


                XAxis xl = chart.getXAxis();
                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                xl.setTextColor(Color.BLACK);
                xl.setTextSize(16f);
                xl.setLabelCount(2);
                xl.setDrawGridLines(false);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setTextColor(Color.BLACK);
                leftAxis.setTextSize(16f);
                leftAxis.setDrawGridLines(true);

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);

                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy");
                String formatted = format.format(today);

                tvTitle.setText("Accelerometer Data for " + formatted);

                LineData data = chart.getData();

                if (data != null)
                {
                    ILineDataSet set = data.getDataSetByIndex(0);

                    if (set == null)
                    {
                        set = createSet();

                        data.addDataSet(set);

                        long acEpoch = 0;
                        long acValue = 0;
                        int count = 0;
                        for (DataSnapshot valueSnapshot : dataSnapshot.getChildren()) {
                            acValue = Long.parseLong((valueSnapshot.getValue(String.class)));
                            acValues.add(acValue);

                            acEpoch = Long.parseLong((valueSnapshot.getKey()));

                            if (count == 0)
                            {
                                referenceTimestamp = acEpoch;
                                System.out.println("referencetimestamp: " + referenceTimestamp);
                            }

                            acEpoch += 28800;

                            acTime.add(acEpoch);

                            acEpoch = acEpoch - acTime.get(0);

                            data.addEntry(new Entry(acEpoch, acValue), 0);

                            count += 1;
                        }

                        xl.setValueFormatter(new XAxisValueFormatter(acTime.get(0)));

                    }

                    else
                    {
                        long acEpoch2 = 0;
                        long acValue2 = 0;
                        int count = 0;
                        for (DataSnapshot valueSnapshot : dataSnapshot.getChildren())
                        {
                            acValue2 = Long.parseLong((valueSnapshot.getValue(String.class)));
                            System.out.println("acValue: " + acValue2);
                            acValues.add(acValue2);
                            long acEpoch = Long.parseLong((valueSnapshot.getKey())) +28800;
                            acTime.add(acEpoch);

                            acEpoch2 = acEpoch - acTime.get(0);
                        }

                        long newAcVal = acValues.get(acValues.size()-1);
                        data.addEntry(new Entry(acEpoch2, newAcVal), 0);
                        xl.setValueFormatter(new XAxisValueFormatter(acTime.get(0)));

                    }

                    reference2.child("threshold").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            threshold = Long.parseLong(dataSnapshot.getValue(String.class));
                            System.out.println("Threshold:" + threshold);

                            if (acValues.get(acValues.size()-1) > threshold) {
                                Intent resultIntent = new Intent(getApplicationContext(), Homepage.class);

                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(Homepage.this);
                                stackBuilder.addNextIntentWithParentStack(resultIntent);
                                stackBuilder.addParentStack(Homepage.class);

                                PendingIntent resultPendingIntent =
                                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(Homepage.this, "PetWatch Channel")
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setContentTitle(textTitle)
                                        .setContentText(textContent)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                builder.setContentIntent(resultPendingIntent);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Homepage.this);

                                notificationManager.notify(100, builder.build());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    IMarker marker = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        marker = new YourMarkerView(getApplicationContext(), R.layout.marker, referenceTimestamp);
                    }
                    chart.setMarker(marker);
                    chart.setExtraOffsets(10, 10, 10, 10);

                    System.out.println("I break here");
                    System.out.println("no here");
                    chart.setData(data);
                    System.out.println("here");
                    data.notifyDataChanged();
                    chart.notifyDataSetChanged();

                    chart.invalidate();
                }

            }

            private ILineDataSet createSet() {
                LineDataSet set = new LineDataSet(null, "Movement of Pet");
                set.setDrawValues(false);
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setColor(Color.rgb(57,57,57));
                set.setLineWidth(3f);
                set.setDrawCircles(true);
                set.setCircleRadius(3.5f);
                set.setCircleColor(Color.rgb(112,141,255));

                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(Homepage.this, R.drawable.blue_gradient);
                    set.setFillDrawable(drawable);
                } else {
                    set.setFillColor(Color.BLACK);
                }

                set.setDrawFilled(true);


                return set;
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("PetWatch Channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}