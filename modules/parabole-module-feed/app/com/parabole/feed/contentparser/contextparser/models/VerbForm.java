package com.parabole.feed.contentparser.contextparser.models;

/**
 * Created by Rajdeep on 10-Jan-17.
 */
public class VerbForm {
    private String Verb;
    private String Tag;

    public VerbForm(String verb, String tag) {
        Verb = verb;
        Tag = tag;
    }

    public String getVerb() {
        return Verb;
    }

    public void setVerb(String verb) {
        Verb = verb;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }
}
