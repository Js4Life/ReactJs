package com.parabole.ccar.application.controllers;

import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.platform.exceptions.AppException;
import com.parabole.ccar.platform.utils.AppUtils;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Created by Sagiruddin on 03-03-2016.
 */
public class AuthorizationController extends BaseController{

    // @Restrict({  @Group(CCAppConstants.ADMIN_AUTH), @Group(CCAppConstants.REGULATORY), @Group(CCAppConstants.ENTERPRISE_WORKING_GROUP), @Group(CCAppConstants.DATA_ADVISORY_COMMITTEE), @Group(CCAppConstants.ENTERPRISE_WORKING_GROUP), @Group(CCAppConstants.BUSINESS_SEGMENT_ALIGNED_WORKING_GROUP)})
    public Result getLandingDataBasedOnAuthorization() throws AppException {
        String user_role = session().get(CCAppConstants.ROLE);
        String jsonFileContent = null;
        if(user_role.equalsIgnoreCase(CCAppConstants.ADMIN_AUTH)){
            jsonFileContent = AppUtils.getFileContent("json/landingPage/adminLanding.json");
            response().setContentType("application/json");
        }
        else if(user_role.equalsIgnoreCase(CCAppConstants.REGULATORY)){
            jsonFileContent = AppUtils.getFileContent("json/landingPage/regulatoryLanding.json");
            response().setContentType("application/json");
        }else if(user_role.equalsIgnoreCase(CCAppConstants.DATA_ADVISORY_COMMITTEE) ||
                user_role.equalsIgnoreCase(CCAppConstants.ENTERPRISE_WORKING_GROUP) ||
                user_role.equalsIgnoreCase(CCAppConstants.BUSINESS_SEGMENT_ALIGNED_WORKING_GROUP)){
            jsonFileContent = AppUtils.getFileContent("json/landingPage/edmLanding.json");
            response().setContentType("application/json");
        }
        return Results.ok(jsonFileContent);
    }
}
