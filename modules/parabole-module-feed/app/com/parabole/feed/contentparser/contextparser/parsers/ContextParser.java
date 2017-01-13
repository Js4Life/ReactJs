package com.parabole.feed.contentparser.contextparser.parsers;


import com.parabole.feed.contentparser.contextparser.helpers.LemmaDetect;
import com.parabole.feed.contentparser.contextparser.helpers.PosDetect;
import com.parabole.feed.contentparser.contextparser.models.Context;
import com.parabole.feed.contentparser.contextparser.models.Triplet;
import com.parabole.feed.contentparser.contextparser.models.VerbForm;
import com.parabole.feed.contentparser.contextparser.providers.MockContextProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajdeep on 06-Jan-17.
 */
public class ContextParser {
    List<Context> contextList = new ArrayList<>();
    List<String> contextTags = new ArrayList<>();

    public ContextParser(MockContextProvider mockContextProvider) {
        contextList = mockContextProvider.getContextList();
    }

    public ContextParser(List<Context> contexts) {
        contextList = contexts;
    }

    public List<String> getParagraphContexts(List<Triplet> triplet){
        for (Triplet aTriplet : triplet){
            for (Context aContext : contextList){
                if (aTriplet.getSubject().equalsIgnoreCase(aContext.getSubject()) &&
                        aTriplet.getObject().equalsIgnoreCase(aContext.getObject())){

                    contextTagger(aTriplet.getPredicate(),aContext.getPredicate(),aContext.getContextTag());
                }
            }
        }
        return contextTags;
    }

    private void contextTagger(String paraPredicate, String contextPredicate, String contextTag){

        LemmaDetect lemmaDetect = new LemmaDetect();
        PosDetect posDetect = new PosDetect();

        List<VerbForm> paraVerbs = posDetect.getVerbForms(paraPredicate);
        List<VerbForm> contextVerbs = posDetect.getVerbForms(contextPredicate);

        for (VerbForm aParaVerb : paraVerbs){
            for (VerbForm aContextVerb : contextVerbs){
                if ( lemmaDetect.lemmatize(aParaVerb.getVerb(), aParaVerb.getTag()).equalsIgnoreCase
                        (lemmaDetect.lemmatize(aContextVerb.getVerb(), aContextVerb.getTag())) ){

                    if (!contextTags.contains(contextTag)){
                        contextTags.add(contextTag);
                    }

                }
            }
        }
    }
}
