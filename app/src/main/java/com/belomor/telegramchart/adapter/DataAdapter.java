package com.belomor.telegramchart.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.belomor.telegramchart.ThemeManager;
import com.belomor.telegramchart.GraphViewListener;
import com.belomor.telegramchart.R;
import com.belomor.telegramchart.data.ModelChart;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private ModelChart modelChart;

    private GraphViewListener graphViewListener;

    public void setColumnsData(ModelChart modelChart) {
        this.modelChart = modelChart;
    }

    public void setGraphViewListener(GraphViewListener graphViewListener) {
        this.graphViewListener = graphViewListener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_data_item, parent, false);
        return new DataViewHolder(view, graphViewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.bindPos(position);
    }

    @Override
    public int getItemCount() {
        return modelChart.getColumns().size() - 1;
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkbox)
        CheckBox checkBox;

        @BindView(R.id.title)
        TextView title;

        private GraphViewListener graphViewListener;

        private int pos;

        public DataViewHolder(@NonNull View itemView, GraphViewListener graphViewListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.graphViewListener = graphViewListener;

            itemView.setOnClickListener(v -> {
                checkBox.setChecked(!checkBox.isChecked());
            });
        }

        public void bindPos(int pos) {
            this.pos = pos;
            checkBox.setChecked(modelChart.getColumns().get(pos + 1).show);
            title.setText(modelChart.getName().getNameByPos(pos));

            title.setTextColor(ContextCompat.getColor(title.getContext(), ThemeManager.nightMode ? R.color.white : R.color.black));


            int color = Color.parseColor(modelChart.getColor().getColorByPos(pos));
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {color, color};
            CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
        }

        @OnCheckedChanged(R.id.checkbox)
        void onChecked(boolean checked) {
            modelChart.getColumns().get(pos + 1).show = checked;
            graphViewListener.onDataChanged(pos + 1, checked);
        }
    }
}
