package com.parabole.cecl.application.controllers;

import com.parabole.cecl.application.exceptions.AppException;
import com.parabole.feed.application.services.TaggingUtilitiesServices;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import org.json.*;

import javax.inject.Inject;

/**
 * Created by ATANU on 7/29/2016.
 */
public class CeclController extends Controller{
    @Inject
    TaggingUtilitiesServices taggingUtilitiesServices;

    public Result startContentParser() {
        Boolean status = false;
        try {
            taggingUtilitiesServices.startContentParser("");
            status = true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return Results.ok(status.toString());
    }

    public Result getParagraphsByConcept(String concept) {
        JSONObject finalJson = new JSONObject();
        Boolean status = false;
        finalJson.put("status", status);
        try {
            String result = taggingUtilitiesServices.getParagraphsByContent(concept, new JSONObject());
            finalJson.put("data", result);
            status = true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }
}
