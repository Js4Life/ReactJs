package com.parabole.feed.application.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.services.*;
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
    protected CoralConfigurationService coralConfigurationService;

    @Inject
    protected OctopusSemanticService octopusSemanticService;

    @Inject
    protected JenaTdbService jenaTdbService;

}
