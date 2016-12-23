package com.parabole.feed.contentparser.glossary;

import com.parabole.feed.contentparser.IDocIndexBuilder;
import com.parabole.feed.contentparser.models.common.DocMetaInfo;
import com.parabole.feed.contentparser.models.common.LineElement;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.parabole.feed.contentparser.models.common.WordElement;
import com.parabole.feed.contentparser.models.fasb.DocumentData;
import com.parabole.feed.contentparser.models.fasb.DocumentElement;
import com.parabole.feed.contentparser.models.glossary.FASBAccountingGlossaryMetadata;
import com.parabole.feed.contentparser.models.glossary.Glossary;
import com.parabole.feed.contentparser.models.glossary.GlossaryDocMeta;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anish on 9/2/2016.
 */
public class FASBAccoutingGlossaryBuilder {

    public FASBAccoutingGlossaryBuilder(IDocIndexBuilder indexBuilder, JSONObject glossaryMetaData){
        docIndexBuilder = indexBuilder;
        if(glossaryMetaData != null){
            this.docMeta = getGlossaryMetadata(glossaryMetaData);
        } else {
            this.docMeta = getGlossaryMetadata();
        }
    }

    public DocumentData buildItemTree() throws IOException {
        List<ParagraphElement> paraList = docIndexBuilder.startProcessing(docMeta);
        return buildTree(paraList);
    }

    private DocumentData buildTree(List<ParagraphElement> paraList) {

        DocumentData documentData = new DocumentData();
        documentData.setDocName(docIndexBuilder.getFileName());
        int isTopic = -1;
        float topicHeight = -1;
        float subtopicHeight = -1;

        for(ParagraphElement aPara : paraList) {
            List<LineElement> lines = aPara.getLines();
            for (LineElement aLine : lines) {
                List<WordElement> wordList = aLine.getWordList();
                WordElement firstWord = wordList.get(0);
                float firstWordHeight = firstWord.getWordHeight();
                String first2words = firstWord.getWord() + wordList.get(1).getWord();
                //If it matches the regex then store the font for the first time
                if (first2words != null) {
                    if (isTopic(first2words)){
                        if(topicHeight == -1 ) {
                            isTopic = 0;
                            topicHeight = firstWord.getWordHeight();
                        }else{
                            if(topicHeight == firstWordHeight)
                                isTopic = 0;
                        }
                    }
                    if( isTopic == -1) { //Not a Topic
                        if( isSubTopic(first2words)) {
                            if (subtopicHeight == -1) {
                                isTopic = 1;
                                subtopicHeight = firstWord.getWordHeight();
                            } else {
                                if (subtopicHeight == firstWordHeight)
                                    isTopic = 1;
                            }
                        }
                    }
                    if( isTopic != -1) {
                        String name = aLine.getRemainingWords(2);
                        if (isTopic == 0)
                            documentData.addNewTopic(firstWord.getWord(), name);
                        else
                            documentData.addNewSubTopic(firstWord.getWord(), name);
                    }
                    isTopic = -1;
                }
            }
        }
        return documentData;
        }

    private boolean isTopic(String topic){
        FASBAccountingGlossaryMetadata glossaryDocMeta = (FASBAccountingGlossaryMetadata)docMeta;
        String topicRegex = glossaryDocMeta.getTopicStartRegEx();
        Pattern pattern = Pattern.compile(topicRegex);
        Matcher matcher = pattern.matcher(topic);
        if(matcher.find())
            return true;
        else
            return false;
    }

    private boolean isSubTopic(String subttopic){
        FASBAccountingGlossaryMetadata glossaryDocMeta = (FASBAccountingGlossaryMetadata)docMeta;
        String topicRegex = glossaryDocMeta.getSubtopicRegEx();
        Pattern pattern = Pattern.compile(topicRegex);
        Matcher matcher = pattern.matcher(subttopic);
        if(matcher.find())
            return true;
        else
            return false;
    }

    private DocMetaInfo getGlossaryMetadata() {
        FASBAccountingGlossaryMetadata glossaryDocMeta = new FASBAccountingGlossaryMetadata();
        glossaryDocMeta.setStartPage(1);
        glossaryDocMeta.setEndPage(15);
        glossaryDocMeta.setTopicStartRegEx("\\w{3,3}[—]");
        glossaryDocMeta.setSubtopicRegEx("\\w{2,3}[—]");
        return glossaryDocMeta;
    }
    private DocMetaInfo getGlossaryMetadata(JSONObject glossaryMetaData) {
        FASBAccountingGlossaryMetadata glossaryDocMeta = new FASBAccountingGlossaryMetadata();
        glossaryDocMeta.setStartPage(glossaryMetaData.getInt("fromPage"));
        glossaryDocMeta.setEndPage(glossaryMetaData.getInt("toPage"));
        glossaryDocMeta.setTopicStartRegEx(glossaryMetaData.getString("topicRegex"));
        glossaryDocMeta.setSubtopicRegEx(glossaryMetaData.getString("subtopicRegex"));
        return glossaryDocMeta;
    }

    IDocIndexBuilder docIndexBuilder;
    DocMetaInfo docMeta;
}
