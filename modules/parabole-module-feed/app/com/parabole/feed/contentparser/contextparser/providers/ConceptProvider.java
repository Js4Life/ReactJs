package com.parabole.feed.contentparser.contextparser.providers;

import com.parabole.feed.contentparser.contextparser.models.Concept;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Rajdeep on 06-Jan-17.
 */

public class ConceptProvider {

    private List<Concept> conceptList  = new ArrayList<>();

    public ConceptProvider(String fileName){
        try {
            loadAllConcepts(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ConceptProvider(List<String> concepts){
        try{
            loadAllConceptsFromList(concepts);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Concept> getAllConcepts(){
        return conceptList;
    }

    private void loadAllConcepts(String fileName) throws FileNotFoundException {
        //Load The Concepts and populate conceptList
        Scanner s = new Scanner(new FileReader(fileName));
        Iterator<String> conceptIter = s.useDelimiter("\n");
        conceptIter.forEachRemaining(a -> {
            Concept aConcept = new Concept(a);
            conceptList.add(aConcept);
        });
    }

    private void loadAllConceptsFromList(List<String> concepts) {
        Iterator<String> conceptIter = concepts.iterator();
        conceptIter.forEachRemaining(a -> {
            Concept aConcept = new Concept(a);
            conceptList.add(aConcept);
        });
    }
}
