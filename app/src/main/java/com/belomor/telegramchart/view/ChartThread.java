package com.belomor.telegramchart.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.ArrayMap;
import android.view.SurfaceHolder;

import com.belomor.telegramchart.R;
import com.belomor.telegramchart.data.ModelChart;
import com.belomor.telegramchart.data.TestChartData;

import java.util.ArrayList;

public class ChartThread extends Thread {
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
    private Bitmap picture;
    Canvas canvas;
    private Matrix matrix;
    private long prevTime;
    ArrayList<ModelChart> chartList;
    Path path;
    Context context;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ChartThread(SurfaceHolder surfaceHolder, Context context, ArrayList<ModelChart> chartList){
        this.surfaceHolder = surfaceHolder;
        this.chartList = chartList;

        matrix = new Matrix();
//        matrix.postScale(30.0f, 30.0f);
//        matrix.postTranslate(100.0f, 100.0f);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        paint.setColor(Color.GREEN);
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    int size;

    public void resize() {
        if (matrix != null) {
            matrix.reset();
            matrix.setTranslate(size, size);
            size += 10;
            canvas.concat(matrix);
        }
    }

    @Override
    public void run() {
        float latestX = 0;
        float latestY = 0;

        canvas = surfaceHolder.lockCanvas(null);
        canvas.drawColor(Color.WHITE);
        String color = chartList.get(0).getColor().getY1();
        paint.setColor(Color.parseColor(color));
        synchronized (surfaceHolder) {
            for (int i = 0; i < chartList.get(0).getColumnSize(1); i++) {
                canvas.drawLine(latestX, latestY, latestX + 200, chartList.get(0).getColumnInt(1, i) * 10, paint);
                latestX = latestX + 200;
                latestY = chartList.get(0).getColumnInt(1, i) * 10;
            }


            matrix = canvas.getMatrix();

            if (canvas != null) {
                // отрисовка выполнена. выводим результат на экран
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

    }
}
