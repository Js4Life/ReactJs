// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// Anchor.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.graphdb;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.exceptions.AppErrorCode;
import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import play.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Anchor is Application Configuration Graph-Database.
 *
 * @author Sagir
 * @since v1.0
 */
@Singleton
public class Anchor extends GraphDb {

    public Anchor() {
        final String graphDbUrl = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".anchor.graphdb.url");
        final String graphDbUser = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".anchor.graphdb.user");
        final String graphDbPassword = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".anchor.graphdb.password");
        final Integer graphDbPoolMinSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".anchor.graphdb.pool.min");
        final Integer graphDbPoolMaxSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".anchor.graphdb.pool.max");
        this.orientGraphFactory = new OrientGraphFactory(graphDbUrl, graphDbUser, graphDbPassword).setupPool(graphDbPoolMinSize, graphDbPoolMaxSize);
    }

    public int generateId(final String counterKeyName) throws AppException {
        Validate.notBlank(counterKeyName, "'counterKeyName' cannot be empty!");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            dbTx.begin();
            final OCommandSQL query = new OCommandSQL("UPDATE " + CCAppConstants.RDA_COUNTER_TABLE + " INCREMENT ID_VALUE = 1 RETURN AFTER @this WHERE ID_NAME = '" + counterKeyName + "'");
            final List<ODocument> results = dbTx.command(query).execute();
            dbTx.commit();
            if (CollectionUtils.isNotEmpty(results)) {
                return (Integer) results.get(0).field("ID_VALUE");
            }
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not generate Id for key ANCHOR [" + counterKeyName + "]", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
        Logger.error("Found schema error while generating Id for key ANCHOR [" + counterKeyName + "]");
        throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
    }


    public Integer saveConfiguration(final String conceptName, final String paragraphs, final String tags) throws AppException {
        final Integer configurationId = generateId(CCAppConstants.RDA_USER_CONFIGS);
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("APP_CONCEPTS_ID", configurationId);
        dataMap.put("CONCEPT_NAME", conceptName);
        dataMap.put("PARAGRAPHS", paragraphs);
        dataMap.put("TAGGS", tags);
        save(CCAppConstants.RDA_USER_CONFIGS, dataMap);
        return configurationId;
    }

    public void save(final String configurationObjectClass, final Map<String, Object> configurationDataMap) throws AppException {
        Validate.notBlank(configurationObjectClass, "'configurationObjectClass' cannot be empty!");
        Validate.notEmpty(configurationDataMap, "'configurationDataMap' cannot be empty!");
        boolean isNew = true;
        final String cfgname = (String) configurationDataMap.get("CFG_NAME");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            dbTx.begin();
            ODocument document = null;
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM " + configurationObjectClass + " WHERE CFG_NAME = '" + cfgname + "'");
            final List<ODocument> results = dbTx.command(query).execute();
            if (results.size() > 0) {
                document = results.get(0);
                isNew = false;
            } else {
                document = new ODocument(configurationObjectClass);
            }
            for (final Map.Entry<String, Object> entry : configurationDataMap.entrySet()) {
                final String key = entry.getKey();
                if (key.equalsIgnoreCase("CFG_ID")) {
                    if (isNew) {
                        document.field(key, entry.getValue());
                    }
                } else {
                    document.field(key, entry.getValue());
                }
            }
            dbTx.save(document);
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not save configuration", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
    }

   
}
