package com.parabole.feed.application.services;


import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.application.utils.AppUtils;
import com.parabole.feed.contentparser.TaggerTest;
import com.parabole.feed.platform.graphdb.Anchor;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static play.mvc.Controller.response;

public class TaggingUtilitiesServices {

    @Inject
    private Anchor anchor;

    @Inject
    private JenaTdbService jenaTdbService;

    @Inject
    private TaggerTest taggerTest;

    public String startContentParser(String file) throws IOException {
        String result= null;
        try {
            //result = taggerTest.startExtraction("C:\\one\\sandbox\\parabole\\parabole-enterprise-scaffolding\\modules\\parabole-module-feed\\conf\\feedFiles\\FASBAccntStandards.pdf");
            result = taggerTest.startExtraction(AppUtils.getApplicationProperty(CCAppConstants.PARAGRAPH + ".filepathToExtract"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeFile(AppUtils.getApplicationProperty(CCAppConstants.PARAGRAPH + ".fileToSaveTheExtraction"), result);

        return result;
    }

    public String getParagraphsAgainstConceptNames(String conceptName){
        String result= null;
        return result;
    }


    private static ArrayList<String> NOUNS = new ArrayList<>();
    private MaxentTagger tagger;

    static {
        NOUNS.add("NN");
        NOUNS.add("NNP");
        NOUNS.add("NNS");
    }

    public TaggingUtilitiesServices(){
       // final URL url = Resources.getResource("files/english-left3words-distsim.tagger");
       // tagger = new MaxentTagger(String.valueOf(Resources.getResource("files/english-left3words-distsim.tagger")));
    }

    public String TagAllConcepts(){
        //Run the SparQL
        return null;
    }


    private List<String> getTheConceptNouns(String text){
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

    public Integer saveListOfSentenceLocationsAgainstAllConcepts() throws com.parabole.feed.platform.exceptions.AppException {

        return anchor.saveConfiguration("admin", "taggedData",
                "ListOfSentenceLocationsAgainstAllWords");

    }

/*
    private Integer saveListOfTaggedWordsAgainstAllURIs() throws com.parabole.feed.platform.exceptions.AppException {
        IndexedConceptsData taggedIndex = new IndexedConceptsData();
        ArrayList<IndexedData> listOfTaggedIndex = new ArrayList<IndexedData>();
        String nameOfTheKeyURI = new String();
        ArrayList<String> listedItems = new ArrayList<String>();
        taggedIndex.setItems(nameOfTheKeyURI, listedItems);
        listOfTaggedIndex.add(taggedIndex);

        return coralConfigurationService.saveConfiguration("admin", "taggedData",
                "ListOfTaggedWordsAgainstAllURIs", listOfTaggedIndex.toString());
    }

    private List<Map<String, String>> getListOfTaggedWordsAgainstAllURIs() throws com.parabole.feed.platform.exceptions.AppException {
        return coralConfigurationService.getConfigurationByName("ListOfTaggedWordsAgainstAllURIs");
    }

    private Integer saveListOfSentenceLocationsAgainstAllWords() throws com.parabole.feed.platform.exceptions.AppException {
        IndexedParagraphsSentencesData indexedParagraphsSentencesData = new IndexedParagraphsSentencesData();
        ArrayList<IndexedData> listOfTaggedIndex = new ArrayList<IndexedData>();
        String wordAsKey = new String();
        // add wordname here
        ArrayList<HashMap<String, ArrayList<Integer>>> listedURIAgainstLocations = new ArrayList<HashMap<String, ArrayList<Integer>>>();
        // Add locations for that word here
        indexedParagraphsSentencesData.setItems(wordAsKey, listedURIAgainstLocations);
        listOfTaggedIndex.add(indexedParagraphsSentencesData);

        return coralConfigurationService.saveConfiguration("admin", "taggedData",
                "ListOfSentenceLocationsAgainstAllWords", listOfTaggedIndex.toString());

    }

    private List<Map<String, String>> getListOfSentenceLocationsAgainstAllWords() throws com.parabole.feed.platform.exceptions.AppException {
        return coralConfigurationService.getConfigurationByName("ListOfSentenceLocationsAgainstAllWords");
    }*/

    public String getConfigurationDetailWithnodeinfo() throws AppException {

        String jsonFileContent = getTheAssignments();
        final JSONObject assignment = new JSONObject(jsonFileContent);
        //jenaTdbService.getRawBindingDataValues(jsonFileContent);

        HashMap<String, String> indexedParagraph = new HashMap<>();
        JSONArray assignmentsArray = assignment.getJSONObject("results").getJSONArray("bindings");

        for (int i=0; i<assignmentsArray.length(); i++) {
            JSONObject eachBinding = assignmentsArray.getJSONObject(i);
            String uriForParagraph = eachBinding.getJSONObject("para").getString("value");
            String paragraph = eachBinding.getJSONObject("definition").getString("value");
            indexedParagraph.put(uriForParagraph, paragraph);
        }

        return indexedParagraph.toString();
    }

    private String writeFile(String canonicalFilename, String text)throws IOException {
        File file = new File (canonicalFilename);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(text);
        out.close();
        return "Saved";
    }

    private String getTheAssignments() throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/assignment_feed.json");
        response().setContentType("application/json");
        return jsonFileContent;

    }


    public String getParagraphsByContent(String concept) throws AppException {

        String jsonFileContent = AppUtils.getFileContent("feedJson/paragraphs.json");
        JSONObject jsonObject = new JSONObject(jsonFileContent);

        JSONArray jsonArray =  jsonObject.getJSONObject("conceptIndex").getJSONArray(concept);
        JSONArray jsonArrayOfParagraphs = new JSONArray();

        for (int i=0; i<jsonArray.length(); i ++){
            jsonArrayOfParagraphs.put(jsonObject.getJSONObject("paragraphs").getJSONObject(jsonArray.getString(i)));
        }

        return jsonArrayOfParagraphs.toString();
    }
}
