//=============================================================================
//Copyright (C) 2014-2015, Parabole LLC
//Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
//Web: http://www.mindparabole.com
//All Rights Reserved.
//
//CoralAction.java
//
//This source code is available under the terms of the GNU Affero General
//Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
//terms, including the availability of proprietary exceptions for closed-source
//commercial applications and Acuity Community IP Partnership Programme.
//=============================================================================
package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.parabole.cecl.application.global.RdaAppConstants;
import com.parabole.cecl.application.services.AssimilationServices;
import com.parabole.cecl.platform.exceptions.AppException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.Map;

/**
 * Play Framework Action Controller dedicated for Assimilation
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */


@Security.Authenticated(ActionAuthenticator.class)
public class AssimilationAction extends BaseAction{

    @Inject
    AssimilationServices assimilationServices;
    
	public Result findlogicalViewForNode() throws AppException {
	    final JsonNode json = request().body().asJson();
        final int nodeId = json.findPath("id").asInt();
		return ok(assimilationServices.getLogicalViewForCompositeAggregatedGraphNode(nodeId).toString());	
	}

    @BodyParser.Of(BodyParser.Json.class)
    public Result assimilatorComposing() throws AppException {
        //future enhancement : aggrName required for each aggregate so it can see the logical view.
        final JsonNode json = request().body().asJson();
        final JSONObject jsonObj = new JSONObject(json);
        final String joinedDB = assimilationServices.AssimilationAggregation(jsonObj);
        return ok(joinedDB);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result aggregateComposingUsingAggregateNames() throws AppException {
        final String jsonText = request().body().asJson().toString();
        final JSONArray arrayOfGraphNames = new JSONArray(jsonText);
        final String joinedDB = assimilationServices.AssimilationAggregationByFetchingGraphById(arrayOfGraphNames);
        return ok(joinedDB);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveComposedAggregators() throws AppException {
        final String jsonText = request().body().asJson().toString();
    	final String userId = session().get(RdaAppConstants.USER_ID);
        final JSONObject  jsonObject = new JSONObject(jsonText);
        final JSONObject composedJsonObject = new JSONObject();
        
        final String name = jsonObject.getString("name");
        final String arrayOfGraphNames = jsonObject.getJSONArray("aggregates").toString();
        composedJsonObject.put("name", name);
        composedJsonObject.put("details", arrayOfGraphNames);
        final Integer configurationId = coralConfigurationService.saveConfigurationByNameAndDetails(userId, composedJsonObject, RdaAppConstants.ConfigurationType.COMPOSITEAGGREGATION);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    public Result getClusteredConfigurationNames() throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        JSONObject jsonArray = new JSONObject();
        final Map<Integer, String> aggregationData = coralConfigurationService.getConfigurationNames(userId, RdaAppConstants.ConfigurationType.AGGREGATION.toString());
        jsonArray.put(RdaAppConstants.ConfigurationType.AGGREGATION.toString(), aggregationData);
        final Map<Integer, String> compositeAggregationData = coralConfigurationService.getConfigurationNames(userId, RdaAppConstants.ConfigurationType.COMPOSITEAGGREGATION.toString());
        jsonArray.put(RdaAppConstants.ConfigurationType.COMPOSITEAGGREGATION.toString(), compositeAggregationData);
        return Results.ok(jsonArray.toString());
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result getAggregateConfigurationDetailsByNameAndType() throws AppException, JSONException {
        final String configarationName = request().body().asJson().findPath("name").asText();
        final String configarationType = request().body().asJson().findPath("type").asText();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final String finalOutput = assimilationServices.getNewGraphAsPerAggregationType(userId, configarationName, configarationType);
        return Results.ok(finalOutput);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteAggregateFromSavedComposedAggregators() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        coralConfigurationService.saveConfiguration(userId, json, RdaAppConstants.ConfigurationType.AGGREGATION);
        response().setContentType(RdaAppConstants.MIME_JSON);

        final String configurationName = json.findPath("name").textValue();
        String finalOutput = new String();
        try {
            finalOutput = assimilationServices.getNewGraphAsPerAggregationType(userId, configurationName, RdaAppConstants.ConfigurationType.COMPOSITEAGGREGATION.toString());
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        return Results.ok(Json.toJson(finalOutput));
    }

}
