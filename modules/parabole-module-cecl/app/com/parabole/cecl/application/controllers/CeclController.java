package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parabole.cecl.application.exceptions.AppException;
import com.parabole.cecl.application.global.CCAppConstants;
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
        try {
            JSONObject result = checkListServices.questionAgainstConceptNameComponentTypeComponentName(conceptName, componentType, componentName);
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
        JSONObject result = jenaTdbService.getChecklistByParagraphId(paraId);
        return ok(result.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistByMultiParagraphId() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONArray paraIds = request.getJSONArray("paragraphIds");
        JSONObject finalJson = new JSONObject();
        JSONObject finalQuestions = new JSONObject();
        JSONObject finalAnswers = new JSONObject();
        JSONObject finalStatus = new JSONObject();
        Boolean haveData = false;
        for(int i=0; i<paraIds.length(); i++){
            JSONObject result = jenaTdbService.getChecklistByParagraphId(paraIds.getString(i));
            if(result.getJSONObject("status").getBoolean("haveData")){
                haveData = true;
                JSONObject questions = result.getJSONObject("questions");
                JSONObject answers = result.getJSONObject("answers");
                Iterator<String> qKeys = questions.keys();
                while (qKeys.hasNext()){
                    String key = qKeys.next();
                    finalQuestions.put(key, questions.getString(key));
                }
                Iterator<String> aKeys = answers.keys();
                while (aKeys.hasNext()){
                    String key = aKeys.next();
                    finalAnswers.put(key, answers.getString(key));
                }
            }

        }
        finalStatus.put("haveData", haveData);
        finalJson.put("questions", finalQuestions).put("answers", finalAnswers).put("status", finalStatus);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistByNode() throws AppException, JSONException {
        final String jsonText = request().body().asJson().toString();
        final JSONObject json = new JSONObject(jsonText);
        String nodeType = json.getString("nodeType");
        final String nodeName = json.getString("nodeName");
        JSONObject finalJson = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            String compName = null;
            switch (nodeType){
                case "Topic":
                case "Sub-Topic":
                case "Section":
                    data = jenaTdbService.getChecklistByNode(CCAppConstants.DocumentName.FASBAccntStandards.toString(), nodeType.trim(), nodeName.trim());
                    break;
                case "FASB Concept":
                    data = jenaTdbService.getChecklistByConcept(nodeName.trim());
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        finalJson.put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addAnswer() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        //final JSONArray answerJson = request.getJSONArray("answers");
        final JSONObject answerJson = request.getJSONObject("answers");
        JSONObject finalJson = new JSONObject();
        Boolean status = false;
        try {
            status = checkListServices.addAnswer(answerJson);
            finalJson.put("status", status);
        } catch (Exception e){
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }

    public Result parseDocumentHierarchy(){
        final String userId = session().get(CCAppConstants.USER_ID);
        final Boolean status = jenaTdbService.parseDocumentHierarchy("parseDocumentHierarchy", CCAppConstants.DocumentName.FASBAccntStandards.toString(), userId);
        JSONObject res = new JSONObject();
        res.put("status", status);
        return ok(res.toString());
    }
}
