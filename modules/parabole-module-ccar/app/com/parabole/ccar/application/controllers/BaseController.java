package com.parabole.ccar.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.application.services.CoralConfigurationService;
import com.parabole.ccar.application.services.CoralUserService;
import com.parabole.ccar.platform.exceptions.AppException;
import com.parabole.ccar.platform.securities.AuthenticationManager;
import com.parabole.ccar.platform.utils.AppUtils;
import play.Configuration;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;


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
    protected AuthenticationManager authenticationManager;

    @Inject
    protected CoralConfigurationService coralConfigurationService;


    @Inject
    Configuration configuration;

    public Result login() throws Exception {
        // return ok(com.parabole.ccar.application.views.html.login.render());
        // final DynamicForm requestData = Form.form().bindFromRequest();
        final String userId = "root";
        final String password = "admin";
        if (authenticationManager.authenticate(userId, password)) {
            final String role = coralUserService.getSpecificDocumentUsingIdAndColumnNameFromUserGroup(userId, CCAppConstants.ATTR_DATABASE_GROUP_NAME_COLUMN_NAME);
            session().put(CCAppConstants.ROLE, role);
            session().put(CCAppConstants.USER_ID, userId);
            session().put(CCAppConstants.USER_NAME, coralUserService.getSpecificDocumentUsingIdAndColumnName(userId, CCAppConstants.ATTR_DATABASE_USER_NAME_COLUMN_NAME));
            return index();
        } else {
            return ok(com.parabole.ccar.application.views.html.login.render());
        }
    }

    public Result dologin() throws Exception {
/*        final DynamicForm requestData = Form.form().bindFromRequest();
        //final String userId = requestData.get("userid");
        final String userId = "root";
        //final String password = requestData.get("password");
        final String password = "admin";
        if (authenticationManager.authenticate(userId, password)) {
            final String role = coralUserService.getSpecificDocumentUsingIdAndColumnNameFromUserGroup (userId, CCAppConstants.ATTR_DATABASE_GROUP_NAME_COLUMN_NAME);
            session().put(CCAppConstants.ROLE, role);
            session().put(CCAppConstants.USER_ID, userId);
            session().put(CCAppConstants.USER_NAME, coralUserService.getSpecificDocumentUsingIdAndColumnName(userId, CCAppConstants.ATTR_DATABASE_USER_NAME_COLUMN_NAME));
            return index();
        } else {
            return login();
        }*/


        if(session().get(CCAppConstants.ROLE) != null)
            return index();
        else
            return ok("Please Login First");
    }

    public Result logout() {
        session().clear();
        return ok(com.parabole.ccar.application.views.html.login.render());
    }

    public Result getHardCodedResponse(final String jsonFileName) throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/" + jsonFileName + ".json");
        response().setContentType("application/json");
        return Results.ok(jsonFileContent);
    }

    protected Result index() throws Exception    {
        final String userName = session().get(CCAppConstants.USER_NAME);
        final String userRole = session().get(CCAppConstants.ROLE);
        Logger.info("userName", userName);
        Logger.info("userRole", userRole);
        ObjectMapper objectM = new ObjectMapper();
        System.out.println(" = ---------------> " + request().path());
       // System.out.println("baseUrl = " + baseUrl);
        return ok(com.parabole.ccar.application.views.html.main.render(userName, userRole));
        //.map(user -> ok(index.render(user)));

    }

    /*	 protected Result index() {
	     final String userName = session().get(CCAppConstants.USER_NAME);
	     final String userRole = session().get(CCAppConstants.ROLE);
	     return ok(com.parabole.ccar.application.views.html.main.render(userName, userRole));
	 }*/

    protected boolean isAdmin() {
        final String userId = session().get(CCAppConstants.USER_ID);
        return (userId == CCAppConstants.ADMIN) ? true : false;
    }

}
