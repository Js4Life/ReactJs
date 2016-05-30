// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// BaseColumn.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.aggregator;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import play.Logger;

/**
 * Logical View Associator
 * This Class will return merged Logical DB view if they are from the same table.
 * 
 * @author Sagiruddin Mondal
 * @since v1.0
 */

public class LogicalViewComposing {
	
	JSONArray columns;
	JSONArray references;
	String name;
	String rootClass;
	
	public LogicalViewComposing(JSONObject logicalView){
		this.columns= new JSONArray();
		this.references=new JSONArray();
		compose(logicalView);	
	}

	public LogicalViewComposing compose(JSONObject logicalView) {
		System.out.println(logicalView.toString());
		if(this.name == null){
			this.name = logicalView.getString("name");
		}else{
			this.name = this.name.concat("_").concat(logicalView.getString("name"));
		}
		this.rootClass = logicalView.getString("rootClass");
		JSONArray columnsArray = logicalView.getJSONArray("columns");
		JSONArray referencesArray = logicalView.getJSONArray("references");
		composeColumns(columnsArray);
		composeReferences(referencesArray);
		return this;
	}

	private void composeColumns(JSONArray columnsArray) {
		Map<String, JSONObject> allColumns = new HashMap<String, JSONObject>();
		try {
			if(this.columns != null){
				for (int i = 0; i < columns.length(); i++) {
					JSONObject jsonObject = columns.getJSONObject(i);
					String generatedKey = jsonObject.getString("name")+jsonObject.getString("tableName")+jsonObject.getString("edgeName");
					allColumns.put(generatedKey, jsonObject);
				}
			}
			for (int i = 0; i < columnsArray.length(); i++) {
				JSONObject newJsonObject = columnsArray.getJSONObject(i);
				String newGeneratedKey = newJsonObject.getString("name")+newJsonObject.getString("tableName")+newJsonObject.getString("edgeName");
				if(allColumns.get(newGeneratedKey) == null){
					allColumns.put(newGeneratedKey, newJsonObject);
					this.columns.put(newJsonObject);
				}
			}
		} catch (Exception e) {
			Logger.info("adding columns is failed");
		}
	}

	private void composeReferences(JSONArray referencesArray) {
		Map<String, JSONObject> allReferences = new HashMap<String, JSONObject>();
		try {
			if(this.references != null){
				for (int i = 0; i < references.length(); i++) {
					JSONObject jsonObject = references.getJSONObject(i);
					String generatedKey = this.name+jsonObject.getString("name");
					allReferences.put(generatedKey, jsonObject);
				}
			}
			for (int i = 0; i < referencesArray.length(); i++) {
				JSONObject newJsonObject = referencesArray.getJSONObject(i);
				String newGeneratedKey = newJsonObject.getString("name");
				if(allReferences.get(newGeneratedKey) == null){
					if(!ifDuplicateReferance(newJsonObject)){
						allReferences.put(newGeneratedKey, newJsonObject);
						this.references.put(newJsonObject);
					}		
				}
			}
		} catch (Exception e) {
			Logger.info("adding reference is failed");
		}
	}
	
	private Boolean  ifDuplicateReferance(JSONObject newJsonObject) {
		Boolean result = false;
		Map<String, JSONObject> trackedReferanceObjects = new HashMap<String, JSONObject>();
		for (int i = 0; i < references.length(); i++) {
			JSONObject jsonObject = references.getJSONObject(i);
			final String generatedKey = jsonObject.getJSONObject("columnFrom").getString("name")
					.concat(jsonObject.getJSONObject("columnFrom").getString("tableName"))
					.concat(jsonObject.getJSONObject("columnTo").getString("name"))
					.concat(jsonObject.getJSONObject("columnTo").getString("tableName"));
			trackedReferanceObjects.put(generatedKey, jsonObject);
		}
		final String newGeneratedKey = newJsonObject.getJSONObject("columnFrom").getString("name")
				.concat(newJsonObject.getJSONObject("columnFrom").getString("tableName"))
				.concat(newJsonObject.getJSONObject("columnTo").getString("name"))
				.concat(newJsonObject.getJSONObject("columnTo").getString("tableName"));
		if(trackedReferanceObjects.get(newGeneratedKey) != null){
			result = true;
		}
		return result;
	}

	public JSONObject getFinalAggregatedDBView(){
		JSONObject finalJsonObject = new JSONObject();
		finalJsonObject.put("name", this.name);
		finalJsonObject.put("rootClass", this.rootClass);
		finalJsonObject.put("columns", this.columns);
		finalJsonObject.put("references", this.references);
		return finalJsonObject;
	}
	

}
