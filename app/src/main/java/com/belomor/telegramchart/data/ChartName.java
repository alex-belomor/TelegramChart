package com.belomor.telegramchart.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChartName implements Serializable {

    @SerializedName("y0")
    private String y0;

    @SerializedName("y1")
    private String y1;

    @SerializedName("y2")
    private String y2;

    @SerializedName("y3")
    private String y3;

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

    public String getNameByPos(int pos) {
        switch (pos) {
            case 0:
                return getY0();
            case 1:
                return getY1();
            case 2:
                return getY2();
            default:
                return getY3();
        }
    }
}