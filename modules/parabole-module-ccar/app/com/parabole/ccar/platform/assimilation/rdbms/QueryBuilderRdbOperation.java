// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// QueryBuilderRdbOperation.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.assimilation.rdbms;

import com.google.inject.Singleton;
import com.parabole.ccar.platform.AppConstants;
import com.parabole.ccar.platform.assimilation.ColumnReference;
import com.parabole.ccar.platform.assimilation.QueryBuilderIDataSourceOperation;
import com.parabole.ccar.platform.assimilation.QueryRequest;
import com.parabole.ccar.platform.assimilation.QueryResultTable;
import com.parabole.ccar.platform.exceptions.AppErrorCode;
import com.parabole.ccar.platform.exceptions.AppException;
import org.apache.commons.dbutils.DbUtils;
import play.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Relational Database Query Genaration Operations Manager.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Singleton
public class QueryBuilderRdbOperation implements QueryBuilderIDataSourceOperation {

    @Override
    public QueryResultTable getViewFromQueryRequest(final QueryRequest queryData) throws AppException {
        return getView((RdbQueryRequest) queryData);
    }

    private QueryResultTable getView(final RdbQueryRequest queryData) throws AppException {
        final Connection dbConnection = this.getConnection(queryData.getCardinals());
        final String query = prepareSql(queryData);

        final QueryResultTable result = getResultTableFromSql(dbConnection, query);
        return result;
    }

    private Iterator<List<String>> generateRowIterator(final Connection dbConnection, final ResultSet rs) {
        final Iterator<List<String>> rowIterator = new Iterator<List<String>>() {
            int columnsNumber;
            boolean hasData = false;
            {
                try {
                    columnsNumber = rs.getMetaData().getColumnCount();
                } catch (final SQLException e) {
                    Logger.error("Exception: ", e);
                }
            }

            @Override
            public void remove() {

            }

            @Override
            public List<String> next() {
                try {
                    if (rs.next()) {

                        final List<String> currRow = new ArrayList<String>();
                        for (int i = 1; i <= columnsNumber; i++) {
                            currRow.add(rs.getString(i));
                        }
                        return currRow;
                    } else {
                        DbUtils.closeQuietly(dbConnection);
                        return null;
                    }
                } catch (final SQLException e) {
                    Logger.error("Exception: ", e);
                    return null;
                }
            }

            @Override
            public boolean hasNext() {
                try {
                    if (!hasData && !rs.isBeforeFirst()) {
                        DbUtils.closeQuietly(dbConnection);
                        return false;
                    }
                    this.hasData = true;
                    if (rs.isLast()) {
                        DbUtils.closeQuietly(dbConnection);
                        return false;
                    }
                } catch (final SQLException e) {
                    Logger.error("Exception: ", e);
                    return false;
                }
                return true;
            }
        };
        return rowIterator;
    }

    private QueryResultTable getResultTableFromSql(final Connection dbCon, final String query) throws AppException {
        try {
            System.out.println("Query " + query);

            final PreparedStatement sqlPs = dbCon.prepareStatement(query);
            final ResultSet rs = sqlPs.executeQuery();
            rs.setFetchSize(AppConstants.DEFAULT_PREVIEW_ROW_NUMBER);
            final QueryResultTable ret = getResultTableFromResultSet(rs);
            //rs.beforeFirst();
            rs.setFetchSize(AppConstants.DATA_LOAD_PAGE_SIZE_IN_ROW);
            final Iterator<List<String>> rowIterator = generateRowIterator(dbCon, rs);
            ret.setRowIterator(rowIterator);
            return ret;
        } catch (final SQLException sqlEx) {
            Logger.error("SQL Exception: ", sqlEx);
            return null;
        }
    }

    private String prepareSql(final RdbQueryRequest queryData) {
        boolean whereClauseUsed = false;
        final StringBuilder query = new StringBuilder();
        final String uniqueColNames = generateSelectableColumnNamesAsString(queryData.getColumns());
        final String uniquetableNames = generateSelectableTableNamesAsString(queryData.getColumns());
        query.append(QueryBuilderConstraints.RDB_SELECT_CLAUSE).append(uniqueColNames).append(QueryBuilderConstraints.RDB_FROM_CLAUSE).append(uniquetableNames);
        if (null != queryData.getFixedClause()) {
            query.append(" ").append(queryData.getFixedClause());
            return query.toString();
        }
        if ((null != queryData.getReferences()) && (queryData.getReferences().size() > 0)) {
            final String joiningCondition = generateReferenceConditionString(queryData.getReferences());
            query.append(QueryBuilderConstraints.RDB_WHERE_CLAUSE).append(joiningCondition);
            whereClauseUsed = true;
        }
        if (null != queryData.getCondition()) {
            final String condition = generateConditionString(queryData.getCondition());
            if (whereClauseUsed) {
                query.append(QueryBuilderConstraints.RDB_AND_OPERATOR).append(condition);
            } else {
                query.append(QueryBuilderConstraints.RDB_WHERE_CLAUSE).append(condition);
            }
        }

        return query.toString().trim().replaceAll(QueryBuilderConstraints.RDB_TOKEN_MULTI_GAP_REGX, QueryBuilderConstraints.RDB_TOKEN_GAP);
    }

    private String generateConditionString(final QueryNCondition condition) {
        if (null != condition) {
            if (condition instanceof QueryCondition) {
            } else {
            }
        }
        return QueryBuilderConstraints.RDB_EMPTY_TOKEN;
    }

    private String generateReferenceConditionString(final List<ColumnReference<RdbColumn>> references) {
        final StringBuilder ret = new StringBuilder();
        for (final ColumnReference<RdbColumn> rdbColumnReference : references) {
            final String currRefCondition = referenceToConditionStirng(rdbColumnReference);
            if (ret.length() != 0) {
                ret.append(QueryBuilderConstraints.RDB_AND_OPERATOR);
            }
            ret.append(currRefCondition);
        }
        ret.insert(0, QueryBuilderConstraints.RDB_ENCLOSER_START_TOKEN).append(QueryBuilderConstraints.RDB_ENCLOSER_END_TOKEN);
        return ret.toString();
    }

    private String referenceToConditionStirng(final ColumnReference<RdbColumn> rdbColumnReference) {
        if (null == rdbColumnReference) {
            return QueryBuilderConstraints.RDB_EMPTY_TOKEN;
        }
        final StringBuilder ret = new StringBuilder();
        ret.append(getAbsoluteColName(rdbColumnReference.getColumnFrom())).append(QueryBuilderConstraints.RDB_EQUAL_OPERATOR).append(getAbsoluteColName(rdbColumnReference.getColumnTo()));
        return ret.toString();
    }

    private String getAbsoluteColName(final RdbColumn col) {
        final StringBuilder ret = new StringBuilder();
        ret.append(col.getSchemaName()).append(QueryBuilderConstraints.RDB_PARENT_CHILD_CONCAT_TOKEN).append(col.getTableName()).append(QueryBuilderConstraints.RDB_PARENT_CHILD_CONCAT_TOKEN).append(col.getName());
        return ret.toString();
    }

    private String getAbsoluteTableName(final RdbColumn col) {
        final StringBuilder ret = new StringBuilder();
        ret.append(col.getSchemaName()).append(QueryBuilderConstraints.RDB_PARENT_CHILD_CONCAT_TOKEN).append(col.getTableName());
        return ret.toString();
    }

    private String generateSelectableTableNamesAsString(final List<RdbColumn> columns) {
        final List<String> uniqueTableNames = new ArrayList<String>();
        for (final RdbColumn col : columns) {
            final String absoluteTableName = getAbsoluteTableName(col);
            if (!uniqueTableNames.contains(absoluteTableName)) {
                uniqueTableNames.add(absoluteTableName);
            }
        }
        return punchListToSingleString(uniqueTableNames, QueryBuilderConstraints.RDB_TOKEN_VALUE_SEPARATOR);
    }

    private String generateSelectableColumnNamesAsString(final List<RdbColumn> columns) {
        final List<String> uniqueColNames = new ArrayList<String>();
        for (final RdbColumn col : columns) {
            String absoluteColName = getAbsoluteColName(col);
            final String aliasName = col.getAliasName();
            if ((aliasName != null) && (aliasName.trim().length() > 0)) {
                absoluteColName += (QueryBuilderConstraints.RDB_TOKEN_GAP + QueryBuilderConstraints.RDB_TOKEN_ENCLOSER + aliasName.trim() + QueryBuilderConstraints.RDB_TOKEN_ENCLOSER);
            }
            if (!uniqueColNames.contains(absoluteColName)) {
                uniqueColNames.add(absoluteColName);
            }
        }
        return punchListToSingleString(uniqueColNames, QueryBuilderConstraints.RDB_TOKEN_VALUE_SEPARATOR);
    }

    private String punchListToSingleString(final List<String> strList, final String separator) {
        return punchListToSingleString(strList, separator, QueryBuilderConstraints.RDB_EMPTY_TOKEN, QueryBuilderConstraints.RDB_EMPTY_TOKEN, QueryBuilderConstraints.RDB_EMPTY_TOKEN);
    }

    private String punchListToSingleString(final List<String> strList, final String separator, final String eachvalueWrapperStr, final String wrapStart, final String wrapEnd) {
        final StringBuilder ret = new StringBuilder();
        for (final String string : strList) {
            final StringBuilder currStr = new StringBuilder().append(eachvalueWrapperStr).append(string).append(eachvalueWrapperStr);
            if (ret.length() == 0) {
                ret.append(currStr);
            } else {
                ret.append(separator).append(currStr);
            }
        }
        return ret.toString();
    }

    private QueryResultTable getResultTableFromResultSet(final ResultSet resultSet) throws AppException {
        final QueryResultTable table = new QueryResultTable();
        int columnsNumber = 0;
        try {
            final ResultSetMetaData rsmd = resultSet.getMetaData();
            columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                table.addColumnName(rsmd.getColumnName(i));
            }
            while (resultSet.next()) {
                final List<String> currRow = new ArrayList<String>();
                for (int i = 1; i <= columnsNumber; i++) {
                    currRow.add(resultSet.getString(i));
                }
                table.addRow(currRow);
                if (table.getTableData().size() == AppConstants.DEFAULT_PREVIEW_ROW_NUMBER) {
                    break;
                }
            }
        } catch (final SQLException sqlEx) {
            Logger.error("SQL Exception: ", sqlEx);
            throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
        }
        return table;
    }

    private Connection getConnection(final RdbAccessCardinals cardinals) throws AppException {
        try {
            Class.forName(cardinals.getDriver());
            return DriverManager.getConnection(cardinals.getUrl(), cardinals.getUserId(), cardinals.getPwd());
        } catch (final ClassNotFoundException e) {
            throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
        } catch (final SQLException sqlEx) {
            Logger.error("SQL Exception: ", sqlEx);
            throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
        }
    }
}
