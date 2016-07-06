// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// EasyTreeUtils.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.utils;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import com.parabole.cecl.application.global.RdaAppConstants;

/**
 * TODO
 *
 * @author Anish Chatterjee
 * @since v1.0
 */
public class EasyTreeUtils {

    public JSONObject createConfigObject(final JSONObject inputObj) {
        final JSONObject outputObj = new JSONObject();
        final JSONArray recArray = inputObj.getJSONArray(RdaAppConstants.ATTR_VIEWCREATION_DATA);
        createTreeModel(recArray, outputObj);
        return outputObj;
    }

    private void createTreeModel(final JSONArray recArray, final JSONObject outputObj) {
        final int numRecs = recArray.length();
        final JSONArray children = new JSONArray();
        outputObj.put("children", children);
        for (int i = 0; i < numRecs; i++) {
            final JSONObject obj = createTreeNodesfromObject(recArray.getJSONObject(i));
            if (obj != null) {
                children.put(obj);
            }
        }
    }

    private JSONObject createTreeNodesfromObject(final JSONObject input) {
        JSONObject outputObj = null;
        outputObj = fillObjectProperties(input);
        return outputObj;
    }

    private JSONObject fillObjectProperties(final JSONObject obj) {
        final JSONObject outputObj = new JSONObject();
        final Iterator<String> iter = obj.keys();
        outputObj.put("name", "Properties");
        outputObj.put("text", "Properties");
        final JSONArray tmpChildren = new JSONArray();
        outputObj.put("children", tmpChildren);
        while (iter.hasNext()) {
            final String propName = iter.next();
            final Object propVal = obj.get(propName);
            final JSONObject propObj = new JSONObject();
            tmpChildren.put(propObj);
            if (propVal instanceof String) {
                propObj.put("name", propName);
                propObj.put("text", propName + "( " + propVal + " )");
            } else {
                JSONArray tmp;
                if (null != propVal) {
                    if (propVal instanceof JSONObject) {
                        tmp = new JSONArray();
                        tmp.put(propVal);
                    } else {
                        tmp = (JSONArray) propVal;
                    }
                    propObj.put("name", propName);
                    propObj.put("text", propName);
                    createTreeModel(tmp, propObj);
                }
            }
        }
        return outputObj;
    }
}
