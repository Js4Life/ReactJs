package com.parabole.feed.application.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.application.services.OctopusLineageService;
import com.parabole.feed.platform.assimilation.DataSourceUtilsFactory;
import com.parabole.feed.platform.assimilation.QueryResultTable;
import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.utils.AppUtils;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static play.mvc.Controller.request;
import static play.mvc.Controller.session;


public class LineageController extends BaseController {

    @Inject
    protected OctopusLineageService octopusLineageService;

    public Result getFullLineageGraph() throws AppException {
    	System.out.println("ok 1 >>>>>>>>>>>>>>>>>>>>>>");
        return Results.ok(octopusLineageService.getGlobalLineage());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageGraphHavingSpecifiedRules() throws AppException {
        final JsonNode json = request().body().asJson();
        final String ruleId = json.findPath("ruleId").asText();
        // here it will get the rule id ~
        Logger.info(ruleId);
        //List<RuleDef> pRule= new ArrayList<RuleDef>();
        //return Results.ok(octopusLineageService.getLineageForSpecificRules(pRule));
        final String lGraph = octopusLineageService.getLineageForSpecificRules(Integer.parseInt(ruleId));
        return Results.ok(lGraph);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageGraphByGlossaryId() throws AppException {
        final JsonNode json = request().body().asJson();
        final String glossaryId = json.findPath("glossaryId").asText();
        final String returnR = octopusLineageService.getLineageForSpecificGlossary(Integer.parseInt(glossaryId), 1);
        Logger.info("getLineageGraphByGlossaryId : "+ returnR);
        return Results.ok(returnR);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageGraphByConceptId() throws AppException {
        final JsonNode json = request().body().asJson();
        final String conceptId = json.findPath("conceptId").asText();
        final String returnR =  octopusLineageService.getLineageForSpecificConcepts(Integer.parseInt(conceptId));
        Logger.info("getLineageGraphByConceptId : "+ returnR);
        return Results.ok(returnR);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageGraphByDB() throws AppException {

        final String jsonText = request().body().asJson().toString();
        final JSONObject jsonObject = new JSONObject(jsonText);
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
        return Results.ok(returnR);
    }


    public Result getHardCodedExcelResponse(final String fileName) throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("excel/" + fileName);
        return Results.ok(jsonFileContent);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getLineageDbData() throws Exception {
        final String jsonText = request().body().asJson().toString();
        final JSONObject queryData = new JSONObject(jsonText);
        final String userId = session().get(CCAppConstants.USER_ID);
        final String dsName = queryData.getString(CCAppConstants.ATTR_DATASOURCE_MAPPING_NAME);
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
        return Results.ok(Json.toJson(ret));
    }
}
