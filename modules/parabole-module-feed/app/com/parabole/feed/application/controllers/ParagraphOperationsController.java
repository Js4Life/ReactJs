package com.parabole.feed.application.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.services.CheckListServices;
import com.parabole.feed.application.services.LightHouseService;
import org.json.JSONObject;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagir on 02-08-2016.
 */

public class ParagraphOperationsController extends BaseController{


    @Inject
    private CheckListServices checkListServices;

    @Inject
    private LightHouseService lightHouseService;



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

        String checkListId = "1234567890";
        String checklistText = "This is a sample checklist Text";

        return ok(checkListServices.saveOrUpdateCheckList(checkListId, checklistText));

    }


    public Result getCheckListById() {

        String checkListId = "1234567890";

        return ok(checkListServices.getCheckListById(checkListId));

    }

    public Result removeCheckList() {

        String checkListId = "1234567890";

        return ok(checkListServices.removeCheckList(checkListId));

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

    public Result getSubtopicsByTopicId(String topicId) {

        return ok(lightHouseService.getSubtopicsByTopicId(topicId).toString());
    }

    public Result getParagraphBySectionId(String nodeId) {

        return ok(lightHouseService.getParagraphBySectionId(nodeId).toString());
    }



}
