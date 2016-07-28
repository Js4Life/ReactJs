package com.parabole.feed.contentparser;

import com.parabole.feed.contentparser.filters.IParagraphProcessor;
import com.parabole.feed.contentparser.models.ParagraphElement;
import com.parabole.feed.contentparser.models.TextFormatInfo;
import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;

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
        if( paraId != null){
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
