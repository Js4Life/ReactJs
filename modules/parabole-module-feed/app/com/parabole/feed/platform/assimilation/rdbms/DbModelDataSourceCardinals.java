// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// DbModelDataSourceCardinals.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.assimilation.rdbms;

import com.parabole.feed.platform.assimilation.BaseDataSourceCardinals;

/**
 * DbModel DataSource Cardinals.
 *
 * @author Atanu Mallick
 * @since v1.0
 */
public class DbModelDataSourceCardinals extends BaseDataSourceCardinals {

    private static final long serialVersionUID = 3991844697303593393L;
    private final String jdbcDriver;
    private final String dbUrl;
    private final String dbUserId;
    private final String dbPassword;
    private final String dataSourceMappingName;

    public DbModelDataSourceCardinals(final String jdbcDriver, final String dbUrl, final String dbUserId, final String dbPassword, final String dataSourceMappingName) {
        this.jdbcDriver = jdbcDriver;
        this.dbUrl = dbUrl;
        this.dbUserId = dbUserId;
        this.dbPassword = dbPassword;
        this.dataSourceMappingName = dataSourceMappingName;
        this.setDsName(dataSourceMappingName);
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUserId() {
        return dbUserId;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDataSourceMappingName() {
        return dataSourceMappingName;
    }
}
