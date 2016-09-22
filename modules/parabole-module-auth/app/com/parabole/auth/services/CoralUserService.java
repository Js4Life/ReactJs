// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// CoralUserService.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.auth.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.parabole.auth.exceptions.AppErrorCode;
import com.parabole.auth.exceptions.AppException;
import com.parabole.platform.authorizations.securities.AppUser;
import com.parabole.platform.authorizations.securities.PasswordManager;
import com.parabole.auth.utils.AppUtils;
import com.parabole.platform.authorizations.graphdb.Coral;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import play.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Management Services on Coral Configuration Database.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Singleton
public class CoralUserService {

    @Inject
    private Coral coral;

    public String getSpecificDocumentUsingIdAndColumnName(final String userId, String columnNameToFetch) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notBlank(userId, "'userId' cannot be empty!");
        final ODatabaseDocumentTx dbNoTx = coral.getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT "+columnNameToFetch+" FROM APP_USERS WHERE USER_ID = '" + userId + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            if (CollectionUtils.isNotEmpty(results)) {
                return results.get(0).field(columnNameToFetch);
            }
        } catch (final Exception ex) {
            Logger.error("Could not get "+columnNameToFetch+" for user: " + userId, ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            coral.closeDocDBConnection(dbNoTx);
        }
        return StringUtils.EMPTY;
    }

    public String getSpecificDocumentUsingIdAndColumnNameFromUser(final String userId, String columnNameToFetch) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notBlank(userId, "'userId' cannot be empty!");
        final ODatabaseDocumentTx dbNoTx = coral.getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT "+columnNameToFetch+" FROM APP_USERS WHERE USER_ID = '" + userId + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            if (CollectionUtils.isNotEmpty(results)) {
                return results.get(0).field(columnNameToFetch);
            }
        } catch (final Exception ex) {
            Logger.error("Could not get "+columnNameToFetch+" for user: " + userId, ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            coral.closeDocDBConnection(dbNoTx);
        }
        return StringUtils.EMPTY;
    }
    
public String getSpecificDocumentUsingIdAndColumnNameFromUserGroup(final String userId, String columnNameToFetch) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notBlank(userId, "'userId' cannot be empty!");
        final ODatabaseDocumentTx dbNoTx = coral.getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT "+columnNameToFetch+" FROM APP_USER_GROUPS WHERE USER_ID = '" + userId + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            if (CollectionUtils.isNotEmpty(results)) {
                return results.get(0).field(columnNameToFetch);
            }
        } catch (final Exception ex) {
            Logger.error("Could not get "+columnNameToFetch+" for user: " + userId, ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            coral.closeDocDBConnection(dbNoTx);
        }
        return StringUtils.EMPTY;
    }


    public void createUser(final AppUser user, final String password) throws AppException {
        Validate.notNull(user, "'user' cannot be null!");
        Validate.notBlank(password, "'password' cannot be empty!");
        Validate.notNull(user, "'user' cannot be null!");
        Validate.notBlank(password, "'password' cannot be blank!");
        final ODatabaseDocumentTx dbTx = coral.getDocDBConnectionTx();
        try {
            dbTx.begin();
            final ODocument document = new ODocument("APP_USERS");
            document.field("USER_ID", user.getUserLoginname());
            document.field("PASSWORD", PasswordManager.generateHashPassword(password));
            document.field("USER_NAME", user.getUserFullName());
            document.field("EMAIL", user.getUserEmail());
            document.field("ACTIVE", user.isEnabled());
            dbTx.save(document);
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not update user: " + user.getUserLoginname(), ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            coral.closeDocDBConnection(dbTx);
        }
    }

    public List<Map<String, String>> getAllUsers(final String userId) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        final ODatabaseDocumentTx dbNoTx = coral.getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT USER_ID, PASSWORD, USER_NAME, EMAIL, ACTIVE, LASTLOGIN FROM APP_USERS");
            final List<ODocument> results = dbNoTx.command(query).execute();
            for (final ODocument result : results) {
                final Map<String, String> outputMap = new HashMap<String, String>();
                final String userid = result.field("USER_ID");
                final String password = result.field("PASSWORD");
                final String name = result.field("USER_NAME");
                final String email = result.field("EMAIL");
                final Boolean active = result.field("ACTIVE");
                final Timestamp lastLogin = result.field("LASTLOGIN");
                outputMap.put("userid", userid);
                outputMap.put("password", password);
                outputMap.put("name", name);
                outputMap.put("email", email);
                outputMap.put("active", Boolean.toString(active));
                outputMap.put("lastLogin", AppUtils.convertlastLoginDate(lastLogin));
                outputList.add(outputMap);
            }
            return outputList;
        } catch (final Exception ex) {
            Logger.error("Could not retrieve All Users", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            coral.closeDocDBConnection(dbNoTx);
        }
    }

    public void updateUser(final AppUser user, final String password) throws AppException {
        Validate.notNull(user, "'user' cannot be null!");
        Validate.notBlank(password, "'password' cannot be blank!");
        final ODatabaseDocumentTx dbTx = coral.getDocDBConnectionTx();
        try {
            dbTx.begin();
            final ODocument document = new ODocument("APP_USERS");
            document.field("USER_ID", user.getUserLoginname());
            document.field("PASSWORD", PasswordManager.generateHashPassword(password));
            document.field("USER_NAME", user.getUserFullName());
            document.field("EMAIL", user.getUserEmail());
            document.field("ACTIVE", user.isEnabled());
            dbTx.save(document);
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not update user: " + user.getUserLoginname(), ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            coral.closeDocDBConnection(dbTx);
        }
    }

    public void deleteUser(final String userId) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be blank!");
        coral.executeUpdate("DELETE FROM APP_USERS WHERE USER_ID = '" + userId + "'");
    }

}
