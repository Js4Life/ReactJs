// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// OctopusAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.application.controllers;

import com.parabole.cecl.application.global.RdaAppConstants;
import com.parabole.cecl.platform.exceptions.AppException;
import com.parabole.cecl.platform.utils.AppUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

/**
 * Play Framework Action Controller dedicated for Graph Db operations.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Security.Authenticated(ActionAuthenticator.class)
public class OctopusAction extends BaseAction {

    public Result getBaseNodes() throws AppException {
        response().setContentType(RdaAppConstants.MIME_JSON);
        final String outputJson = octopusSemanticService.getBaseVertices();
        final String jsonFileContent = AppUtils.getFileContent("json/baseNodeConfigurations.json");
        response().setContentType("application/json");
        return Results.ok(outputJson);
    }

    public Result getRelatedNodes(final Integer nodeId) throws AppException {
        response().setContentType(RdaAppConstants.MIME_JSON);
        final String outputJson = octopusSemanticService.getRelatedVertices(nodeId);
        return Results.ok(outputJson);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getImpactedNodes() throws AppException {
        final String inputJson = request().body().asJson().toString();
        final JSONObject inputJsonObject = new JSONObject(inputJson);
        final int vertexId = inputJsonObject.getInt("vertexId");
        final int level = inputJsonObject.getInt("level");
        final String outputJson = octopusImpactService.getImpactedVertices(vertexId, level);
        return Results.ok(outputJson);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result getImpactedVerticesEndToEndLevel() throws AppException {
        final String inputJson = request().body().asJson().toString();
        final JSONObject inputJsonObject = new JSONObject(inputJson);
        final int vertexId = inputJsonObject.getInt("vertexId");
        final int level = inputJsonObject.getInt("level");
        final String outputJson = octopusImpactService.getImpactedVerticesEndToEndLevel(vertexId, level);
        return Results.ok(outputJson);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result mergedConnectedGraphAndWeightedGraph() throws AppException {
        final String inputJson = request().body().asJson().toString();
        final JSONObject inputJsonObject = new JSONObject(inputJson);
        final int vertexId = inputJsonObject.getInt("vertexId");
        final int level = inputJsonObject.getInt("level");
        final String outputJson = octopusImpactService.mergedConnectedGraphAndWeightedGraph(vertexId, level);
        return Results.ok(outputJson);
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result getPathBetweenTwoNodesFromImpactedVertices() throws AppException {
        final String inputJson = request().body().asJson().toString();
        final JSONObject inputJsonObject = new JSONObject(inputJson);
        final Integer fromNode = inputJsonObject.getInt("fromNode");
        final int level = inputJsonObject.getInt("level");
        final Integer toNode = inputJsonObject.getInt("toNode");
        final Integer impactDirection = inputJsonObject.getInt("impactDirection");
        final String outputJson = octopusImpactService.getPathBetweenTwoNodesFromKnowledgeGraph(fromNode, level, toNode, impactDirection);
		System.out.println(outputJson.toString());        
		return Results.ok(outputJson.toString());
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result getImpactLevelForGivenNodes() throws AppException {
		System.out.println("getImpactLevelForGivenNodes START");
    	final String inputJson = request().body().asJson().toString();
    	final JSONObject inputJsonObject = new JSONObject(inputJson);
    	final Integer fromNode = inputJsonObject.getInt("rootNode");
		
    	final JSONArray toNodeList = inputJsonObject.getJSONArray("destinationNodes");
		System.out.println("from node " + fromNode + " to node " + toNodeList);
    	final String outputJson = octopusImpactService.getImpactLevelForGivenNodes(fromNode, toNodeList);
		System.out.println("getImpactLevelForGivenNodes END");
		System.out.println(outputJson.toString());
    	return Results.ok(outputJson.toString());
    }
}