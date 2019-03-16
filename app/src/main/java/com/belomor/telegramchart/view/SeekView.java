package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.belomor.telegramchart.data.ModelChart;

import androidx.annotation.Nullable;

public class SeekView extends View {

    private ModelChart data;

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

    public void setChartData(ModelChart data) {
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
            drawData(canvas, data);
        }
    }


    private void drawData(Canvas canvas, ModelChart modelChart) {
        heightPerUser = 0f;
        int theMostMaxValue = 0;
        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            if (modelChart.getColumns().get(i).show) {
                int localTheMostMaxValue = modelChart.getColumns().get(i).getMaxValue();
                if (localTheMostMaxValue > theMostMaxValue) {
                    theMostMaxValue = localTheMostMaxValue;
                    heightPerUser = (float) height / (float) theMostMaxValue;
                }
            }
        }
        widthPerSize = (float) width / (float) (modelChart.getColumns().get(0).size() - 2);

        canvas.drawColor(Color.WHITE);

        paint.setAntiAlias(true);

        for (int i = 1; i < modelChart.getColumns().size(); i++) {
            float latestX = 0;
            String color = modelChart.getColor().getColorByPos(i - 1);
            paint.setColor(Color.parseColor(color));
            paint.setStyle(Paint.Style.STROKE);

            Path p = new Path();
            p.moveTo(0f, modelChart.getColumnInt(i, 0));

            for (int j = 1; j < modelChart.getColumnSize(i); j++) {
                p.lineTo(latestX + (widthPerSize), modelChart.getColumnInt(i, j) * heightPerUser);
                latestX = latestX + (widthPerSize);
            }

            canvas.drawPath(p, paint);
        }

//            postInvalidateDelayed(200);
    }


}
