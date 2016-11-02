package com.parabole.contentparser.models.basel;

import com.parabole.contentparser.models.common.DocMetaInfo;

import java.util.Map;

/**
 * Created by parabole on 10/14/2016.
 */
public class BaselDocMeta extends DocMetaInfo {
    public String getParaEndRegEx() {
        return paraEndRegEx;
    }

    public void setParaEndRegEx(String paraEndRegEx) {
        this.paraEndRegEx = paraEndRegEx;
    }

    private String paraEndRegEx;

    public String getParaStartRegEx() {
        return paraStartRegEx;
    }

    public void setParaStartRegEx(String paraStartRegEx) {
        this.paraStartRegEx = paraStartRegEx;
    }

    private String paraStartRegEx;

    public Map<Integer, String> getLevelSelector() {
        return levelSelector;
    }

    public void setLevelSelector(Map<Integer, String> levelSelector) {
        this.levelSelector = levelSelector;
    }

    private Map<Integer, String> levelSelector;

    private String startText;

    public String getStartText() {
        return startText;
    }

    public void setStartText(String startText) {
        this.startText = startText;
    }

    public String getEndText() {
        return endText;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }

    private String endText;

    public int[] getParagraphSelectorLevel() {
        return paragraphSelectorLevel;
    }

    public void setParagraphSelectorLevel(int[] paragraphSelectorLevel) {
        this.paragraphSelectorLevel = paragraphSelectorLevel;
    }

    private int[] paragraphSelectorLevel;

    public float getParagraphFontSize() {
        return paragraphFontSize;
    }

    public void setParagraphFontSize(float paragraphFontSize) {
        this.paragraphFontSize = paragraphFontSize;
    }

    private float paragraphFontSize;
}
