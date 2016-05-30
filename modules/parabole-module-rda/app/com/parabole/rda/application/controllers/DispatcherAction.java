// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// DispatcherAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.controllers;

import play.mvc.Result;
import play.mvc.Security;

/**
 * Play Framework Action Controller dedicated for UI Controller for direct
 * front-end methods.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Security.Authenticated(ActionAuthenticator.class)
public class DispatcherAction extends BaseAction {

    public Result jsonHTTP() {
        return ok(com.parabole.rda.application.views.html.JsonHTTP.render());
    }

    public Result landing() {
        return ok(com.parabole.rda.application.views.html.landing.render());
    }

    public Result riskAggregate() {
        return ok(com.parabole.rda.application.views.html.aggregate.render());
    }

    public Result aggregateGroup() {
        return ok(com.parabole.rda.application.views.html.aggregateGroup.render());
    }

    public Result home() {
        return ok(com.parabole.rda.application.views.html.dashboard.render());
    }

    public Result riskAggregation() {
        return ok(com.parabole.rda.application.views.html.risk.render());
    }
    
    public Result dashboardProfile() {
        return ok(com.parabole.rda.application.views.html.dashboardProfile.render());
    }
    
    public Result status() {
        return ok(com.parabole.rda.application.views.html.status.render());
    }
    
    public Result schedule() {
        return ok(com.parabole.rda.application.views.html.schedule.render());
    }

    public Result gapdetails() {
        return ok(com.parabole.rda.application.views.html.gapdetails.render());
    }
    
    public Result view() {
        return ok(com.parabole.rda.application.views.html.graph.render());
    }

    public Result graph() {
        return ok(com.parabole.rda.application.views.html.graph.render());
    }

    public Result chart() {
        return ok(com.parabole.rda.application.views.html.chart.render());
    }

    public Result reportBuilder() {
        return ok(com.parabole.rda.application.views.html.report.render());
    }

    public Result pageBuilder() {
        return ok(com.parabole.rda.application.views.html.pagebuilder.render());
    }

    public Result layoutBuilder() {
        return ok(com.parabole.rda.application.views.html.layoutbuilder.render());
    }

    public Result mapDatasource() {
        return ok(com.parabole.rda.application.views.html.mapdatasource.render());
    }

    public Result mapDatasourceRelation() {
        return ok(com.parabole.rda.application.views.html.mapdatasourcerelation.render());
    }

    public Result mapDatasourceView() {
        return ok(com.parabole.rda.application.views.html.mapdatasourceview.render());
    }

    public Result userBuilder() {
        return ok(com.parabole.rda.application.views.html.user.render());
    }

    public Result userDetail() {
        return ok(com.parabole.rda.application.views.html.userdetail.render());
    }

    public Result userGroupDetail() {
        return ok(com.parabole.rda.application.views.html.usergroupdetail.render());
    }

    public Result datasourceBuilder() {
        return ok(com.parabole.rda.application.views.html.datasourcebuilder.render());
    }

    public Result reportPreview() {
        return ok(com.parabole.rda.application.views.html.reportpreview.render());
    }

    public Result aggregateDbView() {
        return ok(com.parabole.rda.application.views.html.aggregatedbview.render());
    }

    public Result logicalView() {
        return ok(com.parabole.rda.application.views.html.logicalview.render());
    }

    public Result combinedView() {
        return ok(com.parabole.rda.application.views.html.combinedview.render());
    }

    public Result simulatorView() {
        return ok(com.parabole.rda.application.views.html.simulator.render());
    }

    public Result simulatorGraphView() {
        return ok(com.parabole.rda.application.views.html.simulatorgraph.render());
    }

    public Result newsimulatorView() {
        return ok(com.parabole.rda.application.views.html.newsimulator.render());
    }

    public Result newsimulatorGraphView() {
        return ok(com.parabole.rda.application.views.html.newsimulatorgraph.render());
    }

    public Result glossary() {
        return ok(com.parabole.rda.application.views.html.glossary.render());
    }

    public Result model() {
        return ok(com.parabole.rda.application.views.html.model.render());
    }

    public Result aggregator() {
    	return ok(com.parabole.rda.application.views.html.aggregator.render());
    }
}