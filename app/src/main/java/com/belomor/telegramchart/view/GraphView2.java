package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class GraphView2 extends View {

    private ModelChart data;

    private Paint paint;

    private float heightPerUser = 0f;
    private float widthPerSize = 0f;

    private float changeHeightMultiplier = 0f;

    private boolean animation = false;

    private int height, width;

    private int start, end, count;


    public GraphView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(6f);

        setRotationX(180);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    public void setChartData(ModelChart data, int start, int end) {
        if (!animation) {
            this.data = data;
            this.start = start;
            this.end = end == 0 ? data.getColumnSize(0) : end;
            this.count = end - start;

            requestLayout();
        }
    }

    private ArrayList<Path> buildHeightAnimatedGraphPaths() {
        ArrayList<Path> paths = new ArrayList<>();

        return paths;
    }

    public void rangeChart(int start, int end) {
        this.start = start;
        this.end = end;
        this.count = end - start;

        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data != null && height > 0 && width > 0) {
            if (!animation) {
                drawData(canvas, data);
            } else {
                drawDataAnimate(canvas, data);
            }
        }
    }

    private float calculateAnimatedHeight(ModelChart modelChart) {
        changeHeightMultiplier += 0.05f;
        float difference = (float) height / (float) modelChart.getColumns().get(1).getMaxValueInInterval(start, end) - heightPerUser;
        float returnedValue = heightPerUser + difference * changeHeightMultiplier;
        if (changeHeightMultiplier >= 1f) {
            animation = false;
            changeHeightMultiplier = 0f;
            heightPerUser += difference;
        }

        return returnedValue;
    }

    private void drawData(Canvas canvas, ModelChart modelChart) {
        float newHeightPerUser = (float) height / (float) modelChart.getColumns().get(1).getMaxValueInInterval(start, end);

        if (newHeightPerUser != heightPerUser && heightPerUser > 0f) {
            drawDataAnimate(canvas, modelChart);
            animation = true;
            return;
        }

        heightPerUser = (float) height / (float) modelChart.getColumns().get(1).getMaxValueInInterval(start, end);
        widthPerSize = (float) width / (float) count;
        float latestX = 0;

        canvas.drawColor(Color.WHITE);

        paint.setAntiAlias(true);

        String color = modelChart.getColor().getY1();
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);

        Path p = new Path();
        p.moveTo(0f, modelChart.getColumnInt(1, start) * heightPerUser);

        for (int i = start + 1; i < end; i++) {
            p.lineTo(latestX + widthPerSize, modelChart.getColumnInt(1, i) * heightPerUser);
            latestX = latestX + widthPerSize;
        }

        canvas.drawPath(p, paint);
    }

    private void drawDataAnimate(Canvas canvas, ModelChart modelChart) {
        if (!animation)
            return;
        widthPerSize = (float) width / (float) count;
        float latestX = 0;

        canvas.drawColor(Color.WHITE);

        float newHeightPerUser = calculateAnimatedHeight(modelChart);

        paint.setAntiAlias(true);

        String color = modelChart.getColor().getY1();
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);

        Path p = new Path();
        p.moveTo(0f, modelChart.getColumnInt(1, start) * newHeightPerUser);

        for (int i = start + 1; i < end; i++) {
            p.lineTo(latestX + widthPerSize, modelChart.getColumnInt(1, i) * newHeightPerUser);
            latestX = latestX + widthPerSize;
        }

        canvas.drawPath(p, paint);

        postInvalidateDelayed(1);
    }
}
