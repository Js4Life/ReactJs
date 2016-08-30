package com.parabole.feed.application.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.application.services.*;
import com.parabole.feed.platform.securities.AuthenticationManager;
import play.Configuration;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Base Controller for all the Base Actions
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */

public class BaseController extends Controller {

    @Inject
    protected CoralUserService coralUserService;

    @Inject
    protected CoralConfigurationService coralConfigurationService;

    @Inject
    protected OctopusSemanticService octopusSemanticService;

    @Inject
    protected JenaTdbService jenaTdbService;

    @Inject
    protected TaggingUtilitiesServices taggingUtilitiesServices;


    @Inject
    protected AuthenticationManager authenticationManager;



    @Inject
    Configuration configuration;


    public String login(String username, String password) throws AppException, com.parabole.feed.platform.exceptions.AppException {
        // return ok(com.parabole.ccar.application.views.html.login.render());
        // final DynamicForm requestData = Form.form().bindFromRequest();
        final String userId = username;
        if (authenticationManager.authenticate(userId, password)) {
            final String role = coralUserService.getSpecificDocumentUsingIdAndColumnNameFromUserGroup(userId, CCAppConstants.ATTR_DATABASE_GROUP_NAME_COLUMN_NAME);
            session().put(CCAppConstants.ROLE, role);
            session().put(CCAppConstants.USER_ID, userId);
            session().put(CCAppConstants.USER_NAME, coralUserService.getSpecificDocumentUsingIdAndColumnName(userId, CCAppConstants.ATTR_DATABASE_USER_NAME_COLUMN_NAME));

           // findByUserName

            String baseURLforFeed = configuration.getString("application.baseUrl");
            System.out.println("baseURLforFeed ========>>> " + baseURLforFeed);
            return session().get(CCAppConstants.USER_NAME);
        } else {
            return "not authorised";
        }
    }

}
