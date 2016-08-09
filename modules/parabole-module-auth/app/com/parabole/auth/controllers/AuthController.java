package com.parabole.auth.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.controllers.BaseController;
import com.parabole.feed.platform.exceptions.AppException;
import play.mvc.Result;

import static com.parabole.feed.application.services.SessionServices.gerUserIdFromSession;
import static play.mvc.Http.Context.Implicit.session;
import static play.mvc.Results.ok;

/**
 * Created by Sagir on 09-08-2016.
 */
public class AuthController {

    @Inject
    BaseController baseController;

    public Result getSessionData() throws AppException, com.parabole.feed.application.exceptions.AppException {

        return ok(gerUserIdFromSession());
    }


}
