package com.parabole.feed.application.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.services.CheckListServices;
import org.json.JSONObject;
import play.mvc.Result;

import java.io.IOException;

/**
 * Created by Sagir on 02-08-2016.
 */

public class ParagraphOperationsController extends BaseController{


    @Inject
    private CheckListServices checkListServices;



    public Result addQuestion() throws AppException, IOException {

        String mappedQuestions = checkListServices.findAndAddQuestion();

        return ok(mappedQuestions);

    }


    public Result saveQuestion() throws AppException, IOException {

        String savedQuestions = checkListServices.saveQuestion();

        return ok(savedQuestions);

    }


    public Result saveParagraph() throws AppException, IOException {


        String paragraphId = "1234455";
        String paragraphText = "This is a paragraph";
        String tag = "tag1";
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



/*    public Result createLightHouse() throws AppException {

        return ok(checkListServices.createLightHouse());

    }*/


}
