package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.belomor.telegramchart.R;
import com.belomor.telegramchart.data.ModelChart;
import com.belomor.telegramchart.data.TestChartData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GraphView extends FrameLayout {

    @BindView(R.id.graph_component)
    GraphView2 mGraph;

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
    }

    public void setData(ArrayList<ModelChart> chartList, int maxFollowers) {
        mGraph.setChartData(chartList);
//        mGraph.getLayoutParams().width = chartList.get(0).getColumnSize(1) * 100 - 100;
//        mGraph.setScaleX(0.5f);
    }

    @OnClick(R.id.decrease_btn)
    void onDecrease() {
        multiplier -= 0.01f;
        mGraph.setMultiplier(multiplier);
    }

    @OnClick(R.id.increase_btn)
    void onIncrease() {
        multiplier += 0.01f;
        mGraph.setMultiplier(multiplier);
    }
}
