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
package com.parabole.cecl.platform.aggregator;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import play.Logger;

/**
 * Assimilators Aggregator
 * This will merge dynamic graph data into a single graph!
 * 
 * @author Sagiruddin Mondal
 * @since v1.0
 */

public class AssimilatorsAggregator {
	
	
	JSONArray combinedVertices;
	JSONArray combinedEdges;

	public AssimilatorsAggregator(JSONObject inputGraph){
		this.combinedVertices= new JSONArray();
		this.combinedEdges=new JSONArray();
		compose(inputGraph);	
	}
	
	public AssimilatorsAggregator compose(JSONObject newGraph){
		MergeNewVertices(newGraph);
		MergeNewEdges(newGraph);
		return this;		
	}
	
	private void MergeNewVertices(JSONObject newGraph){
		Map<String, JSONObject> allVertices = new HashMap<String, JSONObject>();
		if(this.combinedVertices != null){
			for (int i = 0; i < this.combinedVertices.length(); i++) {
				JSONObject existingVerticesobjects = this.combinedVertices.getJSONObject(i);
				String verticesId = existingVerticesobjects.get("id").toString();
				allVertices.put(verticesId, existingVerticesobjects);
			}	
		}
		JSONArray newVertices = (JSONArray) newGraph.get("vertices");	
		try {
			for (int i = 0; i < newVertices.length(); i++) {
				JSONObject newVerticesobjects = newVertices.getJSONObject(i);
				String newVerticesId = newVerticesobjects.get("id").toString();
				if(allVertices.get(newVerticesId)== null){
					allVertices.put(newVerticesId, newVerticesobjects);
					this.combinedVertices.put(newVerticesobjects);
				}
			}
		}catch (Exception e) {
			Logger.info("adding combinedVertices failed");
		}
	}
	
	private void MergeNewEdges(JSONObject newGraph){
		Map<String, JSONObject> allEdgesConnections = new HashMap<String, JSONObject>();
		JSONArray newEdges = (JSONArray) newGraph.get("connecions");	
		if(this.combinedEdges != null){
			for (int i = 0; i < this.combinedEdges.length(); i++) {
				JSONObject existingEdgesobjects = this.combinedEdges.getJSONObject(i);
				String from = existingEdgesobjects.get("from").toString();
				String to = existingEdgesobjects.get("to").toString();
					allEdgesConnections.put(from.concat("-").concat(to), existingEdgesobjects);
			}
		}
		try {
			for (int i = 0; i < newEdges.length(); i++) {
			JSONObject newVerticesobjects = newEdges.getJSONObject(i);
			String newEdgeFrom = newVerticesobjects.get("from").toString();
			String newEdgeTo = newVerticesobjects.get("to").toString();
				if(allEdgesConnections.get((newEdgeFrom.concat("-").concat(newEdgeTo))) == null){
					if(newVerticesobjects!=null)
					this.combinedEdges.put(newVerticesobjects);
					allEdgesConnections.put(newEdgeFrom.concat("-").concat(newEdgeTo), newVerticesobjects);
					}
				}		
			} catch (Exception e) {
			Logger.info("adding combinedEdges failed");
		}
	}
	
	public JSONObject getFinalAggregatedGraph(){
		JSONObject finalJsonObject = new JSONObject();
		finalJsonObject.put("vertices", this.combinedVertices);
		finalJsonObject.put("connecions", this.combinedEdges);
		return finalJsonObject;
	}

}
