package com.belomor.telegramchart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.belomor.telegramchart.BelomorUtil;
import com.belomor.telegramchart.ItemDivider;
import com.belomor.telegramchart.R;
import com.belomor.telegramchart.SeekListener;
import com.belomor.telegramchart.adapter.DataAdapter;
import com.belomor.telegramchart.data.ModelChart;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class GraphView extends FrameLayout implements TextSwitcher.ViewFactory {

    @BindView(R.id.graph_component)
    GraphView2 mGraph;

    @BindView(R.id.graph_seek)
    GraphSeek mGraphSeek;

    @BindView(R.id.data_list)
    RecyclerView mDataList;

    @BindViews({R.id.ts_5, R.id.ts_4, R.id.ts_3, R.id.ts_2, R.id.ts_1, R.id.ts_0})
    List<TextSwitcher> mTextViewSwitcher;

    private ModelChart data;

    private int maxValue = 0;

    int start, end;

    public GraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.belomor_chart, null);
        addView(view);

        ButterKnife.bind(this, view);

        for (TextSwitcher textSwitcher : mTextViewSwitcher) {
            textSwitcher.setFactory(this);
        }

        mGraphSeek.setOnSeekListener(new SeekListener() {
            @Override
            public void onLeftChange(int pos, float xOffset, float zoom, float widthPerItem) {
                start = pos;
                mGraph.rangeChart(start, end, widthPerItem, xOffset);
                calculateMaxValue();
            }

            @Override
            public void onRightChange(int pos, float xOffset, float zoom, float widthPerItem) {
                end = pos - 1;
                mGraph.rangeChart(start, end, widthPerItem, xOffset);
                calculateMaxValue();
            }

            @Override
            public void onSeek(int start, int end, float startOffset, float endOffset, float zoom, float widthPerItem) {
                GraphView.this.start = start;
                GraphView.this.end = end - 1;
                mGraph.rangeChart(start, end, widthPerItem, startOffset);
            }

            @Override
            public void onZoom(float zoom, float startOffset, float endOffset) {

            }
        });
    }

    public void calculateMaxValue() {
        int localMaxValue = 0;
        for (int i = 1; i < data.getColumns().size(); i++) {
            if (data.getColumns().get(i).show) {
                if (localMaxValue < data.getColumns().get(i).getMaxValueInInterval(start, end)) {
                    localMaxValue = data.getColumns().get(i).getMaxValueInInterval(start, end);
                }
            }
        }
        if (maxValue == localMaxValue)
            return;

//        if (maxValue > localMaxValue) {
//            for (int i = 1; i < mTextViewSwitcher.size(); i++) {
//                Animation inAnimation = AnimationUtils.loadAnimation(getContext(),
//                        R.anim.increase_in);
//                Animation outAnimation = AnimationUtils.loadAnimation(getContext(),
//                        R.anim.increase_out);
//                mTextViewSwitcher.get(i).setInAnimation(inAnimation);
//                mTextViewSwitcher.get(i).setOutAnimation(outAnimation);
//            }
//        } else {
//            for (int i = 1; i < mTextViewSwitcher.size(); i++) {
//                Animation inAnimation = AnimationUtils.loadAnimation(getContext(),
//                        R.anim.decrease_in);
//                Animation outAnimation = AnimationUtils.loadAnimation(getContext(),
//                        R.anim.decrease_out);
//                mTextViewSwitcher.get(i).setInAnimation(inAnimation);
//                mTextViewSwitcher.get(i).setOutAnimation(outAnimation);
//            }
//        }

        maxValue = localMaxValue;
//
//        mTextViewSwitcher.get(0).setText("0");
//        for (int i = 1; i < mTextViewSwitcher.size(); i++) {
//            float value = (float) maxValue / 5f * (float) i;
//            mTextViewSwitcher.get(i).setText(String.valueOf(Float.valueOf(value).intValue()));
//        }
    }

    public void setData(ModelChart chartList, int maxFollowers) {
        start = 0;
        end = chartList.getColumns().size() - 3;
        this.data = chartList;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        ItemDivider dividerItemDecoration = new ItemDivider(getResources().getDrawable(R.drawable.divider, null));

        mDataList.addItemDecoration(dividerItemDecoration);

        mGraph.setChartData(chartList, start, end);
        mGraphSeek.setChartData(chartList);

        mDataList.setLayoutManager(linearLayoutManager);
        DataAdapter dataAdapter = new DataAdapter();
        dataAdapter.setGraphViewListener((pos, checked) -> {
            mGraph.redrawGraphs(pos, checked);
            mGraphSeek.redrawGraphs(pos, checked);
            calculateMaxValue();
        });
        dataAdapter.setColumnsData(chartList);
        mDataList.setAdapter(dataAdapter);
        calculateMaxValue();
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textView.setTextSize(BelomorUtil.getSpInPx(5f, getContext()));
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.graph_text_color));
        return textView;
    }
}
