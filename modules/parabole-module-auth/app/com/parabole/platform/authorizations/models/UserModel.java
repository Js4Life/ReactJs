package com.parabole.platform.authorizations.models;

import be.objectify.deadbolt.java.models.Subject;
import be.objectify.deadbolt.java.models.Role;
import be.objectify.deadbolt.java.models.Permission;
import com.parabole.auth.global.AuthConstants;

import java.util.ArrayList;
import java.util.List;

import static play.mvc.Http.Context.Implicit.session;

/**
 * Created by Sagiruddin on 2/24/2016.
 */

public class UserModel implements Subject {
/*


    @Override
    public List<? extends Role> getRoles() {
        return null;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    public static Subject findByUserName(String userId) {
        List<UserRoles> roles  = new ArrayList<>();
        roles.add(UserRoles.findByName(session().get(AuthConstants.ROLE)));
        return new UserModel(userId, roles, null);
    }
*/

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
        System.out.println("ROLES = " + roles.toString());
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
        System.out.println("session().get(AuthConstants.ROLE) = " + session().get(AuthConstants.ROLE));
        roles.add(UserRoles.findByName(session().get(AuthConstants.ROLE)));
        return new UserModel(userId, roles, null);
    }
/*
    public static Subject find() {
        List<UserRoles> roles  = new ArrayList<>();
        roles.add(UserRoles.findByName("ADMIN"));
        return new UserModel("root", roles, null);
    }*/
}
