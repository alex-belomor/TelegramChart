package com.belomor.telegramchart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belomor.telegramchart.data.ModelChart;
import com.belomor.telegramchart.view.GraphView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentChart extends Fragment {

    @BindView(R.id.graph_view)
    GraphView mGraph;

    ModelChart modelChart;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        unbinder = ButterKnife.bind(this, view);

        mGraph.setData(modelChart, 0);
        return view;
    }

    public void setModelChart(ModelChart modelChart) {
        this.modelChart = modelChart;
    }
}
