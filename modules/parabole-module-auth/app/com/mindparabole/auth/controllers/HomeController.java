package com.mindparabole.auth.controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

     public Result login() {
        return ok(com.mindparabole.auth.views.html.login.render());
    }

}
