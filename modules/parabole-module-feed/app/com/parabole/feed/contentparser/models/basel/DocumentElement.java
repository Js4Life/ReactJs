package com.parabole.feed.contentparser.models.basel;

import java.util.*;

/**
 * Created by parabole on 10/14/2016.
 */
public class DocumentElement {

    private List<DocumentElement> children;
    private String id;
    private String mapId;
    private ElementTypes elementType;
    private String name;
    private int level;
    private String content;
    private float startX;

    public DocumentElement() {
        this.id = UUID.randomUUID().toString();
        children = new ArrayList<>();
    }

    public enum ElementTypes{
        TOPIC,
        SECTION,
        PARAGRAPH,
        OTHER
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public List<DocumentElement> getChildren() {
        return children;
    }

    public void addChildren(DocumentElement documentElement) {
        children.add(documentElement);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public ElementTypes getElementType() {
        return elementType;
    }

    public void setElementType(ElementTypes elementType) {
        this.elementType = elementType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
