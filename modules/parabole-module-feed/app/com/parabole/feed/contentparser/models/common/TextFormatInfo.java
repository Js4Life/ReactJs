package com.parabole.feed.contentparser.models.common;

import org.apache.pdfbox.text.TextPosition;

import java.util.List;

/**
 * Created by anish on 7/26/2016.
 */
public class TextFormatInfo {
    private int pageNum;
    private boolean isBold;
    private boolean isItalics;
    private float averageTextHeight;
    private List<TextPosition> textPositions;
    private List<CharacterFormatInfo> characterFormatInfos;

    public List<CharacterFormatInfo> getCharacterFormatInfos() {
        return characterFormatInfos;
    }

    public void setCharacterFormatInfos(List<CharacterFormatInfo> characterFormatInfos) {
        this.characterFormatInfos = characterFormatInfos;
    }

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

    public List<TextPosition> getTextPositions() {
        return textPositions;
    }

    public void setTextPositions(List<TextPosition> textPositions) {
        this.textPositions = textPositions;
    }
}
