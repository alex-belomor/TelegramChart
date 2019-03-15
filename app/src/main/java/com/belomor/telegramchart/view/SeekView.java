package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class SeekView extends View {

    private ArrayList<ModelChart> data;

    private float multiplier = 1f;
    private float heightPerUser = 0f;
    private float widthPerSize = 0f;

    private int height, width;

    private int from, to;

    private Paint paint;


    public SeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3f);

        setRotationX(180);

//        setScaleX(0.1f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    public void setChartData(ArrayList<ModelChart> data) {
        this.data = data;

        requestLayout();
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data != null) {
            drawData(canvas, data.get(0), from, to);
        }
    }


    private void drawData(Canvas canvas, ModelChart modelChart, int from, int to) {
        heightPerUser = (float) height / (float) modelChart.getColumns().get(1).getMaxValue();
        widthPerSize = (float) width / (float) modelChart.getColumns().get(1).size();
        float latestX = 0;

        canvas.drawColor(Color.WHITE);

        paint.setAntiAlias(true);

        String color = modelChart.getColor().getY1();
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);

        Path p = new Path();
        p.moveTo(0f, modelChart.getColumnInt(1, 0));

        for (int i = 1; i < modelChart.getColumnSize(1); i++) {
            p.lineTo(latestX + (widthPerSize), modelChart.getColumnInt(1, i) * heightPerUser);
            latestX = latestX + (widthPerSize);
        }

        canvas.drawPath(p, paint);

//            postInvalidateDelayed(200);
    }


}
