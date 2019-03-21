package com.belomor.telegramchart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.belomor.telegramchart.R;
import com.belomor.telegramchart.data.ModelChart;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolderItem> {

    ArrayList<ModelChart> charts;

    private OnItemClickListener onItemClickListener;

    public ItemAdapter(ArrayList<ModelChart> charts, OnItemClickListener onItemClickListener) {
        this.charts = charts;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return charts.size();
    }

    class ViewHolderItem extends RecyclerView.ViewHolder {

        @BindView(R.id.item)
        TextView mItem;

        int pos;

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onItemClickListener.onClick(pos));
        }

        public void bindData(int pos) {
            this.pos = pos;
            mItem.setText("Chart #" + (pos + 1));
        }
    }

    public interface OnItemClickListener {
        void onClick(int pos);
    }
}
