package com.belomor.telegramchart;

public interface SeekListener {
    void onLeftChange(int pos, float xOffset, float zoom, float widthPerItem);
    void onRightChange(int pos, float xOffset, float zoom, float widthPerItem);
    void onSeek(int pos, int pos2, float startOffset, float endOffset, float zoom, float widthPerItem);
}
