package com.example.myapplication;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class XAxisValueFormatter extends ValueFormatter {
    long reference_timestamp;

    public XAxisValueFormatter(long reference_timestamp)
    {
        this.reference_timestamp = reference_timestamp;

    }

    @Override
    public String getFormattedValue(float value) {

        long emissionsMilliSince1970Time = (((long)value + reference_timestamp) * 1000);

        // Show time in local version
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
        DateFormat format = new SimpleDateFormat("hh:mm:ss a");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formatted = format.format(emissionsMilliSince1970Time);

        return formatted;
    }

    public String getAxisLabel(float value, AxisBase axis) {
        return getFormattedValue(value);
    }
}