package com.parabole.feed.contentparser.models.common;

import org.apache.pdfbox.text.TextPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 8/22/2016.
 */
public class WordElement extends ContentElement {

    private float startY;
    private boolean isBold;
    private boolean isItaics;

    public boolean isBold() {
        return isBold;
    }

    public boolean isItaics() {
        return isItaics;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public void addCharacter(char c , CharacterFormatInfo formatInfo){
        //CharacterFormatInfo format = new CharacterFormatInfo();
        isBold = formatInfo.isBold();
        isItaics = formatInfo.isItalics();
        wordString.append(c);
        formatInfos.add(formatInfo);
    }

    public String getWord(){
        return wordString.toString().trim();
    }

    public float getWordHeight(){
        if( wordHeight != -1)
            return wordHeight;
        wordHeight = 0;
        formatInfos.stream().forEach(a -> {
            wordHeight += a.getHeight();
        });
        wordHeight = wordHeight/formatInfos.size();
        return wordHeight;
    }

    public float getFirstCharacterStartY(){
        if(formatInfos.size() == 0)
            return -1;
        CharacterFormatInfo cInfo = formatInfos.get(0);
        return cInfo.getStartX();
    }

    @Override
    public String toString(){
        return wordString.toString();
    }

    float wordHeight = -1;
    StringBuilder wordString = new StringBuilder();
    List<CharacterFormatInfo> formatInfos = new ArrayList<>();
}
