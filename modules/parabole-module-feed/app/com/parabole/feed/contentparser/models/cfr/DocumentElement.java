package com.parabole.feed.contentparser.models.cfr;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by parabole on 12/8/2016.
 */
public class DocumentElement {
    private List<DocumentElement> children;
    private String id;
    private String elementType;
    private String name;
    private int level;
    private String levelId;
    private String content;
    private int index;

    public DocumentElement() {
        this.id = UUID.randomUUID().toString();
        children = new ArrayList<>();
    }

    public enum ElementTypes{
        PARAGRAPH
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

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
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

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
