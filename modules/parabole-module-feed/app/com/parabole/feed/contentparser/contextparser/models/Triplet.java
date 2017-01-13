package com.parabole.feed.contentparser.contextparser.models;

import java.util.List;

/**
 * Created by Rajdeep on 02-Jan-17.
 */
public class Triplet {
    private String subject;
    private String predicate;
    private String object;


    public Triplet(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
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

    @Override
    public String toString(){
        return ("\n(Subject) :: " + this.subject + " (Predicate) :: " + this.predicate + " (Object) :: " + object );
    }
}
