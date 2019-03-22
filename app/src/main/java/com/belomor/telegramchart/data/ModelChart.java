package com.belomor.telegramchart.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelChart implements Serializable {

    @SerializedName("columns")
    private ArrayList<Column> columns;

    @SerializedName("types")
    private ChartType type;

    @SerializedName("names")
    private ChartName name;

    @SerializedName("colors")
    private ChartColor color;

    public int getColumnInt(int posColumn, int pos) {
        return ((Double) columns.get(posColumn).get(pos)).intValue();
    }

    public long getColumnLong(int posColumn, int pos) {
        return ((Double) columns.get(posColumn).get(pos)).longValue();
    }

    public String getColumnName(int posColumn) {
        return (String) columns.get(posColumn).get(0);
    }

    public List<Column> getColumns() {
        return columns;
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
}
