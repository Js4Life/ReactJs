package com.parabole.feed.contentparser.models.common;

import com.parabole.feed.contentparser.models.common.ContentElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 7/25/2016.
 */

public class ParagraphElement  extends ContentElement {

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

    public LineElement getLastLine() {
        int lineSize = sentences.size();
        if(lineSize > 0 )
            return sentences.get(lineSize-1);
        else
            return null;
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

    public void addSentences(List<LineElement> s){
        sentences.addAll( s );
    }

    public void addSentence(LineElement s){
        sentences.add(s);
    }

    public List<LineElement> getLines(){
        return sentences;
    }



    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sentences.stream().forEach(a -> {
            sb.append(a.toString());
            sb.append(" ");
            //sb.append("\n");
        });
        return sb.toString();
    }

    private List<LineElement> sentences = new ArrayList<>();

    private StringBuffer bodyText = new StringBuffer();
    private int startPage;
    private int endPage;
    private String firstLine;
    private boolean willIgnore;
}
