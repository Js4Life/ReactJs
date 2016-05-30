// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// KnowledgeAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.controllers;



import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.parabole.rda.platform.assimilation.DataSourceUtilsFactory;
import com.parabole.rda.application.global.RdaAppConstants;
import com.parabole.rda.application.services.OctopusLineageService;
import com.parabole.rda.platform.assimilation.QueryResultTable;
import com.parabole.rda.platform.exceptions.AppException;
import com.parabole.rda.platform.utils.AppUtils;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * Manages all the Lineage Operations
 *
 * @author Sandip bhaumik
 */
public class LineageAction extends BaseAction {

    @Inject
    protected OctopusLineageService octopusLineageService;

    public Result getFullLineageGraph() throws AppException {

        return Results.ok(octopusLineageService.getGlobalLineage());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getJsonForAllLinege() throws AppException {
        // it will receive inputName as the String value for the type of the Post input
        final JsonNode json = request().body().asJson();
        final String inputName = json.findPath("inputName").asText();

        //------------------testing start

        //final String jsonFileContent = AppUtils.getFileContent("json/jsonForAllLinege.json");
        //response().setContentType("application/json");
        //return Results.ok(jsonFileContent);

        //------------------testing end

        //final JsonNode json = request().body().asJson();
        //final String usecaseId = json.findPath("usecaseID").asText();
        final String usecaseId = "1"; //Temporary setting
        // here it will get the rule id ~
        Logger.info(usecaseId);
        final String lGraph = octopusLineageService.getLineageForSpecificUseCases(Integer.parseInt(usecaseId));
		System.out.println("JSON Ready");
        return ok(lGraph);
        

    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getJsonForAllLinegewithAdaptiveLearning() throws AppException {
        // it will receive inputName as the String value for the type of the Post input

        /*
        final String jsonString = request().body().asJson().toString();
        JSONObject incomingJsonObject = new JSONObject(jsonString);
        JSONObject jsonObject = incomingJsonObject.getJSONObject("data");
        String fileName = jsonObject.getString("fileName");
        JSONObject dateRange = jsonObject.getJSONObject("dateRange");
        final String startyear = dateRange.getString("from");
        final String endyear = dateRange.getString("to");
        */

        //final JsonNode json = request().body().asJson();
        //final String usecaseId = json.findPath("usecaseID").asText();
		//final String filename = json.findPath("filename").asText();
		//final String filename = "DB.xlsx";
		//final String startyear = json.findPath("startyear").asText();
		//final String endyear = json.findPath("endyear").asText();
		//final String startyear = "2010";
		//final String endyear = "2015";
        final String usecaseId = "1"; //Temporary setting

		final String jsonString = request().body().asJson().toString();
		JSONObject incomingJsonObject = new JSONObject(jsonString);
		JSONObject jsonObject = incomingJsonObject.getJSONObject("data");
		String filename = jsonObject.getString("fileName");
		JSONObject dateRange = jsonObject.getJSONObject("dateRange");
		final String startyear = dateRange.getString("from");
		final String endyear = dateRange.getString("to");
		
		
		System.out.println("fileName " + filename + " startyear " + startyear + " endyear " + endyear);

        // here it will get the rule id ~
        Logger.info(usecaseId);
        final String lGraph = octopusLineageService.getLineageForSpecificUseCaseswithAL(Integer.parseInt(usecaseId), filename, Integer.parseInt(startyear), Integer.parseInt(endyear));
		System.out.println("JSON Ready");
        return ok(lGraph);
        

    }
	
	/*
    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 100 * 1024)
    public Result getJsonForAllLinegewithAdaptiveLearning() throws AppException {
        // it will receive inputName as the String value for the type of the Post input
        final JsonNode json = request().body().asJson();
        final String data = json.findPath("data").asText();
        System.out.println("data = " + data);
        return null;


    }
	*/

    @BodyParser.Of(BodyParser.Json.class)
    public Result getJsonForAllLinegeAgainstNodeIdwithAdaptiveLearning() throws AppException {
        // it will receive inputName as the String value for the type of the Post input
        final JsonNode json = request().body().asJson();
        final String data = json.findPath("data").asText();
        System.out.println("data = " + data);
        return null;


    }


    @BodyParser.Of(BodyParser.Json.class)
    public Result getJsonForAllLinegeAgainstNodeId() throws AppException {
        // it will receive inputName as the String value for the type of the Post input


        final JsonNode json = request().body().asJson();
        final String inputName = json.findPath("inputName").asText();
        final String node = json.findValue("node").toString();
        final JSONObject jsonObjectOfNodeProperties = new JSONObject(node); // here you will get all the properties + extras

		//final String nodeId = jsonObjectOfNodeProperties.getString("id");	
		int nodeId = jsonObjectOfNodeProperties.getInt("id");


        final String usecaseId = "1"; //Temporary setting
        // here it will get the rule id ~
        Logger.info(usecaseId);
        final String lGraph = octopusLineageService.getLineageForNodeIdSpecificUseCases(Integer.parseInt(usecaseId), nodeId);
		System.out.println("JSON Ready");
        return ok(lGraph);

	}
	
	/*
    @BodyParser.Of(BodyParser.Json.class)
    public Result getJsonForAllLinegeAgainstNodeIdwithAdaptiveLearning() throws AppException {
        // it will receive inputName as the String value for the type of the Post input


        final JsonNode json = request().body().asJson();
        final String inputName = json.findPath("inputName").asText();
        final String node = json.findValue("node").toString();
        final JSONObject jsonObjectOfNodeProperties = new JSONObject(node); // here you will get all the properties + extras

		//final String nodeId = jsonObjectOfNodeProperties.getString("id");	
		int nodeId = jsonObjectOfNodeProperties.getInt("id");


		final String filename = json.findPath("filename").asText();
		final String startyear = json.findPath("startyear").asText();
		final String endyear = json.findPath("endyear").asText();
        final String usecaseId = "1"; //Temporary setting
        // here it will get the rule id ~
        Logger.info(usecaseId);
        final String lGraph = octopusLineageService.getLineageForNodeIdSpecificUseCases(Integer.parseInt(usecaseId), nodeId);
		System.out.println("JSON Ready");
        return Results.ok(lGraph);

	}
	*/
	
    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageGraphHavingSpecifiedRules() throws AppException {
        final JsonNode json = request().body().asJson();
        final String ruleId = json.findPath("ruleId").asText();
        // here it will get the rule id ~
        Logger.info(ruleId);
        //List<RuleDef> pRule= new ArrayList<RuleDef>();
        //return Results.ok(octopusLineageService.getLineageForSpecificRules(pRule));
        final String lGraph = octopusLineageService.getLineageForSpecificRules(Integer.parseInt(ruleId));
        return ok(lGraph);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageGraphByGlossaryId() throws AppException {
        final JsonNode json = request().body().asJson();
        final String glossaryId = json.findPath("glossaryId").asText();
        final String returnR = octopusLineageService.getLineageForSpecificGlossary(Integer.parseInt(glossaryId), 1);
        Logger.info("getLineageGraphByGlossaryId : "+ returnR);
        return ok(returnR);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageGraphByConceptId() throws AppException {
        final JsonNode json = request().body().asJson();
        final String conceptId = json.findPath("conceptId").asText();
        final String returnR =  octopusLineageService.getLineageForSpecificConcepts(Integer.parseInt(conceptId));
        Logger.info("getLineageGraphByConceptId : "+ returnR);
        return ok(returnR);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageGraphByDB() throws AppException {

        final String jsonText = request().body().asJson().toString();
        final JSONObject  jsonObject = new JSONObject(jsonText);
        final String DB_Name = jsonObject.getString("dbname");
        final String DB_table = jsonObject.getString("table");
        final String DB_col = jsonObject.getString("column");

        System.out.println(DB_Name+DB_table+DB_col);

        /*    	final JsonNode json = request().body().asJson();
        final String DB_Name = json.findPath("dbName").asText();
        final String DB_table = json.findPath("dbTable").asText();
        final String DB_col = json.findPath("dbCol").asText();*/

        final String returnR =  octopusLineageService.getLineageForSpecificDB(DB_Name, DB_table, DB_col);
        Logger.info("getLineageGraphByDB : "+ returnR);
        return ok(returnR);
    }


    public Result getHardCodedExcelResponse(final String fileName) throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("excel/" + fileName);
        return ok(jsonFileContent);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageDbData() throws Exception {
        final String jsonText = request().body().asJson().toString();
        final JSONObject queryData = new JSONObject(jsonText);
        final String userId = session().get(RdaAppConstants.USER_ID);
        final String dsName = queryData.getString(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME);
        final List<Map<String, String>> cfgDetailsList = coralConfigurationService.getConfigurationByName(userId, dsName);
        final Map<String, Object> ret = new HashMap<String, Object>();
        try {
            final QueryResultTable resultTable = DataSourceUtilsFactory.fetchLineageDbData(userId, queryData.toString(), cfgDetailsList.get(0));
            if (resultTable != null) {
                ret.put("status", true);
                ret.put("data", resultTable);
            } else {
                throw new Exception("Data Source Not Found");
            }
        } catch (final Exception ex) {
            Logger.error("fetchView Exception", ex);
            ret.put("status", false);
            ret.put("errMessage", ex.getStackTrace());
        }
        return ok(Json.toJson(ret));
    }
}
