// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// Global.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.global;

import java.util.Properties;
import java.util.Set;

import com.parabole.rda.platform.utils.AppUtils;
import com.parabole.rda.platform.exceptions.AppException;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Play Framework Global Settings coordinator runs at OctopusAction start-up.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class Global extends GlobalSettings {

    private static final String AJAX_HEADER_KEY = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";

    @Override
    public void onStart(final Application app) {
        jdbcProperties();
    }

    @Override
    public Promise<Result> onError(final RequestHeader request, final Throwable throwable) {
        Logger.error("Global->onError() called");
        Logger.error("Request Header: " + request);
        Logger.error("Exception: ", throwable);
        final String ajaxHeader = request.getHeader(AJAX_HEADER_KEY);
        String errorData = null;

        if (AppUtils.compareValue(AJAX_HEADER_VALUE, ajaxHeader)) {
            Logger.error("This Request was AJAX Request");
            errorData = throwable.getMessage();
            // return Promise.<Result>
            // pure(Results.internalServerError(throwable.getMessage()));
        } else {
            Logger.error("This Request was Non-AJAX Request");
            return Promise.<Result> pure(Results.internalServerError(com.parabole.rda.application.views.html.errorpage.render(throwable.getMessage())));
        }

        return Promise.<Result> pure(Results.internalServerError(errorData));
    }

    private void jdbcProperties() {
        try {
            final Properties properties = AppUtils.loadProperties("drivers.properties");
            final Set<String> databases = properties.stringPropertyNames();
            for (final String database : databases) {
                final String dbDriver = properties.getProperty(database);
                Class.forName(dbDriver);
            }
        } catch (final AppException e) {
            throw new IllegalStateException("Database Driver Configuration File Not Found.");
        } catch (final ClassNotFoundException classEx) {
            Logger.error("Database Driver could not be loaded.", classEx);
            throw new IllegalStateException("Database Driver could not be loaded.");
        }
    }
}