// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ReasonerAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.parabole.cecl.platform.exceptions.AppException;
import com.parabole.cecl.platform.utils.AppUtils;

import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Manages all the Reasoner Graph Operations
 *
 * @author Subhasis Sanyal
 */
public class ReasonerAction extends BaseAction {

    public Result getFullReasonerGraph() throws Exception {
        return Results.ok(octopusSemanticService.getFullReasonerGraph().toJson());
    }

    public Result getFullUnaryReasonerGraph() throws Exception {
        return Results.ok(octopusSemanticService.getFullUnaryReasonerGraph().toJson());
    }

    public Result getFullBinaryReasonerGraph() throws Exception {
        return Results.ok(octopusSemanticService.getFullBinaryReasonerGraph().toJson());
    }

    public Result getFullBinaryReasonerGraphHavingSpecifiedEdges() throws Exception {
        return Results.ok(octopusSemanticService.getFullBinaryReasonerGraphHavingSpecifiedEdges().toJson());
    }
    
    public Result getJsonForvisualizationReasonerGraph() throws AppException {
    	 final String jsonFileContent = AppUtils.getFileContent("json/jsonForvisualizationReasonerGraphAll.json");
         response().setContentType("application/json");
         return Results.ok(jsonFileContent);
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result getJsonForvisualizationReasonerGraphById() throws AppException {
        final JsonNode json = request().body().asJson();
        final String ruleId = json.findPath("ruleId").asText();
   	 	final String jsonFileContent = AppUtils.getFileContent("json/jsonForvisualizationReasonerGraph"+ruleId+".json");
        response().setContentType("application/json");
        return Results.ok(jsonFileContent);
   }
}
