package com.parabole.feed.application.controllers;

import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.platform.utils.AppUtils;
import org.json.JSONObject;
import play.mvc.Result;

import java.io.IOException;


/**
 * Created by Sagir on 19-07-2016.
 */

public class FeedController extends BaseController{


    public Result getRawBindingDataValues(String fileName) throws AppException {

        final String configurationId = jenaTdbService.getRawBindingDataValues(fileName);

        return ok(configurationId);

    }

    public Result saveListOfSentenceLocationsAgainstAllConcepts() throws com.parabole.feed.platform.exceptions.AppException {

        final Integer configurationId = taggingUtilitiesServices.saveListOfSentenceLocationsAgainstAllConcepts();

        return ok(configurationId.toString());

    }

    public Result getTheassignments() throws AppException {

        final String configurationId = taggingUtilitiesServices.getConfigurationDetailWithnodeinfo();

        return ok(configurationId);

    }

    public Result startContentParser(String file) throws AppException, IOException {

        final String result = taggingUtilitiesServices.startContentParser(file);
        return ok(result);

    }

    public Result getParagraphsByContent(String concept) throws AppException, com.parabole.feed.platform.exceptions.AppException {

        final String result = taggingUtilitiesServices.getParagraphsByContent(concept);
        return ok(result);

    }

}
