package com.parabole.feed.contentparser.models.glossary;

import java.util.Map;
import java.util.Set;

/**
 * Created by anish on 8/22/2016.
 */
public class Glossary {

    public Map<String, Set<GlossaryItem>> getGlossaryItemIndex() {
        return glossaryItemIndex;
    }

    public void setGlossaryItemIndex(Map<String, Set<GlossaryItem>> glossaryItemIndex) {
        this.glossaryItemIndex = glossaryItemIndex;
    }

    private Map<String,Set<GlossaryItem>> glossaryItemIndex;

}
