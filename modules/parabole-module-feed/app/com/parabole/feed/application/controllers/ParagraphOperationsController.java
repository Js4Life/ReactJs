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


        HashMap<String, Boolean> paragraphIDs = new HashMap<>();
        paragraphIDs.put("320-10-35-34B", Boolean.FALSE);
        HashMap<String, Boolean> componentTypeIDs = new HashMap<>();
        componentTypeIDs.put("http://www.mindparabole.com/ontology/finance/Parabole-Model#FinconModel", Boolean.TRUE);

        return ok(checkListServices.saveOrUpdateCheckList(toSave, paragraphIDs, componentTypeIDs));
    }

    public Result saveOrUpdateCheckListAttachment() {

        HashMap<String, Object> toSave = new HashMap<>();
        toSave.put("checklistId","123456");
        toSave.put("data","okok");
        toSave.put("data_id", "12345");
        toSave.put("name", "file");
        toSave.put("mime", "PNG");

        return ok(checkListServices.saveOrUpdateCheckListAttachment(toSave));
    }


    public Result getCheckListById() {

        String checkListId = "1234567890";

        return ok(checkListServices.getCheckListById(checkListId));

    }

    public Result getCheckListAttachmentById() {

        String checkListId = "6503f06d-f51d-4104-a061-afae245e44a6";
        return ok(checkListServices.getCheckListAttachmentById(checkListId).toString());

    }

    public Result getCheckListAttachmentsByChecklistID() {

        String checkListId = "123456";
        return ok(checkListServices.getCheckListAttachmentsByChecklistID(checkListId).toString());
    }

    public Result removeCheckList() {

        String checkListId = "89477f8c-2ddc-4c72-a587-5449158e4f6a";

        return ok(checkListServices.removeCheckList(checkListId));

    }

    public Result removeCheckListAttachment() {

        String checkListAttachmetId = "89477f8c-2ddc-4c72-a587-5449158e4f6a";
        return ok(checkListServices.removeCheckListAttachment(checkListAttachmetId));
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

    public Result getAllBaselTopic() {

        return ok(lightHouseService.getAllBaselTopic().toString());
    }

    public Result getAllComponents() {

        String vertexType = "COMPONENT";

        return ok(lightHouseService.getAllVertexesByType(vertexType).toString());
    }

    public Result getAllConcepts() {

        return ok(lightHouseService.getAllConcepts().toString());
    }

    public Result getAllProducts() {

        return ok(lightHouseService.getAllProducts().toString());
    }

    public Result getSubtopicsByTopicId(String topicId) {

        return ok(lightHouseService.getSubtopicsByTopicId(topicId, "SECTION").toString());
    }

    public Result getBaselSubtopicsByTopicId(String topicId) {

        return ok(lightHouseService.getBaselSubtopicsByTopicId(topicId, "BASELSUBTOPIC").toString());
    }

    public Result getParagraphBySectionId(String nodeId) {
        return ok(lightHouseService.getParagraphBySectionId(nodeId).toString());
    }

    public Result getBaselParagraphBySubTopicId(String nodeId) {
        return ok(lightHouseService.getParagraphBySectionId(nodeId).toString());
    }

    public Result getRelatedParagraphsByNames() {

        ArrayList<String> listOfParagraphIDs = new ArrayList<>();
        listOfParagraphIDs.add("basel1-1-7-3-P6");
        return ok(lightHouseService.getRelatedParagraphsByNames(listOfParagraphIDs).toString());
    }

    public Result getAllDocFileNamesByType() throws IOException {
        String fType = "FILE";
        return ok(lightHouseService.getAllDocFileNamesByType(fType).toString());
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

    public Result getProductByBusinessSegmentIds() {
        ArrayList<String> listOfBusinessSegmentIDs = new ArrayList<>();
        //listOfBusinessSegmentIDs.add("326-10-65-1");
        return ok(lightHouseService.getProductByBusinessSegmentIds(listOfBusinessSegmentIDs).toString());
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

    public Result getChecklistByProduct() {
        ArrayList<String> listOfProductIds = new ArrayList<>();
        listOfProductIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#MachineryLease");
        listOfProductIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#BuildingLease");
        listOfProductIds.add("http://www.mindparabole.com/ontology/finance/Parabole-Model#Repurchaseagreement");
        return ok(lightHouseService.getChecklistByProducts(listOfProductIds).toString());
    }

    public Result getChecklistDetailsForReport() {
        ArrayList<String> listOfCheckListIds = new ArrayList<>();
        listOfCheckListIds.add("9964fc0e-552a-4e3a-a33e-3ebd99636e33");
       // return ok(checkListServices.getChecklistDetailsForReport(listOfCheckListIds).toString());
        return null;
    }

    public Result getParagraphTypeCounts() {
        ArrayList<String> listOfCheckListIds = new ArrayList<>();
        listOfCheckListIds.add("9964fc0e-552a-4e3a-a33e-3ebd99636e33");
        return ok(checkListServices.getParagraphTypeCounts().toString());
    }

    public Result getRelatedComponentsByComponent() {
        String nodeID = "http://www.mindparabole.com/ontology/finance/Parabole-Model#AssetRecognitionPolicy";
        String filteredBY = "COMPONENT";
        return ok(checkListServices.getRelatedNodesByNodeID(nodeID, filteredBY).toString());
    }

    public Result getCompliedAndNotCompliedCounts() {
        return ok(checkListServices.getCompliedAndNotCompliedCounts().toString());
    }

    public Result getRelatedBusinessSegentsByBusinessSegment() {
        String nodeID = "http://www.mindparabole.com/ontology/finance/Parabole-Model#AssetRecognitionPolicy";
        String filteredBY = "BUSINESSSEGMENT";
        return ok(checkListServices.getRelatedNodesByNodeID(nodeID, null).toString());
    }

    public Result getChecklistByConcept() {
        return ok(lightHouseService.getChecklistByConcept("http://mindparabole.com/finance/fasb_concepts#ValuationAllowance").toString());
    }

    public Result getChecklistBySection() {
        ArrayList<String> sections = new ArrayList<>();
        sections.add("326-20-30");
        return ok(lightHouseService.getChecklistsByRootNodeIDs(sections).toString());
    }

    public Result getChecklistBySubTopic() {
        ArrayList<String> subTopics = new ArrayList<>();
        subTopics.add("326-20");
        return ok(lightHouseService.getChecklistsByRootNodeIDs(subTopics).toString());
    }

    public Result getChecklistByTopic() {
        ArrayList<String> topics = new ArrayList<>();
        topics.add("326");
        return ok(lightHouseService.getChecklistsByRootNodeIDs(topics).toString());
    }


}
