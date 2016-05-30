// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// OctopusImpactService.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.parabole.rda.application.global.RdaAppConstants;
import com.parabole.rda.platform.exceptions.AppException;
import com.parabole.rda.platform.graphdb.Octopus;
import com.parabole.rda.platform.knowledge.KGraphPathFinder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parabole.rda.platform.knowledge.KGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

/**
 * Octopus Impact Services.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */

@Singleton
public class OctopusImpactService {

    @Inject
    protected Octopus octopus;

    @Inject
    protected OctopusSemanticService octopusSemanticService;

    private int count = 0;
    private int listCount = 0;
    private Integer pathCount =0;
    
    private static final List<String> ignoredAttributes = Lists.newArrayList(RdaAppConstants.TAG, RdaAppConstants.ELEMENT_ID);
    
    public String getImpactLevelForGivenNodes(final Integer fromNode, final JSONArray toNodeList) throws AppException {
        final Integer maxPathLength = 10;
        final JSONObject graphdata = new JSONObject();
        final JSONArray verticesJsonArray = new JSONArray();
        final JSONObject vertextJsonObject = new JSONObject();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        vertextJsonObject.put("id", fromNode);
		System.out.println("from Node " + fromNode);
        vertextJsonObject.put("name", octopus.getVertex(graphDbNoTx, fromNode).getProperty("name").toString());  
		System.out.println("name " + octopus.getVertex(graphDbNoTx, fromNode).getProperty("name").toString());	
        vertextJsonObject.put("level", "999");
        verticesJsonArray.put(vertextJsonObject);
        for (int i = 0; i < toNodeList.length(); i++) {
        	final KGraph graph = octopusSemanticService.getFullBinaryKnowledgeGraph();
            final KGraphPathFinder finder = new KGraphPathFinder(graph);
            final KGraphPathFinder.TraverseResult result = finder.findAllPathsBetweenTwoNodes(fromNode, toNodeList.getInt(i), maxPathLength, null);
            nodeColorDefination(result.getTotalWeight().floatValue(), result.getPathListVertices().size());
            createVisNode(result, fromNode, toNodeList.getInt(i), verticesJsonArray);
		}
        
        return (graphdata.putOpt("vertices", verticesJsonArray).toString());
    }
    
    private void createVisNode(KGraphPathFinder.TraverseResult result, Integer fromNode, Integer toNode, JSONArray verticesJsonArray) {
    	final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
    	final JSONObject vertextJsonObject = new JSONObject();
        vertextJsonObject.put("id", toNode);
        vertextJsonObject.put("name", octopus.getVertex(graphDbNoTx, toNode).getProperty("name").toString());   
        vertextJsonObject.put("level", nodeColorDefination(result.getTotalWeight().floatValue(), result.getPathListVertices().size()));
        verticesJsonArray.put(vertextJsonObject);
	}

    public String getPathBetweenTwoNodesFromKnowledgeGraph(final Integer fromNode, final Integer level, final Integer toNode, final Integer impactDirection) throws AppException {
        final Integer maxPathLength = 10;
        final KGraph graph = octopusSemanticService.getFullBinaryKnowledgeGraph();
        final KGraphPathFinder finder = new KGraphPathFinder(graph);
        final KGraphPathFinder.TraverseResult result = finder.findAllPathsBetweenTwoNodes(fromNode, toNode, maxPathLength, null);
        Logger.info("No of Paths [" + fromNode + "] to [" + toNode + "] = " + result.getPathListVertices().size() + " Total Weight: " + result.getTotalWeight());
        return createVisGraphFormatJson(result, fromNode, toNode);
    }

    private String createVisGraphFormatJson(final KGraphPathFinder.TraverseResult result, final int rootVertex, final int destinationVertexId) {
        final JSONObject outputJson = new JSONObject();
        final JSONObject graphdata = new JSONObject();
        final JSONArray verticesJsonArray = new JSONArray();
        final JSONArray connecionsJsonArray = new JSONArray();
        final List<ArrayList<Integer>> pathListEdges = result.getPathListEdges();
        final Float totalWeightOfThePath = result.getTotalWeight().floatValue();
        final Integer totalCountOfThePath = result.getPathListVertices().size();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        final Set<Integer> visitedVerticesSet = new HashSet<Integer>();
        try {
            pathListEdges.forEach((final ArrayList<Integer> path) -> {	
            	this.listCount = path.size();
            	pathCount = 1;
                path.forEach((final Integer edgeId) -> { 
                	final JSONObject connectionJsonObject = new JSONObject();
                    final Edge edge = octopus.getEdge(graphDbNoTx, edgeId);
                    final Vertex fromVertex = edge.getVertex(Direction.OUT);
                    final Vertex toVertex = edge.getVertex(Direction.IN);
                    checkAndCreateVertexEntry(verticesJsonArray, fromVertex, rootVertex, destinationVertexId, visitedVerticesSet, "999");            
                    if (this.listCount == 1) {
                    	final int vertexId = octopus.getId(toVertex);
                    	if (!visitedVerticesSet.contains(vertexId)) {
                    	final JSONObject vertextJsonObject = new JSONObject();
                        vertextJsonObject.put("id", vertexId);
                        vertextJsonObject.put("name", octopus.getName(toVertex));  
                        vertextJsonObject.put("level", nodeColorDefination(totalWeightOfThePath, totalCountOfThePath ));
                            final Set<String> propertyKeys = toVertex.getPropertyKeys();
                            for (final String propertyKey : propertyKeys) {
                                if (!ignoredAttributes.contains(propertyKey)) {
                                	vertextJsonObject.put(propertyKey, toVertex.getProperty(propertyKey).toString());
                                }
                            }        
                        verticesJsonArray.put(vertextJsonObject);
                        
                    }
                        visitedVerticesSet.add(vertexId);                   	
                    }else{     	
                    	checkAndCreateVertexEntry(verticesJsonArray, toVertex, rootVertex, destinationVertexId, visitedVerticesSet, pathCount.toString());
                        
                    }
                    connectionJsonObject.put("from", octopus.getId(fromVertex).toString());
                    connectionJsonObject.put("to", octopus.getId(toVertex).toString());
                    connectionJsonObject.put("relType", octopus.getName(edge));
                    connectionJsonObject.put("level", pathCount);
                    connecionsJsonArray.put(connectionJsonObject);
                    this.listCount--;
                    pathCount++;
                });
                this.count++;
            });
            this.listCount = 0;
            this.count = 0;
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
        
        graphdata.putOpt("vertices", verticesJsonArray);
        graphdata.putOpt("connecions", connecionsJsonArray);
        outputJson.put("graphData", graphdata);
        return outputJson.toString();
    }

	private void checkAndCreateVertexEntry(final JSONArray verticesJsonArray, final Vertex vertex,  final int rootVertex, final int destinationVertexId, final Set<Integer> visitedVerticesSet, String weightAsPerPath) {
        final int vertexId = octopus.getId(vertex);
        if( vertexId != destinationVertexId){
	        if (!visitedVerticesSet.contains(vertexId)) {
	            final JSONObject vertextJsonObject = new JSONObject();
	            vertextJsonObject.put("id", vertexId);
	            vertextJsonObject.put("name", octopus.getName(vertex));   
	            vertextJsonObject.put("level", weightAsPerPath.toString());
	            final Set<String> propertyKeys = vertex.getPropertyKeys();
	            for (final String propertyKey : propertyKeys) {
                    if (!ignoredAttributes.contains(propertyKey)) {
                    	vertextJsonObject.put(propertyKey, vertex.getProperty(propertyKey).toString());
                    }
                }
	            verticesJsonArray.put(vertextJsonObject);
	            visitedVerticesSet.add(vertexId);
	        }
        }
    }
	
	private String nodeColorDefination(Float weightAsPerPath, Integer countOfPath){
	        Float normalisedWeight = weightAsPerPath/countOfPath;
	        String colorWeight;
	        
	       if(normalisedWeight == 0.0){
	    	   colorWeight = "0";
	       }else if(normalisedWeight >0.0 && normalisedWeight <= 0.1){
	    	   colorWeight = "1";
	       }else if(normalisedWeight >0.1 && normalisedWeight <= 0.2){
	    	   colorWeight = "2";
	       }else if(normalisedWeight >0.2 && normalisedWeight <= 0.3){
	    	   colorWeight = "3";
	       }else if(normalisedWeight >0.3 && normalisedWeight <= 0.4){
	    	   colorWeight = "4";
	       }else if(normalisedWeight >0.4 && normalisedWeight <= 0.5){
	    	   colorWeight = "5";
	       }else if(normalisedWeight >0.5 && normalisedWeight <= 0.6){
	    	   colorWeight = "6";
	       }else if(normalisedWeight >0.6 && normalisedWeight <= 0.7){
	    	   colorWeight = "7";
	       }else if(normalisedWeight >0.7 && normalisedWeight <= 0.8){
	    	   colorWeight = "8";
	       }else if(normalisedWeight >0.8 && normalisedWeight <= 0.9){
	    	   colorWeight = "9";
	       }else if(normalisedWeight >0.9 && normalisedWeight <= 1.0){
	    	   colorWeight = "10";
	       }else{
	    	   colorWeight = "100";
	       }
	       
	       return(colorWeight);
    }

    public String mergedConnectedGraphAndWeightedGraph(final Integer vertexId, final int level) {
        final JSONObject mergedJson = new JSONObject();
        final String weightedGraph = getImpactedVerticesEndToEndLevel(vertexId, level);
        final String connectedGraph = getImpactedVertices(vertexId, level);
        mergedJson.put("connectedGraph", connectedGraph);
        mergedJson.put("weightedGraph", weightedGraph);
        return mergedJson.toString();
    }

    public String getImpactedVertices(final Integer rootVertexId, final int level) {
        Validate.notNull(rootVertexId, "'rootVertexId' cannot be null!");
        final JSONObject finalJsonObject = new JSONObject();
        final JSONObject graphData = new JSONObject();
        final JSONArray verticesJsonArray = new JSONArray();
        final JSONArray connectionsJsonArray = new JSONArray();
        final ArrayList<Integer> visitedVertices = new ArrayList<Integer>();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            final Vertex rootVertex = octopus.getVertex(graphDbNoTx, rootVertexId);
            if (null != rootVertex) {
                final JSONObject collectedVertexDetails = addVertexEntryObject(rootVertex, 0);
                verticesJsonArray.put(collectedVertexDetails);
                visitedVertices.add(rootVertexId);
                final Map<Integer, JSONArray> listOfLevelOrientedJsonObject = new HashMap<Integer, JSONArray>();
                addImpactedVertices(rootVertex, verticesJsonArray, connectionsJsonArray, 1, level, visitedVertices, listOfLevelOrientedJsonObject);
                graphData.put("vertices", verticesJsonArray);
                graphData.put("connecions", connectionsJsonArray);
                finalJsonObject.put("graphData", graphData);
                finalJsonObject.put("chartData", listOfLevelOrientedJsonObject);
            }
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
        return finalJsonObject.toString();
    }

    public String getImpactedVerticesEndToEndLevel(final Integer rootVertexId, final int level) {
        Validate.notNull(rootVertexId, "'rootVertexId' cannot be null!");
        final JSONObject finalJsonObject = new JSONObject();
        final JSONObject graphData = new JSONObject();
        final JSONArray verticesJsonArray = new JSONArray();
        final JSONArray connectionsJsonArray = new JSONArray();
        final ArrayList<Integer> visitedVertices = new ArrayList<Integer>();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            final Vertex rootVertex = octopus.getVertex(graphDbNoTx, rootVertexId);
            if (null != rootVertex) {
                final JSONObject collectedVertexDetails = addVertexEntryObject(rootVertex, 0);
                verticesJsonArray.put(collectedVertexDetails);
                visitedVertices.add(rootVertexId);
                final Map<Integer, JSONArray> listOfLevelOrientedJsonObject = new HashMap<Integer, JSONArray>();
                addImpactedVerticesEndToEndLevel(rootVertex, verticesJsonArray, connectionsJsonArray, 1, level, visitedVertices, listOfLevelOrientedJsonObject, rootVertexId);
                graphData.put("vertices", verticesJsonArray);
                graphData.put("connecions", connectionsJsonArray);
                finalJsonObject.put("graphData", graphData);
                finalJsonObject.put("chartData", listOfLevelOrientedJsonObject);
            }
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
        return finalJsonObject.toString();
    }

    private void addImpactedVerticesEndToEndLevel(final Vertex vertex, final JSONArray verticesJsonArray, final JSONArray connectionsJsonArray, final int currentLevel, final int threasholdLevel, final ArrayList<Integer> visitedVertices, final Map<Integer, JSONArray> listOfLevelOrientedJsonObject, final Integer vertexIdFixed) {
        if (currentLevel <= threasholdLevel) {
            final Set<Pair<Edge, Vertex>> adjacentBinaryRelationPairs = octopus.getAdjacentBinaryRelations(vertex);
            if (CollectionUtils.isNotEmpty(adjacentBinaryRelationPairs)) {
                adjacentBinaryRelationPairs.forEach((final Pair<Edge, Vertex> adjacentPair) -> {
                    final Vertex peerVertex = adjacentPair.getRight();
                    final Integer peerVertexId = octopus.getId(peerVertex);
                    final Edge binaryEdge = adjacentPair.getLeft();
                    final String relationType = octopus.getName(binaryEdge);
                    if (!visitedVertices.contains(peerVertexId)) {
                        if (currentLevel == threasholdLevel) {
                            final JSONObject collectedVertexDetails = addVertexEntryObject(peerVertex, currentLevel);
                            verticesJsonArray.put(collectedVertexDetails);
                            final JSONObject connectionJsonObject = new JSONObject();
                            connectionJsonObject.put("from", vertexIdFixed);
                            connectionJsonObject.put("to", peerVertexId);
                            connectionJsonObject.put("relType", relationType);
                            connectionsJsonArray.put(connectionJsonObject);
                            if (listOfLevelOrientedJsonObject.containsKey(currentLevel)) {
                                listOfLevelOrientedJsonObject.get(currentLevel).put(collectedVertexDetails);
                            } else {
                                final JSONArray jsonArrayForNewLevel = new JSONArray();
                                jsonArrayForNewLevel.put(collectedVertexDetails);
                                listOfLevelOrientedJsonObject.put(currentLevel, jsonArrayForNewLevel);
                            }
                        }
                        visitedVertices.add(peerVertexId);
                        addImpactedVerticesEndToEndLevel(peerVertex, verticesJsonArray, connectionsJsonArray, (currentLevel + 1), threasholdLevel, visitedVertices, listOfLevelOrientedJsonObject, vertexIdFixed);
                    }
                });
            }
        }
    }

    private void addImpactedVertices(final Vertex vertex, final JSONArray verticesJsonArray, final JSONArray connectionsJsonArray, final int currentLevel, final int threasholdLevel, final ArrayList<Integer> visitedVertices, final Map<Integer, JSONArray> listOfLevelOrientedJsonObject) {
        if (currentLevel <= threasholdLevel) {
            final Set<Pair<Edge, Vertex>> adjacentBinaryRelationPairs = octopus.getAdjacentBinaryRelations(vertex);
            if (CollectionUtils.isNotEmpty(adjacentBinaryRelationPairs)) {
                adjacentBinaryRelationPairs.forEach((final Pair<Edge, Vertex> adjacentPair) -> {
                    final Vertex peerVertex = adjacentPair.getRight();
                    final Integer peerVertexId = octopus.getId(peerVertex);
                    final Edge binaryEdge = adjacentPair.getLeft();
                    final String relationType = octopus.getName(binaryEdge);
                    if (!visitedVertices.contains(peerVertexId)) {
                        final JSONObject collectedVertexDetails = addVertexEntryObject(peerVertex, currentLevel);
                        verticesJsonArray.put(collectedVertexDetails);
                        final JSONObject connectionJsonObject = new JSONObject();
                        connectionJsonObject.put("from", octopus.getId(vertex));
                        connectionJsonObject.put("to", peerVertexId);
                        connectionJsonObject.put("relType", relationType);
                        connectionsJsonArray.put(connectionJsonObject);
                        if (listOfLevelOrientedJsonObject.containsKey(currentLevel)) {
                            listOfLevelOrientedJsonObject.get(currentLevel).put(collectedVertexDetails);
                        } else {
                            final JSONArray jsonArrayForNewLevel = new JSONArray();
                            jsonArrayForNewLevel.put(collectedVertexDetails);
                            listOfLevelOrientedJsonObject.put(currentLevel, jsonArrayForNewLevel);

                        }
                        visitedVertices.add(peerVertexId);
                        addImpactedVertices(peerVertex, verticesJsonArray, connectionsJsonArray, (currentLevel + 1), threasholdLevel, visitedVertices, listOfLevelOrientedJsonObject);
                    }
                });
            }
        }
    }

    private JSONObject addVertexEntryObject(final Vertex vertex, final int currentLevel) {
        final JSONObject vertexjsonObject = new JSONObject();
        vertexjsonObject.put("id", octopus.getId(vertex));
        vertexjsonObject.put("level", currentLevel);
        vertex.getPropertyKeys().forEach((final String propertyKey) -> {
            if (!ignoredAttributes.contains(propertyKey)) {
                vertexjsonObject.put(propertyKey, vertex.<Object> getProperty(propertyKey));
            }
        });
        return vertexjsonObject;
    }


}
