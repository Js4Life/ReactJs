package com.parabole.feed.contentparser.models.common;

/**
 * Created by anish on 7/25/2016.
 */
public abstract class ContentElement {

    private String id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
