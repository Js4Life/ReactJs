// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ActionAuthenticator.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.controllers;

import com.parabole.rda.application.global.RdaAppConstants;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Play Authentication mechanism using Annotation.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class ActionAuthenticator extends Security.Authenticator {

    @Override
    public String getUsername(final Context ctx) {
        return ctx.session().get(RdaAppConstants.USER_ID);
    }

    @Override
    public Result onUnauthorized(final Context ctx) {
        ctx.session().clear();
        return redirect(com.parabole.rda.application.controllers.routes.BaseAction.login());
    }
    
}
