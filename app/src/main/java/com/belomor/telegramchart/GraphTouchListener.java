package com.belomor.telegramchart;

public interface GraphTouchListener {
    void onTouch(int pos, float x, float y);
    void onStopTouch();
}
