package com.parabole.feed.contentparser.models.fasb;

import com.parabole.feed.contentparser.models.common.ParagraphElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by anish on 7/26/2016.
 */
public class FASBIndexedDocument {

    private Map<String,ParagraphElement> paragraphs;
    private HashMap<String,Set<String>> conceptIndex = new HashMap<>() ;

    public Map<String, ParagraphElement> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(Map<String, ParagraphElement> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public HashMap<String, Set<String>> getConceptIndex() {
        return conceptIndex;
    }

    public void setConceptIndex(HashMap<String, Set<String>> conceptIndex) {
        this.conceptIndex = conceptIndex;
    }
}
