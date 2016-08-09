package com.parabole.auth.controllers;

import com.google.inject.Inject;
import com.parabole.auth.services.CoralUserService;
import com.parabole.platform.authorizations.models.UserModel;
import com.parabole.platform.authorizations.securities.AuthenticationManager;
import play.mvc.Result;

import static com.parabole.feed.application.services.SessionServices.gerUserIdFromSession;
import static play.mvc.Http.Context.Implicit.session;
import static play.mvc.Results.ok;

/**
 * Created by Sagir on 09-08-2016.
 */
public class AuthController {

    @Inject
    protected AuthenticationManager authenticationManager;


    @Inject
    protected CoralUserService coralUserService;


    public Result login(String username, String password)  {
        // return ok(com.parabole.ccar.application.views.html.login.render());
        // final DynamicForm requestData = Form.form().bindFromRequest();
        final String userId = username;
        if (authenticationManager.authenticate(userId, password)) {
            final String role = coralUserService.getSpecificDocumentUsingIdAndColumnNameFromUserGroup(userId, CCAppConstants.ATTR_DATABASE_GROUP_NAME_COLUMN_NAME);
            session().put(CCAppConstants.ROLE, role);
            session().put(CCAppConstants.USER_ID, userId);
            session().put(CCAppConstants.USER_NAME, coralUserService.getSpecificDocumentUsingIdAndColumnName(userId, CCAppConstants.ATTR_DATABASE_USER_NAME_COLUMN_NAME));

            UserModel.findByUserName(userId);

            return ok(session().get(CCAppConstants.USER_NAME));
        } else {
            return ok("not authorised");
        }
    }

    public Result getSessionData() throws AppException, com.parabole.feed.application.exceptions.AppException {

        return ok(gerUserIdFromSession());
    }


}
