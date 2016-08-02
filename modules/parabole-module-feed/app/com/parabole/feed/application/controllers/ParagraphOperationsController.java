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


    public Result questionAgainstParagraphId(String paragrphId) throws AppException, IOException {

        JSONObject mappedQuestions = checkListServices.questionAgainstParagraphId(paragrphId);

        return ok(mappedQuestions.toString());

    }





}
