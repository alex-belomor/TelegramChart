package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.belomor.telegramchart.data.ModelChart;
import com.belomor.telegramchart.data.TestChartData;

import java.util.ArrayList;
import java.util.Random;

public class ChartSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private ChartThread drawThread;

    private ArrayList<ModelChart> chartList;


    public ChartSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new ChartThread(getHolder(), getContext(), chartList);
        drawThread.setRunning(true);
        drawThread.start();
    }

    public void setChartList(ArrayList<ModelChart> chartList) {
        this.chartList = chartList;
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        drawThread.resize();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        // завершаем работу потока
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // если не получилось, то будем пытаться еще и еще
            }
        }
    }
}
