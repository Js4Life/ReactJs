package com.parabole.feed.contentparser.filters;

import com.parabole.feed.contentparser.models.fasb.FASBDocMeta;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anish on 7/26/2016.
 */
public class FASBParagraphProcessor implements IParagraphProcessor {

    public FASBParagraphProcessor( FASBDocMeta docMeta){
        fasbDocMeta = docMeta;
    }

    @Override
    public String IsNewParagraphStart(String text) {

        String firstWord = getFirstWord(text);
        if( firstWord == null)
            return null;

        Pattern pattern = Pattern.compile(fasbDocMeta.getParaStartRegEx());
        Matcher matcher = pattern.matcher(firstWord);
        if(matcher.find())
            return matcher.group();
        else
            return null;
    }

    @Override
    public boolean IsEndOfParagraph( String text , ParagraphElement para  , TextFormatInfo formatInfo) {

        if( formatInfo.isBold() || formatInfo.isBold() || formatInfo.getPageNum() > fasbDocMeta.getEndPage())
            return true;
        else
            return false;
    }

    private String getFirstWord( String text){
        if( text.isEmpty())
            return null;
        int idx = text.indexOf(" ");
        if( idx == -1 )
            return null;
        return text.substring(0,idx);
    }

    private String firstWord;
    private FASBDocMeta fasbDocMeta;
}
