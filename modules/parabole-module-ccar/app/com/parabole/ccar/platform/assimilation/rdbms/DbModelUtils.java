// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// DbModelUtils.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.assimilation.rdbms;

import com.google.inject.Singleton;
import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.platform.assimilation.IDataSourceUtils;
import com.parabole.ccar.platform.assimilation.OperationResult;
import com.parabole.ccar.platform.exceptions.AppException;
import com.parabole.ccar.platform.assimilation.BaseDataSourceCardinals;
import com.parabole.ccar.platform.exceptions.AppErrorCode;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import play.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DbModel DataSource Operations Manager.
 *
 * @author Atanu Mallick
 * @since v1.0
 */
@Singleton
public class DbModelUtils implements IDataSourceUtils {

    private DbModelDataSourceCardinals _cardinals;

    public DbModelUtils() {
    }

    public DbModelUtils(final BaseDataSourceCardinals cardinals) {
        _cardinals = (DbModelDataSourceCardinals) cardinals;
    }

    public OperationResult isValidCardinals(final String dbDriver, final String dbUrl, final String dbUserId, final String dbPassword) {
        Validate.notBlank(dbUrl, "'dbUrl' cannot be null!");
        Validate.notBlank(dbUserId, "'dbUserId' cannot be null!");
        Validate.notNull(dbPassword, "'dbPassword' cannot be null!");
        Connection dbConnection = null;
        try {
            Class.forName(dbDriver);
            dbConnection = DriverManager.getConnection(dbUrl, dbUserId, dbPassword);
            return new OperationResult(true);
        } catch (final SQLException sqlEx) {
            return new OperationResult(false, sqlEx.getMessage());
        } catch (final ClassNotFoundException e) {
            // TODO Auto-generated catch block
            return new OperationResult(false, e.getMessage());
        } finally {
            DbUtils.closeQuietly(dbConnection);
        }
    }

    @Override
    public JSONObject exploreDataSource() throws AppException {
        final DbModelDataSourceCardinals dbCardinals = _cardinals;
        Validate.notNull(dbCardinals, "'cardinals' cannot be null!");
        Connection dbConnection = null;
        try {
            final DbModelDataSource dbModelDataSource = new DbModelDataSource();
            final String dataSourceText = dbCardinals.getDataSourceMappingName();
            final String dbUrl = dbCardinals.getDbUrl();
            final String dbUserId = dbCardinals.getDbUserId();
            final String dbPassword = dbCardinals.getDbPassword();
            Class.forName(dbCardinals.getJdbcDriver());
            dbConnection = DriverManager.getConnection(dbUrl, dbUserId, dbPassword);
            final DatabaseMetaData metaData = dbConnection.getMetaData();
            dbModelDataSource.setText(dataSourceText);
            dbModelDataSource.setType("datasource");
            dbModelDataSource.setName(metaData.getDatabaseProductName());
            ResultSet schemas = null;
            if (metaData.getDatabaseProductName().equalsIgnoreCase(CCAppConstants.DATABASE_MYSQL)) {
                schemas = metaData.getSchemas();
            } else {
                schemas = metaData.getCatalogs();
            }
            if (null != schemas) {
                while (schemas.next()) {
                    final DbModelDatabase currDb = fetchDatabase(dbCardinals, dataSourceText, schemas.getString(1), dbConnection);
                    if (null != currDb) {
                        dbModelDataSource.addChild(currDb);
                    }
                }
            }
            return new JSONObject(dbModelDataSource);
        } catch (final SQLException sqlEx) {
            Logger.error("DbModelDataSource Fetch Error", sqlEx);
            throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
        } catch (final ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
        } finally {
            DbUtils.closeQuietly(dbConnection);
        }
    }

    private DbModelDatabase fetchDatabase(final DbModelDataSourceCardinals cardinals, final String dataSourceText, final String schemaName, final Connection dbConnection) throws AppException {
        Validate.notNull(cardinals, "'cardinals' cannot be null!");
        Validate.notBlank(dataSourceText, "'dataSourceText' cannot be null!");
        Validate.notBlank(schemaName, "'schemaName' cannot be null!");
        Validate.notNull(dbConnection, "'dbConnection' cannot be null!");
        final List<DbModelTable> dbModelTables = fetchTables(cardinals, dataSourceText, schemaName, dbConnection);
        final DbModelDatabase dbModelDatabase = new DbModelDatabase();
        dbModelDatabase.setText(schemaName);
        dbModelDatabase.setType("database");
        dbModelDatabase.appendChildren(dbModelTables);
        return dbModelDatabase;
    }

    private List<DbModelTable> fetchTables(final DbModelDataSourceCardinals cardinals, final String dataSourceText, final String schemaName, final Connection dbConnection) throws AppException {
        Validate.notNull(cardinals, "'cardinals' cannot be null!");
        Validate.notBlank(dataSourceText, "'dataSourceText' cannot be null!");
        Validate.notBlank(schemaName, "'schemaName' cannot be null!");
        Validate.notNull(dbConnection, "'dbConnection' cannot be null!");
        final List<DbModelTable> dbModelTables = new ArrayList<DbModelTable>();
        try {
            final DatabaseMetaData metaData = dbConnection.getMetaData();
            final ResultSet tableResultSet = metaData.getTables(schemaName, null, "%", new String[] { "TABLE" });
            while (tableResultSet.next()) {
                final DbModelTable currTable = new DbModelTable();
                final String name = tableResultSet.getString(3);
                currTable.setText(name);
                currTable.setType("table");
                final String absolutePathUptoTable = dataSourceText + "/" + schemaName + "/" + currTable.getText();
                final ResultSet columns = metaData.getColumns(schemaName, null, name, null);
                final List<DbModelAttribute> dbModelAttributes = getAttributes(columns, absolutePathUptoTable);
                currTable.appendChildren(dbModelAttributes);
                dbModelTables.add(currTable);
            }
        } catch (final SQLException sqlEx) {
            Logger.error("DbModelTable Fetch Error", sqlEx);
            throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
        }
        return dbModelTables;
    }

    private List<DbModelAttribute> getAttributes(final ResultSet columns, final String parentPath) throws AppException {
        final List<DbModelAttribute> dbModelAttributes = new ArrayList<DbModelAttribute>();
        try {
            while (columns.next()) {
                final DbModelAttribute currAttr = new DbModelAttribute();
                currAttr.setText(columns.getString(4));
                currAttr.setType("column");
                currAttr.setPath(parentPath + "/" + currAttr.getText());
                final String dataType = columns.getString(6);
                currAttr.setDataType(dataType);
                dbModelAttributes.add(currAttr);
            }
        } catch (final SQLException sqlEx) {
            Logger.error("DbModelAttribute Fetch Error", sqlEx);
            throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
        }
        return dbModelAttributes;
    }
}
