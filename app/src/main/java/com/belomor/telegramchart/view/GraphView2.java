package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GraphView2 extends View {

    private ArrayList<ModelChart> data;

    private Paint paint;


    public GraphView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(6f);

        setRotationX(180);
//        setScaleX(0.1f);
    }

    public void setChartData(ArrayList<ModelChart> data) {
        this.data = data;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data != null) {
            drawData(canvas, data.get(0));
        }

//        Matrix matrix = new Matrix();
//        matrix.postRotate(45f);
//        canvas.save();
//        canvas.concat(matrix);
//        canvas.restore();
    }

    private void drawData(Canvas canvas, ModelChart modelChart) {

        float latestX = 0;
        float latestY = 0;

        canvas.drawColor(Color.WHITE);

        String color = modelChart.getColor().getY1();
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        float[] points = new float[modelChart.getColumnSize(1) * 2 + 2];

        Path p = new Path();
        p.moveTo(0, 0);

        for (int i = 1; i < modelChart.getColumnSize(1); i++) {
            p.lineTo(latestX + 100, modelChart.getColumnInt(1, i) * 5);
            latestX = latestX + 100;
        }

        canvas.drawPath(p, paint);
    }


}
