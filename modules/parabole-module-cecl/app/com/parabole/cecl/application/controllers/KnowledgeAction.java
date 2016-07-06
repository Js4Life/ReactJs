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
package com.parabole.cecl.application.controllers;

import com.parabole.cecl.platform.exceptions.AppException;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Manages all the Knowledge Graph Operations
 *
 * @author Subhasis Sanyal
 */
public class KnowledgeAction extends BaseAction {

    public Result getFullKnowledgeGraph() throws AppException {
        return Results.ok(octopusSemanticService.getFullKnowledgeGraph().toJson());
    }

    public Result getFullUnaryKnowledgeGraph() throws AppException {
        return Results.ok(octopusSemanticService.getFullUnaryKnowledgeGraph().toJson());
    }

    public Result getFullBinaryKnowledgeGraph() throws AppException {
        return Results.ok(octopusSemanticService.getFullBinaryKnowledgeGraph().toJson());
    }

    public Result getFullBinaryKnowledgeGraphHavingSpecifiedEdges() throws AppException {
        return Results.ok(octopusSemanticService.getFullBinaryKnowledgeGraphHavingSpecifiedEdges().toJson());
    }
}
