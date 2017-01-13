package com.parabole.feed.contentparser.contextparser.models;

/**
 * Created by Rajdeep on 03-Jan-17.
 */
public class Concept implements Comparable<Concept> {
    private int rank;
    private String conceptText;
    private int size;

    public Concept(String value) {
        this.rank = 0;
        this.conceptText = value;
        this.size = conceptText.split(" ").length + 1;
    }

    public Concept(int rank, String value) {
        this.rank = rank;
        this.conceptText = value;
        this.size = conceptText.split(" ").length;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getConceptText() {
        return conceptText;
    }

    public void setConceptText(String conceptText) {
        this.conceptText = conceptText;
    }

    @Override
    public int compareTo(Concept compareContent) {
        int compareRank= compareContent.getRank();
        return this.rank-compareRank;
    }
}
