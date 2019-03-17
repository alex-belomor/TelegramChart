package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.belomor.telegramchart.R;
import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class GraphView2 extends TextureView implements TextureView.SurfaceTextureListener, Runnable {

    private ModelChart data;

    private Paint paint;

    private Thread mThread;

    private Surface mSurface;

    private float heightPerUser = 0f;
    private float widthPerSize = 0f;
    private int maxGlobalValue;

    private float changeHeightMultiplier = 0f;

    private int dateOffset = 100;

    private boolean startDraw = false;

    private boolean animation = false;

    private boolean smoothMove = false;

    private boolean redrawShow = false;

    ArrayList<Path> movesPathArray = new ArrayList<>();

    private float smoothX = 0;

    private int height, width;

    private boolean done;

    private boolean moveAnimation = false;

    private int redrawPos = -1;
    private boolean redrawGraph = false;

    private int start, end, count;


    public GraphView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(6f);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);


        setRotationX(180);

        mThread = new Thread(this, "GraphView2");
        setSurfaceTextureListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    public void setChartData(ModelChart data, int start, int end) {
        if (!animation) {
            startDraw = true;
            end = end == 0 ? data.getColumnSize(0) : end;
            this.data = data;
            this.start = start;
            this.end = end;
            this.count = end - start;
            done = false;

//            requestLayout();
        }
    }

    private ArrayList<Path> buildHeightAnimatedGraphPaths() {
        ArrayList<Path> paths = new ArrayList<>();
        widthPerSize = (float) width / (float) count;


        float newHeightPerUser = calculateAnimatedHeight(data);

        for (int i = 1; i < data.getColumns().size(); i++) {
            if (data.getColumns().get(i).show && redrawPos != i) {
                float latestX = 0;
                String color = data.getColor().getColorByPos(i - 1);
                paint.setColor(Color.parseColor(color));

                paint.setAlpha(255);

                if (i == redrawPos && redrawGraph && changeHeightMultiplier > 0 && changeHeightMultiplier <= 1f)
                    paint.setAlpha((int) (255 * changeHeightMultiplier));

                Path p = new Path();
                p.moveTo(0f, data.getColumnInt(i, start) * newHeightPerUser);

                for (int j = start + 1; j < end; j++) {
                    p.lineTo(latestX + widthPerSize, data.getColumnInt(i, j) * newHeightPerUser);
                    latestX = latestX + widthPerSize;
                }

                paths.add(p);
            }
        }

        if (redrawPos != -1) {
            float latestX = 0;
            String color = data.getColor().getColorByPos(redrawPos - 1);
            paint.setColor(Color.parseColor(color));

            paint.setAlpha((int) (redrawShow ? 255 * changeHeightMultiplier : 255 - 255 * changeHeightMultiplier));

            Path p = new Path();
            p.moveTo(0f, data.getColumnInt(redrawPos, start) * newHeightPerUser);

            for (int j = start + 1; j < end; j++) {
                p.lineTo(latestX + widthPerSize, data.getColumnInt(redrawPos, j) * newHeightPerUser);
                latestX = latestX + widthPerSize;
            }

            paths.add(p);
        }

        return paths;
    }

    private void buildMovesPath(int start, int end) {
        for (int i = 1; i < data.getColumns().size(); i++) {
            if (data.getColumns().get(i).show) {
                float latestX = 0;
                String color = data.getColor().getColorByPos(i - 1);
                paint.setColor(Color.parseColor(color));
                paint.setAlpha(255);

                Path p = new Path();
                p.moveTo(0f, data.getColumnInt(i, start) * heightPerUser);

                for (int j = start + 1; j < end; j++) {
                    p.lineTo(latestX + widthPerSize, data.getColumnInt(i, j) * heightPerUser);
                    latestX = latestX + widthPerSize;
                }

                movesPathArray.add(p);
            }
        }
    }

    public void rangeChart(int start, int end) {
        end = end == 0 ? data.getColumnSize(0) : end;
        this.start = start;
        this.end = end;
        this.count = end - start;
        smoothMove = true;
        startDraw = true;
        moveAnimation = true;
//        requestLayout();
    }

    public void redrawGraphs(int pos, boolean show) {
        redrawGraph = true;
        redrawPos = pos;
        animation = true;
        redrawShow = show;
        done = false;
        startDraw = true;
//        requestLayout();
    }


    private float calculateAnimatedHeight(ModelChart modelChart) {
        changeHeightMultiplier += 0.05f;
        if (changeHeightMultiplier >= 1f)
            changeHeightMultiplier = 1f;
        int maxValue = 0;
        float difference = 0f;
        for (int i = 1; i < modelChart.getColumns().size(); i++) {
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
            maxGlobalValue = maxValue;
            redrawGraph = false;
            redrawPos = -1;
            done = true;
            startDraw = false;
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

        maxGlobalValue = maxValue;
        heightPerUser = newHeightPerUser;
        widthPerSize = (float) width / (float) count;



        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show) {
                float latestX = 0;
                String color = modelChart.getColor().getColorByPos(i - 1);
                paint.setColor(Color.parseColor(color));
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
//
////        canvas.scale(1f, -1f);
//        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
//        int textSize = 35;
//        paintText.setTextSize(textSize);
//        paintText.setColor(ContextCompat.getColor(getContext(), R.color.graph_text_color));
//        for (int i = 0; i < 6; i++) {
//            float floatMaxValue = ((float)maxGlobalValue / 5f * (float) i);
//            String text = String.valueOf(Float.valueOf(floatMaxValue).intValue());
//            float yInterval = height / 5;
//            float y = yInterval * i - textSize * i;
//            canvas.drawText(text, 0, y, paintText);
//        }
        startDraw = false;
    }

    public void drawMaxValue(int maxValue) {

    }

    public void drawLines(Canvas canvas, boolean animate, boolean increase) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.graph_text_color));
        paint.setStrokeWidth(1.2f);
    }

    private void drawDataAnimate(Canvas canvas, ModelChart modelChart) {
        if (!animation)
            return;
        widthPerSize = (float) width / (float) count;

        float newHeightPerUser = calculateAnimatedHeight(modelChart);


        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show && redrawPos != i) {
                float latestX = 0;
                String color = modelChart.getColor().getColorByPos(i - 1);
                paint.setColor(Color.parseColor(color));

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

        if (redrawPos != -1) {
            float latestX = 0;
            String color = modelChart.getColor().getColorByPos(redrawPos - 1);
            paint.setColor(Color.parseColor(color));

            paint.setAlpha((int) (redrawShow ? 255 * changeHeightMultiplier : 255 - 255 * changeHeightMultiplier));

            Path p = new Path();
            p.moveTo(0f, modelChart.getColumnInt(redrawPos, start) * newHeightPerUser);

            for (int j = start + 1; j < end; j++) {
                p.lineTo(latestX + widthPerSize, modelChart.getColumnInt(redrawPos, j) * newHeightPerUser);
                latestX = latestX + widthPerSize;
            }

            canvas.drawPath(p, paint);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        mThread.start();
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

    @Override
    public void run() {

        while (true) {
            try {
                if (startDraw) {
                    long start = System.currentTimeMillis();
                    Canvas canvas = mSurface.lockHardwareCanvas();
                    canvas.drawColor(Color.WHITE);
//                    synchronized (mSurface) {
                        if (data != null && height > 0 && width > 0 && end > 0) {
                            if (!animation) {
                                drawData(canvas, data);
                            } else {
                                drawDataAnimate(canvas, data);
                            }
                        }
//                    }

                    mSurface.unlockCanvasAndPost(canvas);

                    long end = System.currentTimeMillis() - start;
                    if (end > 15)
                        Log.w("RENDERNING", end + "ms");

                    try {
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.e("GraphView2", e.toString());
            }
        }
    }
}
