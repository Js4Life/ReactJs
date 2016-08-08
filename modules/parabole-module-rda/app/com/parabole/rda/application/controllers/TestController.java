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
package com.parabole.rda.application.controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.rda.application.global.RdaAppConstants;
import com.parabole.rda.platform.assimilation.DataSourceUtilsFactory;
import com.parabole.rda.platform.assimilation.OperationResult;
import com.parabole.rda.platform.assimilation.QueryResultTable;
import com.parabole.rda.platform.assimilation.ViewQueryRequest;
import com.parabole.rda.platform.assimilation.rdbms.DbModelUtils;
import com.parabole.rda.platform.exceptions.AppErrorCode;
import com.parabole.rda.platform.exceptions.AppException;
import com.parabole.rda.platform.utils.AppUtils;
import com.parabole.rda.platform.utils.EasyTreeUtils;
import com.parabole.rda.platform.utils.W2UIUtils;
import org.json.JSONObject;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Restrict({@Group("ADMIN")})
public class TestController extends BaseAction {

    @SubjectPresent
    public Result testAction1() throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/testJson.json");
        response().setContentType("application/json");
        return Results.ok(jsonFileContent);
    }

    @SubjectNotPresent
    public Result testAction2() throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/testJson.json");
        response().setContentType("application/json");
        return Results.ok(jsonFileContent);
    }

    @Restrict({  @Group("ADMIN")})
    public Result testAction3() throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/testJson.json");
        response().setContentType("application/json");
        return Results.ok(jsonFileContent);
    }

    @Restrict(@Group({"ADMIN"}))
    public Result testAction4()
    {
        return ok("okk !");
    }

}