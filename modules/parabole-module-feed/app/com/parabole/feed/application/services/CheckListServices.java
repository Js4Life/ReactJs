package com.parabole.feed.application.services;

import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.utils.AppUtils;
import com.parabole.feed.platform.graphdb.LightHouse;
import com.parabole.feed.platform.graphdb.StarFish;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Environment;

import java.io.IOException;
import java.util.*;

import static play.mvc.Http.Context.Implicit.session;

/**
 * Created by Sagir on 02-08-2016.
 */
public class CheckListServices {

    @Inject
    private Environment environment;

    @Inject
    private StarFish starFish;

    @Inject
    private LightHouse lightHouse;

    @Inject
    private StarfishServices starfishServices;


    private String getUniqueID() {
        UUID uniqueKey = UUID.randomUUID();
        return uniqueKey.toString();
    }

    public String addQuestion(JSONObject incomingQuestion) throws AppException, IOException {

        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject jsonObject = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
        JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");

        System.out.println("indexes.toString() = " + indexes.toString());

        // looping all the questions ----------------->
        JSONArray allQuestions = incomingQuestion.getJSONArray("questions");
        for (int i = 0; i < allQuestions.length(); i++) {
            JSONObject singleQuestionJsonObject = allQuestions.getJSONObject(i);

            String QuestionId = getUniqueID();
            JSONArray paragraphAgainstId = new JSONArray();

            fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions").put(QuestionId, singleQuestionJsonObject.getString("text"));
            JSONArray allParagraphs = incomingQuestion.getJSONArray("paragraphs");
            for (int iteration = 0; iteration < allParagraphs.length(); iteration++) {
                if (indexes.getJSONObject("paragraphs").has(allParagraphs.getString(i)))
                    paragraphAgainstId = indexes.getJSONObject("paragraphs").getJSONArray(allParagraphs.getString(i));
                //if paragraph id is present  ----------->
                if (paragraphAgainstId != null && paragraphAgainstId.length() > 0) {
                    paragraphAgainstId.put(QuestionId);
                } else {
                    JSONArray listOfPid = new JSONArray();
                    listOfPid.put(QuestionId);
                    indexes.getJSONObject("paragraphs").put(allParagraphs.getString(i), listOfPid);
                }
            }


            // adding other components------------------>
            //// loop against all the components----->
            for (int j = 0; j < singleQuestionJsonObject.getJSONArray("components").length(); j++) {
                JSONObject component = singleQuestionJsonObject.getJSONArray("components").getJSONObject(j);
                JSONArray containArrayOfcomponentName = new JSONArray();

                Boolean continueationOfThisFlow = true;

                if(indexes.has(incomingQuestion.getString("conceptName")))
                    if (indexes.getJSONObject(incomingQuestion.getString("conceptName")).has(component.getString("type")))
                    {
                        if (indexes.getJSONObject(incomingQuestion.getString("conceptName")).getJSONObject(component.getString("type")).has(component.getString("name"))) {
                            containArrayOfcomponentName = indexes.getJSONObject(incomingQuestion.getString("conceptName")).getJSONObject(component.getString("type")).getJSONArray(component.getString("name"));
                        }else{
                            JSONArray componentName = new JSONArray();
                            componentName.put(QuestionId);
                            indexes.getJSONObject(incomingQuestion.getString("conceptName")).getJSONObject(component.getString("type")).put(component.getString("name"), componentName);
                            continueationOfThisFlow = false;
                        }
                    }else{
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(QuestionId);
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put(component.getString("name"), jsonArray);
                        indexes.getJSONObject(incomingQuestion.getString("conceptName")).put(component.getString("type"), jsonObject1);
                        continueationOfThisFlow = false;
                    }

                // handling component name list
                if(continueationOfThisFlow)
                    if(containArrayOfcomponentName != null && containArrayOfcomponentName.length() > 0){
                        System.out.println("paragraphAgainstId = null " + containArrayOfcomponentName);
                        containArrayOfcomponentName.put(QuestionId);
                    }
                    else{
                        System.out.println("paragraphAgainstId = " + containArrayOfcomponentName);
                        JSONArray listOfConceptName = new JSONArray();
                        listOfConceptName.put(QuestionId);
                        JSONObject componentType = new JSONObject();
                        componentType.put(component.getString("name"), listOfConceptName);
                        JSONObject newConcept = new JSONObject();
                        newConcept.put(component.getString("type"), componentType);
                        indexes.put(incomingQuestion.getString("conceptName"), newConcept);
                    }
            }

        }

        // Saving ------------------------------------>
        AppUtils.writeFile(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedJson\\mappedQuestions.json", jsonObject.toString());
        return jsonObject.toString();

    }

    public String findAndAddQuestion() throws AppException, IOException {

        String sampleIncomingQuestion = AppUtils.getFileContent("feedJson\\sampleIncomingQuestion.json");

        JSONObject jsonObject = new JSONObject(sampleIncomingQuestion);

        return addQuestion(jsonObject);
    }

    public String saveQuestion() throws AppException, IOException {

        String sampleIncomingQuestion = null;
        try {
            sampleIncomingQuestion = starFish.saveQuestion();
        } catch (com.parabole.feed.platform.exceptions.AppException e) {
            e.printStackTrace();
        }

        return sampleIncomingQuestion;
    }

    public String saveParagraph(String paragraphId, String paragraphText, String tag) throws AppException, IOException {

        String sampleIncomingParagraph = null;
        try {
            sampleIncomingParagraph = starFish.saveParagraph(paragraphId, paragraphText, tag);
        } catch (com.parabole.feed.platform.exceptions.AppException e) {
            e.printStackTrace();
        }

        return sampleIncomingParagraph;
    }

    public String savetagsToParagraphs(JSONObject tagsObject) throws AppException, IOException {

        String sampleIncomingQuestion = AppUtils.getFileContent("feedJson\\taggedParagraphsType1.json");
        JSONObject fullJson = new JSONObject(sampleIncomingQuestion);

        Iterator<?> keys = tagsObject.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if ( tagsObject.get(key) instanceof JSONObject ) {
                if(!fullJson.has(key))
                    fullJson.put(key, tagsObject.get(key));
            }
        }

        // Saving ------------------------------------>
        AppUtils.writeFile(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedJson\\taggedParagraphsType1.json", fullJson.toString());
        return fullJson.toString();

    }

    public String getQuestion() throws AppException, IOException {

        Map<String, String> sampleIncomingQuestion = null;
/*        try {
          //  sampleIncomingQuestion = starFish.getAllQuestions();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return sampleIncomingQuestion.toString();
    }

    public String getPararaphs() throws AppException, IOException, com.parabole.feed.platform.exceptions.AppException {

        List<Map<String, String>> paragraphs = starFish.getAllParagraphs();
        return paragraphs.toString();
    }

    public String getAllParagraphsByTag(String tagInput) throws AppException, IOException, com.parabole.feed.platform.exceptions.AppException {

        List<Map<String, String>> paragraphs = starFish.getAllParagraphsByTag(tagInput);
        return paragraphs.toString();
    }

    public String getParagraphsByParagraphid(List<String> paragraphIds) throws AppException, IOException, com.parabole.feed.platform.exceptions.AppException {


        JSONObject toreturn = new JSONObject();
        for(String paragraphId  : paragraphIds){
            Map<String, String> paragraphs = starFish.getParagraphTagByParagraphid(paragraphId);
            toreturn.put(paragraphId, paragraphs.get("tag"));
        }

        return toreturn.toString();
    }

    public String findAndAddAnswer() throws AppException, IOException {

        String sampleIncomingAnswer = AppUtils.getFileContent("feedJson\\answersToAdd.json");

        JSONObject jsonObject = new JSONObject(sampleIncomingAnswer);
        JSONArray answersToAddArray = jsonObject.getJSONArray("answers");
        return addAnswer(answersToAddArray);
    }

    public String addAnswer(JSONArray answersToAddArray) throws AppException, IOException {
        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject fullJson = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = fullJson.getJSONObject("FASBAccntStandards");
        JSONObject alreadyAddedAnswers = new JSONObject();

        if(fileMappedQuestionsfromFASBAccntStandards.has("answers")) {
            alreadyAddedAnswers = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("answers");
        }
        else{
            JSONObject answer = new JSONObject();
            fileMappedQuestionsfromFASBAccntStandards.put("answers", answer);
            alreadyAddedAnswers = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("answers");
        }

        // looping all the answers ----------------->
        if(answersToAddArray != null & answersToAddArray.length() > 0 )
            for (int i = 0; i < answersToAddArray.length(); i++) {
                if(!alreadyAddedAnswers.has(answersToAddArray.getString(i)))
                    alreadyAddedAnswers.put(answersToAddArray.getString(i), true);
            }

        // Saving ------------------------------------>
        AppUtils.writeFile(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedJson\\mappedQuestions.json", fullJson.toString());
        return fullJson.toString();

    }

    public Boolean addAnswer(JSONObject answersToAdd) throws AppException, IOException {
        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject fullJson = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = fullJson.getJSONObject("FASBAccntStandards");
        JSONObject alreadyAddedAnswers = new JSONObject();
        Boolean status = false;
        try {
            if (fileMappedQuestionsfromFASBAccntStandards.has("answers")) {
                alreadyAddedAnswers = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("answers");
            } else {
                JSONObject answer = new JSONObject();
                fileMappedQuestionsfromFASBAccntStandards.put("answers", answer);
                alreadyAddedAnswers = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("answers");
            }

            // looping all the answers ----------------->
            if (answersToAdd != null & answersToAdd.length() > 0) {
                Iterator<String> keys = answersToAdd.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    alreadyAddedAnswers.put(key, answersToAdd.getBoolean(key));
                }
            }

            // Saving ------------------------------------>
            AppUtils.writeFile(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedJson\\mappedQuestions.json", fullJson.toString());
            status = true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    public JSONObject questionAgainstParagraphId(String paragraphId) throws AppException {

        JSONObject finalReturn = new JSONObject();

        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject jsonObject = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
        JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");
        JSONObject paragraphs = indexes.getJSONObject("paragraphs");
        JSONObject questions = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions");
        JSONObject allAnswers = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("answers");
        JSONObject answers = new JSONObject();

        JSONObject allQuestions = new JSONObject();
        JSONObject status = new JSONObject();
        JSONArray questionIds = new JSONArray();

            if(paragraphs.has(paragraphId)) {
                questionIds = paragraphs.getJSONArray(paragraphId);
                status.put("haveData", true);
                status.put("message", "It has total of "+questionIds.length()+" paragraphs");
            }else {
                status.put("haveData", false);
                status.put("message", "No Question Present on this flow !");
            }

            if (questionIds != null && questionIds.length() > 0) {
                for (int i = 0; i < questionIds.length(); i++) {
                    allQuestions.put(questionIds.getString(i), questions.getString(questionIds.getString(i)));
                    if(allAnswers.has(questionIds.getString(i)))
                        answers.put(questionIds.getString(i), allAnswers.getBoolean(questionIds.getString(i)));
                }
            }

        finalReturn.put("questions", allQuestions);
        finalReturn.put("status", status);
        finalReturn.put("answers", answers);
        return finalReturn;
    }

    public JSONObject questionAgainstConceptNameComponentTypeComponentName(String conceptName, String componentType,  String componentName) throws AppException {

        JSONObject finalReturn = new JSONObject();
        JSONObject status = new JSONObject();

        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject jsonObject = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
        JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");
        JSONObject questions = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions");
        JSONObject allAnswers = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("answers");
        JSONObject answers = new JSONObject();

        JSONObject qByConcept = new JSONObject();

        if(indexes.has(conceptName)) {
            qByConcept = indexes.getJSONObject(conceptName);
            status.put("haveConceptName", true);
            status.put("message", "conceptName : "+conceptName);
        }else {
            status.put("haveConceptName", false);
            status.put("message", "no such concept Name:&: input error");
        }

        JSONObject qByComponentByType = new JSONObject();
        JSONArray qByComponentByName = new JSONArray();

        if(qByConcept.has(componentType)) {
            qByComponentByType = qByConcept.getJSONObject(componentType);
            status.put("haveComponentType", true);
            status.put("message", "ComponentType : "+componentType);
        }else {
            status.put("haveComponentType", false);
            status.put("message", "input error");
        }

        if(qByComponentByType.has(componentName)) {
            qByComponentByName = qByComponentByType.getJSONArray(componentName);
            status.put("haveComponentName", true);
            status.put("message", "componentName : "+componentName);
        }else {
            status.put("haveComponentName", false);
            status.put("message", "Input Error");
        }

        JSONObject allQuestions = new JSONObject();

        for (int i = 0; i < qByComponentByName.length(); i++) {
            allQuestions.put(qByComponentByName.getString(i), questions.getString(qByComponentByName.getString(i)));
            if(allAnswers.has(qByComponentByName.getString(i)))
                answers.put(qByComponentByName.getString(i), allAnswers.getBoolean(qByComponentByName.getString(i)));
        }

        if(qByComponentByName.length() > 0){
            status.put("haveData", true);
        } else {
            status.put("haveData", false);
        }

        finalReturn.put("questions", allQuestions);
        finalReturn.put("status", status);
        finalReturn.put("answers", answers);
        return finalReturn;
    }

    public String createLightHouse(){
        try {
            lightHouse.createLightHouse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Saved";
    }

    public String saveOrUpdateCheckList(HashMap<String, Object> toSave, HashMap<String, Boolean> paragraphIDs, HashMap<String, Boolean> componentTypeIDs) {

        if(toSave.get("DATA_ID") == null || toSave.get("DATA_ID").toString().trim().isEmpty()) {
            toSave.put("DATA_ID", getUniqueID());
            toSave.put("CREATED_BY", session().get("USER_ID"));
            toSave.put("CREATED_AT", new Date());
        }
        toSave.put("UPDATED_BY", session().get("USER_ID"));
        toSave.put("UPDATED_AT", new Date());

        String result = null;
        try {
            Map<String, String> nodeData = new HashMap<>();
            nodeData.put("name", toSave.get("DATA_ID").toString());
            nodeData.put("type", "CHECKLIST");
            nodeData.put("elementID", toSave.get("DATA_ID").toString());
            lightHouse.createNewVertex(nodeData);
            starFish.saveOrUpdateCheckList(toSave);
            paragraphIDs.forEach((String k, Boolean v)->{
                if(v){
                    lightHouse.establishEdgeByVertexIDs(k, toSave.get("DATA_ID").toString(), "paragraphToChecklist", "paragraphToChecklist");
                } else {
                    lightHouse.deleteEdgeByVertexIDs(k, toSave.get("DATA_ID").toString());
                }
            });

            componentTypeIDs.forEach((String k, Boolean v)->{
                if(v){
                    lightHouse.establishEdgeByVertexIDs(k, toSave.get("DATA_ID").toString(), "componentTypeToChecklist", "componentTypeToChecklist");
                } else {
                    lightHouse.deleteEdgeByVertexIDs(k, toSave.get("DATA_ID").toString());
                }
            });
            result = toSave.get("DATA_ID").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public String saveOrUpdateCheckListAttachment(HashMap<String, Object> toSave) {
        String data_id = getUniqueID();
        toSave.put("data_id", data_id);
        toSave.put("created_by", session().get("USER_ID"));
        toSave.put("created_at", new Date());
        starFish.saveOrUpdateCheckListAttachment(toSave);
        return data_id;
    }


    public String editChecklistCheck(HashMap<String, Boolean> checklistCheckInfo) {

        for ( String s : checklistCheckInfo.keySet()) {
            HashMap<String, Object> toSave = new HashMap<>();
            toSave.put("DATA_ID",s);
            toSave.put("IS_CHECKED",checklistCheckInfo.get(s));
            toSave.put("UPDATED_BY", session().get("USER_ID"));
            toSave.put("UPDATED_AT", new Date());
            starFish.saveOrUpdateCheckList(toSave);
        }

        return "{status: Saved}";

    }

    public String removeCheckList(String checkListId){
        String result = null;
        try {
            lightHouse.deleteAVertexByID(checkListId);
            starFish.removeCheckList(checkListId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{status: success }";
    }

    public String removeCheckListAttachment(String checkListAttachmetId){
        String result = null;
        try {
            starFish.removeCheckListAttachment(checkListAttachmetId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{status: success }";
    }

    public String getCheckListById(String checkListId){
        String result = null;
        try {
            result = starFish.getCheckListById(checkListId);
        } catch (com.parabole.feed.platform.exceptions.AppException e) {
            e.printStackTrace();
        }
        return result;

    }


    public List<Map<String, String>> getCheckListAttachmentById(String CheckListAttachmentId){
        List<Map<String, String>> result = null;
        try {
            result = starFish.getCheckListAttachmentIdById(CheckListAttachmentId);
        } catch (com.parabole.feed.platform.exceptions.AppException e) {
            e.printStackTrace();
        }
        return result;

    }


    public List<Map<String, String>> getCheckListAttachmentsByChecklistID(String checkListId){
        List<Map<String, String>> result = null;
        try {
            result = starFish.getCheckListAttachmentsByChecklistID(checkListId);
        } catch (com.parabole.feed.platform.exceptions.AppException e) {
            e.printStackTrace();
        }
        return result;

    }

    public ArrayList<HashMap<String, String>> getChecklistDetailsForReport(ArrayList<String> listOfCheckListIds) {
        ArrayList<HashMap<String, String>> allChecklistData = starfishServices.getChecklistByID(listOfCheckListIds);
        for (HashMap<String, String> stringStringHashMap : allChecklistData) {
            String checklistID = stringStringHashMap.get("DATA_ID");
            stringStringHashMap.put("paragraphs", getAllParagraphsAgainstTheChecklistID(checklistID));
            //stringStringHashMap.put("componentTypes", getAllComponentTypesAgainstTheChecklistID(checklistID));
            stringStringHashMap.put("components", getAllComponentsAgainstTheChecklistID(checklistID));
            stringStringHashMap.remove("CREATED_AT");
            stringStringHashMap.remove("UPDATED_AT");
        }
        return allChecklistData;
    }

    public HashMap<String, String> getParagraphTypeCounts() {
        HashMap<String, String> allChecklistData = lightHouse.getParagraphCountGroupByTag();
        return allChecklistData;
    }



    public List<HashMap<String, String>> getRelatedNodesByNodeID(String nodeID, String filteredBY) {
        ArrayList<HashMap<String, String>> finalResult = new ArrayList<>();
        ArrayList<HashMap<String, String>> componentTypes = lightHouse.getRootVerticesByChildVertexId(nodeID);
        ArrayList<String> allElement = getAllElementIDs(componentTypes);
        for (String elementID : allElement) {
            ArrayList<HashMap<String, String>> components = lightHouse.getChildVerticesByRootVertexId(elementID);
            finalResult.addAll(components);
        }
        if(filteredBY != null)
            finalResult = filteringByfilteredBY(finalResult, filteredBY);

        return finalResult;
    }

    private ArrayList<HashMap<String, String>> filteringByfilteredBY(ArrayList<HashMap<String, String>> toFilters, String filteredBY) {

        ArrayList<HashMap<String, String>> resultData = new ArrayList<>();
        for (HashMap<String, String> toFilter : toFilters) {
            if(toFilter.get("type").equalsIgnoreCase(filteredBY) ){
                resultData.add(toFilter);
            }
        }
        return resultData;
    }

    private ArrayList<String> getAllElementIDs(ArrayList<HashMap<String, String>> componentTypes){
        ArrayList<String> resultData = new ArrayList<>();
        for (HashMap<String, String> componentType : componentTypes) {
            resultData.add(componentType.get("elementID"));
        }
        return resultData;
    }


    private String getAllParagraphsAgainstTheChecklistID(String checklistID) {
        String paragraphIDs = "";
        ArrayList<HashMap<String, String>> allRootNodeDetails = lightHouse.getRootVerticesByChildVertexId(checklistID);
        
        for (HashMap<String, String> allRootNodeDetail : allRootNodeDetails) {
            if(allRootNodeDetail.get("type").equals("PARAGRAPH")){
                if(paragraphIDs != "") {
                    paragraphIDs += ", " + (allRootNodeDetail.get("elementID"));
                }else{
                    paragraphIDs += (allRootNodeDetail.get("elementID"));
                }
            }
        }
        return paragraphIDs;
    }


    private String getAllComponentTypesAgainstTheChecklistID(String checklistID) {
        String componentTypeIDs = "";
        ArrayList<HashMap<String, String>> allRootNodeDetails = lightHouse.getRootVerticesByChildVertexId(checklistID);
        for (HashMap<String, String> allRootNodeDetail : allRootNodeDetails) {
            if(allRootNodeDetail.get("type").equals("COMPONENTTYPE")){
                if(componentTypeIDs != "") {
                    componentTypeIDs += ", " + (allRootNodeDetail.get("name"));
                }else{
                    componentTypeIDs += (allRootNodeDetail.get("name"));
                }


            }
        }
        return componentTypeIDs;
    }

    private String getAllComponentsAgainstTheChecklistID(String checklistID) {
        String componentTypeIDs = "";
        ArrayList<String> componentTypes = new ArrayList<>();
        ArrayList<HashMap<String, String>> allRootNodeDetails = lightHouse.getRootVerticesByChildVertexId(checklistID);
        for (HashMap<String, String> allRootNodeDetail : allRootNodeDetails) {
            if(allRootNodeDetail.get("type").equals("COMPONENTTYPE")){
                componentTypes.add(allRootNodeDetail.get("elementID"));
            }
        }

        for (String componentType : componentTypes) {
            ArrayList<HashMap<String, String>> allChildNodeDetails = lightHouse.getChildVerticesByRootVertexId(componentType);
            for (HashMap<String, String> allChildNodeDetail : allChildNodeDetails) {
                if(allChildNodeDetail.get("type").equals("COMPONENT")){
                    if(componentTypeIDs != "") {
                        componentTypeIDs += ", " + (allChildNodeDetail.get("name"));
                    }else{
                        componentTypeIDs += (allChildNodeDetail.get("name"));
                    }
                }
            }
        }


        return componentTypeIDs;
    }

    public HashMap<String, String> getCompliedAndNotCompliedCounts() {

        return starfishServices.getCompliedAndNotCompliedCounts();
    }
}
