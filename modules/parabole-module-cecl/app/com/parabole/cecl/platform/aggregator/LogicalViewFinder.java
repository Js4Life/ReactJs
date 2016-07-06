// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// OctopusAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.aggregator;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Logical view finder
 * associate logical view with newly created merged graph
 * 
 * @author Sagiruddin Mondal
 * @since v1.0
 */

public class LogicalViewFinder {

	public String findLogicalViewAssociatedWithNode(List<Map<String, String>> combinedLogicalView){
		String finalReturn = null;
		JSONArray result = new JSONArray();
		if(combinedLogicalView.size() > 1){
			for (int i = 0; i < combinedLogicalView.size()-1; i++) {
				JSONObject logicalviewData	 = new JSONObject(combinedLogicalView.get(i).get("details"));
				String referenceFromTableName = logicalviewData.getJSONArray("references")
						.getJSONObject(0)
						.getJSONObject("columnFrom")
						.getString("tableName");
				String referanceToTableName = logicalviewData.getJSONArray("references")
						.getJSONObject(0)
						.getJSONObject("columnTo")
						.getString("tableName");
				
				JSONObject secondLogicalviewData	 = new JSONObject(combinedLogicalView.get(i+1).get("details"));
				String secondReferenceFromTableName = logicalviewData.getJSONArray("references")
						.getJSONObject(0)
						.getJSONObject("columnFrom")
						.getString("tableName");
				String secondReferanceToTableName = logicalviewData.getJSONArray("references")
						.getJSONObject(0)
						.getJSONObject("columnTo")
						.getString("tableName");
				if(referenceFromTableName.equals(secondReferenceFromTableName)&& referanceToTableName.equals(secondReferanceToTableName)){
					result.put(callComposeLogicalViewSystem(logicalviewData, secondLogicalviewData));
				}else{
					result.put(logicalviewData);
					result.put(secondLogicalviewData);
				}
			}
			
			finalReturn = result.toString();
					
		}else{
			for (Map<String, String> map : combinedLogicalView) {
				finalReturn =  map.get("details").toString();
			}
		}
		return finalReturn;
	}

	private JSONObject callComposeLogicalViewSystem(JSONObject logicalviewData, JSONObject secondLogicalviewData) {
		LogicalViewComposing logicalViewComposing = new LogicalViewComposing(logicalviewData);
		logicalViewComposing.compose(secondLogicalviewData);
		return logicalViewComposing.getFinalAggregatedDBView();
	}

}
