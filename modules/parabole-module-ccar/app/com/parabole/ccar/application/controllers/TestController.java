package com.parabole.ccar.application.controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.parabole.ccar.application.views.html.accessOk;
import com.parabole.ccar.platform.exceptions.AppException;
import com.parabole.ccar.platform.utils.AppUtils;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;



/**
 * Created by Sagiruddin on 2/26/2016.
 */

@Restrict({@Group("ADMIN"),
        @Group("EWG")})
public class TestController extends Controller {

    @SubjectPresent
    public Result testAction1() throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/testJson.json");
        response().setContentType("application/json");
        return Results.ok(jsonFileContent);
    }

    @SubjectNotPresent
    public Result testAction2() throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/testJson.json");
        response().setContentType("application/json");
        return Results.ok(jsonFileContent);
    }

    @Restrict({  @Group("ADMIN"), @Group("EWG")})
    public Result testAction3() throws AppException {
        final String jsonFileContent = AppUtils.getFileContent("json/testJson.json");
        response().setContentType("application/json");
        return Results.ok(jsonFileContent);
    }

    @Restrict(@Group({"!ADMIN"}))
    public F.Promise<Result> testAction4()
    {
        return F.Promise.promise(() -> ok(accessOk.render()));
    }

}
