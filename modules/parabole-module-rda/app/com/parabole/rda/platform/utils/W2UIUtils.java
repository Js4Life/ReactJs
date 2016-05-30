// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// W2UIUtils.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.utils;

import java.util.Iterator;

import com.parabole.rda.platform.AppConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import com.parabole.rda.application.global.RdaAppConstants;

/**
 * TODO
 *
 * @author Anish Chatterjee
 * @since v1.0
 */
public class W2UIUtils {

    public JSONObject createConfigObject(final JSONObject inputObj, final String name) {
        final JSONObject outputObj = new JSONObject();
        createColumnConfig((JSONArray) inputObj.get(RdaAppConstants.ATTR_VIEWCREATION_COLS), outputObj);
        createRecordArray((JSONArray) inputObj.get(RdaAppConstants.ATTR_VIEWCREATION_DATA), outputObj);
        outputObj.put(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME, name);
        outputObj.put("header", name);
        return outputObj;
    }

    private void createRecordArray(final JSONArray inputRecs, final JSONObject outputObj) {
        final JSONArray w2Recs = new JSONArray();
        final int recSize = inputRecs.length();
        for (int i = 0; i < recSize; i++) {
            final JSONObject aRecord = new JSONObject();
            createARecordObj((JSONObject) inputRecs.get(i), aRecord);
            aRecord.put("recid", i + 1);
            w2Recs.put(aRecord);
        }
        outputObj.put(AppConstants.ATTR_CLASS_INSTANCES, w2Recs);
        return;
    }

    private void createARecordObj(final JSONObject inputRecObject, final JSONObject outputRecObj) {
        final Iterator<String> iter = inputRecObject.keys();
        while (iter.hasNext()) {
            final String propName = iter.next();
            final Object value = inputRecObject.get(propName);
            if (value instanceof JSONArray) {
                final JSONArray arr = (JSONArray) value;
                createRowRepeat(arr, outputRecObj);
            } else {
                if (value instanceof JSONObject) {
                    createARecordObj((JSONObject) value, outputRecObj);
                } else {
                    outputRecObj.put(propName, value);
                }
            }
        }
        return;
    }

    private void createRowRepeat(final JSONArray arr, final JSONObject outputRecObj) {
        if (arr.length() == 0) {
            return;
        }
        for (int i = 0; i < arr.length(); i++) {
            final JSONObject obj = arr.getJSONObject(i);
            final Iterator<String> iter = obj.keys();
            while (iter.hasNext()) {
                final String name = iter.next();
                if (!outputRecObj.has(name)) {
                    outputRecObj.put(name, "");
                }
                final String tmp = outputRecObj.getString(name);
                outputRecObj.put(name, tmp + "<div>" + obj.get(name) + "</div>");
            }
        }
    }

    private void createColumnConfig(final JSONArray cols, final JSONObject outputObj) {
        final int colSize = cols.length();
        final JSONArray w2Cols = new JSONArray();
        final JSONArray w2ColGroups = new JSONArray();
        for (int i = 0; i < colSize; i++) {
            final Object val = cols.get(i);
            if (val instanceof String) {
                createSimpleColumn((String) val, w2ColGroups, w2Cols);
            } else if (val instanceof JSONObject) {
                createColumnSet((JSONObject) val, w2ColGroups, w2Cols);
            }
        }
        outputObj.put(RdaAppConstants.ATTR_VIEWCREATION_COLS, w2Cols);
        outputObj.put("columnGroups", w2ColGroups);
    }

    private void createColumnSet(final JSONObject val, final JSONArray w2ColGroups, final JSONArray w2Cols) {
        final String linkName = val.keys().next();
        final JSONArray joinedCols = val.getJSONArray(linkName);
        final int colsLength = joinedCols.length();
        final JSONObject aColGroup = createColumnGroup(colsLength, linkName, false);
        w2ColGroups.put(aColGroup);
        for (int i = 0; i < colsLength; i++) {
            w2Cols.put(createAColumn((String) joinedCols.get(i)));
        }
    }

    private JSONObject createAColumn(final String colName) {
        final JSONObject colObj = new JSONObject();
        colObj.put("field", colName);
        colObj.put("caption", colName);
        colObj.put("sortable", true);
        colObj.put("resizable", true);
        colObj.put("size", "10%");
        return colObj;
    }

    private JSONObject createColumnGroup(final int length, final String linkName, final boolean isMaster) {
        final JSONObject colGroup = new JSONObject();
        colGroup.put("span", length);
        colGroup.put("caption", linkName);
        colGroup.put("master", isMaster);
        return colGroup;
    }

    private void createSimpleColumn(final String val, final JSONArray w2ColGroups, final JSONArray w2Cols) {
        final JSONObject aColGroup = createColumnGroup(1, "", true);
        w2ColGroups.put(aColGroup);
        w2Cols.put(createAColumn(val));
    }
}
