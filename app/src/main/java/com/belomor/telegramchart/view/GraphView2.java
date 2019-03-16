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

    private int redrawPos = -1;
    private boolean redrawGraph = false;

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
        this.end = end == 0 ? data.getColumnSize(0) : end;
        this.count = end - start;

        requestLayout();
    }

    public void redrawGraphs(int pos) {
        redrawGraph = true;
        redrawPos = pos;
        animation = true;
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
        int maxValue = 0;
        float difference = 0f;
        for (int i = 1; i<modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show) {
                int localMaxValue = modelChart.getColumns().get(i).getMaxValueInInterval(start, end);
                if (localMaxValue > maxValue) {
                    maxValue = localMaxValue;
                    difference = (float) height / (float) maxValue - heightPerUser;
                }
            }
        }
        float returnedValue = heightPerUser + difference * changeHeightMultiplier;
        if (changeHeightMultiplier >= 1f) {
            animation = false;
            changeHeightMultiplier = 0f;
            heightPerUser += difference;
            redrawGraph = false;
            redrawPos = -1;
        }

        return returnedValue;
    }

    private void drawData(Canvas canvas, ModelChart modelChart) {
        float newHeightPerUser = 0f;
        int maxValue = 0;

        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show) {
                int localMaxValue = modelChart.getColumns().get(i).getMaxValueInInterval(start, end);
                if (localMaxValue > maxValue) {
                    maxValue = localMaxValue;
                    newHeightPerUser = (float) height / (float) localMaxValue;
                }
            }
        }

        if (newHeightPerUser != heightPerUser && heightPerUser > 0f) {
            animation = true;
            drawDataAnimate(canvas, modelChart);
            return;
        }

        heightPerUser = newHeightPerUser;
        widthPerSize = (float) width / (float) count;

        canvas.drawColor(Color.WHITE);

        paint.setAntiAlias(true);

        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show) {
                float latestX = 0;
                String color = modelChart.getColor().getColorByPos(i - 1);
                paint.setColor(Color.parseColor(color));
                paint.setStyle(Paint.Style.STROKE);
                paint.setAlpha(255);

                Path p = new Path();
                p.moveTo(0f, modelChart.getColumnInt(i, start) * heightPerUser);

                for (int j = start + 1; j < end; j++) {
                    p.lineTo(latestX + widthPerSize, modelChart.getColumnInt(i, j) * heightPerUser);
                    latestX = latestX + widthPerSize;
                }

                canvas.drawPath(p, paint);
            }
        }
    }

    private void drawDataAnimate(Canvas canvas, ModelChart modelChart) {
        if (!animation)
            return;
        widthPerSize = (float) width / (float) count;

        canvas.drawColor(Color.WHITE);

        float newHeightPerUser = calculateAnimatedHeight(modelChart);

        paint.setAntiAlias(true);

        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show) {
                float latestX = 0;
                String color = modelChart.getColor().getColorByPos(i - 1);
                paint.setColor(Color.parseColor(color));
                paint.setStyle(Paint.Style.STROKE);

                paint.setAlpha(255);

                if (i == redrawPos && redrawGraph && changeHeightMultiplier > 0 && changeHeightMultiplier <= 1f)
                    paint.setAlpha((int) (255 * changeHeightMultiplier));

                Path p = new Path();
                p.moveTo(0f, modelChart.getColumnInt(i, start) * newHeightPerUser);

                for (int j = start + 1; j < end; j++) {
                    p.lineTo(latestX + widthPerSize, modelChart.getColumnInt(i, j) * newHeightPerUser);
                    latestX = latestX + widthPerSize;
                }

                canvas.drawPath(p, paint);
            }
        }

        postInvalidateDelayed(1);
    }
}
