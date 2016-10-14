// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// BaseAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.auth.global.AuthConstants;
import com.parabole.cecl.application.global.RdaAppConstants;
import com.parabole.cecl.application.services.BiotaServices;
import com.parabole.cecl.application.services.CoralUserService;
import com.parabole.cecl.platform.exceptions.AppException;
import com.parabole.cecl.platform.securities.AuthenticationManager;
import com.parabole.cecl.platform.utils.AppUtils;
import com.parabole.feed.application.services.*;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Configuration;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.parabole.auth.controllers.AuthController.getLoginD;

//import com.parabole.feed.services.ApplicationTimer;

/**
 * Play Framework Base Action Controller.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class BaseAction extends Controller {

    @Inject
    protected CoralConfigurationService coralConfigurationService;

    @Inject
    protected CoralUserService coralUserService;

    @Inject
    protected OctopusSemanticService octopusSemanticService;

    @Inject
    protected BiotaServices biotaServices;

    @Inject
    protected AuthenticationManager authenticationManager;

    @Inject
    protected CommonService commonService;

    @Inject
    protected Configuration configuration;


    public Result login() {
        return ok(com.parabole.cecl.application.views.html.login.render());
    }

    public Result dologin() throws Exception {
        if(session().get(AuthConstants.ROLE) != null)
            return index();
        else
            return ok("Please Login First");
    }

    public Result logout() {
        session().clear();
        return login();
    }

    public Result getHardCodedResponse(final String jsonFileName) throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/" + jsonFileName + ".json");
        response().setContentType("application/json");
        System.out.println(jsonFileContent);
        return Results.ok(jsonFileContent);
    }

    public Result getLogicalViewJson(final String viewName) throws Exception {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final List<Map<String, String>> cfgList = coralConfigurationService.getConfigurationByName(userId, viewName);
        JSONObject detailObj = null;
        if (cfgList.size() > 0) {
            final String details = cfgList.get(0).get(RdaAppConstants.ATTR_VIEWCREATION_DETAILS);
            detailObj = new JSONObject(details);
            detailObj = detailObj.getJSONObject("requestCfg");
        }
        response().setContentType("application/json");
        if (detailObj != null) {
            detailObj = biotaServices.showAggregatedView(userId, detailObj);
            return Results.ok(detailObj.toString());
        }
        return Results.ok("");
    }

    protected Result index() throws Exception {
        final String userName = session().get(AuthConstants.USER_ID);
        final String name = session().get("userName");
        final String role = session().get("role");
        String baseUrl = configuration.getString("application.baseUrl");
        System.out.println("baseUrl = " + configuration.toString());
        return ok(com.parabole.cecl.application.views.html.main.render(name, role, baseUrl));
    }

    protected boolean isAdmin() {
        final String userId = session().get(RdaAppConstants.USER_ID);
        return (userId == RdaAppConstants.ADMIN) ? true : false;
    }

    public Result getRegulations() throws Exception {
        String jsonFileContent = null;
        String role = session().get(AuthConstants.ROLE);
        JSONObject finalJson = new JSONObject();
        JSONArray data = new JSONArray();
        Boolean status = true;
        try {
            if(role != null) {
                jsonFileContent = AppUtils.getFileContent("json/regulations.json");
                final JSONArray jsonArray = new JSONArray(jsonFileContent);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    JSONArray roles = obj.getJSONArray("roles");
                    for (int j = 0; j < roles.length(); j++) {
                        if (roles.getString(j).equalsIgnoreCase(role)) {
                            obj.remove("roles");
                            data.put(obj);
                            break;
                        }
                    }
                }
            } else {
                status = false;
            }
        } catch (final com.parabole.cecl.platform.exceptions.AppException e) {
            status = false;
            e.printStackTrace();
        }
        finalJson.put("status", status).put("data", data);
        return Results.ok(finalJson.toString());
    }
}