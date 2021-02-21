package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class YourMarkerView extends MarkerView {

    private TextView tvContent;
    long reference_timestamp;
    private LocalDateTime mDate;
    private DateTimeFormatter mDataFormat;
    private LineChart chart;
    private Context mContext;


    public YourMarkerView(Context context, int layoutResource, long reference_timestamp) {
        super(context, layoutResource);

//         find your layout components
        chart = findViewById(R.id.petChart);
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.mContext = context;
        this.reference_timestamp = reference_timestamp;
        this.mDataFormat = DateTimeFormatter.ofPattern("h:mm:ss a");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        long currentTimestamp = (long)e.getX() + reference_timestamp;
        System.out.println("currentTimestamp:" + currentTimestamp);
        tvContent.setText(e.getY() + " m/s^2 at " + getTimedate(currentTimestamp)); // set the entry-value as the display text

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }


    @Override
    public void draw(Canvas canvas, float posx, float posy)
    {
        // Check marker position and update offsets.
        System.out.println("Posx:" + posx);
        int w = getWidth();
        int h = getHeight();
        if((getResources().getDisplayMetrics().widthPixels-posx-w) < w) {
            posx -= w;
        }
        System.out.println("Posx:" + posx);


        if (getResources().getDisplayMetrics().heightPixels-posy-h < h)
        {
            posy-=h;
        }

        if (posx < 0)
        {
            posx+=w;
        }


        canvas.translate(posx, posy);
        draw(canvas);
        canvas.translate(-posx, -posy);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getTimedate(long timestamp) {

        try {
            Instant instant = Instant.ofEpochSecond(timestamp);
            LocalDateTime dt =
                    instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

//            System.out.println("Marker: " + dt);

            String formatted = mDataFormat.format(dt);
            return formatted;
        } catch (Exception ex) {
            return "xx";
        }
    }
}