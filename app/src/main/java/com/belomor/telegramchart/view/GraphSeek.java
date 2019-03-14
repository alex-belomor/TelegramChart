package com.belomor.telegramchart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.belomor.telegramchart.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GraphSeek extends FrameLayout {

    public GraphSeek(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.graph_seek, this, false);
        addView(view);
    }
}
