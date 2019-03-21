package com.belomor.telegramchart.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChartType implements Serializable {

    @SerializedName("y0")
    private String y0;

    @SerializedName("y1")
    private String y1;

    @SerializedName("y2")
    private String y2;

    @SerializedName("y3")
    private String y3;

    @SerializedName("x")
    private String x;

    public String getY0() {
        return y0;
    }

    public void setY0(String y0) {
        this.y0 = y0;
    }

    public String getY1() {
        return y1;
    }

    public void setY1(String y1) {
        this.y1 = y1;
    }

    public String getY2() {
        return y2;
    }

    public void setY2(String y2) {
        this.y2 = y2;
    }

    public String getY3() {
        return y3;
    }

    public void setY3(String y3) {
        this.y3 = y3;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }
}