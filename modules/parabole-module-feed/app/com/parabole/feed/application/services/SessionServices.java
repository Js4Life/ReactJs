package com.parabole.feed.application.services;

import com.parabole.feed.application.global.CCAppConstants;

import static play.mvc.Http.Context.Implicit.session;

/**
 * Created by Sagir on 09-08-2016.
 */
public class SessionServices {

    public static String gerUserIdFromSession(){

        String userid = session().get(CCAppConstants.USER_NAME);
        return userid;
    }

    public static String gerUserRoleFromSession(){

        String userRole = session().get(CCAppConstants.ROLE);
        return userRole;
    }
}
