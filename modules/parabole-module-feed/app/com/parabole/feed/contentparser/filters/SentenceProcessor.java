package com.parabole.feed.contentparser.filters;

import com.parabole.feed.contentparser.models.common.CharacterFormatInfo;
import com.parabole.feed.contentparser.models.common.LineElement;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;
import com.parabole.feed.contentparser.models.common.WordElement;
import org.apache.pdfbox.text.TextPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 8/22/2016.
 */
public class SentenceProcessor {

    public List<LineElement> processTextSentencesByYCOrd( String text , TextFormatInfo formatInfo){

        int textLen = text.length();

        List<TextPosition> textPositions = formatInfo.getTextPositions();
        float prevY = -1;
        float prevAvg = 0;
        sentenceList = new ArrayList<>();
        int idx = 0;
        for (TextPosition tp : textPositions){
            float yPos = tp.getY();
            if( prevY != -1 && Math.abs(yPos-prevY) > 5 ){ //May Be a New Word Start
                if(lineStart){
                    endCurrentLine();
                    prevY = -1;
                }
            }
            addToCurrentLine(text.charAt(idx), formatInfo.getCharacterFormatInfos().get(idx));
            prevY = yPos;
            idx++;
        }
        if(lineStart)
            endCurrentLine();
        return sentenceList;
    }

    private void endCurrentLine(){
        //if( currentWord.getWord().length() > 0 )
        //wordList.add(currentWord);
        sentenceList.add(currentLineELement);
        currentLineELement = null;
        lineStart = false;
    }

    private void addToCurrentLine(char c , CharacterFormatInfo formatInfo){
        if( currentLineELement == null){
            currentLineELement = new LineElement();
            lineStart = true;
        }
        currentLineELement.addToWord(c,formatInfo);
    }

    boolean lineStart = false;
    WordElement currentWord = null;
    LineElement currentLineELement;
    List<LineElement> sentenceList;
}

//List of Terms Included in this Guide   144A Bond Accounts Payable Accounts Receivable Accrued Expenses Accrued Taxes Algorithmic Trading All Ordinaries American Depositary Receipt (ADR) American Option Amortization Amortization Schedule Amortized Loan Annuity Arbitrage / Arbitraging Asian Option Ask Price Asset-backed Securities (ABS) Asset Swap At-the-money Bank for International Settlements (BIS) Bank of Canada Bank Rate Bankers’ Acceptances Basel Accords Basis Points Bear Spread Behavioral Finance Beta Bid Price Bid-ask Spread Black-Scholes Option Pricing Model/Formula Board of Directors Bond Bond Rating
