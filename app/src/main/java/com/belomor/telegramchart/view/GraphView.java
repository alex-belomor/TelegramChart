package com.belomor.telegramchart.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.belomor.telegramchart.GlobalManager;
import com.belomor.telegramchart.GraphTouchListener;
import com.belomor.telegramchart.ItemDivider;
import com.belomor.telegramchart.R;
import com.belomor.telegramchart.SeekListener;
import com.belomor.telegramchart.adapter.DataAdapter;
import com.belomor.telegramchart.data.ModelChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class GraphView extends FrameLayout implements GraphTouchListener {

    @BindView(R.id.graph_component)
    GraphComponent mGraph;

    @BindView(R.id.graph_seek)
    GraphSeek mGraphSeek;

    @BindView(R.id.data_list)
    RecyclerView mDataList;

    View popupParent;

    TextView popupDate;

    PopupWindow mPopup;

    @BindViews({R.id.ts_5, R.id.ts_4, R.id.ts_3, R.id.ts_2, R.id.ts_1, R.id.ts_0})
    List<TextSwitcher> mTextViewSwitcher;

    ArrayList<View> valuesTextViewList = new ArrayList<>();

    ItemDivider dividerItemDecoration;

    LinearLayout mContainer;

    private ModelChart data;

    private int maxValue = 0;

    DataAdapter dataAdapter;

    View popUpView;

    int start, end;

    public GraphView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GraphView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.belomor_chart, null);
        addView(view);

        ButterKnife.bind(this, view);

        mGraph.setGraphTouchListener(this);

        mGraphSeek.setOnSeekListener(new SeekListener() {
            @Override
            public void onLeftChange(int pos, float xOffset, float zoom, float widthPerItem) {
                start = pos;
                mGraph.rangeChart(start, end, widthPerItem, xOffset);
                calculateMaxValue();
            }

            @Override
            public void onRightChange(int pos, float xOffset, float zoom, float widthPerItem) {
                end = pos;
                mGraph.rangeChart(start, end, widthPerItem, xOffset);
                calculateMaxValue();
            }

            @Override
            public void onSeek(int start, int end, float startOffset, float endOffset, float zoom, float widthPerItem) {
                GraphView.this.start = start;
                GraphView.this.end = end;
                mGraph.rangeChart(start, end, widthPerItem, startOffset);
            }
        });
    }

    private void createPopup() {
        popUpView = LayoutInflater.from(getContext()).inflate(R.layout.popup_data,
                null);

        mContainer = popUpView.findViewById(R.id.texts_container);
        popupParent = popUpView.findViewById(R.id.parent);
        popupDate = popUpView.findViewById(R.id.date);

        for (int i = 1; i < data.getColumns().size(); i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_data_item, null, false);
            ((TextView) view.findViewById(R.id.count)).setTextColor(Color.parseColor(data.getColor().getColorByPos(i - 1)));
            ((TextView) view.findViewById(R.id.name)).setTextColor(Color.parseColor(data.getColor().getColorByPos(i - 1)));
            mContainer.addView(view);
            valuesTextViewList.add(view);
        }

        mPopup = new PopupWindow(popUpView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, false);
        mPopup.setAnimationStyle(android.R.style.Animation_Dialog);
    }

    private void showPopup(int pos, float x, float y) {
        DateFormat simple = new SimpleDateFormat("E, MMM dd");
        long date = data.getColumnLong(0, pos + 1);
        Date result = new Date(date);
        String text = simple.format(result);
        popupDate.setText(text);

        for (int i = 1; i < data.getColumns().size(); i++) {
            if (data.getColumns().get(i).show) {
                valuesTextViewList.get(i - 1).setVisibility(VISIBLE);
                ((TextView) valuesTextViewList.get(i - 1).findViewById(R.id.count)).setText(String.valueOf(data.getColumnInt(i, pos + 1)));
                String name = data.getColumnName(i);
                ((TextView) valuesTextViewList.get(i - 1).findViewById(R.id.name)).setText(name);
            } else {
                valuesTextViewList.get(i - 1).setVisibility(GONE);
            }
        }

        if (mPopup.isShowing())
            mPopup.update((int) x, (int) y, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
        else
            mPopup.showAtLocation(popUpView, Gravity.NO_GRAVITY, (int) x, (int) y);
    }

    private void dismissPopup() {
        mPopup.dismiss();
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

        maxValue = localMaxValue;
    }

    public void setData(ModelChart chartList, int maxFollowers) {
        start = 0;
        end = chartList.getColumns().size() - 1;
        this.data = chartList;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        ItemDivider dividerItemDecoration = new ItemDivider(getResources().getDrawable(GlobalManager.nightMode ? R.drawable.divider_night : R.drawable.divider_light, null));

        mDataList.addItemDecoration(dividerItemDecoration);

        mGraph.setChartData(chartList, start, end);
        mGraphSeek.setChartData(chartList);

        mDataList.setLayoutManager(linearLayoutManager);
        dataAdapter = new DataAdapter();
        dataAdapter.setGraphViewListener((pos, checked) -> {
            mGraph.redrawGraphs(pos, checked);
            mGraphSeek.redrawGraphs(pos, checked);
            calculateMaxValue();
        });
        dataAdapter.setColumnsData(chartList);
        mDataList.setAdapter(dataAdapter);
        calculateMaxValue();

        createPopup();
    }

    public void updateTheme() {
        if (dividerItemDecoration != null) {
            mDataList.removeItemDecoration(dividerItemDecoration);
        }

        dividerItemDecoration = new ItemDivider(getResources().getDrawable(GlobalManager.nightMode ? R.drawable.divider_night : R.drawable.divider_light, null));

        mDataList.addItemDecoration(dividerItemDecoration);

        dataAdapter.notifyDataSetChanged();

        mGraph.updateColors();

        popupParent.setBackgroundResource(GlobalManager.nightMode ? R.drawable.popup_background_dark : R.drawable.popup_background_light);

        popupDate.setTextColor(ContextCompat.getColor(getContext(), GlobalManager.nightMode ? R.color.white : R.color.black));


        mGraphSeek.updateTheme();

        mGraph.redrawGraphs(-1, true);
    }

    @Override
    public void onTouch(int pos, float x, float y) {
        post(() -> showPopup(pos, x, y));
    }

    @Override
    public void onStopTouch() {
        post(() -> {
            dismissPopup();
        });
    }
}
