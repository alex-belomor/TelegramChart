package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.belomor.telegramchart.R;
import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class GraphSeek extends FrameLayout {


    @BindView(R.id.from)
    ImageView mFrom;

    @BindView(R.id.to)
    ImageView mTo;

    @BindView(R.id.graph_seek_view)
    SeekView mSeekView;

    private int height = 0;
    private int width = 0;

    private int xDelta;
    private int yDelta;
    private int xStart;
    private int margin;
    private int startMarginFrom;
    private int startMarginTo;

    private ArrayList<ModelChart> data;


    public GraphSeek(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.graph_seek, this, false);
        addView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    public void setChartData(ArrayList<ModelChart> data) {
        this.data = data;

        mSeekView.setChartData(data);
    }

    @OnTouch(R.id.to)
    boolean onToTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ConstraintLayout.LayoutParams lParams = (ConstraintLayout.LayoutParams)
                        view.getLayoutParams();

                xStart = (int) event.getRawX();
                startMarginTo = lParams.rightMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view
                        .getLayoutParams();

                int x = (int) (xStart - event.getRawX());

                layoutParams.rightMargin = startMarginTo + x;

                if (layoutParams.rightMargin < 0)
                    layoutParams.rightMargin = 0;

                view.setLayoutParams(layoutParams);
                break;
        }

        invalidate();
        return false;
    }

    @OnTouch(R.id.from)
    boolean onFromTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ConstraintLayout.LayoutParams lParams = (ConstraintLayout.LayoutParams)
                        view.getLayoutParams();

                xStart = (int) event.getRawX();
                startMarginFrom = lParams.leftMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view
                        .getLayoutParams();

                int x = (int) (event.getRawX() - xStart);

                layoutParams.leftMargin = startMarginFrom + x;

                if (layoutParams.leftMargin < 0)
                    layoutParams.leftMargin = 0;

                view.setLayoutParams(layoutParams);
                break;
        }

        invalidate();
        return false;
    }

    @OnTouch(R.id.selector)
    boolean onSelectorTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ConstraintLayout.LayoutParams lParamsFrom = (ConstraintLayout.LayoutParams)
                        mFrom.getLayoutParams();
                ConstraintLayout.LayoutParams lParamsTo = (ConstraintLayout.LayoutParams)
                        mTo.getLayoutParams();

                startMarginTo = lParamsTo.rightMargin;
                startMarginFrom = lParamsFrom.leftMargin;

                xStart = (int) event.getRawX();

                break;
            case MotionEvent.ACTION_MOVE:
                ConstraintLayout.LayoutParams lParamsFrom1 = (ConstraintLayout.LayoutParams)
                        mFrom.getLayoutParams();
                ConstraintLayout.LayoutParams lParamsTo1 = (ConstraintLayout.LayoutParams)
                        mTo.getLayoutParams();

                int x = (int) (event.getRawX() - xStart);

                if (startMarginFrom + x < 0) {
                    int difference = Math.abs(0 - (startMarginFrom + x));
                    lParamsFrom1.leftMargin = startMarginFrom + x + difference;
                    lParamsTo1.rightMargin = startMarginTo - x - difference;
                } else if (startMarginTo - x < 0) {
                    int difference = Math.abs(0 - (startMarginTo - x));
                    lParamsFrom1.leftMargin = startMarginFrom + x - difference;
                    lParamsTo1.rightMargin = startMarginTo - x + difference;
                } else {
                    lParamsFrom1.leftMargin = startMarginFrom + x;
                    lParamsTo1.rightMargin = startMarginTo - x;
                }


                mFrom.setLayoutParams(lParamsFrom1);
                mTo.setLayoutParams(lParamsTo1);

                break;
        }

        invalidate();
        return false;
    }
}
