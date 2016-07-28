package com.parabole.feed.contentparser.models;

/**
 * Created by anish on 7/26/2016.
 */
public class FASBDocMeta {

    private String paraStartRegEx;
    private int startPage;
    private int endPage;
    private String paraIgnore;

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


    public String getParaStartRegEx() {
        return paraStartRegEx;
    }

    public void setParaStartRegEx(String paraStartRegEx) {
        this.paraStartRegEx = paraStartRegEx;
    }

    public String getParaIgnore() {
        return paraIgnore;
    }

    public void setParaIgnore(String paraIgnore) {
        this.paraIgnore = paraIgnore;
    }
}
