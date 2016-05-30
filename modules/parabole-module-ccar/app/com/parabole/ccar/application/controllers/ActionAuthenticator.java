package com.parabole.ccar.application.controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import com.parabole.ccar.application.global.CCAppConstants;

public class ActionAuthenticator extends Security.Authenticator {

    @Override
    public String getUsername(final Context ctx) {
        return ctx.session().get(CCAppConstants.ATTR_DATABASE_USER_NAME);
    }

    @Override
    public Result onUnauthorized(final Context ctx) {
        ctx.session().clear();
        return redirect(com.parabole.ccar.application.controllers.routes.BaseController.login());
    }
    
}
