package com.parabole.feed.application.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sagiruddin on 16-12-2016.
 */
public class RelatedParagraphsAndMappedConcepts {

    private ArrayList<HashMap<String, String>> paragraphs;
    private Map<String, Set<String>> rConcept;
    private Map<String, Set<String>> contexts;

    public ArrayList<HashMap<String, String>> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(ArrayList<HashMap<String, String>> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public Map<String, Set<String>> getrConcept() {
        return rConcept;
    }

    public void setrConcept(Map<String, Set<String>> rConcept) {
        this.rConcept = rConcept;
    }

    public Map<String, Set<String>> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, Set<String>> contexts) {
        this.contexts = contexts;
    }
}
