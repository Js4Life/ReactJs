package com.parabole.feed.contentparser.models.fasb;

import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by anish on 9/2/2016.
 */
public class DocumentElement {

    public enum ElementTypes{
        TOPIC,
        SUBTOPIC
    }

    private Set <DocumentElement> children = new HashSet<>();
    private String id;
    private ElementTypes elementType;
    private String name;

    public Set<DocumentElement> getChildren() {
        return children;
    }

    public void setChildren(Set<DocumentElement> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ElementTypes getElementType() {
        return elementType;
    }

    public void setElementTypes(ElementTypes elementType) {
        this.elementType = elementType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
