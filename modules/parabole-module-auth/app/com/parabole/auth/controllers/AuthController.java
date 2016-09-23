package com.parabole.auth.controllers;

import com.google.inject.Inject;
import com.parabole.auth.exceptions.AppException;
import com.parabole.auth.global.AuthConstants;
import com.parabole.auth.services.CoralUserService;
import com.parabole.platform.authorizations.models.UserModel;
import com.parabole.platform.authorizations.securities.AuthenticationManager;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;

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

    public static String getLoginD(){
        return session().get(AuthConstants.ROLE);
    }


    public Boolean login(String username, String password)  {
        // return ok(com.parabole.ccar.application.views.html.login.render());
        // final DynamicForm requestData = Form.form().bindFromRequest();
        final String userId = username;
        String role = null;
        String name = username;
        Boolean status = false;
        if (authenticationManager.authenticate(userId, password)) {

            try {
                role = coralUserService.getSpecificDocumentUsingIdAndColumnNameFromUserGroup(userId, AuthConstants.ATTR_DATABASE_GROUP_NAME_COLUMN_NAME);
                name = coralUserService.getSpecificDocumentUsingIdAndColumnNameFromUser(userId, AuthConstants.ATTR_DATABASE_USER_NAME_COLUMN_NAME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            session().put(AuthConstants.ROLE, role);
            session().put(AuthConstants.USER_ID, userId);
            session().put("userName", name);
            try {
                session().put(AuthConstants.USER_NAME, coralUserService.getSpecificDocumentUsingIdAndColumnName(userId, AuthConstants.ATTR_DATABASE_USER_NAME_COLUMN_NAME));
            } catch (Exception e) {
                e.printStackTrace();
            }

            UserModel.findByUserName(userId);
            status = true;
            return status;
        } else {
            return status;
        }
    }

}
