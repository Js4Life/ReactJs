// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// GraphDb.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.graphdb;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.parabole.feed.platform.AppConstants;
import com.parabole.feed.platform.assimilation.QueryResultTable;
import com.parabole.feed.platform.exceptions.AppErrorCode;
import com.parabole.feed.platform.exceptions.AppException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import play.Logger;

import java.util.*;

/**
 * Base Graph Database Operations (Orient-DB).
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public abstract class GraphDb {

    protected OrientGraphFactory orientGraphFactory;

    protected TinkerGraphFactory tinkerGraphFactory;

    public OrientGraphNoTx getGraphConnectionNoTx() {
        final OrientGraphNoTx graphDbNoTx = orientGraphFactory.getNoTx();
        ODatabaseRecordThreadLocal.INSTANCE.set(graphDbNoTx.getRawGraph());
        return graphDbNoTx;
    }

/*    public TinkerGraphFactory getTinkerGraphConnections(){
        final TinkerGraphFactory tinkerGraphFactoryResult = tinkerGraphFactory.
        return null;
    }*/

    public void closeGraphConnection(final OrientGraphNoTx graphDbNoTx) {
        if (null != graphDbNoTx) {
            graphDbNoTx.shutdown();
        }
    }

    public ODatabaseDocumentTx getDocDBConnectionTx() {
        final OrientGraph graphDbTx = orientGraphFactory.getTx();
        final ODatabaseDocumentTx dbTx = graphDbTx.getRawGraph();
        ODatabaseRecordThreadLocal.INSTANCE.set(dbTx);
        return dbTx;
    }

    public ODatabaseDocumentTx getDocDBConnectionNoTx() {
        final OrientGraphNoTx graphDbNoTx = orientGraphFactory.getNoTx();
        final ODatabaseDocumentTx dbNoTx = graphDbNoTx.getRawGraph();
        ODatabaseRecordThreadLocal.INSTANCE.set(dbNoTx);
        return dbNoTx;
    }

    public void closeDocDBConnection(final ODatabaseDocumentTx dbTx) {
        if (null != dbTx) {
            dbTx.close();
        }
    }

    public List<Map<String, Object>> executeQuery(final String sqlQuery) throws AppException {
        Validate.notNull(sqlQuery, "'sqlQuery' cannot be empty!");
        final List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            final List<ODocument> results = dbTx.command(new OCommandSQL(sqlQuery)).execute();
            if (CollectionUtils.isNotEmpty(results)) {
                final String[] attrNames = results.get(0).fieldNames();
                results.forEach((final ODocument doc) -> {
                    final Map<String, Object> aRec = new HashMap<String, Object>();
                    for (final String attr : attrNames) {
                        aRec.put(attr, doc.field(attr));
                    }
                    resultMap.add(aRec);
                });
            }
        } finally {
            closeDocDBConnection(dbTx);
        }
        return resultMap;
    }

    public int executeDelete(final String sqlQuery) throws AppException {
        Validate.notNull(sqlQuery, "'sqlQuery' cannot be empty!");
        final List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        int result = 0;
        try {
            result = dbTx.command(new OCommandSQL(sqlQuery)).execute();
        } catch(Exception e){
            e.printStackTrace();
            return result;
        } finally {
            closeDocDBConnection(dbTx);
        }
        return result;
    }

    public void executeUpdate(final String sqlQuery) throws AppException {
        Validate.notBlank(sqlQuery, "'sqlQuery' cannot be empty!");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            dbTx.command(new OCommandSQL(sqlQuery)).execute();
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not execute SQL Query: " + sqlQuery, ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
    }

    public void executeBatchUpdate(final List<String> sqlQuerys) throws AppException {
        Validate.noNullElements(sqlQuerys, "Empty SQL Scripts");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            sqlQuerys.forEach((final String query) -> dbTx.command(new OCommandSQL(query)).execute());
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not execute SQL", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
    }

    public OClass createTable(final String tableName, final List<String> columns) throws AppException {
        Validate.notBlank(tableName, "'tableName' cannot be empty!");
        Validate.notEmpty(columns, "'columns' cannot be empty!");
        ODatabaseDocumentTx dbNoTx = null;

        try {
            if (orientGraphFactory.getDatabase().getMetadata().getSchema().existsClass(tableName)) {
                orientGraphFactory.getDatabase().getMetadata().getSchema().dropClass(tableName);
            }
            dbNoTx = getDocDBConnectionNoTx();
            final OSchema schema = dbNoTx.getMetadata().getSchema();
            final OClass clazz = schema.createClass(tableName);
            for (final String column : columns) {
                clazz.createProperty(column, OType.STRING);
            }
            createIndex(tableName, columns);
            return clazz;
        } catch (final Exception ex) {
            Logger.error("Could not create table [" + tableName + "]", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

    private void createIndex(final String tableName, final List<String> columns) throws AppException {
        Validate.notBlank(tableName, "'tableName' cannot be empty!");
        Validate.notEmpty(columns, "'columns' cannot be empty!");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            dbTx.begin();
            for (final String column : columns) {
                dbTx.command(new OCommandSQL("CREATE INDEX " + tableName + "." + column + " NOTUNIQUE")).execute();
            }
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not create index on table [" + tableName + "]", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
    }

    public void loadBulkDataInTable(final String tableName, final List<String> columns, final List<List<Object>> data) throws AppException {
        Validate.notBlank(tableName, "'tableName' cannot be empty!");
        Validate.notEmpty(columns, "'columns' cannot be empty!");
        Validate.notEmpty(data, "'data' cannot be empty!");
        final int maxRow = data.size();
        final int maxColumns = columns.size();
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            dbTx.declareIntent(new OIntentMassiveInsert());
            dbTx.begin();
            for (int i = 0; i < maxRow; i++) {
                final ODocument document = new ODocument(tableName);
                for (int j = 0; j < maxColumns; j++) {
                    document.field(columns.get(j), data.get(i).get(j));
                }
                dbTx.save(document);
            }
            dbTx.commit();
            dbTx.declareIntent(null);
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not load data into table [" + tableName + "]", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
    }

    public void loadBulkDataInTable(final String tableName, final List<String> columns, final QueryResultTable data) throws AppException {
        Validate.notBlank(tableName, "'tableName' cannot be empty!");
        Validate.notEmpty(columns, "'columns' cannot be empty!");
        final int maxColumns = columns.size();
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            dbTx.declareIntent(new OIntentMassiveInsert());
            dbTx.begin();
            for (final List<String> aRow : data) {
                final ODocument document = new ODocument(tableName);
                for (int j = 0; j < maxColumns; j++) {
                    document.field(columns.get(j), aRow.get(j));
                }
                dbTx.save(document);
            }
            dbTx.commit();
            dbTx.declareIntent(null);
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not load data on table [" + tableName + "]", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
    }

    public List<List<String>> fetchAllDataFromTable(final String tableName) throws AppException {
        Validate.notBlank(tableName, "'tableName' cannot be empty!");
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSchema schema = dbNoTx.getMetadata().getSchema();
            final OClass table = schema.getClass(tableName);
            final Collection<OProperty> columns = table.declaredProperties();
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT * FROM " + tableName);
            final List<ODocument> results = dbNoTx.command(query).execute();
            final List<List<String>> tableData = new ArrayList<List<String>>();
            final List<String> headerRow = new ArrayList<String>();
            columns.forEach((final OProperty column) -> headerRow.add(column.getName()));
            tableData.add(headerRow);
            for (final ODocument result : results) {
                final List<String> dataRow = new ArrayList<String>();
                columns.forEach((final OProperty column) -> {
                    try {
                        final Object obj = result.field(column.getName());
                        final String t = obj.getClass().getName() + "OBJECT TO STRING :: " + obj.toString() + " LINKED CLASS :: " + column.getLinkedClass().getDefaultClusterId();
                        dataRow.add(t);
                    } catch (final Exception e) {
                    }
                });
                tableData.add(dataRow);
            }
            return tableData;
        } catch (final Exception ex) {
            Logger.error("Could not fetch data from table [" + tableName + "]", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

    public void dropTables(final List<String> tableNames) {
        if (CollectionUtils.isNotEmpty(tableNames)) {
            final OSchema dbSchema = orientGraphFactory.getNoTx().getRawGraph().getMetadata().getSchema();
            for (final String tableName : tableNames) {
                if (dbSchema.existsClass(tableName)) {
                    dbSchema.dropClass(tableName);
                }
            }
        }
    }

    public void deleteAllDataFromTable(final String tableName) throws AppException {
        Validate.notBlank(tableName, "'tableName' cannot be empty!");
        executeUpdate("DELETE FROM " + tableName);
    }

    public void createLinkToTable(final GraphDbLinkDefinition linkDef) throws AppException {
        Validate.notBlank(linkDef.getFromClass(), "'configurationObjectClassFrom' cannot be empty!");
        Validate.notBlank(linkDef.getToClass(), "'configurationObjectClassTo' cannot be empty!");
        Validate.notBlank(linkDef.getFromProperty(), "'configurationObjectDocumentFrom' cannot be empty!");
        Validate.notBlank(linkDef.getToProperty(), "'configurationObjectDocumentTo' cannot be empty!");
        Validate.notBlank(linkDef.getName(), "'LinkName' cannot be empty!");
        String lhsClass = null, rhsClass = null;
        String lhsProp = null, rhsProp = null;
        final int cardinality = linkDef.getCardinality();
        boolean isInverse = true;
        final String linkType = "LINKSET";
        switch (cardinality) {
            case AppConstants.CARDINALITY1TO1:
                lhsClass = linkDef.getFromClass();
                rhsClass = linkDef.getToClass();
                lhsProp = linkDef.getFromProperty();
                rhsProp = linkDef.getToProperty();
                isInverse = false;
                break;
            case AppConstants.CARDINALITY1TON:
                lhsClass = linkDef.getToClass();
                rhsClass = linkDef.getFromClass();
                lhsProp = linkDef.getToProperty();
                rhsProp = linkDef.getFromProperty();
                break;
            case AppConstants.CARDINALITYNTO1:
                lhsClass = linkDef.getFromClass();
                rhsClass = linkDef.getToClass();
                lhsProp = linkDef.getFromProperty();
                rhsProp = linkDef.getToProperty();
                break;
        }
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final StringBuffer sBuffer = new StringBuffer();
            sBuffer.append("CREATE LINK ").append(linkDef.getName()).append(" TYPE ").append(linkType).append(" FROM ").append(lhsClass).append(".").append(lhsProp).append(" TO ").append(rhsClass).append(".").append(rhsProp);
            if (isInverse) {
                sBuffer.append(" INVERSE ");
            }
            final String query = sBuffer.toString();
            dbNoTx.command(new OCommandSQL(query)).execute();
        } catch (final Exception ex) {
            Logger.error("Could not create Link", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }



}
