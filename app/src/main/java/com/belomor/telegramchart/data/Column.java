package com.belomor.telegramchart.data;

import java.util.ArrayList;

public class Column extends ArrayList {

    int maxValue = -1;

    public boolean show = true;

    public Column() {
        super();
    }

    public int getMaxValue() {
        if (maxValue == -1) {
            for (int i = 1; i < size(); i++) {
                if (((Double) get(i)).intValue() > maxValue) {
                    maxValue = ((Double) get(i)).intValue();
                }
            }
        }

        return maxValue;
    }

    public int getMaxValueInInterval(int start, int end) {
        int maxValue = 0;
        for (int i = start + 1; i < end; i++) {
            if (((Double) get(i)).intValue() > maxValue) {
                maxValue = ((Double) get(i)).intValue();
            }
        }
        return maxValue;
    }

    @Override
    public int size() {
        return super.size() - 1;
    }
}