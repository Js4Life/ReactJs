package com.parabole.feed.application.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.services.CheckListServices;
import com.parabole.feed.application.services.TaggingUtilitiesServices;
import org.json.JSONObject;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagir on 02-08-2016.
 */

public class DocumentController extends BaseController{


    @Inject
    private CheckListServices checkListServices;

    public Result documentParsing(String file){
        String retData = null;
        
        try {
             retData =  taggingUtilitiesServices.startContentParser(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(retData);
    }


    public Result getNodesFromParagraphJSon()  throws Exception {
        String retData = null;

        try {
             retData =  taggingUtilitiesServices.getNodesFromParagraphJSon();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok(retData);
    }


}
