package com.example.myapplication;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class XAxisValueFormatter extends ValueFormatter {
    long reference_timestamp;

    public XAxisValueFormatter(long reference_timestamp)
    {
        this.reference_timestamp = reference_timestamp;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String getFormattedValue(float value) {

        long epoch = (((long)value + reference_timestamp));

        // Show time in local version
        Instant instant = Instant.ofEpochSecond(epoch);
        LocalDateTime dt =
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss a");
        String formatted = dtf.format(dt);

        return formatted;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getAxisLabel(float value, AxisBase axis) {
        return getFormattedValue(value);
    }
}