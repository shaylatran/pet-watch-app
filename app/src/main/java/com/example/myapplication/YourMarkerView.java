package com.example.myapplication;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class YourMarkerView extends MarkerView {

    private TextView tvContent;
    long reference_timestamp;
    private Date mDate;
    private DateFormat mDataFormat;
    private LineChart chart;


    public YourMarkerView(Context context, int layoutResource, long reference_timestamp) {
        super(context, layoutResource);

        // find your layout components
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.reference_timestamp = reference_timestamp;
        this.mDataFormat = new SimpleDateFormat("h:mm:ss a", Locale.ENGLISH);
        this.mDate = new Date();
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        long currentTimestamp = (long)e.getX() + reference_timestamp;
        tvContent.setText(e.getY() + " m/s^2 at " + getTimedate(currentTimestamp)); // set the entry-value as the display text

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }


    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }

    private String getTimedate(long timestamp) {

        try {
            mDate.setTime(timestamp * 1000);
            mDataFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return mDataFormat.format(mDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}