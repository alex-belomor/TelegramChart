package com.belomor.telegramchart;

public interface SeekListener {
    void onLeftChange(int pos, float xOffset);
    void onRightChange(int pos, float xOffset);
    void onSeek(int pos, int pos2, float startOffset, float endOffset, float zoom, float widthPerItem);
    void onZoom(float zoom, float startOffset, float endOffset);
}
