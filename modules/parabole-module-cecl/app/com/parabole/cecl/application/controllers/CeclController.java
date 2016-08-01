package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parabole.cecl.application.exceptions.AppException;
import com.parabole.cecl.application.services.JenaTdbService;
import com.parabole.feed.application.services.CheckListServices;
import com.parabole.feed.application.services.OctopusSemanticService;
import com.parabole.feed.application.services.TaggingUtilitiesServices;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import org.json.*;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by ATANU on 7/29/2016.
 */
public class CeclController extends Controller{
    @Inject
    TaggingUtilitiesServices taggingUtilitiesServices;

    @Inject
    CheckListServices checkListServices;

    @Inject
    JenaTdbService jenaTdbService;

    @Inject
    OctopusSemanticService octopusSemanticService;

    @BodyParser.Of(BodyParser.Json.class)
    public Result getFilteredDataByCompName() throws AppException, JSONException {
        final JsonNode json = request().body().asJson();
        final String compName = json.findPath("compName").textValue();
        final String filterStr = json.findPath("filterStr").textValue();
        return Results.ok(jenaTdbService.getFilteredDataByCompName(compName, filterStr).toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getFunctionalAreasByProducts() throws AppException, JSONException {
        final String jsonText = request().body().asJson().toString();
        final JSONObject json = new JSONObject(jsonText);
        final String compName = json.getString("compName");
        final JSONArray productsArr = json.getJSONArray("products");
        System.out.println(productsArr);
        final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> valueMap = new LinkedHashMap<>();
        final ArrayList<String> areas = new ArrayList<String>(Arrays.asList("concept", "model", "policy", "report"));
        Set<String> columns = null;
        for(int i=0; i<productsArr.length(); i++){
            JSONObject res = jenaTdbService.getFilteredDataByCompName(compName, productsArr.getString(i));
            JSONArray arr = res.getJSONArray("data");
            for(int j=0; j<arr.length(); j++){
                JSONObject obj = arr.getJSONObject(j);
                String productKey = obj.getString("product");
                if(!valueMap.containsKey(productKey)){
                    LinkedHashMap<String, ArrayList<String>> innerMap = new LinkedHashMap<>();
                    for (String area: areas) {
                        innerMap.put(area, new ArrayList<>());
                    }
                    valueMap.put(productKey, innerMap);
                }
                LinkedHashMap<String, ArrayList<String>> innerMap = valueMap.get(productKey);
                for (String area: areas) {
                    if(obj.has(area)){
                        ArrayList<String> areaList = innerMap.get(area);
                        areaList.add(obj.getString(area));
                    }
                }
                columns = innerMap.keySet();
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        JSONObject finalRes = new JSONObject();
        finalRes.put("columns", columns);
        try {
            finalRes.put("data", new JSONObject(mapper.writeValueAsString(valueMap)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Results.ok(finalRes.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getMultiFilteredDataByCompName() throws AppException, JSONException {
        final String jsonText = request().body().asJson().toString();
        final JSONObject json = new JSONObject(jsonText);
        final String compName = json.getString("compName");
        final JSONArray filters = json.getJSONArray("filters");
        return Results.ok(jenaTdbService.getMultiFilteredDataByCompName(compName, filters).toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getDescriptionByUri() throws Exception {
        final String jsonText = request().body().asJson().toString();
        final JSONObject json = new JSONObject(jsonText);
        final String uriStr = json.getString("uriStr");
        return Results.ok(octopusSemanticService.getDescriptionByUri(uriStr).toString());
    }

    public Result startContentParser() {
        Boolean status = false;
        try {
            taggingUtilitiesServices.startContentParser("");
            status = true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return Results.ok(status.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getParagraphsByConcept() {
        final JsonNode json = request().body().asJson();
        final String concept = json.findPath("concept").textValue();
        JSONObject finalJson = new JSONObject();
        Boolean status = false;
        try {
            String result = taggingUtilitiesServices.getParagraphsByContent(concept);
            status = true;
            finalJson.put("status", status);
            finalJson.put("data", result);
        } catch (Exception e){
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addChecklist() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONObject checkListJson = request.getJSONObject("checkList");
        System.out.println("checkListJson = " + checkListJson);
        JSONObject finalJson = new JSONObject();
        Boolean status = false;
        try {
            String result = checkListServices.addQuestion(checkListJson);
            status = true;
            finalJson.put("status", status);
            finalJson.put("data", result);
        } catch (Exception e){
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistByConceptAndComponent() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String conceptName = request.getString("conceptName");
        final String componentType = request.getString("componentType");
        final String componentName = request.getString("componentName");
        JSONObject finalJson = new JSONObject();
        Boolean status = false;
        try {
            JSONObject result = checkListServices.questionAgainstConceptNameComponentTypeComponentName(conceptName, componentType, componentName);
            status = true;
            finalJson.put("status", status);
            finalJson.put("data", result);
        } catch (Exception e){
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistByParagraphId() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String paraId = request.getString("paragraphId");
        JSONObject finalJson = new JSONObject();
        try {
            /*JSONObject result = new JSONObject();
            for (int i=0; i<paraIds.length(); i++){
                JSONObject tempObj = checkListServices.questionAgainstParagraphId(paraIds.getString(i));
                Iterator<String> tempKeys = tempObj.keys();
                while(tempKeys.hasNext()){
                    String key = tempKeys.next();
                    result.put(key, tempObj.getString(key));
                }
            }*/
            JSONObject result = checkListServices.questionAgainstParagraphId(paraId);
            finalJson.put("data", result);
        } catch (Exception e){
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistByNode() throws AppException, JSONException {
        final String jsonText = request().body().asJson().toString();
        final JSONObject json = new JSONObject(jsonText);
        final String nodeType = json.getString("nodeType");
        final String nodeName = json.getString("nodeName");
        JSONObject finalJson = new JSONObject();
        JSONObject data = new JSONObject();
        finalJson.put("data", data);
        try {
            String compName = null;
            switch (nodeType){
                case "Topic":
                    compName = "paragraphIdByTopic";
                    break;
                case "Sub-Topic":
                    compName = "paragraphIdBySubTopic";
                    break;
                case "Section":
                    compName = "paragraphIdBySection";
                    break;
                case "FASB Concept":
                    compName = "paragraphIdByConcept";
                    break;
            }
            JSONObject paraIdObj = jenaTdbService.getFilteredDataByCompName(compName, nodeName);
            System.out.println("paraIdObj = " + paraIdObj);
            JSONArray paraIdArr = paraIdObj.getJSONArray("data");
            for (int i=0; i<paraIdArr.length(); i++){
                JSONObject obj = paraIdArr.getJSONObject(i);
                String paraId = obj.getString("paragraphId");
                JSONObject tempObj = checkListServices.questionAgainstParagraphId(paraId);
                JSONObject status = tempObj.getJSONObject("status");
                if(status.getBoolean("haveData")){
                    JSONObject questions = tempObj.getJSONObject("questions");
                    Iterator<String> tempKeys = questions.keys();
                    while(tempKeys.hasNext()){
                        String key = tempKeys.next();
                        data.put(key, questions.getString(key));
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }
}
