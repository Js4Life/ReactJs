package com.parabole.feed.application.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.services.CoralConfigurationService;
import com.parabole.feed.application.services.CoralUserService;
import com.parabole.feed.platform.securities.AuthenticationManager;
import play.mvc.Controller;

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

}
