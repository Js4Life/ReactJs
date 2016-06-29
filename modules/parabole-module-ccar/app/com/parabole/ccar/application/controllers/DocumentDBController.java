package com.parabole.ccar.application.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.application.services.CoralConfigurationService;
import com.parabole.ccar.platform.assimilation.OperationResult;
import com.parabole.ccar.platform.exceptions.AppException;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;

import java.util.List;
import java.util.Map;

public class DocumentDBController extends BaseController  {
	
	@Inject
    protected CoralConfigurationService coralConfigurationService;
	
    public Result getConfigurationNames(final String ConfigarationType) throws AppException {
    	final String userId = session().get(CCAppConstants.USER_ID);
        final Map<Integer, String> outputMap = coralConfigurationService.getConfigurationNames(userId, ConfigarationType);
        return Results.ok(Json.toJson(outputMap));
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result saveDataSource() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(CCAppConstants.USER_ID);
        final Integer configurationId = coralConfigurationService.saveConfiguration(userId, json, CCAppConstants.ConfigurationType.DATASOURCE);
        response().setContentType(CCAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }
    

    public Result getConfiguration(final Integer ConfigarationId) throws AppException {
        final String ConfigurationData = coralConfigurationService.getConfigurationDetail(ConfigarationId);
        return Results.ok(Json.toJson(ConfigurationData));
    }
    
    public Result getConfigurationDetailsByName(final String ConfigarationName) throws AppException {
        final String userId = session().get(CCAppConstants.USER_ID);
        final List<Map<String, String>> outputMap = coralConfigurationService.getConfigurationByName(userId, ConfigarationName);
        return Results.ok(Json.toJson(outputMap));
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteConfiguration() throws AppException {
        final JsonNode json = request().body().asJson();
        final Integer cfgId = json.findPath(CCAppConstants.RDA_CFG_ID).asInt();
        coralConfigurationService.deleteConfiguration(cfgId);
        return Results.ok(Json.toJson(true));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result connectionChecker() throws AppException {
        final JsonNode cardinalsJson = request().body().asJson();
        final String dbDriver = cardinalsJson.findPath(CCAppConstants.ATTR_DATABASE_DRIVER_NAME).textValue();
        String dbUrl = cardinalsJson.findPath(CCAppConstants.ATTR_DATABASE_SERVER_URL).textValue();
        dbUrl = dbUrl.replace("&amp;", "&");
        final String dbUser = cardinalsJson.findPath(CCAppConstants.ATTR_DATABASE_USER_NAME).textValue();
        final String dbPassword = cardinalsJson.findPath(CCAppConstants.ATTR_DATABASE_PASSWORD).textValue();
        final String result = "Success !";
        return Results.ok(Json.toJson(result));
    }



}
