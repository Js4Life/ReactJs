package com.parabole.feed.contentparser.fasb;

import com.parabole.feed.contentparser.filters.IParagraphProcessor;
import com.parabole.feed.contentparser.models.common.CharacterFormatInfo;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Created by anish on 7/26/2016.
 */
public class FASBParagraphBuider implements IParagraphBuilder {

    public FASBParagraphBuider( IParagraphProcessor paraProc){
        paragraphProcessor = paraProc;
    }

    @Override
    public void buildParagraph(String text, TextFormatInfo format) {
        int pageNum = format.getPageNum();
        String paraId = paragraphProcessor.IsNewParagraphStart(text);
        boolean isBold = false;
        if(paraId != null){
            List<CharacterFormatInfo> charFmtList = format.getCharacterFormatInfos();
            int boldCount = 0;
            for(int i = 0; i < paraId.length();i++){
                if(charFmtList.get(i).isBold())
                    boldCount++;
            }
            isBold = boldCount == paraId.length();
        }
        if( paraId != null && isBold){

            if( currentParagraph != null){
                endParagraph(pageNum);
            }
            startNewParagraph(paraId,text,pageNum);

        }else{ //May be a continuation of prev or the last para

            if( currentParagraph != null) {
                if (paragraphProcessor.IsEndOfParagraph(text, currentParagraph, format)) {
                    endParagraph(pageNum);
                } else {
                    if (currentParagraph != null) {
                        currentParagraph.addBodyText("\n");
                        currentParagraph.addBodyText(text);
                    }
                }
            }
        }

    }

    @Override
    public void addToCurrentParagraph(String text) {
        if (currentParagraph != null)
            currentParagraph.addBodyText(text);
    }

    @Override
    public void addPostProcessParagraph(Consumer<ParagraphElement> functor) {
        paraPostProcessor = functor;
    }

    @Override
    public Map<String, ParagraphElement> getAllParagraphs() {
        return paragraphMap;
    }

    private void startNewParagraph(String id , String text ,int pageNum) {
        currentParagraph = new ParagraphElement();
        currentParagraph.setId(id);
        currentParagraph.setStartPage(pageNum);
        currentParagraph.addBodyText(text);
        currentParagraph.setFirstLine(text);
        paragraphMap.put(id,currentParagraph);
    }

    private void endParagraph(int pageNum){
        currentParagraph.setEndPage(pageNum);
        postProcessParagraph();
    }

    private void postProcessParagraph() {
        if(paraPostProcessor != null)
            paraPostProcessor.accept(currentParagraph);
    }

    private Map<String,ParagraphElement> paragraphMap = new TreeMap<>();
    private ParagraphElement currentParagraph = null;
    private IParagraphProcessor paragraphProcessor;
    private Consumer<ParagraphElement> paraPostProcessor = null;
}
