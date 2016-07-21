package com.parabole.feed.platform.customs;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sagir on 21-07-2016.
 */
public class IndexedParagraphsSentencesData implements IndexedData{


    private String word;
    private ArrayList<HashMap<String, ArrayList<Integer>>> listedURIAgainstLocations;


    public IndexedParagraphsSentencesData() {

    }

    public void setItems(String word, ArrayList<HashMap<String, ArrayList<Integer>>> listedURIAgainstLocations){

        this.word = word;
        this.listedURIAgainstLocations = listedURIAgainstLocations;

    }

    public IndexedParagraphsSentencesData(String word, ArrayList<HashMap<String, ArrayList<Integer>>> listedURIAgainstLocations) {
        this.word = word;
        this.listedURIAgainstLocations = listedURIAgainstLocations;
    }


    public ArrayList<HashMap<String, ArrayList<Integer>>> getListedURIAgainstLocations() {
        return listedURIAgainstLocations;
    }

    public void setListedURIAgainstLocations(ArrayList<HashMap<String, ArrayList<Integer>>> listedURIAgainstLocations) {
        this.listedURIAgainstLocations = listedURIAgainstLocations;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
