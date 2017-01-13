package com.parabole.feed.contentparser;

import com.parabole.feed.contentparser.filters.SentenceProcessor;
import com.parabole.feed.contentparser.models.common.DocMetaInfo;
import com.parabole.feed.contentparser.models.common.LineElement;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 9/2/2016.
 */
public class GeneralParaBuilder extends AbstractDocBuilder implements IDocIndexBuilder {


    private Boolean isTableOfContent = false;
    public GeneralParaBuilder(String path, Boolean isTableOfContent) throws IOException {
        super(path);
        if(isTableOfContent != null)
            this.isTableOfContent = isTableOfContent;
    }

    @Override
    public List<ParagraphElement>  startProcessing( DocMetaInfo metaInfo) throws IOException {
        docMeta = metaInfo;
        startStriping();
        return paragraphElementList;
    }

    @Override
    public void addChunk(String text, TextFormatInfo format, boolean isNewPara) {
        int pageNum = format.getPageNum();
        if( text.trim().length() == 0 )
            return;
        List<LineElement> lineList = sentenceProcessor.processTextSentencesByYCOrd(text, format);

        if( isNewPara ){

            for (LineElement aLine : lineList){
                float curStart = aLine.getLineStart();
                float lastWordHeight = -1 , currentWordHeight= aLine.getWordList().get(0).getWordHeight();
                if(currentPara != null){
                    lastWordHeight = currentPara.getLastLine().getWordList().get(0).getWordHeight();
                }

                if(isTableOfContent){
                    currentPara = new ParagraphElement();
                    currentPara.setStartPage(pageNum);
                    currentPara.setEndPage(pageNum);
                    paragraphElementList.add(currentPara);
                    currentPara.addSentence(aLine);
                } else {
                    if (curStart != paraStart || (currentWordHeight != lastWordHeight)) { //This is not a new Para Start
                        currentPara = new ParagraphElement();
                        currentPara.setStartPage(pageNum);
                        paragraphElementList.add(currentPara);
                    }
                    if (currentPara != null) {
                        currentPara.addSentence(aLine);
                        currentPara.setEndPage(pageNum);
                    }
                }
            }
            if(paraStart == 0 && lineList.size() > 0) {
                LineElement le = lineList.get(0);
                paraStart = le.getLineStart();
            }
        }else {
            if (currentPara != null)
                currentPara.addSentences(lineList);
        }
    }

    float paraStart = 0;
    SentenceProcessor sentenceProcessor = new SentenceProcessor();
    ParagraphElement currentPara = null;
    List<ParagraphElement> paragraphElementList = new ArrayList<>();
}
