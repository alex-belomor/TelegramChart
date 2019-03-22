package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;

import com.belomor.telegramchart.GlobalManager;
import com.belomor.telegramchart.R;
import com.belomor.telegramchart.data.ModelChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class GraphView2 extends TextureView implements TextureView.SurfaceTextureListener, Runnable {

    private ModelChart data;

    private Paint paint;
    private Paint paintText;

    private Thread mThread;

    private Surface mSurface;

    private float heightPerUser = 0f;
    private float widthPerSize = 0f;
    private int maxGlobalValue;

    private float changeHeightMultiplier = 0f;

    private int dateOffset = 100;

    private boolean startDraw = false;

    private boolean animation = false;

    private boolean redrawShow = false;

    private int height, width;

    private boolean block;

    Paint clearPaint = new Paint();

    private float offsetX;

    private int redrawPos = -1;
    private boolean redrawGraph = false;

    Path path = new Path();

    private int start, end;

    private boolean increaseHeight = false;

    private Paint paintLine;

    private Paint paintVertLine;

    private Paint paintCircle;

    private int maxValue = 0;

    private float touchX;

    private boolean touched = false;

    private boolean savedBeforeTouch = false;

    private float startY;

    private boolean threadRunning;


    public GraphView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOpaque(false);

        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        int textSize = 38;
        paintText.setTextSize(textSize);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(6f);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        startY = 65f;

        paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLine.setStrokeWidth(3f);
        paintLine.setAntiAlias(true);
        paintLine.setStyle(Paint.Style.STROKE);

        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);

        paintVertLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintVertLine.setStrokeWidth(3.5f);
        paintVertLine.setAntiAlias(true);
        paintVertLine.setStyle(Paint.Style.STROKE);

        setRotationX(180);

        mThread = new Thread(this, "GraphView2");
        mThread.setPriority(Thread.MAX_PRIORITY);
        setSurfaceTextureListener(this);

        updateColors();
    }

    public void updateColors() {
        paintText.setColor(ContextCompat.getColor(getContext(), GlobalManager.nightMode ? R.color.chart_text_dark : R.color.chart_text_light));
        paintLine.setColor(ContextCompat.getColor(getContext(), GlobalManager.nightMode ? R.color.chart_line_dark : R.color.chart_line_light));
        paintVertLine.setColor(ContextCompat.getColor(getContext(), GlobalManager.nightMode ? R.color.chart_line_dark : R.color.chart_line_light));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = MeasureSpec.getSize(heightMeasureSpec) - (int) startY;
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    public void setChartData(ModelChart data, int start, int end) {
        if (!animation) {
            startDraw = true;
            end = end == 0 ? data.getColumnSize(0) : end;
            this.data = data;
            this.start = start;
            this.end = end;
        }
    }

    public void rangeChart(int start, int end, float widthPerSize, float offsetX) {
        end = end == 0 ? data.getColumnSize(0) : end;
        this.start = start;
        this.end = end;
        this.widthPerSize = widthPerSize;
        this.offsetX = offsetX;
        startDraw = true;
    }

    public void redrawGraphs(int pos, boolean show) {
        redrawGraph = true;
        redrawPos = pos;
        animation = true;
        redrawShow = show;
        startDraw = true;
    }


    private float calculateAnimatedHeight(ModelChart modelChart) {
        changeHeightMultiplier += 0.02f;
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
            this.maxValue = maxValue;
            maxGlobalValue = maxValue;
            redrawGraph = false;
            redrawPos = -1;
            startDraw = false;
        }

        return returnedValue;
    }

    private void drawData(Canvas canvas, ModelChart modelChart) {
        float newHeightPerUser = 0f;
        int maxValue = 0;

        paintLine.setAlpha(255);
        canvas.drawLine(0, startY, width, startY, paintLine);

        float transitionY = ((float) height - startY) / 5f;
        for (int i = 1; i < 6; i++) {
            canvas.drawLine(0, startY + transitionY * i, width, startY + transitionY * i, paintLine);
        }

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
            increaseHeight = newHeightPerUser > heightPerUser;
            animation = true;
            drawDataAnimate(canvas, modelChart);
            return;
        }

        maxGlobalValue = maxValue;
        heightPerUser = newHeightPerUser;
        this.maxValue = maxValue;

        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show) {
                path.reset();

                float latestX = offsetX;
                String color = modelChart.getColor().getColorByPos(i - 1);
                paint.setColor(Color.parseColor(color));
                paint.setAlpha(255);

                path.moveTo(latestX, startY + modelChart.getColumnInt(i, 0) * heightPerUser);

                for (int j = 1; j < modelChart.getColumnSize(0); j++) {
                    path.lineTo(latestX + widthPerSize, startY + modelChart.getColumnInt(i, j) * heightPerUser);
                    latestX = latestX + widthPerSize;
                }

                canvas.drawPath(path, paint);
            }
        }

        drawDates(canvas, modelChart);
        drawValues(canvas, modelChart);

        startDraw = false;
    }

    private void drawValues(Canvas canvas, ModelChart modelChart) {

        float newHeightPerUser = calculateAnimatedHeight(modelChart);

        canvas.save();
        canvas.scale(1f, -1f, (float) width / 2f, (float) height / 2f);

        paintText.setTextAlign(Paint.Align.LEFT);

        int newMaxValue = 0;

        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show) {
                int localMaxValue = modelChart.getColumns().get(i).getMaxValueInInterval(start, end);
                if (localMaxValue > newMaxValue) {
                    newMaxValue = localMaxValue;
                }
            }
        }


        float transitionY = ((float) height - startY) / 5f;

        paintText.setAlpha(255);
        canvas.drawText("0", 0, height - startY - 16, paintText);

        int newCalculatedMaxValue = newMaxValue - (int) ((((height) - transitionY * 5f)) / newHeightPerUser);
        int calculatedMaxValue = maxValue - (int) ((((height) - transitionY * 5f)) / heightPerUser);

        if (newHeightPerUser != heightPerUser) {
            increaseHeight = newHeightPerUser > heightPerUser;

            paintText.setAlpha(255);
            canvas.drawText("0", 0, height - startY - 16, paintText);


            //showed lines
            paintText.setAlpha((int) (255 * changeHeightMultiplier));
            float showStart = (!increaseHeight ? 128 : -128) - (!increaseHeight ? 128 : -128) * changeHeightMultiplier;
            for (int i = 1; i < 6; i++) {
                int value = (int) ((float) newCalculatedMaxValue / 5f * (float) i);
                canvas.drawText(String.valueOf(value >= 0 ? value : 0), 0, height - startY - transitionY * i - 16 - showStart * i, paintText);
            }

            //hiding lines
            paintText.setAlpha(255 - (int) (255 * changeHeightMultiplier));
            float hideStart = (increaseHeight ? 128 : -128) * changeHeightMultiplier;
            for (int i = 1; i < 6; i++) {
                int value = (int) ((float) calculatedMaxValue / 5f * (float) i);
                canvas.drawText(String.valueOf(value >= 0 ? value : 0), 0, height - startY - transitionY * i - 16 - hideStart * i, paintText);
            }
        } else {
            paintText.setAlpha(255);

            for (int i = 1; i < 6; i++) {
                int value = (int) ((float) calculatedMaxValue / 5f * (float) i);
                canvas.drawText(String.valueOf(value >= 0 ? value : 0), 0, height - startY - transitionY * i - 16, paintText);
            }
        }

        canvas.restore();
    }

    private void drawDates(Canvas canvas, ModelChart modelChart) {
        paintText.setAlpha(255);

        int itemsDate = modelChart.getColumns().get(0).size() - 1;
        DateFormat simple = new SimpleDateFormat("MMM dd");

        int textSize = 35;
        canvas.save();
        paintText.setTextSize(textSize);
        canvas.scale(1f, -1f, (float) width / 2f, (float) height / 2f);

        int denominator = 1;

        float viewPort = widthPerSize * (float) (itemsDate - 1);

        denominator = (int) viewPort / (int) width;

        float widthDate = viewPort / (6f * (float) denominator);

        paintText.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < 6 * denominator; i++) {
            float x = (viewPort) / (6 * denominator) * i + widthDate / 2;
            int pos = (int) (x / widthPerSize);
            if (pos > itemsDate)
                pos = itemsDate;
            long date = data.getColumnLong(0, pos);
            Date result = new Date(date);
            String text = simple.format(result);
            canvas.drawText(text, offsetX + x, height - textSize + 15, paintText);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            touched = true;
        else if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            savedBeforeTouch = false;
            touched = false;
            startDraw = true;
            return true;
        }

        touchX = event.getX();

//        int pos = (int) ((Math.abs(offsetX)  + event.getX()) / widthPerSize);
//        for (int i = 1; i < data.getColumns().size(); i++) {
//            Log.d("TEST_POS", data.getColumnInt(i, pos) + "");
//        }
        return true;
    }


    private void drawDataAnimate(Canvas canvas, ModelChart modelChart) {
        if (!animation)
            return;

        block = true;

        float newHeightPerUser = calculateAnimatedHeight(modelChart);

        if (newHeightPerUser != heightPerUser) {
            increaseHeight = newHeightPerUser > heightPerUser;

            paintLine.setAlpha(255);
            canvas.drawLine(0, startY, width, startY, paintLine);

            float transitionY = ((float) height - startY) / 5f;

            //showed lines
            paintLine.setAlpha((int) (255 * changeHeightMultiplier));
            float showStart = (!increaseHeight ? 128 : -128) - (!increaseHeight ? 128 : -128) * changeHeightMultiplier;
            for (int i = 1; i < 6; i++) {
                canvas.drawLine(0, startY + transitionY * i + showStart * i, width, startY + transitionY * i + showStart * i, paintLine);
            }

            //hiding lines
            paintLine.setAlpha(255 - (int) (255 * changeHeightMultiplier));
            float hideStart = (increaseHeight ? 128 : -128) * changeHeightMultiplier;
            for (int i = 1; i < 6; i++) {
                canvas.drawLine(0, startY + transitionY * i + hideStart * i, width, startY + transitionY * i + hideStart * i, paintLine);
            }
        } else {
            paintLine.setAlpha(255);
            canvas.drawLine(0, startY, width, startY, paintLine);

            float transitionY = ((float) height - startY) / 5f;
            for (int i = 1; i < 6; i++) {
                canvas.drawLine(0, startY + transitionY * i, width, startY + transitionY * i, paintLine);
            }
        }


        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show && redrawPos != i) {
                path.reset();
                float latestX = offsetX;
                String color = modelChart.getColor().getColorByPos(i - 1);
                paint.setColor(Color.parseColor(color));

                paint.setAlpha(255);

                if (i == redrawPos && redrawGraph && changeHeightMultiplier > 0 && changeHeightMultiplier <= 1f)
                    paint.setAlpha((int) (255 * changeHeightMultiplier));

                path.moveTo(latestX, startY + modelChart.getColumnInt(i, 0) * newHeightPerUser);

                for (int j = 1; j < modelChart.getColumnSize(0); j++) {
                    path.lineTo(latestX + widthPerSize, startY + modelChart.getColumnInt(i, j) * newHeightPerUser);
                    latestX = latestX + widthPerSize;
                }

                canvas.drawPath(path, paint);
            } else if (redrawPos == i) {
                path.reset();
                float latestX = offsetX;
                String color = modelChart.getColor().getColorByPos(redrawPos - 1);
                paint.setColor(Color.parseColor(color));

                int alpha = (int) (redrawShow ? 255 * changeHeightMultiplier : 255 - 255 * changeHeightMultiplier);

                paint.setAlpha(alpha);

                path.moveTo(latestX, startY + modelChart.getColumnInt(redrawPos, 0) * newHeightPerUser);

                for (int j = 1; j < modelChart.getColumnSize(0); j++) {
                    path.lineTo(latestX + widthPerSize, startY + modelChart.getColumnInt(redrawPos, j) * newHeightPerUser);
                    latestX = latestX + widthPerSize;
                }

                if (alpha > 1)
                    canvas.drawPath(path, paint);
            }
        }

        drawDates(canvas, modelChart);
        drawValues(canvas, modelChart);

        block = false;
    }

    private void drawVertLine(Canvas canvas, ModelChart data) {
        drawData(canvas, data);

        canvas.drawLine(touchX, startY, touchX, height + startY, paintVertLine);

        int pos = (int) ((Math.abs(offsetX) + touchX + widthPerSize / 2) / widthPerSize);

        for (int i = 1; i < data.getColumns().size(); i++) {
            if (data.getColumns().get(i).show) {
                int value = data.getColumnInt(i, pos);

                paintCircle.setColor(ContextCompat.getColor(getContext(), GlobalManager.nightMode ? R.color.chart_background_dark : R.color.chart_background_light));
                paintCircle.setStyle(Paint.Style.FILL);
                canvas.drawCircle(offsetX + widthPerSize * pos, value * heightPerUser + startY, 20, paintCircle);

                paintCircle.setColor(Color.parseColor(data.getColor().getColorByPos(i - 1)));
                paintCircle.setStyle(Paint.Style.STROKE);
                paintCircle.setStrokeWidth(6f);
                canvas.drawCircle(offsetX + widthPerSize * pos, value * heightPerUser + startY, 20 - (2f / 2), paintCircle);

                Log.d("TEST_POS", data.getColumnInt(i, pos) + "");
            }
        }


    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        threadRunning = true;
        mThread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        threadRunning = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void run() {

        while (threadRunning) {
//            try {
            if (startDraw || touched) {
                if (!block) {
                    long start = System.currentTimeMillis();
                    long ended = 0;

                    Canvas canvas = mSurface.lockCanvas(null);
                    Log.w("CANVAS_LOCK", (System.currentTimeMillis() - start) + "ms");

                    start = System.currentTimeMillis();

                    Log.w("CANVAS_BG", (System.currentTimeMillis() - start) + "ms");

                    start = System.currentTimeMillis();
                    synchronized (mSurface) {
                        if (data != null && height > 0 && width > 0 && end > 0) {
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            if (touched) {
                                drawVertLine(canvas, data);
                            } else if (!animation) {
                                drawData(canvas, data);
                            } else {
                                drawDataAnimate(canvas, data);
                            }
                        }
                    }

                    ended = (System.currentTimeMillis() - start);
                    if (ended > 16) {
                        Log.e("CANVAS_PREPARING", ended + "ms");
                    } else {
                        Log.w("CANVAS_PREPARING", ended + "ms");
                    }


                    start = System.currentTimeMillis();
                    Log.d("CANVAS_SIZE", "WIDTH = " + canvas.getWidth() + "; HEIGHT = " + canvas.getHeight() + "; CLIP_LEFT = " + canvas.getClipBounds().left);
                    mSurface.unlockCanvasAndPost(canvas);
                    ended = (System.currentTimeMillis() - start);
                    if (ended > 16) {
                        Log.e("CANVAS_POST", ended + "ms");
                    } else {
                        Log.w("CANVAS_POST", ended + "ms");
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
//            } catch (Exception e) {
//                Log.e("GraphView2", e.toString());
//            }
        }
    }
}
