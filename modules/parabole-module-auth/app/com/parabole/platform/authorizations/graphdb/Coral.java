// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// Coral.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.platform.authorizations.graphdb;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.exceptions.AppErrorCode;
import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import play.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * Coral is Application Configuration Graph-Database.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Singleton
public class Coral extends GraphDb {

    public Coral() {
        final String graphDbUrl = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".coral.graphdb.url");
        final String graphDbUser = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".coral.graphdb.user");
        final String graphDbPassword = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".coral.graphdb.password");
        final Integer graphDbPoolMinSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".coral.graphdb.pool.min");
        final Integer graphDbPoolMaxSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".coral.graphdb.pool.max");
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
            Logger.error("Could not generate Id for key [" + counterKeyName + "]", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
        Logger.error("Found schema error while generating Id for key [" + counterKeyName + "]");
        throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
    }

    public Map<Integer, String> getConfigurationNames(final String userId, final String configurationType) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notBlank(configurationType, "'configurationType' cannot be empty!");
        final Map<Integer, String> outputMap = new HashMap<Integer, String>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT CFG_ID, CFG_NAME FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_USER = '" + userId + "' AND CFG_TYPE = '" + configurationType + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            results.forEach((final ODocument result) -> {
                final Integer configurationId = result.field("CFG_ID");
                final String configurationName = result.field("CFG_NAME");
                outputMap.put(configurationId, configurationName);
            });
            return outputMap;
        } catch (final Exception ex) {
            Logger.error("Could not retrieve configuration names", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

    public String getConfigurationDetail(final Integer configurationId) throws AppException {
        Validate.notNull(configurationId, "'configurationId' cannot be empty!");
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT CFG_INFO FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_ID = " + configurationId);
            final List<ODocument> results = dbNoTx.command(query).execute();
            if (CollectionUtils.isNotEmpty(results)) {
                return results.get(0).field("CFG_INFO");
            }
        } catch (final Exception ex) {
            Logger.error("Could not retrieve configuration", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
        return StringUtils.EMPTY;
    }

    public List<Map<String, String>> getConfigurationByUserId(final String userId, final String configurationType) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notBlank(configurationType, "'configurationType' cannot be empty!");
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT CFG_NAME, CFG_INFO FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_USER = '" + userId + "' AND CFG_TYPE = '" + configurationType + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            results.forEach((final ODocument result) -> {
                final Map<String, String> outputMap = new HashMap<String, String>();
                final String configurationName = result.field("CFG_NAME");
                final String configurationDetails = result.field("CFG_INFO");
                outputMap.put("name", configurationName);
                outputMap.put("details", configurationDetails);
                outputList.add(outputMap);
            });
            return Collections.unmodifiableList(outputList);
        } catch (final Exception ex) {
            Logger.error("Could not retrieve configuration details", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }
    
    public List<Map<String, String>> getConfigurationInfoByNameOnly(final String configurationName) throws AppException {
        Validate.notBlank(configurationName, "'nodeName' cannot be empty!");
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        JSONObject jsonObject = null;
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT CFG_NAME, CFG_INFO  FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_NAME = '" + configurationName + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            results.forEach((final ODocument result) -> {
            	final Map<String, String> outputMap = new HashMap<String, String>();
                final String configurationNameCollected = result.field("CFG_NAME");
                final String configurationDetails = result.field("CFG_INFO");
                outputMap.put("name", configurationNameCollected);
                outputMap.put("details", configurationDetails);
                outputList.add(outputMap);
            });
            return (outputList);
        } catch (final Exception ex) {
            Logger.error("Could not retrieve configuration", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

    public List<Map<String, String>> getConfigurationByName(final String configurationName) throws AppException {
        Validate.notBlank(configurationName, "'nodeName' cannot be empty!");
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT CFG_NAME, CFG_INFO  FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_NAME like '" + configurationName + "_%' ");
            final List<ODocument> results = dbNoTx.command(query).execute();
            results.forEach((final ODocument result) -> {
                final Map<String, String> outputMap = new HashMap<String, String>();
                final String configurationNameCollected = result.field("CFG_NAME");
                final String configurationDetails = result.field("CFG_INFO");
                outputMap.put("name", configurationNameCollected);
                outputMap.put("details", configurationDetails);
                outputList.add(outputMap);
            });
            return Collections.unmodifiableList(outputList);
        } catch (final Exception ex) {
            Logger.error("Could not retrieve configuration", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

    public List<Map<String, String>> getConfigurationByName(final String userId, final String configurationName, final boolean useLikePattern) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notBlank(configurationName, "'configurationType' cannot be empty!");
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            String sqlQuery = null;
            if (useLikePattern) {
                sqlQuery = "SELECT CFG_NAME, CFG_INFO FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_USER = '" + userId + "' AND CFG_NAME like '" + configurationName + "-%'";
            } else {
                sqlQuery = "SELECT CFG_NAME, CFG_INFO FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_USER = '" + userId + "' AND CFG_NAME = '" + configurationName + "'";
            }
            
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sqlQuery);
            final List<ODocument> results = dbNoTx.command(query).execute();
            results.forEach((final ODocument result) -> {
                final Map<String, String> outputMap = new HashMap<String, String>();
                final String configurationNameCollected = result.field("CFG_NAME");
                final String configurationDetails = result.field("CFG_INFO");
                outputMap.put("name", configurationNameCollected);
                outputMap.put("details", configurationDetails);
                outputList.add(outputMap);
            });
            return Collections.unmodifiableList(outputList);
        } catch (final Exception ex) {
            Logger.error("Could not get configuration [" + configurationName + "] for user [" + userId + "]" + userId, ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

    public Integer saveConfiguration(final String userId, final String configurationType, final String configurationName, final String configurationDetails) throws AppException {
        final Integer configurationId = generateId(CCAppConstants.RDA_USER_CONFIGS);
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("CFG_ID", configurationId);
        dataMap.put("CFG_USER", userId);
        dataMap.put("CFG_NAME", configurationName);
        dataMap.put("CFG_TYPE", configurationType);
        dataMap.put("CFG_INFO", configurationDetails);
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
            for (final Entry<String, Object> entry : configurationDataMap.entrySet()) {
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

    public void saveFile(final String userId, final Integer configurationId, final byte[] bytes) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notNull(configurationId, "'configurationId' cannot be empty!");
        Validate.notNull(bytes, "'bytes' cannot be null!");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            dbTx.begin();
            final ORecordBytes record = new ORecordBytes(bytes);
            final ODocument document = new ODocument("APP_FILES");
            document.field("USER_ID", userId);
            document.field("CFG_ID", configurationId);
            document.field("FILE", record);
            dbTx.save(document);
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not save file for user [" + userId + "] and configuration Id [" + configurationId + "]", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
    }

    public void deleteConfiguration(final Integer configurationId) throws AppException {
        Validate.notNull(configurationId, "'configurationId' cannot be null!");
        executeUpdate("DELETE FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_ID = " + configurationId);
    }

    public void deleteConfigurationByName(final String userId, final String configurationName) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be null!");
        Validate.notBlank(configurationName, "'configurationName' cannot be empty!");
        executeUpdate("DELETE FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_USER = '" + userId + "' AND CFG_NAME = '" + configurationName + "'");
    }
    
    public String saveConceptTags(final String nodeId, final String nodeName, final Map<String, Boolean> tagDetails) throws AppException {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("NODE_ID", nodeId);
        dataMap.put("NODE-NAME", nodeName);
        if(tagDetails.get("isBaseNode") != null)
        	dataMap.put("IS_BASE_NODE", tagDetails.get("isBaseNode"));
        if(tagDetails.get("isImpactRoot") != null)
        	dataMap.put("IS_IMPACT_ROOT", tagDetails.get("isImpactRoot"));
        if(tagDetails.get("isImpactDestination") != null)
        	dataMap.put("IS_IMPACT_DESTINATION", tagDetails.get("isImpactDestination"));
        save(CCAppConstants.RDA_CONCEPT_TAGS, dataMap); 
        return nodeId;
    }
    
    public Map<Integer, String> getAllConceptByTag(final String tag) throws AppException {
        Validate.notBlank(tag, "'tag' cannot be empty!");
        final Map<Integer, String> outputMap = new HashMap<Integer, String>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT NODE_ID, NODE_NAME FROM " + CCAppConstants.RDA_CONCEPT_TAGS + "WHERE '" + tag + "' = '" + true + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            results.forEach((final ODocument result) -> {
                final Integer nodeId = result.field("NODE_ID");
                final String nodeName = result.field("NODE_NAME");
                outputMap.put(nodeId, nodeName);
            });
            return outputMap;
        } catch (final Exception ex) {
            Logger.error("Could not retrieve node tag details", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }
}
