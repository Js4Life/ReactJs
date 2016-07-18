package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.cecl.application.global.CCAppConstants;
import com.parabole.feed.application.services.JenaTdbService;
import com.parabole.cecl.platform.utils.AppUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Controller;

import javax.inject.Inject;
import java.util.Iterator;


/**
 * Jena TDB Actions
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */

public class JenaTdbController extends Controller {

    @Inject
    JenaTdbService jenaTdbService;

    public Result jenaTdbFetching(final String fileIdentification) throws AppException {
        final String resultData = jenaTdbService.jenaTdbFetching(fileIdentification);
        return Results.ok(resultData);
    }

    public Result jenaTDBBatch(final String fileIdentification) throws AppException, JSONException {
        //jenaTdbService.updateTest(fileIdentification);
        jenaTdbService.insertBatch(fileIdentification);
        return Results.ok(StringUtils.EMPTY);
    }

    public Result loadAllFromBatch() throws AppException, JSONException {
        String jsonFileContent = null;
        try {
            jsonFileContent = AppUtils.getFileContent("json/batchInsert.json");
            response().setContentType("application/json");
        } catch (final com.parabole.cecl.platform.exceptions.AppException e) {
            e.printStackTrace();
        }
        final JSONObject jsonObject = new JSONObject(jsonFileContent);

        final Iterator<String> keys = jsonObject.keys();

        while( keys.hasNext() ) {
            final String key = keys.next();
            Logger.info("========>>> " + key + " =======>>> Loading....");
            jenaTdbService.insertBatch(key);
        }
        return Results.ok(StringUtils.EMPTY);
    }

    public Result jenaTDBInsert(final String fileIdentification) throws AppException {
        //jenaTdbService.updateTest(fileIdentification);
        jenaTdbService.insert(fileIdentification);
        return Results.ok(StringUtils.EMPTY);
    }

    public Result jenaTDBUpdate(final String fileIdentification) throws AppException {
        jenaTdbService.updateTest(fileIdentification);
        return Results.ok(StringUtils.EMPTY);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getAlldashboardTableData() throws AppException {
        final JsonNode json = request().body().asJson();
        final String compName = json.findPath("compName").textValue();
        return Results.ok(jenaTdbService.loadWidgetData(compName).toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getAllSeriesData() throws AppException {
        final JsonNode json = request().body().asJson();
        final String compName = json.findPath("compName").textValue();
        final String reportType = json.findPath("reportType").textValue();
        return Results.ok(jenaTdbService.loadSeriesWidgetData(compName, reportType).toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getSingleSeriesData() throws AppException, JSONException {
        final JsonNode json = request().body().asJson();
        final String compName = json.findPath("compName").textValue();
        return Results.ok(jenaTdbService.loadSingleSeriesWidgetData(compName).toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getAllHeatMapTableData() throws AppException, JSONException {
        final JsonNode json = request().body().asJson();
        final String param1 = json.findPath("param1").textValue();
        final String param2 = json.findPath("param2").textValue();
        return Results.ok(jenaTdbService.loadWidgetDataByParams(param1, param2).toString());
    }


    @BodyParser.Of(BodyParser.Json.class)
        public Result getLiquidityFilters() throws AppException {
                final JsonNode json = request().body().asJson();
                final String countryCode = json.findPath("countryCode").textValue();
                return Results.ok(jenaTdbService.getLiquidityFilters(countryCode).toString());
            }

        @BodyParser.Of(BodyParser.Json.class)
        public Result getLiquidityDataByFilters() throws AppException, JSONException {
                final JsonNode json = request().body().asJson();
                final String filters = json.findPath("filters").toString();
                final JSONArray filterArr = new JSONArray(filters);
                return Results.ok(jenaTdbService.getLiquidityDataByFilters(filterArr).toString());
           }

    public Result downloadFileByName(final String name, final String type) throws AppException, JSONException {
        // final JsonNode json = request().body().asJson();
        // final String name = json.findPath("name").textValue();
        // final String type = json.findPath("type").textValue();
        final String fileName = name + "." + type;
        String fileContentType = null;
        try {
            final byte[] fileData = jenaTdbService.downloadFileByName(fileName);
            response().setHeader("Content-Disposition", "attachment; filename=" + fileName);

            if (type.equalsIgnoreCase(CCAppConstants.FileFormat.XLSX.toString())) {
                fileContentType = "text/csv";
            }
            response().setContentType(fileContentType);
            return ok(fileData).as(fileContentType);

        } catch (final Exception e) {
            e.printStackTrace();
            return ok("Failed To Fetch Document").as("text/html");
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getFilteredDataByCompName() throws AppException, JSONException {
        final JsonNode json = request().body().asJson();
        final String compName = json.findPath("compName").textValue();
        final String filterStr = json.findPath("filterStr").textValue();
        return Results.ok(jenaTdbService.getFilteredDataByCompName(compName, filterStr).toString());
    }
}
