// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// SystemAdminAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Security;
import com.fasterxml.jackson.databind.JsonNode;
import com.parabole.rda.application.global.RdaAppConstants;
import com.parabole.rda.platform.exceptions.AppErrorCode;
import com.parabole.rda.platform.exceptions.AppException;
import com.parabole.rda.platform.securities.AppUser;

/**
 * Play Framework Action Controller dedicated for System Administration
 * functionality like User Management.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Security.Authenticated(ActionAuthenticator.class)
public class SystemAdminAction extends BaseAction {

    public Result downloadLogfile() throws AppException {
        try {
            response().setContentType(RdaAppConstants.MIME_LOG);
            response().setHeader("Content-Disposition", "attachment;filename=application.log");
            final String logFilePath = Play.application().path().getAbsolutePath() + "/logs/application.log";
            return ok(FileUtils.readFileToString(new File(logFilePath)));
        } catch (final IOException ioEx) {
            Logger.error("Log File download error", ioEx);
            throw new AppException(AppErrorCode.FILE_NOT_FOUND);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result createUser() throws AppException {
        final JsonNode json = request().body().asJson();
        final String userLoginname = json.findPath("userid").textValue();
        final String userFullName = json.findPath("name").textValue();
        final String userEmail = json.findPath("email").textValue();
        final Boolean isEnabled = json.findPath("active").asBoolean();
        final String password = json.findPath("password").textValue();
        final AppUser user = new AppUser();
        user.setUserLoginname(userLoginname);
        user.setUserFullName(userFullName);
        user.setUserEmail(userEmail);
        user.setEnabled(isEnabled);
        coralUserService.createUser(user, password);
        response().setContentType(RdaAppConstants.MIME_JSON);
        return ok(Json.toJson(user));
    }

    public Result deleteUser(final String userId) throws AppException {
        coralUserService.deleteUser(userId);
        return ok("true");
    }

    public Result getAllUsers() throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final List<Map<String, String>> outputMap = coralUserService.getAllUsers(userId);
        return ok(Json.toJson(outputMap));
    }

}