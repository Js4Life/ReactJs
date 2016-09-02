package com.parabole.feed.application.services;


import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.application.utils.AppUtils;
import com.parabole.feed.contentparser.TaggerTest;
import com.parabole.feed.platform.graphdb.Anchor;
import com.parabole.feed.platform.graphdb.LightHouse;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Environment;
import play.Play;

import java.io.*;
import java.util.*;
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

    @Inject
    private Environment environment;

    @Inject
    LightHouse lightHouse;

    public String startContentParser(String file) throws IOException {
        String result= null;
        try {
            result = taggerTest.startExtraction(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedFiles\\" + file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeFile(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedJson\\paragraphs.json", result);

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

    public byte[] downloadFileByName(final String fileName) throws IOException {
        final java.io.InputStream strm = Play.application().classloader().getResourceAsStream(CCAppConstants.FEED_JSON_PATH + "/" + fileName);
        final byte[] data = IOUtils.toByteArray(strm);
        return data;
    }


    public String getParagraphsByContent(String concept) throws AppException {

        String sampleIncomingQuestion = AppUtils.getFileContent("feedJson\\taggedParagraphsType1.json");
        JSONObject fullJson = new JSONObject(sampleIncomingQuestion);
        
        String jsonFileContent = AppUtils.getFileContent("feedJson/paragraphs.json");
        JSONObject jsonObject = new JSONObject(jsonFileContent);
        JSONObject finalContentObject = new JSONObject();

        JSONArray jsonArray =  jsonObject.getJSONObject("conceptIndex").getJSONArray(concept);
        JSONArray jsonArrayOfParagraphs = new JSONArray();

        for (int i=0; i<jsonArray.length(); i ++){

            if(fullJson.get(jsonArray.getString(i)) != null)
                finalContentObject.put(jsonArray.getString(i), fullJson.get(jsonArray.getString(i)));

            finalContentObject.put("paragraphText", jsonObject.getJSONObject("paragraphs").getJSONObject(jsonArray.getString(i)));
            jsonArrayOfParagraphs.put(finalContentObject);

        }

        return jsonArrayOfParagraphs.toString();
    }

    public  JSONArray getParagraphIdsByConcept(String concept) throws AppException{
        String jsonFileContent = AppUtils.getFileContent("feedJson/paragraphs.json");
        JSONObject jsonObject = new JSONObject(jsonFileContent);
        JSONArray jsonArray =  jsonObject.getJSONObject("conceptIndex").getJSONArray(concept);
        return jsonArray;
    }

    public String getNodesFromParagraphJSon() throws Exception {

        String jsonFileContent = AppUtils.getFileContent("feedJson/paragraphs.json");
        JSONObject jsonObject = new JSONObject(jsonFileContent);
        JSONObject finalObj = new JSONObject();
        JSONObject paragraphJSON = jsonObject.getJSONObject("paragraphs");
        //JSONObject topicToSubTopic = new JSONObject();
        List<String> arrayOfTopics = new ArrayList<>();
        Iterator<?> keys = paragraphJSON.keys();

        HashMap<String, String> topicToSubTopic = new HashMap<String, String>();

        while( keys.hasNext() ) {
            String key = (String)keys.next();

            if ( paragraphJSON.get(key) instanceof JSONObject ) {
                List<String> elephantList = Arrays.asList(key.split("-"));
                arrayOfTopics.add( elephantList.get(0));
                topicToSubTopic.put(elephantList.get(0), elephantList.get(1));
                //     System.out.println("elephantList = " + elephantList.get(0));

            }
        }

        finalObj.put("topicToSubTopic", topicToSubTopic);
        lightHouse.saveListOfVertices(arrayOfTopics);
        return finalObj.toString();
    }

    public String getTopicNodesFromParagraphJSon() throws Exception {

        String jsonFileContent = AppUtils.getFileContent("feedJson/paragraphs.json");
        JSONObject jsonObject = new JSONObject(jsonFileContent);
        JSONObject finalObj = new JSONObject();
        JSONObject paragraphJSON = jsonObject.getJSONObject("paragraphs");
        //JSONObject topicToSubTopic = new JSONObject();
        List<String> arrayOfTopics = new ArrayList<>();
        Iterator<?> keys = paragraphJSON.keys();

        HashMap<String, String> topicToSubTopic = new HashMap<String, String>();

        while( keys.hasNext() ) {
            String key = (String)keys.next();

            if ( paragraphJSON.get(key) instanceof JSONObject ) {
                List<String> elephantList = Arrays.asList(key.split("-"));
                arrayOfTopics.add( elephantList.get(0));
                topicToSubTopic.put(elephantList.get(0), elephantList.get(1));
                //     System.out.println("elephantList = " + elephantList.get(0));

            }
        }

        finalObj.put("topicToSubTopic", topicToSubTopic);
        lightHouse.saveListOfVertices(arrayOfTopics);
        return arrayOfTopics.toString();
    }
}
