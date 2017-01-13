package com.parabole.feed.contentparser.contextparser.parsers;

import com.parabole.feed.contentparser.contextparser.helpers.PosDetect;
import com.parabole.feed.contentparser.contextparser.helpers.TokenDetect;
import com.parabole.feed.contentparser.contextparser.models.Concept;
import com.parabole.feed.contentparser.contextparser.models.Triplet;
import com.parabole.feed.contentparser.contextparser.providers.ConceptProvider;

import java.io.IOException;
import java.util.*;

/**
 * Created by Rajdeep on 03-Jan-17.
 */
public class TripletParser {

    private List<String> sentenceTokens;
    private List<Concept> occurredConceptList = new ArrayList<>();
    ConceptProvider m_ConceptProvider;

    private List<Triplet> triplets = new ArrayList<>();

    public TripletParser(ConceptProvider conceptProvider) throws IOException {
        m_ConceptProvider = conceptProvider;
    }

    private void sentenceTokenizer(String sentence) throws IOException {
        String [] tokens = new TokenDetect(sentence).getAllTokens();
        sentenceTokens = Arrays.asList(tokens);

        getConcepts();
    }

    private void getConcepts() throws IOException {
        List<Concept> conceptList = m_ConceptProvider.getAllConcepts();
        for (Concept aConcept : conceptList){
            occurredConcepts(aConcept.getConceptText());
        }
        sortConcepts();
    }

    private void occurredConcepts(String phrase) throws IOException {
        List<String> conceptToken;
        String [] tokens = new TokenDetect(phrase).getAllTokens();
        conceptToken = Arrays.asList(tokens);
        int c;
        String occurredConcept="";
        boolean flag=false;
        for (int i=0; i < (sentenceTokens.size() - conceptToken.size() ); i++){
            c=i;
            if ( sentenceTokens.get(i).equalsIgnoreCase(conceptToken.get(0)) ){

                for (String temp:conceptToken){
                    occurredConcept+=sentenceTokens.get(c) + " ";
                    flag = sentenceTokens.get(c).equalsIgnoreCase(temp);
                    c++;
                }
            }
            if (flag){
                occurredConceptList.add(new Concept(i, occurredConcept.trim()));
                flag=false;
            }
            occurredConcept="";
        }
    }

    private void sortConcepts() {
        Collections.sort(occurredConceptList);
        conceptIterator();
    }

    private void conceptIterator() {
        Concept subject;
        Concept object;
        for (int i=0;i< occurredConceptList.size()-1;i++){
            subject = occurredConceptList.get(i);
            object = occurredConceptList.get(i+1);
            tripletGenerator(subject,object);
        }
    }

    private void tripletGenerator(Concept subject, Concept object){
        PosDetect posDetect = new PosDetect();
        int startIndex = subject.getRank()+subject.getSize();
        int endIndex = object.getRank();
        String predicate = predicateParser(startIndex,endIndex);
        if (!Objects.equals(predicate, "") && posDetect.getVerbForms(predicate).size()!=0) {

            triplets.add(new Triplet(subject.getConceptText(), predicate, object.getConceptText()));
        }
    }

    private String predicateParser(int start,int end){
        String predicate="";
        for (int i = start;i<end;i++){
            predicate=(predicate + sentenceTokens.get(i)+" ");
        }
        return predicate.trim();
    }

    public List<Triplet> getAllTriplets(String sentence) throws IOException {
        occurredConceptList.clear();
        triplets.clear();
        sentenceTokenizer(sentence);
        return  triplets;
    }
}
