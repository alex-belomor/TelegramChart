package com.belomor.telegramchart.view.graph;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.belomor.telegramchart.manager.ThemeManager;
import com.belomor.telegramchart.R;
import com.belomor.telegramchart.SeekListener;
import com.belomor.telegramchart.data.ModelChart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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

    @BindView(R.id.off_1)
    View mOff1;

    @BindView(R.id.off_2)
    View mOff2;

    @BindView(R.id.top_border)
    View mTopBorder;

    @BindView(R.id.bottom_border)
    View mBottomBorder;

    private int width = 0;
    private float zoom = 1f;

    private int xStart;
    private int startMarginFrom;
    private int finalMarginFrom;
    private int startMarginTo;
    private int finalMarginTo;
    private float percent;

    private int maxZoomWidth;

    private ModelChart data;

    private float widthPerItem;

    private boolean init = false;

    private SeekListener seekListener;

    public GraphSeek(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.graph_seek, this, false);
        addView(view);

        ButterKnife.bind(this, view);

        updateTheme();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        maxZoomWidth = width / 100 * 85;

        percent = (float) width / 100;

        if (data != null)
            widthPerItem = (float) width / (float) getItemsCount();
    }

    public void updateTheme() {
        mFrom.setImageDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), ThemeManager.nightMode ? R.color.seek_view_border_color_dark : R.color.seek_view_border_color_light)));
        mTo.setImageDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), ThemeManager.nightMode ? R.color.seek_view_border_color_dark : R.color.seek_view_border_color_light)));
        mTopBorder.setBackgroundColor(ContextCompat.getColor(getContext(), ThemeManager.nightMode ? R.color.seek_view_border_color_dark : R.color.seek_view_border_color_light));
        mBottomBorder.setBackgroundColor(ContextCompat.getColor(getContext(), ThemeManager.nightMode ? R.color.seek_view_border_color_dark : R.color.seek_view_border_color_light));
        mOff1.setBackgroundColor(ContextCompat.getColor(getContext(), ThemeManager.nightMode ? R.color.seek_view_off_color_dark : R.color.seek_view_off_color_light));
        mOff2.setBackgroundColor(ContextCompat.getColor(getContext(), ThemeManager.nightMode ? R.color.seek_view_off_color_dark : R.color.seek_view_off_color_light));
    }

    public void setOnSeekListener(SeekListener seekListener) {
        this.seekListener = seekListener;
    }

    public void setChartData(ModelChart data) {
        this.data = data;
        widthPerItem = width / (float) getItemsCount();

        percent = (float) width / 100;

        mSeekView.setChartData(data);
    }

    public void redrawGraphs(int pos, boolean show) {
        if (!init) {
            init = true;
            seekListener.onSeek(0, getItemsCount(), 0, finalMarginTo * zoom, zoom, widthPerItem);
        }
        mSeekView.redrawGraphs(pos, show);
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

                if (layoutParams.rightMargin + finalMarginFrom > maxZoomWidth) {
                    int correctMargin = maxZoomWidth - finalMarginFrom;
                    layoutParams.rightMargin = correctMargin;
                }

                finalMarginTo = layoutParams.rightMargin;

                view.setLayoutParams(layoutParams);
                break;
        }

        zoom = getZoom();

        int change = (int) ((width - finalMarginTo) / widthPerItem);
        seekListener.onRightChange(change + 1, -getOffsetX(), zoom, getNewWidthPerItem());
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

                if (layoutParams.leftMargin + finalMarginTo > maxZoomWidth) {
                    int correctMargin = maxZoomWidth - finalMarginTo;
                    layoutParams.leftMargin = correctMargin;
                }

                finalMarginFrom = layoutParams.leftMargin;

                view.setLayoutParams(layoutParams);
                break;
        }


        zoom = getZoom();

        int change = (int) (finalMarginFrom / widthPerItem);
        seekListener.onLeftChange(change, -getOffsetX(), zoom, getNewWidthPerItem());
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

                finalMarginFrom = lParamsFrom1.leftMargin;
                finalMarginTo = lParamsTo1.rightMargin;

                mFrom.setLayoutParams(lParamsFrom1);
                mTo.setLayoutParams(lParamsTo1);

                break;
        }

        int changeFrom = Math.round((float) finalMarginFrom / widthPerItem);
        int changeTo = Math.round(((float) width - (float) finalMarginTo) / widthPerItem);

        zoom = getZoom();

        seekListener.onSeek(changeFrom, changeTo + 1, -getOffsetX(), finalMarginTo * zoom, zoom, getNewWidthPerItem());
        return false;
    }

    private float getZoom() {
        float marginZoom = (100 - (((float) finalMarginFrom + (float) finalMarginTo) / percent)) / 100;
        return marginZoom;
    }

    private float getZoom(int finalMarginFrom, int finalMarginTo) {
        float marginZoom = (100 - (((float) finalMarginFrom + (float) finalMarginTo) / percent)) / 100;
        return marginZoom;
    }

    private float getNewWidthPerItem() {
        float zoom = getZoom();
        float visibleItems = (float) getItemsCount() * zoom;
        float newWidthPerItem = ((float) width / visibleItems);
        return newWidthPerItem;
    }

    private float getOffsetX() {
        float offsetX = (float) finalMarginFrom / widthPerItem * getNewWidthPerItem();
        return offsetX;
    }

    private int getItemsCount() {
        return data.getColumns().get(0).size() - 2;
    }
}
