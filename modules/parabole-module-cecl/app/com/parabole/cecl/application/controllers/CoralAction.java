// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// CoralAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.cecl.application.global.RdaAppConstants;
import com.parabole.cecl.platform.assimilation.DataSourceUtilsFactory;
import com.parabole.cecl.platform.assimilation.OperationResult;
import com.parabole.cecl.platform.assimilation.QueryResultTable;
import com.parabole.cecl.platform.assimilation.ViewQueryRequest;
import com.parabole.cecl.platform.assimilation.rdbms.DbModelUtils;
import com.parabole.cecl.platform.exceptions.AppErrorCode;
import com.parabole.cecl.platform.exceptions.AppException;
import com.parabole.cecl.platform.utils.AppUtils;
import com.parabole.cecl.platform.utils.EasyTreeUtils;
import com.parabole.cecl.platform.utils.W2UIUtils;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

import java.io.IOException;
import java.util.*;

/**
 * Play Framework Action Controller dedicated for User Configuration and
 * Preferences related functionality.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Security.Authenticated(ActionAuthenticator.class)
public class CoralAction extends BaseAction {

    @Inject
    protected DbModelUtils dbModelUtils;

    public Result getAggregateRootName() throws AppException {
        final JsonNode json = request().body().asJson();
        final int nodeId = json.findPath("id").asInt();
        final String aggregateRoot = octopusSemanticService.findBaseVertexOf(nodeId);
        return Results.ok(aggregateRoot);
    }

    public Result saveBaseNodeConfiguration() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final int cfgId = coralConfigurationService.saveRootNodeMapping(userId, json, RdaAppConstants.ConfigurationType.BASENODE_CFG);
        return Results.ok(String.valueOf(cfgId));
    }

    public Result getBaseNodesMapping() throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        return Results.ok(coralConfigurationService.getAllRootNodesMapping(userId).toString());
    }

    public Result saveDataNodeConfiguration() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final int cfgId = coralConfigurationService.saveRootNodeMapping(userId, json, RdaAppConstants.ConfigurationType.DATANODE_CFG);
        return Results.ok(String.valueOf(cfgId));
    }

    public Result getConfiguration(final Integer ConfigarationId) throws AppException {
        final String ConfigurationData = coralConfigurationService.getConfigurationDetail(ConfigarationId);
        return Results.ok(Json.toJson(ConfigurationData));
    }

    public Result getConfigurationDetailWithnodeinfo(final Integer ConfigarationId) throws AppException {

        final String jsonFileContent = AppUtils.getFileContent("json/assignment.json");

        response().setContentType("application/json");
        final JSONObject assignment = new JSONObject(jsonFileContent);
        final String ConfigurationData = coralConfigurationService.getConfigurationDetailWithnodeinfo(ConfigarationId, assignment);
        return Results.ok(Json.toJson(ConfigurationData));
    }

    public Result getConfigurationNames(final String ConfigarationType) throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final Map<Integer, String> outputMap = coralConfigurationService.getConfigurationNames(userId, ConfigarationType);
        return Results.ok(Json.toJson(outputMap));
    }

    public Result getAllConfigurationDetailsByType(final String ConfigarationType) throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final List<Map<String, String>> outputMap = coralConfigurationService.getConfigurationByUserId(userId, ConfigarationType);
        return Results.ok(Json.toJson(outputMap));
    }

    public Result getConfigurationDetailsByName(final String ConfigarationName) throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final List<Map<String, String>> outputMap = coralConfigurationService.getConfigurationByName(userId, ConfigarationName);
        return Results.ok(Json.toJson(outputMap));
    }

    public Result getAggregateConfigurationDetailsByName(final String AggregateConfigarationName) throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final StringBuffer configurationName = new StringBuffer(AggregateConfigarationName);
        final List<Map<String, String>> outputMap = coralConfigurationService.getConfigurationByName(userId, configurationName.toString());
        return Results.ok(Json.toJson(outputMap));
    }

    public Result deleteConfigurationDetailsByName(final String configarationName) throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        coralConfigurationService.deleteConfigurationDetailsByName(userId, configarationName.toString());
        return Results.ok(Json.toJson(true));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteAggregate() throws AppException {
        final String aggrName = request().body().asJson().findPath("name").asText();
        final String userId = session().get(RdaAppConstants.USER_ID);
        coralConfigurationService.deleteAggergate(userId, aggrName, true);
        return Results.ok(Json.toJson(true));
    }

    public Result getAdminData(final String ConfigarationType) throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        if (isAdmin()) {
            final List<Map<String, String>> outputMap = coralConfigurationService.getConfigurationByUserId(userId, ConfigarationType);
            return Results.ok(Json.toJson(outputMap));
        } else {
            throw new AppException(AppErrorCode.SECURITY_EXCEPTION);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getDBViewsOfANode() throws AppException {
        final JsonNode json = request().body().asJson();
        final Integer nodeId = json.findPath(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME).asInt();
        final String userId = session().get(RdaAppConstants.USER_ID);
        return Results.ok(Json.toJson(coralConfigurationService.getDBViewsForANode(userId, nodeId)));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getConfigurationDetailsByPartialNames() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final String cfgName = json.textValue();
        final List<Map<String, String>> tmpList = coralConfigurationService.getConfigurationByPartialName(userId, cfgName);
        return Results.ok(Json.toJson(tmpList));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getConfigurationDetailsByMultinames(final String AggregateConfigarationName) throws AppException {
        final JsonNode json = request().body().asJson();
        final List<Map<String, String>> multilists = new ArrayList<Map<String, String>>();
        final String userId = session().get(RdaAppConstants.USER_ID);
        for (final JsonNode jsonNode : json) {
            final String cfgName = jsonNode.textValue();
            final List<Map<String, String>> tmpList = coralConfigurationService.getConfigurationByName(userId, cfgName);
            if (tmpList.size() > 0) {
                multilists.add(tmpList.get(0));
            }
        }
        return Results.ok(Json.toJson(multilists));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getConfigurationDetailsByPartialName(final String AggregateConfigarationName) throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final String cfgName = json.textValue();
        final List<Map<String, String>> tmpList = coralConfigurationService.getConfigurationByPartialName(userId, cfgName);
        return Results.ok(Json.toJson(tmpList));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveAdminData() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final Integer configurationId = coralConfigurationService.saveConfiguration(userId, json, RdaAppConstants.ConfigurationType.ADMINDATA);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveReport() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final Integer configurationId = coralConfigurationService.saveConfiguration(userId, json, RdaAppConstants.ConfigurationType.REPORT);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveDbViewConfiguration() throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final JsonNode json = request().body().asJson();
        final Integer configurationId = coralConfigurationService.saveConfiguration(userId, json, RdaAppConstants.ConfigurationType.DBVIEW);
        biotaServices.createView(new JSONObject(json.get(RdaAppConstants.ATTR_VIEWCREATION_DETAILS).textValue()));
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    public Result createLink() throws AppException {
        return Results.ok(Json.toJson("id"));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveLogicalViewOne() throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final JsonNode json = request().body().asJson();
        ViewQueryRequest viewQuery = null;
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String logicalViewCfgName = json.findPath(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME).textValue();
        try {
            final String viewQueryJsonStr = json.findPath("details").textValue();
            viewQuery = mapper.readValue(viewQueryJsonStr, ViewQueryRequest.class);
        } catch (final IOException ioEx) {
            Logger.error("IOException", ioEx);
        }
        biotaServices.updateLogicalWithDB(userId, viewQuery);
        final JsonNode updatedViewQueryJson = Json.toJson(viewQuery);
        final Integer configurationId = coralConfigurationService.saveConfiguration(userId, logicalViewCfgName, updatedViewQueryJson.toString(), RdaAppConstants.ConfigurationType.LOGICAL_VIEW_ONE, null);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveAggregateMapping() throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final JsonNode json = request().body().asJson();
        final Integer configurationId = coralConfigurationService.saveAggregateMapping(userId, json, RdaAppConstants.ConfigurationType.AGGREGATEMAPPING);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveAggregation() throws AppException {
        final JsonNode json = request().body().asJson();
        final String jsonFileContent = AppUtils.getFileContent("json/assignment.json");
        JSONObject assignment = new JSONObject(jsonFileContent);
        final String userId = session().get(RdaAppConstants.USER_ID);
        final Integer configurationId = coralConfigurationService.saveConfigurationWithAssignment(userId, json, assignment, RdaAppConstants.ConfigurationType.AGGREGATION);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveNodeMap() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        for (final JsonNode objNode : json) {
            coralConfigurationService.saveConfiguration(userId, objNode, RdaAppConstants.ConfigurationType.NODEMAP);
        }
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(true));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveDataSource() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final Integer configurationId = coralConfigurationService.saveConfiguration(userId, json, RdaAppConstants.ConfigurationType.DATASOURCE);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveCombinedView() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final Integer configurationId = coralConfigurationService.saveConfiguration(userId, json, RdaAppConstants.ConfigurationType.COMBINED_VIEW);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result dataSourceLookUp() throws Exception {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final JsonNode jsonNode = request().body().asJson();
        final String dsName = jsonNode.findPath(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME).textValue();
        final List<Map<String, String>> cfgDetailsList = coralConfigurationService.getConfigurationByName(userId, dsName);
        if (cfgDetailsList.size() == 0) {
            return Results.ok("");
        }
        final Map<String, String> cfgDetails = cfgDetailsList.get(0);
        final JSONObject ret = DataSourceUtilsFactory.getDataSourceUtils(cfgDetails).exploreDataSource();
        return Results.ok(ret.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result connectionChecker() throws AppException {
        final JsonNode cardinalsJson = request().body().asJson();
        final String dbDriver = cardinalsJson.findPath(RdaAppConstants.ATTR_DATABASE_DRIVER_NAME).textValue();
        String dbUrl = cardinalsJson.findPath(RdaAppConstants.ATTR_DATABASE_SERVER_URL).textValue();
        dbUrl = dbUrl.replace("&amp;", "&");
        final String dbUser = cardinalsJson.findPath(RdaAppConstants.ATTR_DATABASE_USER_NAME).textValue();
        final String dbPassword = cardinalsJson.findPath(RdaAppConstants.ATTR_DATABASE_PASSWORD).textValue();
        final OperationResult result = dbModelUtils.isValidCardinals(dbDriver, dbUrl, dbUser, dbPassword);
        return Results.ok(Json.toJson(result));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteConfiguration() throws AppException {
        final JsonNode json = request().body().asJson();
        final Integer cfgId = json.findPath(RdaAppConstants.RDA_CFG_ID).asInt();
        coralConfigurationService.deleteConfiguration(cfgId);
        return Results.ok(Json.toJson(true));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteMultipleConfig() throws AppException {
        final JsonNode json = request().body().asJson();
        final Integer cfgId = json.findPath(RdaAppConstants.RDA_CFG_ID).asInt();
        coralConfigurationService.deleteConfiguration(cfgId);
        return Results.ok(Json.toJson(true));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result fetchView() throws AppException {
        final JsonNode queryData = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final String dsName = queryData.findPath(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME).textValue();
        final List<Map<String, String>> cfgDetailsList = coralConfigurationService.getConfigurationByName(userId, dsName);
        // final String query = queryData.findValue("details").asText();
        System.out.println("query:" + queryData);
        // Logger.info("query :"+ query);
        final Map<String, Object> ret = new HashMap<String, Object>();
        try {
            final QueryResultTable resultTable = DataSourceUtilsFactory.fetchView(userId, queryData.toString(), cfgDetailsList.get(0));
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

    @BodyParser.Of(BodyParser.Json.class)
    public Result getDataFromLogicalView() throws AppException {
        final JsonNode queryData = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final String viewNameLevelTwo = queryData.findPath("viewName").textValue();
        final ViewQueryRequest viewQuery = DataSourceUtilsFactory.deserializeViewQueryRequest(userId, viewNameLevelTwo, coralConfigurationService);
        final JSONObject retObj = biotaServices.getDataFromLogicalView(userId, viewQuery);
        final boolean hasLogical = false;// retObj.getBoolean("HASLOGICAL");
        final JSONObject w2CfgJson = new JSONObject();
        w2CfgJson.put("tree", false);
        if (!hasLogical) {
            w2CfgJson.put(RdaAppConstants.ATTR_VIEWCREATION_DATA, new W2UIUtils().createConfigObject(retObj, viewNameLevelTwo));
        } else {
            w2CfgJson.put("tree", true);
            w2CfgJson.put(RdaAppConstants.ATTR_VIEWCREATION_DATA, new EasyTreeUtils().createConfigObject(retObj));
        }
        w2CfgJson.put("status", true);
        return Results.ok(w2CfgJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getCombinedLogicalView() throws AppException {
        final String jsonStr = request().body().asJson().toString();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final JSONObject jsonObj = new JSONObject(jsonStr);
        final JSONObject retObj = biotaServices.showAggregatedView(userId, jsonObj);
        final JSONObject w2CfgJson = new JSONObject();
        w2CfgJson.put(RdaAppConstants.ATTR_VIEWCREATION_DATA, new EasyTreeUtils().createConfigObject(retObj));
        w2CfgJson.put("status", true);
        return Results.ok(w2CfgJson.toString());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result fetchNoSQLViewData() throws AppException {
        final JsonNode queryData = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final String edgeName = queryData.findPath("edgeName").textValue();
        final QueryResultTable dataToDump = DataSourceUtilsFactory.getViewDataFromEdgeName(userId, edgeName, coralConfigurationService);
        final Map<String, Object> ret = DataSourceUtilsFactory.dumpToDocumentDb(dataToDump, biotaServices);
        return Results.ok(Json.toJson(ret));
    }

    public Result getLogicalViewsByNodeId() throws AppException {
        final JsonNode queryData = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final int nodeId = queryData.findPath("id").asInt();
        final String aggrName = queryData.findPath("aggrName").textValue();
        final List<Map<String, String>> views = biotaServices.getAggregatedViewsForNode(userId, nodeId, aggrName);
        return Results.ok(Json.toJson(views));
    }

    public Result getAllLogicalViewsByNodeId() throws AppException {
        final JsonNode queryData = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final int nodeId = queryData.findPath("id").asInt();
        final String nodeName = queryData.findPath("nodeName").textValue();
        final List<Map<String, String>> views = biotaServices.getAllAggregatedViewsForNode(userId, nodeId, nodeName);
        if (views != null) {
            return Results.ok(Json.toJson(views));
        } else {
            return Results.ok("[]");
        }
    }

    public Result getBaseNodeConfig() throws AppException {
        final JsonNode queryData = request().body().asJson();
        final Integer nodeId = queryData.findPath("nodeId").intValue();
        final String aggrRoot = octopusSemanticService.findBaseVertexOf(nodeId);
        final String userId = session().get(RdaAppConstants.USER_ID);
        final List<Map<String, String>> outputMapList = coralConfigurationService.getConfigurationByName(userId, aggrRoot.concat(RdaAppConstants.BASENODEMAPPING_SUFFIX));
        String retStr = "";
        if (outputMapList.size() > 0) {
            retStr = outputMapList.get(0).get(RdaAppConstants.ATTR_VIEWCREATION_DETAILS);
        }
        return Results.ok(retStr);
    }

    public Result getCombinedViews() throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final List<Map<String, String>> outputMap = coralConfigurationService.getConfigurationByUserId(userId, RdaAppConstants.ConfigurationType.COMBINED_VIEW.toString());
        return Results.ok(Json.toJson(outputMap));
    }

    @BodyParser.Of(value = BodyParser.Json.class)
    public Result saveImageData() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userId = session().get(RdaAppConstants.USER_ID);
        final Integer configurationId = coralConfigurationService.saveConfiguration(userId, json, RdaAppConstants.ConfigurationType.IMPACT_GRAPH_IMAGE);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return Results.ok(Json.toJson(configurationId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result saveConceptTags() throws AppException {
        final JsonNode queryData = request().body().asJson();
        final String nodeId = coralConfigurationService.saveConceptTags(queryData);
        return Results.ok(Json.toJson(nodeId));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getAllConceptByTag() throws AppException {
        final JsonNode queryData = request().body().asJson();
        final String tag = queryData.get("tag").toString();
        final Map<Integer, String> nodeId = coralConfigurationService.getAllConceptByTag(tag);
        return Results.ok(Json.toJson(nodeId.toString()));
    }
}