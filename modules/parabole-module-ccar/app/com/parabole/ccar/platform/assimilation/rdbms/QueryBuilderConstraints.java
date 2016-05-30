// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// QueryBuilderConstraints.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.assimilation.rdbms;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public abstract class QueryBuilderConstraints {

    public static final String RDB_SELECT_CLAUSE = "SELECT ";
    public static final String RDB_FROM_CLAUSE = " FROM ";
    public static final String RDB_INSERT_CLAUSE = "INSERT INTO ";
    public static final String RDB_VALUES_CLAUSE = "  VALUES";
    public static final String RDB_WHERE_CLAUSE = " WHERE ";
    public static final String RDB_AND_OPERATOR = " AND ";
    public static final String RDB_OR_OPERATOR = " OR ";
    public static final String RDB_EQUAL_OPERATOR = " = ";
    public static final String RDB_NOT_EQUAL_OPERATOR = " != ";
    public static final String RDB_GREATER_THAN_OPERATOR = " > ";
    public static final String RDB_LOWER_THAN_OPERATOR = " < ";
    public static final String RDB_GREATER_THAN_EQUAL_OPERATOR = " <= ";
    public static final String RDB_LOWER_THAN_EQUAL_OPERATOR = " > ";
    public static final String RDB_ENCLOSER_START_TOKEN = "(";
    public static final String RDB_ENCLOSER_END_TOKEN = ")";
    public static final String RDB_TOKEN_VALUE_SEPARATOR = ",";
    public static final String RDB_TOKEN_ENCLOSER = "\"";
    public static final String RDB_VALUE_ENCLOSER = "'";
    public static final String RDB_PARENT_CHILD_CONCAT_TOKEN = ".";
    public static final String RDB_TOKEN_GAP = " ";
    public static final String RDB_TOKEN_MULTI_GAP_REGX = " +";
    public static final String RDB_EMPTY_TOKEN = "";
    public static final String MESSAGE_INVALID_RDB_QUERY_REQUEST = "Invalid Query Request Object for Relational Database";
    public static final String MESSAGE_DRIVER_NOT_FOUND = "Driver Not Found";
    public static final String DATABSE_PROVIDER_ORACLE = "ORACLE";
    public static final String DATABSE_PROVIDER_MYSQL = "MYSQL";
    public static final String DATABSE_PROVIDER_SQLSERVER = "SQLSQRVER";
}
