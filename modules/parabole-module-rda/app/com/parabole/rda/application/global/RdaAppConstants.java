// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// RdaAppConstants.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.global;

import java.util.Set;

import com.parabole.rda.platform.AppConstants;
import com.parabole.rda.platform.utils.AppUtils;

/**
 * RDA OctopusAction Constants.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class RdaAppConstants extends AppConstants {

    public static final String INDUSTRY = AppUtils.getApplicationProperty("industry");
    public static final String ATTR_BASE_NODE = "baseNode";
    public static final Set<String> RDA_RELATIONSHIPS = AppUtils.createSetFromApplicationProperty(INDUSTRY + ".relationships.filter", "|");
    public static final String RDA_COUNTER_TABLE = "APP_ID_GENERATOR";
    public static final String RDA_APP_USERS = "APP_USERS";
    public static final String RDA_APP_USER_ROLES = "APP_USER_ROLES";
    public static final String RDA_USER_CONFIGS = "APP_USER_CONFIGS";
    public static final String RDA_CONCEPT_TAGS = "APP_CONCEPT_TAGS";
    public static final String RDA_GLOSSARY_CONFIGS = "GLOSSARY_CONFIGS";
    public static final String RDA_GLOSSARY_TABLE = "GLOSSARY_TABLE";
    public static final String RDA_CFG_TYPE_REPORT = "REPORTS";
    public static final String ATTR_VIEWCREATION_DETAILS = "details";
    public static final String ATTR_VIEWCREATION_DATA = "data";
    public static final String ATTR_VIEWCREATION_COLS = "columns";
    public static final String ATTR_VIEWCREATION_REFS = "references";
    public static final String ATTR_DATA_SOURCE_TYPE = "type";
    public static final String DATA_SOURCE_TYPE_EXCEL = "excel";
    public static final String DATA_SOURCE_TYPE_RDB = "db";
    public static final String ATTR_EXCEL_FILENAME = "fileName";
    public static final String ATTR_EXCEL_DATA_BYTES = "data";
    public static final String ATTR_DATABASE_DRIVER_NAME = "driver";
    public static final String ATTR_DATABASE_SERVER_URL = "connectionstr";
    public static final String ATTR_DATABASE_USER_NAME = "userid";
    public static final String ATTR_DATABASE_PASSWORD = "password";
    public static final String ATTR_DATABASE_PASSWORD_COLUMN_NAME = "PASSWORD";
    public static final String ATTR_DATABASE_USER_NAME_COLUMN_NAME = "USER_NAME";
    public static final String ATTR_DATASOURCE_MAPPING_NAME = "name";
    public static final String ATTR_DATASOURCE_MAPPING_FILEDATA = "filedata";
    public static final String ATTR_AGGREGATEMAPPING = "mappableEdges";
    public static final String AGGREGATEMAPPING_SUFFIX = "_Mapping";
    public static final String BASENODEMAPPING_SUFFIX = "_BaseMapping";
    public static final String DATANODEMAPPING_SUFFIX = "_DataNodeMapping";
    public static final String ERROR_MESSAGE_DRIVER_NOT_FOUND = "Driver not found";
    public static final String DATABASE_MYSQL = "MySQL";
    public static final String DATABASE_ORACLE = "Oracle";
    public static final String ADMIN = "root";
    public static final String RDA_CFG_ID = "cfgid";
    public static final String TOKEN_BASE64_STR = "base64,";
    public static final String LICENSE_FILE = "rda.lic";

    public static enum ExcelFormat {
        XLS, XLSX, CSV
    }

    public static enum ConfigurationType {
        NODEMAP, REPORT, AGGREGATION, ADMINDATA, DATASOURCE, EXCEL_METADATA, AGGREGATEMAPPING, DBVIEW, LOGICAL_VIEW_ONE, BASENODE_CFG, DATANODE_CFG, COMBINED_VIEW, IMPACT_GRAPH_IMAGE, COMPOSITEAGGREGATION
    }
}
