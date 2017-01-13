package com.parabole.feed.contentparser.contextparser.parsers;

import com.parabole.feed.contentparser.contextparser.helpers.SentenceDetect;
import com.parabole.feed.contentparser.contextparser.models.Triplet;
import com.parabole.feed.contentparser.contextparser.providers.ConceptProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajdeep on 03-Jan-17.
 */
public class ParagraphParser {
    ConceptProvider m_ConceptProvider;

    public ParagraphParser(ConceptProvider conceptProvider) {
        m_ConceptProvider = conceptProvider;
    }

    private List<Triplet> generateTriplets(String[] sentences) throws IOException {
        List<Triplet> triplets = new ArrayList<>();
        TripletParser tripletParser = new TripletParser(m_ConceptProvider);
        for(String aSentence:sentences){
            triplets.addAll(tripletParser.getAllTriplets(aSentence));
        }
        return triplets;
    }

    public List<Triplet> getAllTriplets(String paragraph) throws IOException {
        SentenceDetect sentenceDetect = new SentenceDetect(paragraph);
        return generateTriplets(sentenceDetect.getAllSentences());
    }
}
