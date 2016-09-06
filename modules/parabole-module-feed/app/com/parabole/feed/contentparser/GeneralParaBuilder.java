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


    public GeneralParaBuilder(String path) throws IOException {
        super(path);
    }

    @Override
    public List<ParagraphElement>  startProcessing( DocMetaInfo metaInfo) throws IOException {
        docMeta = metaInfo;
        startStriping();
        return paragraphElementList;
    }

    @Override
    public void addChunk(String text, TextFormatInfo format, boolean isNewPara) {
        if( text.trim().length() == 0 )
            return;
        List<LineElement> lineList = sentenceProcessor.processTextSentencesByYCOrd(text, format);
        if( isNewPara ){
            currentPara = new ParagraphElement();
            paragraphElementList.add(currentPara);
        }
        if(currentPara != null)
            currentPara.addSentences(lineList);
    }

    SentenceProcessor sentenceProcessor = new SentenceProcessor();
    ParagraphElement currentPara = null;
    List<ParagraphElement> paragraphElementList = new ArrayList<>();
}
