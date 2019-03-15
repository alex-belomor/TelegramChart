package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.belomor.telegramchart.R;
import com.belomor.telegramchart.SeekListener;
import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GraphView extends FrameLayout {

    @BindView(R.id.graph_component)
    GraphView2 mGraph;

    @BindView(R.id.graph_seek)
    GraphSeek mGraphSeek;

    int start, end;

    public GraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.belomor_chart, null);
        addView(view);

        ButterKnife.bind(this, view);

        mGraphSeek.setOnSeekListener(new SeekListener() {
            @Override
            public void onLeftChange(int pos) {
                start = pos;
                mGraph.rangeChart(start, end);
            }

            @Override
            public void onRightChange(int pos) {
                end = pos - 1;
                mGraph.rangeChart(start, end);
            }
        });
    }

    public void setData(ArrayList<ModelChart> chartList, int maxFollowers) {
        mGraph.setChartData(chartList, start, end);
        mGraphSeek.setChartData(chartList);
    }
}
