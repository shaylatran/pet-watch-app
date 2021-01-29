package com.example.myapplication;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class DayAxisValueFormatter extends ValueFormatter {
    private final LineChart chart;
    public DayAxisValueFormatter(LineChart chart) {
        this.chart = chart;
    }

    public String getFormattedValue(float value)
    {
        return "your text" + value;
    }
}
