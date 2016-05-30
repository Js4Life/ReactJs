package com.parabole.ccar.application.controllers;

import java.util.Iterator;
import javax.inject.Inject;

import com.parabole.ccar.application.exceptions.AppException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.parabole.ccar.application.services.JenaTdbService;
import com.parabole.ccar.platform.utils.AppUtils;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;


/**
 * Jena TDB Actions
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */
public class JenaTdbController extends BaseController {

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
        } catch (final com.parabole.ccar.platform.exceptions.AppException e) {
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
        return Results.ok(jenaTdbService.loadSeriesWidgetData(compName).toString());
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
}
