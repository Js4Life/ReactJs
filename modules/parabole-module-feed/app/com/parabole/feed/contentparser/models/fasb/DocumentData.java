package com.parabole.feed.contentparser.models.fasb;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by anish on 9/2/2016.
 */
public class DocumentData {
    private String docName;

    public DocumentData(){
        topics = new HashSet<>();
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public Set<DocumentElement> getTopics() {
        return topics;
    }

    public void setTopics(Set<DocumentElement> topics) {
        this.topics = topics;
    }

    public void addNewTopic(String id , String topicName){
        if( currentTopic != null)
            currentTopic = null;
        currentTopic = new DocumentElement();
        currentTopic.setId(id.trim());
        currentTopic.setName(topicName.trim());
        currentTopic.setElementTypes(DocumentElement.ElementTypes.TOPIC);
        //
        topics.add(currentTopic);
    }

    public void addNewSubTopic(String id , String subtopicName) {
        if(currentTopic == null)
            return;
        DocumentElement subTopic = new DocumentElement();
        subTopic.setName(subtopicName.trim());
        subTopic.setId(id.trim());
        subTopic.setElementTypes(DocumentElement.ElementTypes.SUBTOPIC);
        currentTopic.getChildren().add(subTopic);
    }

    private DocumentElement currentTopic;
    private Set<DocumentElement> topics;
}
