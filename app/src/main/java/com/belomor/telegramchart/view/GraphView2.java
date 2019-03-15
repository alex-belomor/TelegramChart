package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class GraphView2 extends SurfaceView implements SurfaceHolder.Callback{

    private ArrayList<ModelChart> data;

    private GraphViewChartThread chartThread;

    private float multiplier = 1f;
    private float heightPerUser = 0f;
    private float widthPerSize = 0f;

    private int height, width;

    private int from, to;

    private int start, end, count;

    private Paint paint;


    public GraphView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(6f);

        setRotationX(180);
        getHolder().addCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    public void setChartData(ArrayList<ModelChart> data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
        this.count = end - start;

        requestLayout();
    }

    public void rangeChart(int start, int end) {
        this.start = start;
        this.end = end;
        this.count = end - start;

        if (chartThread != null && chartThread.isAlive())
            chartThread.interrupt();
        chartThread = new GraphViewChartThread(getHolder(), getContext(), data.get(0), width, height, start, end, count);
        chartThread.start();
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data != null) {
            drawData(canvas, data.get(0));
        }
    }


    private void drawData(Canvas canvas, ModelChart modelChart) {
        heightPerUser = (float) height / (float) modelChart.getColumns().get(1).getMaxValue();
        widthPerSize = (float) width / (float) count;
        float latestX = 0;

        canvas.drawColor(Color.WHITE);

        paint.setAntiAlias(true);

        String color = modelChart.getColor().getY1();
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);

        Path p = new Path();
        p.moveTo(0f, modelChart.getColumnInt(1, start) * heightPerUser);

        for (int i = start+1; i < end; i++) {
            p.lineTo(latestX + widthPerSize, modelChart.getColumnInt(1, i) * heightPerUser);
            latestX = latestX + widthPerSize;
        }

        canvas.drawPath(p, paint);

//            postInvalidateDelayed(200);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        chartThread = new GraphViewChartThread(getHolder(), getContext(), data.get(0));
//        chartThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
