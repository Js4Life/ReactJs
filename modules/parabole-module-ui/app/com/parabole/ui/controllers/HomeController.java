package com.parabole.ui.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parabole.auth.controllers.AuthController;
import com.parabole.ui.views.html.index;
import com.parabole.ui.views.html.main;
import play.Configuration;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    AuthController authController;

    @Inject
    Configuration configuration;

    final static String appTitle = "Parabole Platform";
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result dologin() throws Exception {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String userId = requestData.get("userid");
        final String password = requestData.get("password");
        Boolean status = authController.login(userId, password);
        String baseUrl = configuration.getString("application.baseUrl");
        System.out.println("baseUrl = " + baseUrl);
        if(status)
            return ok(main.render(appTitle, baseUrl));
        else
            return index();
    }

    public Result main() {
        return ok(main.render(appTitle, ""));
    }

    public Result logout() {
        session().clear();
        return index();
    }

}
