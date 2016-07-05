package com.parabole.ccar.application.global;

import com.parabole.ccar.platform.AppConstants;
import com.parabole.ccar.platform.utils.AppUtils;

import java.util.Set;

public class CCAppConstants extends AppConstants {

    public static final String INDUSTRY = "finance";
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
    public static final String HIERARCHY_GRAPH = "hierarchy";
    public static final String JENA_FILTERDEF_FILE = "filterMetadata.json";
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
    public static final String ATTR_DATABASE_GROUP_NAME_COLUMN_NAME = "GROUP_NAME";
    public static final String ATTR_DATABASE_USER_NAME_COLUMN_NAME = "USER_NAME";
    public static final String ATTR_DATASOURCE_MAPPING_NAME = "name";
    public static final String ATTR_DATASOURCE_MAPPING_FILEDATA = "filedata";
    public static final String ATTR_AGGREGATEMAPPING = "mappableEdges";
    public static final String AGGREGATEMAPPING_SUFFIX = "_Mapping";
    public static final String BASENODEMAPPING_SUFFIX = "_BaseMapping";
    public static final String DATANODEMAPPING_SUFFIX = "_DataNodeMapping";
    public static final String ERROR_MESSAGE_DRIVER_NOT_FOUND = "Driver not found";
    public static final String DATABASE_MYSQL = "MySQL";
    public static final String ADMIN = "root";
    public static final String RDA_CFG_ID = "cfgid";
    public static final String TOKEN_BASE64_STR = "base64,";

    public static final String BAR_CHART = "barChart";
    public static final String PROGRESS_CHART = "progressChart";
    public static final String COLUMN_CHART = "column";
    public static final String LINE_CHART = "line";
    public static final String PIE_CHART = "pie";
    public static final String DONUT_CHART = "donut";
    public static final String COMBINE_CHART = "combine";
    public static final String TIMELINE_CHART = "timeline";
    public static final String JENA_TDB_STOREROOM = "tdb";
    public static final String JENA_WIDGETDEF_FILE = "uiMetadata.json";
    public static final String JENA_BATCHINSRT_FILE = "batchInsert.json";
    public static final String REGULATORY_WIDGETS = "RegulatoryWidgets";
    public static final String EDM_WIDGETS = "EdmWidgets";
    public static final String FRY14Q_WIDGETS = "Fry14QWidgets";
    public static final String SCHEDULE_COMPLETION = "ScheduleCompletion";
    public static final String TABLE_WIDGETS = "Table";
    public static final String GRAPH_WIDGETS = "Graph";

    public static final String ADMIN_AUTH = "ADMIN";
    public static final String REGULATORY = "REGULATORY";
    public static final String BUSINESS_SEGMENT_ALIGNED_WORKING_GROUP = "BSAWG";
    public static final String ENTERPRISE_WORKING_GROUP = "EWG";
    public static final String DATA_ADVISORY_COMMITTEE = "DAC";
    public static final String CONF_FILE_PATH = "conf/files/";


    public static enum ExcelFormat {
        XLS, XLSX, CSV
    }

    public static enum ConfigurationType {
        NODEMAP, REPORT, AGGREGATION, ADMINDATA, DATASOURCE, EXCEL_METADATA, AGGREGATEMAPPING, DBVIEW, LOGICAL_VIEW_ONE, BASENODE_CFG, DATANODE_CFG, COMBINED_VIEW, IMPACT_GRAPH_IMAGE, COMPOSITEAGGREGATION
    }

    public static enum FileFormat {
        XLS, XLSX, CSV, DOC, DOCX, TXT
    }
}
