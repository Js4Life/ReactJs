package com.parabole.feed.contentparser.models.common;

/**
 * Created by anish on 8/22/2016.
 */
public class CharacterFormatInfo {
    private boolean isBold;
    private boolean isItalics;
    private float   height;
    private float   startX;
    private float   startY;

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }



    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }
}
