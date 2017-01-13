package com.parabole.feed.contentparser.contextparser.models;

/**
 * Created by Rajdeep on 06-Jan-17.
 */
public class Context {
    private String subject;
    private String predicate;
    private String object;
    private String contextTag;
    private String id;

    public String getContextTag() {
        return contextTag;
    }

    public void setContextTag(String contextTag) {
        this.contextTag = contextTag;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
