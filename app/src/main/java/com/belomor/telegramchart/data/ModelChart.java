package com.belomor.telegramchart.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelChart implements Serializable {

    @SerializedName("columns")
    private ArrayList<ArrayList<Object>> columns;

    @SerializedName("types")
    private ChartType type;

    @SerializedName("names")
    private ChartName name;

    @SerializedName("colors")
    private ChartColor color;

    public int getColumnInt(int posColumn, int pos) {
        return ((Double) columns.get(posColumn).get(pos + 1)).intValue();
    }

    public int getColumnSize(int posColumn) {
        return (int) columns.get(posColumn).size() - 1;
    }

    public String getColumnName(int posColumn) {
        return (String) columns.get(posColumn).get(0);
    }

    public void setColumns(ArrayList<ArrayList<Object>> columns) {
        this.columns = columns;
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
    }

    public ChartName getName() {
        return name;
    }

    public void setName(ChartName name) {
        this.name = name;
    }

    public ChartColor getColor() {
        return color;
    }

    public void setColor(ChartColor color) {
        this.color = color;
    }

    public class ChartColor implements Serializable {

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
    }

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
    }

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
}