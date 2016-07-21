package com.parabole.feed.application.controllers;

import com.parabole.feed.application.exceptions.AppException;
import play.mvc.Result;


/**
 * Created by Sagir on 19-07-2016.
 */
public class FeedController extends BaseController{


    public Result getRawBindingDataValues(String fileName) throws AppException {

        final String configurationId = jenaTdbService.getRawBindingDataValues(fileName);

        return ok(configurationId);

    }

    public Result getTheassignments(String fileName) throws AppException {

        final String configurationId = taggingUtilitiesServices.getTheassignment();

        return ok(configurationId);

    }

}
