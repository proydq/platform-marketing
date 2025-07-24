package com.platform.marketing.dashboard.dto;

import lombok.Data;

@Data
public class TrendPoint {
    private String date;
    private int value;

    public TrendPoint() {}

    public TrendPoint(String date, int value) {
        this.date = date;
        this.value = value;
    }
}
