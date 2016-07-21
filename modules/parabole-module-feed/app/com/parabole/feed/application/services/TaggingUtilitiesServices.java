package com.parabole.feed.application.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.application.utils.AppUtils;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

import java.util.ArrayList;

import static play.mvc.Controller.response;

//import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TaggingUtilitiesServices {

    @Inject
    private CoralConfigurationService coralConfigurationService;

    @Inject
    private JenaTdbService jenaTdbService;


    private static ArrayList<String> NOUNS = new ArrayList<>();
   // private MaxentTagger tagger;

    static {
        NOUNS.add("NN");
        NOUNS.add("NNP");
        NOUNS.add("NNS");
    }

    public TaggingUtilitiesServices(){
   //     tagger = new MaxentTagger(MaxentTagger.DEFAULT_DISTRIBUTION_PATH);
    }

    public String TagAllConcepts(){
        //Run the SparQL
        return null;
    }
/*

    private List<String> getTheConceptNouns( String text){
        List<String> nounList = new ArrayList<>();
        List<List<HasWord>> sentences =
                MaxentTagger.tokenizeText(new StringReader(text));
        List<TaggedWord> taggedSentence = tagger.tagSentence(sentences.get(0));
        return taggedSentence
                .stream()
                .filter(word -> NOUNS.stream().anyMatch(b -> b.equalsIgnoreCase(word.tag())))
                .map(a -> a.value())
                .collect(Collectors.toList());
    }

    private HashMap<String,List<Integer>> tagTheParagraphNouns( String text , List<String> restrictions){
        HashMap<String,List<Integer>> taggedSentences = new HashMap<>();
        List<List<HasWord>> sentences;
        sentences = MaxentTagger.tokenizeText(new StringReader(text));
        AtomicInteger counter = new AtomicInteger(1);
        sentences.stream().forEach( sentence -> {
            List<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
            taggedSentence.stream()
                    .filter(word -> NOUNS.stream().anyMatch( b -> b.equalsIgnoreCase(word.tag())))
                    .forEach(word -> {
                        String val = word.value();
                        List<Integer> lineNumberList = null;
                        if(!taggedSentences.containsKey(val)) {
                            lineNumberList = new ArrayList<Integer>();
                            taggedSentences.put(val,lineNumberList);
                        }
                        else
                            lineNumberList = taggedSentences.get(val);
                        lineNumberList.add(counter.get());
                    });
            counter.incrementAndGet();
        });
        return taggedSentences;
    }
*/


    public String getConfigurationDetailWithnodeinfo(final Integer ConfigarationId) throws AppException {

        String jsonFileContent = getTheassignments();
        final JSONObject assignment = new JSONObject(jsonFileContent);
        jenaTdbService.getRawBindingDataValues(jsonFileContent);
        // TODO
        return null;
    }

    public String getTheassignments() throws AppException {

        final String jsonFileContent = AppUtils.getFileContent("json/assignment.json");
        response().setContentType("application/json");
        return jsonFileContent;

    }


    public Integer saveData(final String userId, final JsonNode json, final CCAppConstants.ConfigurationType configurationType) throws AppException, com.parabole.feed.platform.exceptions.AppException {
        Validate.notNull(json, "'json' cannot be null!");
        final String configurationName = json.findPath("name").textValue();
        final String configurationDetails = json.findPath("details").textValue();
        Validate.notBlank(configurationName, "'configurationName' cannot be empty!");
        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        return coralConfigurationService.saveConfiguration(userId, configurationType.toString(), configurationName, configurationDetails);
    }


}
