package com.parabole.feed.contentparser.models;

/**
 * Created by anish on 7/25/2016.
 */
public class ParagraphElement  extends ContentElement{

    public String getBodyText() {
        return bodyText.toString();
    }

    public void addBodyText(String bodyText) {
        this.bodyText.append(bodyText);
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public boolean isWillIgnore() {
        return willIgnore;
    }

    public void setWillIgnore(boolean willIgnore) {
        this.willIgnore = willIgnore;
    }

    private StringBuffer bodyText = new StringBuffer();
    private int startPage;
    private int endPage;
    private String firstLine;
    private boolean willIgnore;
}
