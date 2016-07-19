package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.parabole.cecl.application.global.RdaAppConstants;
import com.parabole.cecl.platform.exceptions.AppException;
import com.parabole.feed.application.services.OctopusSemanticService;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;

import static play.mvc.Controller.request;
import static play.mvc.Controller.response;

/**
 * Created by Sagir on 18-07-2016.
 */
public class GraphDBController {

    @Inject
    protected OctopusSemanticService octopusSemanticService;


    public Result getRelatedVerticesByURI (final String uri) throws AppException {
        response().setContentType(RdaAppConstants.MIME_JSON);
        String outputJson = null;
        try {
            outputJson = octopusSemanticService.getRelatedVerticesByURI(uri);
        } catch (com.parabole.feed.platform.exceptions.AppException e) {
            e.printStackTrace();
        }
        return Results.ok(outputJson);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getRelatedVerticesByUri () throws AppException {
        final JsonNode json = request().body().asJson();
        final String uri = json.findPath("uri").textValue();
        //String hcData = "http://www.mindparabole.com/ontology/finance/Parabole-Model#Segment";
        response().setContentType(RdaAppConstants.MIME_JSON);
        String outputJson = null;
        try {
            outputJson = octopusSemanticService.getRelatedVerticesByURI(uri);
        } catch (com.parabole.feed.platform.exceptions.AppException e) {
            e.printStackTrace();
        }
        return Results.ok(outputJson);
    }
}
