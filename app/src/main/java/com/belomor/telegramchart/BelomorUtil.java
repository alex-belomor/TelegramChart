package com.belomor.telegramchart;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class BelomorUtil {

    public static int getDpInPx(float value, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics);
    }
}
