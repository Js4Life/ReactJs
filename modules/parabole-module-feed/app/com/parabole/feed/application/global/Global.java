/*
package com.parabole.feed.application.global;

import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.utils.AppUtils;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;

import java.util.Properties;
import java.util.Set;

*
 * Play Framework Global Settings coordinator runs at OctopusAction start-up.
 *
 * @author Subhasis Sanyal
 * @since v1.0


public class Global extends GlobalSettings {

    private static final String AJAX_HEADER_KEY = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";

    @Override
    public void onStart(final Application app) {
        try {
        	jdbcProperties();
            Class.forName("com.denodo.vdp.jdbc.Driver");
        } catch (final Exception appEx) {
            Logger.error("Ontology Parsing Error", appEx);
        }
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
        } else {
            Logger.error("This Request was Non-AJAX Request");
            return Promise.<Result> pure(Results.internalServerError(com.parabole.feed.application.views.html.errorpage.render(throwable.getMessage())));
        }
        return Promise.<Result> pure(Results.internalServerError(errorData));
    }
    

    private void jdbcProperties() throws com.parabole.feed.application.exceptions.AppException, AppException {
        try {
            final Properties properties = AppUtils.loadProperties("drivers.properties");
            final Set<String> databases = properties.stringPropertyNames();
            for (final String database : databases) {
                final String dbDriver = properties.getProperty(database);
                Class.forName(dbDriver);
            }
        } catch (final ClassNotFoundException classEx) {
            Logger.error("Database Driver could not be loaded.", classEx);
            throw new IllegalStateException("Database Driver could not be loaded.");
        }
    }
}
*/
