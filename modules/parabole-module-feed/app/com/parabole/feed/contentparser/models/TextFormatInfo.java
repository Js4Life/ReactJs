package com.parabole.feed.contentparser.models;

/**
 * Created by anish on 7/26/2016.
 */
public class TextFormatInfo {
    private int pageNum;
    private boolean isBold;
    private boolean isItalics;
    private float averageTextHeight;

    public float getAverageTextHeight() {
        return averageTextHeight;
    }

    public void setAverageTextHeight(float averageTextHeight) {
        this.averageTextHeight = averageTextHeight;
    }


    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setIsBold(boolean isBold) {
        this.isBold = isBold;
    }

    public boolean isItalics() {
        return isItalics;
    }

    public void setIsItalics(boolean isItalics) {
        this.isItalics = isItalics;
    }

}
