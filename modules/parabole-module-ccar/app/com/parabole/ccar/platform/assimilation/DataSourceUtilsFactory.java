// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// DataSourceUtilsFactory.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.assimilation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.application.services.BiotaServices;
import com.parabole.ccar.application.services.CoralConfigurationService;
import com.parabole.ccar.platform.assimilation.excel.ExcelModelDataSourceCardinals;
import com.parabole.ccar.platform.assimilation.excel.ExcelUtils;
import com.parabole.ccar.platform.assimilation.rdbms.*;
import com.parabole.ccar.platform.exceptions.AppException;
import com.parabole.ccar.platform.assimilation.excel.ExcelQueryRequest;
import com.parabole.ccar.platform.exceptions.AppErrorCode;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Source Dealing.
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */
public class DataSourceUtilsFactory {

    @Inject
    protected DbModelUtils dbModelUtils;

    public static IDataSourceUtils getDataSourceUtils(final Map<String, String> dsDetails) {
        IDataSourceUtils dsUtils = null;
        final BaseDataSourceCardinals cardinals = createCardinalObj(dsDetails);
        if (cardinals instanceof ExcelModelDataSourceCardinals) {
            dsUtils = new ExcelUtils(cardinals);
        } else {
            dsUtils = new DbModelUtils(cardinals);
        }
        return dsUtils;
    }

    private static BaseDataSourceCardinals createCardinalObj(final Map<String, String> dsDetails) {
        final String dsName = dsDetails.get("name");
        BaseDataSourceCardinals cardinals = null;
        final String detailsStr = dsDetails.get("details");
        final JSONObject detailJsonObj = new JSONObject(detailsStr);
        final String dsType = detailJsonObj.getString(CCAppConstants.ATTR_DATA_SOURCE_TYPE);
        if (dsType.equalsIgnoreCase(CCAppConstants.DATA_SOURCE_TYPE_RDB)) {
            final String dbDriver = detailJsonObj.getString(CCAppConstants.ATTR_DATABASE_DRIVER_NAME);
            String dbUrl = detailJsonObj.getString(CCAppConstants.ATTR_DATABASE_SERVER_URL);
            dbUrl = dbUrl.replace("&amp;", "&");
            final String dbUser = detailJsonObj.getString(CCAppConstants.ATTR_DATABASE_USER_NAME);
            final String dbPassword = detailJsonObj.getString(CCAppConstants.ATTR_DATABASE_PASSWORD);
            final String dbName = detailJsonObj.getString(CCAppConstants.ATTR_DATASOURCE_MAPPING_NAME);
            cardinals = new DbModelDataSourceCardinals(dbDriver, dbUrl, dbUser, dbPassword, dbName);
        } else if (dsType.equalsIgnoreCase(CCAppConstants.DATA_SOURCE_TYPE_EXCEL)) {
            final JSONObject fileObj = detailJsonObj.getJSONObject(CCAppConstants.ATTR_DATASOURCE_MAPPING_FILEDATA);
            final String excelFileName = fileObj.getString(CCAppConstants.ATTR_DATASOURCE_MAPPING_NAME);
            String excelDataStr = fileObj.getString(CCAppConstants.ATTR_EXCEL_DATA_BYTES);
            excelDataStr = excelDataStr.substring(excelDataStr.indexOf(CCAppConstants.TOKEN_BASE64_STR) + CCAppConstants.TOKEN_BASE64_STR.length());
            final byte[] excelFileBytes = org.apache.commons.codec.binary.Base64.decodeBase64(excelDataStr.getBytes());
            cardinals = new ExcelModelDataSourceCardinals(dsName, excelFileName, excelFileBytes);
        }
        return cardinals;
    }

    public static QueryResultTable fetchView(final String userId, final String queryData, final Map<String, String> dsDetails) throws AppException {
        if (dsDetails.size() == 0) {
            return null;
        } else {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                QueryRequest queryReq;
                QueryBuilderIDataSourceOperation dsOperation;
                final BaseDataSourceCardinals cardinals = createCardinalObj(dsDetails);
                if (cardinals instanceof ExcelModelDataSourceCardinals) {
                    queryReq = mapper.readValue(queryData, ExcelQueryRequest.class);
                    ((ExcelQueryRequest) queryReq).setCardinals((ExcelModelDataSourceCardinals) cardinals);
                    dsOperation = new ExcelUtils();
                } else {
                    queryReq = mapper.readValue(queryData.toString(), RdbQueryRequest.class);
                    final DbModelDataSourceCardinals dbCardilans = (DbModelDataSourceCardinals) cardinals;
                    ((RdbQueryRequest) queryReq).setCardinals(new RdbAccessCardinals(dbCardilans.getJdbcDriver(), dbCardilans.getDbUrl(), dbCardilans.getDbUserId(), dbCardilans.getDbPassword()));
                    dsOperation = new QueryBuilderRdbOperation();
                }
                return dsOperation.getViewFromQueryRequest(queryReq);
            } catch (final IOException ioEx) {
                Logger.error("Error in RDBMS query result fetch", ioEx);
                throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
            }
        }
    }

    public static QueryResultTable getViewDataFromEdgeName(final String userId, final String edgeName, final CoralConfigurationService coralConfigurationService) throws AppException {
        final List<Map<String, String>> cfgDetailsList = coralConfigurationService.getConfigurationByName(userId, edgeName);
        if (cfgDetailsList.size() == 0) {
            return null;
        }
        final Map<String, String> cfgData = cfgDetailsList.get(0);
        final String cfgInfo = cfgData.get(CCAppConstants.ATTR_VIEWCREATION_DETAILS);
        final JSONObject jsonObj = new JSONObject(cfgInfo);
        final String dsName = jsonObj.getJSONArray("columns").getJSONObject(0).getString("dsName");
        final String viewName = jsonObj.getString("name");
        final List<Map<String, String>> cfgDetailsListDs = coralConfigurationService.getConfigurationByName(userId, dsName);
        QueryResultTable dataToDump = null;
        try {
            dataToDump = DataSourceUtilsFactory.fetchView(userId, cfgInfo, cfgDetailsListDs.get(0));
            dataToDump.setName(viewName);
        } catch (final Exception e) {
            return null;
        }
        return dataToDump;
    }

    public static Map<String, Object> dumpToDocumentDb(final QueryResultTable dataToDump, final BiotaServices rdaRTDb) {
        final Map<String, Object> ret = new HashMap<String, Object>();
        try {
            if (rdaRTDb.insertData(dataToDump, true)) {
                ret.put("status", true);
                ret.put("data", dataToDump);
            } else {
                throw new Exception("Exception during insert data to documrnt db");
            }
        } catch (final Exception ex) {
            Logger.error("dumpToDocumentDb Exception", ex);
            ret.put("status", false);
            ret.put("errMessage", ex.getMessage());
        }
        return ret;
    }

    public static QueryResultTable jsonArrayToQueryResultTable(final JSONArray jsonArr, final List<ViewColumn> columns, final ColumnReference<ViewColumn> colRef, final String linkName) {
        final String fromTableName = colRef.getColumnFrom().getTableName();
        final QueryResultTable ret = new QueryResultTable();
        for (final ViewColumn viewColumn : columns) {
            if (viewColumn.getTableName().equalsIgnoreCase(fromTableName)) {
                ret.addColumnName(viewColumn.getName());
            }
        }
        ret.addColumnName(linkName);
        for (int idx = 0; idx < jsonArr.length(); idx++) {
            final List<String> aRow = new ArrayList<String>();
            final JSONObject eachRow = jsonArr.getJSONObject(idx);
            for (final String cName : ret.getColumnNames()) {
                if (!cName.equalsIgnoreCase(linkName)) {
                    aRow.add(eachRow.getString(cName));
                }
            }
            final JSONArray linkedRecs = eachRow.getJSONArray(linkName);
            aRow.add(linkedRecs.toString());
            ret.addRow(aRow);
        }
        return ret;
    }

    public static ViewQueryRequest deserializeViewQueryRequest(final String userId, final String cfgName, final CoralConfigurationService cfgDb) throws AppException {
        ViewQueryRequest viewQuery = null;
        final List<Map<String, String>> cfgDetailsList = cfgDb.getConfigurationByName(userId, cfgName);
        if (cfgDetailsList.size() == 0) {
            return null;
        }
        final Map<String, String> cfgData = cfgDetailsList.get(0);
        final String cfgInfo = cfgData.get(CCAppConstants.ATTR_VIEWCREATION_DETAILS);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            viewQuery = mapper.readValue(cfgInfo, ViewQueryRequest.class);
        } catch (final IOException ioEx) {
            Logger.error("IOException", ioEx);
        }
        return viewQuery;
    }

    public static QueryResultTable fetchLineageDbData(final String userId, final String queryData, final Map<String, String> dsDetails) throws AppException {
        if (dsDetails.size() == 0) {
            return null;
        } else {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                QueryRequest queryReq;
                QueryBuilderIDataSourceOperation dsOperation;
                final BaseDataSourceCardinals cardinals = createCardinalObj(dsDetails);
                queryReq = mapper.readValue(queryData, RdbQueryRequest.class);
                final DbModelDataSourceCardinals dbCardilans = (DbModelDataSourceCardinals) cardinals;
                ((RdbQueryRequest) queryReq).setCardinals(new RdbAccessCardinals(dbCardilans.getJdbcDriver(), dbCardilans.getDbUrl(), dbCardilans.getDbUserId(), dbCardilans.getDbPassword()));
                dsOperation = new QueryBuilderRdbOperation();
                return dsOperation.getViewFromQueryRequest(queryReq);
            } catch (final IOException ioEx) {
                Logger.error("Error in RDBMS query result fetch", ioEx);
                throw new AppException(AppErrorCode.RDBMS_OPERATION_ERROR);
            }
        }
    }
}
