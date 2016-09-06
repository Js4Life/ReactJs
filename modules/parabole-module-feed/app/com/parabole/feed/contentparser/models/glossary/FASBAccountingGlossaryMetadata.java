package com.parabole.feed.contentparser.models.glossary;

import com.parabole.feed.contentparser.models.common.DocMetaInfo;

/**
 * Created by anish on 9/2/2016.
 */
public class FASBAccountingGlossaryMetadata extends DocMetaInfo {

    private String topicStartRegEx;

    private String subtopicRegEx;

    public String getTopicStartRegEx() {
        return topicStartRegEx;
    }

    public void setTopicStartRegEx(String topicStartRegEx) {
        this.topicStartRegEx = topicStartRegEx;
    }

    public String getSubtopicRegEx() {
        return subtopicRegEx;
    }

    public void setSubtopicRegEx(String subtopicRegEx) {
        this.subtopicRegEx = subtopicRegEx;
    }
}
