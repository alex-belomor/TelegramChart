package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.belomor.telegramchart.ItemDivider;
import com.belomor.telegramchart.R;
import com.belomor.telegramchart.SeekListener;
import com.belomor.telegramchart.adapter.DataAdapter;
import com.belomor.telegramchart.data.ModelChart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GraphView extends FrameLayout {

    @BindView(R.id.graph_component)
    GraphView2 mGraph;

    @BindView(R.id.graph_seek)
    GraphSeek mGraphSeek;

    @BindView(R.id.data_list)
    RecyclerView mDataList;

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

    public void setData(ModelChart chartList, int maxFollowers) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        ItemDivider dividerItemDecoration = new ItemDivider(getResources().getDrawable(R.drawable.divider, null));

        mDataList.addItemDecoration(dividerItemDecoration);

        mGraph.setChartData(chartList, start, end);
        mGraphSeek.setChartData(chartList);

        mDataList.setLayoutManager(linearLayoutManager);
        DataAdapter dataAdapter = new DataAdapter();
        dataAdapter.setGraphViewListener(() -> mGraph.rangeChart(start, end));
        dataAdapter.setColumnsData(chartList);
        mDataList.setAdapter(dataAdapter);
    }
}
