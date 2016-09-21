package com.parabole.feed.application.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.services.CheckListServices;
import com.parabole.feed.application.services.LightHouseService;
import com.parabole.feed.application.services.StarfishServices;
import org.json.JSONObject;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sagir on 02-08-2016.
 */

public class ParagraphOperationsController extends BaseController{


    @Inject
    private CheckListServices checkListServices;

    @Inject
    private LightHouseService lightHouseService;

    @Inject
    private StarfishServices starfishServices;



    public Result addQuestion() throws AppException, IOException {

        String mappedQuestions = checkListServices.findAndAddQuestion();

        return ok(mappedQuestions);

    }


    public Result saveQuestion() throws AppException, IOException {

        String savedQuestions = checkListServices.saveQuestion();

        return ok(savedQuestions);

    }


    public Result saveParagraph() throws AppException, IOException {


        String paragraphId = "12345";
        String paragraphText = "This is a paragraph";
        String tag = "tag3";
        String savedParagraph = checkListServices.saveParagraph(paragraphId, paragraphText, tag);

        return ok(savedParagraph);

    }


    public Result savetagsToParagraphs() throws AppException, IOException {

        JSONObject tagsObject = new JSONObject();
        String savedParagraphTags = checkListServices.savetagsToParagraphs(tagsObject);

        return ok(savedParagraphTags);

    }


    public Result getQuestion() throws AppException, IOException {

        String getQuestions = checkListServices.getQuestion();

        return ok(getQuestions);

    }


    public Result getParagraph() throws Exception, IOException {

        String getParagraphs = checkListServices.getPararaphs();

        return ok(getParagraphs);

    }


    public Result getAllParagraphsByTag(String tagName) throws Exception, IOException {

        String getParagraphs = checkListServices.getAllParagraphsByTag(tagName);

        return ok(getParagraphs);

    }

    public Result getParagraphsByParagraphid() throws Exception, IOException {



        List<String> paragraphIds = new ArrayList<String>();
        paragraphIds.add("12342");
        paragraphIds.add("12345");
        paragraphIds.add("12341");

        String getParagraphs = checkListServices.getParagraphsByParagraphid(paragraphIds);

        return ok(getParagraphs);

    }


    public Result addAnswer() throws AppException, IOException {

        String mappedQuestions = checkListServices.findAndAddAnswer();

        return ok(mappedQuestions);

    }


    public Result questionAgainstParagraphId(String paragrphId) throws AppException, IOException {

        JSONObject mappedQuestions = checkListServices.questionAgainstParagraphId(paragrphId);

        return ok(mappedQuestions.toString());

    }


    public Result questionAgainstConceptNameComponentTypeComponentName(String conceptName, String componentType,  String componentName) throws AppException {

        JSONObject mappedQuestions = checkListServices.questionAgainstConceptNameComponentTypeComponentName(conceptName, componentType,  componentName);

        return ok(mappedQuestions.toString());

    }


    public Result createLightHouse() throws AppException {

        return ok(checkListServices.createLightHouse());

    }


    // -----------------------------------------------------------------------------
    //   Check List Operation Test
    // -----------------------------------------------------------------------------

    public Result saveOrUpdateCheckList() {

        HashMap<String, Object> toSave = new HashMap<>();
        toSave.put("DATA_ID","123456");
        toSave.put("BODY_TEXT","the test body text");
        toSave.put("IS_MANDATORY", true);
        toSave.put("STATE","okok");
        toSave.put("ATTACHMENTINFO", "{ok : ok}");
        toSave.put("CREATED_BY", "root");
        toSave.put("UPDATED_BY", "root");
        toSave.put("CREATED_AT", new Date());
        toSave.put("UPDATED_AT", new Date());


        HashMap<String, Boolean> paragraphIDs = null;
        HashMap<String, Boolean> componentTypeIDs = null;

        return ok(checkListServices.saveOrUpdateCheckList(toSave, paragraphIDs, componentTypeIDs));
    }


    public Result getCheckListById() {

        String checkListId = "1234567890";

        return ok(checkListServices.getCheckListById(checkListId));

    }

    public Result removeCheckList() {

        String checkListId = "89477f8c-2ddc-4c72-a587-5449158e4f6a";

        return ok(checkListServices.removeCheckList(checkListId));

    }

    public Result editChecklistCheck() {

        HashMap<String, Boolean> checklistCheckInfo = new HashMap<>();
        checklistCheckInfo.put("65df410d-ec99-4871-a97b-5ce60d2388d5", true);

        return ok(checkListServices.editChecklistCheck(checklistCheckInfo));

    }



    // -----------------------------------------------------------------------------
    //   testing creation of Topic, subtopic, section and their relationships
    // -----------------------------------------------------------------------------



    public Result createNewTopic() {

        return ok(lightHouseService.createNewTopic());
    }


    public Result createNewSubtopic() {

        return ok(lightHouseService.createNewSubtopic());
    }


    public Result createNewSection() {

        return ok(lightHouseService.createNewSection());
    }


    public Result createRelationBetweenTwoNodes() {

        return ok(lightHouseService.createRelationBetweenTwoNodes());
    }

    public Result getAlltopic() {

        return ok(lightHouseService.getAlltopic().toString());
    }

    public Result getAllComponents() {

        String vertexType = "COMPONENT";

        return ok(lightHouseService.getAllVertexesByType(vertexType).toString());
    }

    public Result getAllConcepts() {

        return ok(lightHouseService.getAllConcepts().toString());
    }

    public Result getSubtopicsByTopicId(String topicId) {

        return ok(lightHouseService.getSubtopicsByTopicId(topicId).toString());
    }

    public Result getParagraphBySectionId(String nodeId) {
        return ok(lightHouseService.getParagraphBySectionId(nodeId).toString());
    }

    public Result addAnewVertexproperty() {

        String vertexID = "320-10-35-34B";
        HashMap<String, String> mapOfProperties = new HashMap<>();
        mapOfProperties.put("testTag", "TestData");
        mapOfProperties.put("anotherTestTag", "Another TestData");

        return ok(lightHouseService.addAnewVertexproperty(vertexID, mapOfProperties));
    }

    public Result getVertexProperties() {
        String vertexID = "320-10-35-34B";
        ArrayList<String>  listOfPropertyNames = new ArrayList<String>();
        return ok(lightHouseService.getVertexProperties(vertexID, listOfPropertyNames));
    }

    public Result getComponentTypesByParagraphIds() {
        ArrayList<String> listOfParagraphIDs = new ArrayList<>();
        listOfParagraphIDs.add("326-10-65-1");
        return ok(lightHouseService.getComponentTypesByParagraphIds(listOfParagraphIDs).toString());
    }

    public Result getChecklistByID() {
        List<String> checklistIDs = new ArrayList<>();
        checklistIDs.add("07bdb9b6-858b-4022-8e0e-20361f39bd34");
        return ok(starfishServices.getChecklistByID(checklistIDs).toString());
        //return null;
    }

    public Result getChecklistsByParagraphs() {
        ArrayList<String> listOfParagraphIDs = new ArrayList<>();
        listOfParagraphIDs.add("310-10-50-10");
        return ok(lightHouseService.getChecklistsByParagraphs(listOfParagraphIDs).toString());
    }

    public Result getChecklistsByComponentTypes() {
        ArrayList<String> listOfParagraphIDs = new ArrayList<>();
        listOfParagraphIDs.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#Regulatory_Report");
        return ok(lightHouseService.getChecklistsByComponentTypes(listOfParagraphIDs).toString());
    }

    public Result getChecklistByComponent() {
        ArrayList<String> listOfComponetIds = new ArrayList<>();
        listOfComponetIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#AnnualFinancialStatement");
        return ok(lightHouseService.getChecklistByComponent(listOfComponetIds).toString());
    }

    public Result getChecklistByBusinessSegment() {
        ArrayList<String> listOfComponetIds = new ArrayList<>();
        listOfComponetIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#WholesaleFinance");
        listOfComponetIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#CommercialRealEstate");
        listOfComponetIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#EquipmentFinance");
        listOfComponetIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#CreditCard");
        listOfComponetIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#AutoLoan");
        return ok(lightHouseService.getChecklistByBusinessSegment(listOfComponetIds).toString());
    }

    public Result getChecklistDetails() {
        // TODO
        ArrayList<String> listOfCheckListIds = new ArrayList<>();
        listOfCheckListIds.add("9964fc0e-552a-4e3a-a33e-3ebd99636e33");
        return ok(checkListServices.getChecklistDetails(listOfCheckListIds).toString());
    }

    public Result getChecklistByConcept() {
        return ok(lightHouseService.getChecklistByConcept("http://mindparabole.com/finance/fasb_concepts#ValuationAllowance").toString());
    }


}
