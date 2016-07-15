package com.parabole.feed.platform.authorizations.models;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import com.parabole.feed.application.global.CCAppConstants;

import java.util.ArrayList;
import java.util.List;

import static play.mvc.Http.Context.Implicit.session;

/**
 * Created by Sagiruddin on 2/24/2016.
 */

public class UserModel implements Subject {

    public String userId;

    public List<UserRoles> roles;

    public List<UserPermisions> permissions;

    public UserModel(String userId, List<UserRoles> roles, List<UserPermisions> permissions) {
        this.userId = userId;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public List<? extends Role> getRoles()
    {
        System.out.println("userId = " + roles.toString());
        return roles;
    }

    @Override
    public List<? extends Permission> getPermissions()
    {
        return permissions;
    }

    @Override
    public String getIdentifier()
    {
        return userId;
    }

    public static Subject findByUserName(String userId) {
        List<UserRoles> roles  = new ArrayList<>();
        roles.add(UserRoles.findByName(session().get(CCAppConstants.ROLE)));
        return new UserModel(userId, roles, null);
    }
}
