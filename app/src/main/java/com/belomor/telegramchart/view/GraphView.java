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
    Bitmap frame;
    Canvas frameDrawer;
    Rect bounds;
    Paint paint;

    float multiplier = 1f;

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
                Log.d("SUKABLYAT_LEFT", pos + "");
            }

            @Override
            public void onRightChange(int pos) {
                end = pos - 1;
                mGraph.rangeChart(start, end);
                Log.d("SUKABLYAT_RIGHT", pos + "");
            }
        });
    }

    public void setData(ArrayList<ModelChart> chartList, int maxFollowers) {
        mGraph.setChartData(chartList, start, end);
        mGraphSeek.setChartData(chartList);
//        mGraph.getLayoutParams().width = chartList.get(0).getColumnSize(1) * 100 - 100;
//        mGraph.setScaleX(0.5f);
    }

//    @OnClick(R.id.decrease_btn)
//    void onDecrease() {
//        multiplier -= 0.01f;
//        mGraph.setMultiplier(multiplier);
//    }
//
//    @OnClick(R.id.increase_btn)
//    void onIncrease() {
//        multiplier += 0.01f;
//        mGraph.setMultiplier(multiplier);
//    }
}
