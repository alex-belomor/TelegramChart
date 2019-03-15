package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.SurfaceHolder;

import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

public class GraphViewChartThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private Context context;
    private ModelChart modelChart;

    private float multiplier = 1f;
    private float heightPerUser = 0f;
    private float widthPerSize = 0f;

    private int height, width;

    private int from, to;

    private int start, end, count;

    private Paint paint;

    public GraphViewChartThread(SurfaceHolder surfaceHolder, Context context, ModelChart chart,
                                int height, int width, int start, int end, int count) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        this.modelChart = chart;
        this.width = width;
        this.height = height;
        this.start = start;
        this.end = end;
        this.count = count;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.GREEN);
    }

    @Override
    public void run() {
        Canvas canvas = null;

        heightPerUser = (float) height / (float) modelChart.getColumns().get(1).getMaxValue();
        widthPerSize = (float) width / (float) count;
        float latestX = 0;

        canvas = surfaceHolder.lockCanvas(null);
        canvas.drawColor(Color.WHITE);

        paint.setAntiAlias(true);

        String color = modelChart.getColor().getY1();
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);

        synchronized (surfaceHolder) {
            Path p = new Path();
            p.moveTo(0f, modelChart.getColumnInt(1, start) * heightPerUser);


            for (int i = start + 1; i < end; i++) {
                p.lineTo(latestX + widthPerSize, modelChart.getColumnInt(1, i) * heightPerUser);
                latestX = latestX + widthPerSize;
            }

            canvas.drawPath(p, paint);

            if (canvas != null) {
                // отрисовка выполнена. выводим результат на экран
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
