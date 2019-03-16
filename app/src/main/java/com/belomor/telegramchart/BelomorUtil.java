package com.belomor.telegramchart;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class BelomorUtil {

    public static int getDpInPx(float value, Context context) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics()));
    }

    public static int getSpInPx(float value, Context context) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,
                context.getResources().getDisplayMetrics()));
    }
}
