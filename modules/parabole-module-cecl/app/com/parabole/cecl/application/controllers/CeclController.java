package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parabole.cecl.application.exceptions.AppException;
import com.parabole.cecl.application.global.CCAppConstants;
import com.parabole.cecl.application.services.JenaTdbService;
import com.parabole.cecl.application.utils.BodyParserMaxLength;
import com.parabole.feed.application.services.CheckListServices;
import com.parabole.feed.application.services.LightHouseService;
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

    @Inject
    LightHouseService lightHouseService;

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
    public Result getParagraphsBySubsection() {
        final JsonNode json = request().body().asJson();
        final String subSectionId = json.findPath("subSectionId").textValue();
        JSONObject finalJson = new JSONObject();
        Boolean status = false;
        try {
            JSONArray result = taggingUtilitiesServices.getParagraphsBySubsection(subSectionId);
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

    /*@BodyParser.Of(BodyParser.Json.class)
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
    }*/

    @BodyParser.Of(BodyParser.Json.class)
    public Result addAnswer() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONObject answerJson = request.getJSONObject("answers");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        HashMap<String, Boolean> answerMap = new Gson().fromJson(answerJson.toString(), new TypeToken<HashMap<String, Boolean>>() {}.getType());
        try {
            data = checkListServices.editChecklistCheck(answerMap);
            finalJson.put("status", status);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    public Result parseDocumentHierarchy(){
        final String userId = session().get(CCAppConstants.USER_ID);
        final Boolean status = jenaTdbService.parseDocumentHierarchy("parseDocumentHierarchy", CCAppConstants.DocumentName.FASBAccntStandards.toString(), userId);
        JSONObject res = new JSONObject();
        res.put("status", status);
        return ok(res.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveParagraphTags() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONObject paraTags = request.getJSONObject("paraTags");
        JSONObject finalJson = new JSONObject();
        Boolean status = false;
        try {
            Iterator<String> keys = paraTags.keys();
            while (keys.hasNext()){
                String paraId = keys.next();
                String tag = paraTags.getString(paraId);
                HashMap<String, String> aParaMap = new HashMap<>();
                aParaMap.put("tag", tag);
                lightHouseService.addAnewVertexproperty(paraId, aParaMap);
            }
            status = true;
            finalJson.put("status", status);
        } catch (Exception e){
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getParagraphTags() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONArray paraIds = request.getJSONArray("paraIds");
        JSONObject finalJson = new JSONObject();
        Boolean status = false;
        List<String> paraIdList = new ArrayList<String>();
        try {
            for (int i=0; i<paraIds.length(); i++){
                paraIdList.add(paraIds.getString(i));
            }
            String data = checkListServices.getParagraphsByParagraphid(paraIdList);
            status = true;
            finalJson.put("status", status);
            finalJson.put("data", new JSONObject(data));
        } catch (Exception e){
            e.printStackTrace();
        }
        return ok(finalJson.toString());
    }


    //DOCUMENT RELATED OPERATIONS
    public Result getAllTopics() {
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            ArrayList<HashMap<String, String>> res = lightHouseService.getAlltopic();
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getSubtopicsByTopicId() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            ArrayList<HashMap<String, String>> res = lightHouseService.getSubtopicsByTopicId(id, "SUBTOPIC");
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getSectionsBySubtopicId() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            ArrayList<HashMap<String, String>> res = lightHouseService.getSubtopicsByTopicId(id, "SECTION");
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getParagraphsBySectionId() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            ArrayList<HashMap<String, String>> res = lightHouseService.getParagraphBySectionId(id);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getParagraphsByConceptId() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            ArrayList<HashMap<String, String>> res = lightHouseService.getParagraphBySectionId(id);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    public Result getAllConcepts() {
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            ArrayList<HashMap<String, String>> res = lightHouseService.getAllConcepts();
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    public Result getAllBusinessSegments() {
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            ArrayList<HashMap<String, String>> res = lightHouseService.getAllBusinessSegments();
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    public Result getAllComponents() {
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            String vertexType = "COMPONENT";
            ArrayList<HashMap<String, String>> res = lightHouseService.getAllVertexesByType(vertexType);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    public Result getAllProducts() {
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            String vertexType = "PRODUCT";
            ArrayList<HashMap<String, String>> res = lightHouseService.getAllVertexesByType(vertexType);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getComponentTypesByParagraphIds() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONArray paraIds = request.getJSONArray("paraIds");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        ArrayList<String> req = new ArrayList<>();
        try {
            for(int i=0; i<paraIds.length(); i++){
                req.add(paraIds.getString(i));
            }
            HashMap<String, String> res = lightHouseService.getComponentTypesByParagraphIds(req);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveOrUpdateCheckList() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONObject checklistItem = request.getJSONObject("checklistItem");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        HashMap<String, Object> req = new HashMap<>();
        try {
            HashMap<String, Boolean> paragraphs = new Gson().fromJson(checklistItem.getJSONObject("paragraphs").toString(), new TypeToken<HashMap<String, Boolean>>() {}.getType());
            HashMap<String, Boolean> componentTypes = new Gson().fromJson(checklistItem.getJSONObject("componentTypes").toString(), new TypeToken<HashMap<String, Boolean>>() {}.getType());
            if(checklistItem.has("id"))
                req.put("DATA_ID", checklistItem.getString("id"));
            req.put("BODY_TEXT", checklistItem.getString("bodyText"));
            req.put("IS_MANDATORY", checklistItem.getBoolean("isMandatory"));
            req.put("STATE", "OPEN");
            req.put("ATTACHMENTINFO", "No");
            data = checkListServices.saveOrUpdateCheckList(req, paragraphs, componentTypes);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistsByParagraphIds() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONArray paraIds = request.getJSONArray("paraIds");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        ArrayList<String> req = new ArrayList<>();
        try{
            for(int i=0; i<paraIds.length(); i++){
                req.add(paraIds.getString(i));
            }
            ArrayList<HashMap<String, HashMap<String, String>>> res = transformChecklistWithTagsToViewModel(lightHouseService.getChecklistsByParagraphs(req));
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistsByConcept() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String req = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            ArrayList<HashMap<String, String>> res = transformChecklistToViewModel(lightHouseService.getChecklistByConcept(req));
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistsByComponent() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        ArrayList<String> req = new ArrayList<>();
        req.add(id);
        try{
            ArrayList<HashMap<String, String>> res = transformChecklistToViewModel(lightHouseService.getChecklistByComponent(req));
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistsByBussinessSegment() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        ArrayList<String> req = new ArrayList<>();
        req.add(id);
        try{
            ArrayList<HashMap<String, String>> res = transformChecklistToViewModel(lightHouseService.getChecklistByBusinessSegment(req));
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }
    private ArrayList<HashMap<String, HashMap<String, String>>> transformChecklistWithTagsToViewModel(ArrayList<HashMap<String, HashMap<String, String>>> inData){
        ArrayList<HashMap<String, HashMap<String, String>>> outData = new ArrayList<>();

        for(HashMap<String, HashMap<String, String>> curr : inData){
            HashMap<String, HashMap<String, String>> checklistWithTags = new HashMap<>();
            HashMap<String, String> checklist = curr.get("checklist");
            HashMap<String, String> aData = new HashMap<>();
            aData.put("id", checklist.get("DATA_ID"));
            aData.put("bodyText", checklist.get("BODY_TEXT"));
            aData.put("isMandatory", checklist.get("IS_MANDATORY"));
            aData.put("isChecked", checklist.get("IS_CHECKED"));
            checklistWithTags.put("checklist", aData);
            checklistWithTags.put("paragraphs", curr.get("paragraphs"));
            checklistWithTags.put("componentTypes", curr.get("componentTypes"));
            outData.add(checklistWithTags);
        }
        return outData;
    }
    private ArrayList<HashMap<String, String>> transformChecklistToViewModel(ArrayList<HashMap<String, String>> inData){
        ArrayList<HashMap<String, String>> outData = new ArrayList<>();
        for(HashMap<String, String> curr : inData)
        {
            HashMap<String, String> aData = new HashMap<>();
            aData.put("id", curr.get("DATA_ID"));
            aData.put("bodyText", curr.get("BODY_TEXT"));
            aData.put("isMandatory", curr.get("IS_MANDATORY"));
            aData.put("isChecked", curr.get("IS_CHECKED"));
            outData.add(aData);
        }
        return outData;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result removeChecklistById() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            data = checkListServices.removeCheckList(id);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result checklistDetailsByIds() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final JSONArray ids = request.getJSONArray("checklistIds");
        final String id = request.getString("id");
        final String type = request.getString("type");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            ArrayList<String> checklistIds = new Gson().fromJson(ids.toString(), new TypeToken<ArrayList<String>>() {}.getType());
            ArrayList<HashMap<String, String>> res = checkListServices.getChecklistDetailsForReport(checklistIds, id, type);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    public Result getParagraphCountsByTags() {
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try {
            HashMap<String, String> res = checkListServices.getParagraphTypeCounts();
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getRelatedComponentsByComponent() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            String filteredBY = "COMPONENT";
            List<HashMap<String, String>> res = checkListServices.getRelatedNodesByNodeID(id, filteredBY);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getRelatedBusinessSegentsByBusinessSegment() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            String filteredBY = "BUSINESSSEGMENT";
            List<HashMap<String, String>> res = checkListServices.getRelatedNodesByNodeID(id, filteredBY);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    public Result getCompliedAndNotCompliedChecklistCounts() {
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            HashMap<String, String> res = checkListServices.getCompliedAndNotCompliedCounts();
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistByParagraph() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        ArrayList<String> req = new ArrayList<>();
        req.add(id);
        try{
            ArrayList<HashMap<String, String>> res = transformChecklistToViewModel(lightHouseService.getChecklistsByParagraphIDs(req));
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistBySection() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        ArrayList<String> req = new ArrayList<>();
        req.add(id);
        try{
            ArrayList<HashMap<String, String>> res = transformChecklistToViewModel(lightHouseService.getChecklistsByRootNodeIDs(req));
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistBySubtopic() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        ArrayList<String> req = new ArrayList<>();
        req.add(id);
        try{
            ArrayList<HashMap<String, String>> res = transformChecklistToViewModel(lightHouseService.getChecklistsByRootNodeIDs(req));
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getChecklistByTopic() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        ArrayList<String> req = new ArrayList<>();
        req.add(id);
        try{
            ArrayList<HashMap<String, String>> res = transformChecklistToViewModel(lightHouseService.getChecklistsByRootNodeIDs(req));
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParserMaxLength.class)
    public Result uploadAttachmentByChecklistId() {
        final String json = request().body().asJson().toString();
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        HashMap<String, Object> req = new Gson().fromJson(json, new TypeToken<HashMap<String, Object>>() {}.getType());
        try{
            data = checkListServices.saveOrUpdateCheckListAttachment(req);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getAttachmentsByChecklistId() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            List<Map<String, String>> res = checkListServices.getCheckListAttachmentsByChecklistID(id);
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.writeValueAsString(res);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteAttachmentById() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            data = checkListServices.removeCheckListAttachment(id);
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }

    public Result downloadAttachmentById(String id) {
        try{
            List<Map<String, String>> res = checkListServices.getCheckListAttachmentById(id);
            String data = res.get(0).get("data");
            String mime = res.get(0).get("mime");
            String name = res.get(0).get("name");
            String temp = data.substring(data.indexOf(',') + 1);
            byte[] bytes = Base64.getDecoder().decode(temp);
            response().setContentType(mime);
            response().setHeader("Content-Disposition", "attachment; filename=" + name);
            return ok(bytes).as(mime);
        } catch (Exception e){
            e.printStackTrace();
            return ok("Failed to download file!!!").as("text/html");
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getCommentAttachmentById() {
        final String json = request().body().asJson().toString();
        final JSONObject request = new JSONObject(json);
        final String id = request.getString("id");
        JSONObject finalJson = new JSONObject();
        Boolean status = true;
        String data = null;
        try{
            List<Map<String, String>> res = checkListServices.getCheckListAttachmentById(id);
            data = res.get(0).get("data");
        } catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return ok(finalJson.toString());
    }
}
