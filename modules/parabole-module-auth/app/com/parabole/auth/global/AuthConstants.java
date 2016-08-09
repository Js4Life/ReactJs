package com.parabole.auth.global;

import java.util.Set;

/**
 * Created by Sagir on 08-08-2016.
 */
public class AuthConstants {

    public static final String ROLE = "role";
    public static final String USER_NAME = "userid";
    public static final String PASSWORD = "password";
    public static final String ATTR_DATABASE_USER_NAME = "userid";


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
