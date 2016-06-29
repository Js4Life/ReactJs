// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// AppUser.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.securities;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.parabole.ccar.platform.BaseDTO;

/**
 * Application User.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class AppUser extends BaseDTO {

    private static final long serialVersionUID = -6343301279206981492L;

    private String userLoginname;
    private String userFullName;
    private String userEmail;
    private boolean isEnabled = true;
    private Timestamp lastLoginTime;
    private final Set<AppGroup> groups = new HashSet<AppGroup>();

    public String getUserLoginname() {
        return userLoginname;
    }

    public void setUserLoginname(final String userLoginname) {
        this.userLoginname = userLoginname;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(final String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(final String userEmail) {
        this.userEmail = userEmail;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(final Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Set<AppGroup> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    public void appendGroups(final Set<AppGroup> newGroups) {
        groups.addAll(newGroups);
    }

    public void addGroup(final AppGroup group) {
        groups.add(group);
    }

    public void removeGroup(final AppGroup group) {
        groups.remove(group);
    }

    public void clearGroups() {
        groups.clear();
    }
}
