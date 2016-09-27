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
public class StarfishServices {

    @Inject
    private Environment environment;

    @Inject
    private StarFish starFish;


    public ArrayList<HashMap<String, String>> getChecklistByID(List<String> checklistIDs) {

        ArrayList<HashMap<String, String>> finalResult = new ArrayList<>();
        for(String checklistID  : checklistIDs){
            HashMap<String, String> checklist = starFish.getChecklistByID(checklistID);
            finalResult.add(checklist);
        }

        return finalResult;
    }

    public HashMap<String, String> getCompliedAndNotCompliedCounts() {

        HashMap<String, String> compliedAndNotCompliedCounts = starFish.getCompliedAndNotCompliedCounts();
        return compliedAndNotCompliedCounts;
    }
}
