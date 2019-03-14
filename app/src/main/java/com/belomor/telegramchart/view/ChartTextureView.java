package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

public class ChartTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    Canvas canvas;

    ArrayList<ModelChart> chartList;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ChartTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        float latestX = 0;
        float latestY = 0;

        canvas = lockCanvas(null);
        canvas.drawColor(Color.YELLOW);
        String color = chartList.get(0).getColor().getY1();
        paint.setColor(Color.parseColor(color));
        for (int i = 0; i < chartList.get(0).getColumnSize(1); i++) {
            canvas.drawLine(latestX, latestY, latestX + 200, chartList.get(0).getColumnInt(1, i) * 10, paint);
            latestX = latestX + 200;
            latestY = chartList.get(0).getColumnInt(1, i) * 10;
        }



        if (canvas != null) {
            // отрисовка выполнена. выводим результат на экран
            unlockCanvasAndPost(canvas);
        }
    }

    public void setChartList(ArrayList<ModelChart> chartList) {
        this.chartList = chartList;
    }

    int size;

    public void resize() {
        Bitmap bitmap = getBitmap();
        Matrix matrix = new Matrix();
        matrix.postRotate(size);
        setTransform(matrix);
        size += 50;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        resize();
        return super.onTouchEvent(event);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
