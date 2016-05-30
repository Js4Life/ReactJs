package com.parabole.ccar.application.controllers;

import com.google.inject.Inject;
import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.application.services.CoralConfigurationService;
import com.parabole.ccar.application.services.CoralUserService;
import com.parabole.ccar.platform.authorizations.models.UserModel;
import com.parabole.ccar.platform.exceptions.AppException;
import com.parabole.ccar.platform.securities.AuthenticationManager;
import com.parabole.ccar.platform.utils.AppUtils;
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


	public Result login() {
		return ok(com.parabole.ccar.application.views.html.login.render());
	}

	 public Result dologin() throws AppException {
	     final DynamicForm requestData = Form.form().bindFromRequest();
	     final String userId = requestData.get("userid");
	     final String password = requestData.get("password");
	     if (authenticationManager.authenticate(userId, password)) {
             String role = coralUserService.getSpecificDocumentUsingIdAndColumnNameFromUserGroup (userId, CCAppConstants.ATTR_DATABASE_GROUP_NAME_COLUMN_NAME);
             session().put(CCAppConstants.ROLE, role);
             session().put(CCAppConstants.USER_ID, userId);
	         session().put(CCAppConstants.USER_NAME, coralUserService.getSpecificDocumentUsingIdAndColumnName(userId, CCAppConstants.ATTR_DATABASE_USER_NAME_COLUMN_NAME));
	         return index();
	     } else {
	         return login();
	     }
	 }

	 public Result logout() {
	     session().clear();
	     return login();
	 }

	public Result getHardCodedResponse(final String jsonFileName) throws AppException {
		 final String jsonFileContent = AppUtils.getFileContent("json/" + jsonFileName + ".json");
		 response().setContentType("application/json");
		 return Results.ok(jsonFileContent);
	}

	protected Result index()
	{
		final String userName = session().get(CCAppConstants.USER_NAME);
		final String userRole = session().get(CCAppConstants.ROLE);
		Logger.info("userName", userName);
		Logger.info("userRole", userRole);
		UserModel.findByUserName(userName);
		return ok(com.parabole.ccar.application.views.html.main.render(userName, userRole));
				//.map(user -> ok(index.render(user)));

	}
	
/*	 protected Result index() {
	     final String userName = session().get(CCAppConstants.USER_NAME);
	     final String userRole = session().get(CCAppConstants.ROLE);
	     return ok(com.parabole.application.views.html.main.render(userName, userRole));
	 }*/
	
	 protected boolean isAdmin() {
	     final String userId = session().get(CCAppConstants.USER_ID);
	     return (userId == CCAppConstants.ADMIN) ? true : false;
	 }
		
}
