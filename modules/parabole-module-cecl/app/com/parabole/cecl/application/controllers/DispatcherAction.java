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
package com.parabole.cecl.application.controllers;

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

    public Result landing() {
        return ok(com.parabole.cecl.application.views.html.landing.render());
    }

    public Result home() {
        return ok(com.parabole.cecl.application.views.html.home.render());
    }

    public Result riskAggregation() {
        return ok(com.parabole.cecl.application.views.html.risk.render());
    }

    public Result impact() {
        return ok(com.parabole.cecl.application.views.html.impact.render());
    }

    public Result regulation() {
        return ok(com.parabole.cecl.application.views.html.regulation.render());
    }

    public Result checklistBuilder() {
        return ok(com.parabole.cecl.application.views.html.checklistBuilder.render());
    }
}