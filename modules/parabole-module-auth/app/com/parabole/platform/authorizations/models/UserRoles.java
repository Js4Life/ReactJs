package com.parabole.platform.authorizations.models;

import be.objectify.deadbolt.java.models.Role;


/**
 * Created by Sagiruddin on 2/24/2016.
 */
public class UserRoles implements Role {

    public String name;

    public UserRoles(String name) {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public static UserRoles findByName(String name)
    {
        return new UserRoles(name);
    }
}
