// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// OctopusSemanticService.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.application.services;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.graphdb.Octopus;
import com.parabole.feed.platform.knowledge.KGraph;
import com.parabole.feed.platform.reasoner.RGraph;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


/**
 * Semantic Services for Octopus Rule-Engine DB.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Singleton
public class OctopusSemanticService {

    @Inject
    protected Octopus octopus;

    private static final List<String> ignoredAttributes = Lists.newArrayList(CCAppConstants.TAG, CCAppConstants.ELEMENT_ID);

    public String getBaseVertices() throws AppException {
        final JSONObject finalJsonObject = new JSONObject();
        final JSONArray verticesJsonArray = new JSONArray();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            final GraphQuery graphQuery = graphDbNoTx.query().has(CCAppConstants.ATTR_BASE_NODE, Compare.EQUAL, "true");
            final List<Vertex> vertices = Lists.newArrayList(graphQuery.vertices());
            vertices.forEach((final Vertex vertex) -> addVertexEntry(verticesJsonArray, vertex));
            finalJsonObject.put("vertices", verticesJsonArray);
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
        return finalJsonObject.toString();
    }

    public String getRelatedVertices(final Integer vertexId) throws AppException {
        Validate.notNull(vertexId, "'vertexId' cannot be null!");
        final JSONObject finalJsonObject = new JSONObject();
        final JSONArray verticesJsonArray = new JSONArray();
        final JSONArray conectionsJsonArray = new JSONArray();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            // ===================================================================
            // Retrieve the Vertex
            // ===================================================================
            final Vertex vertex = octopus.getVertex(graphDbNoTx, vertexId);
            if (null != vertex) {
                // ===================================================================
                // Retrieve the Unary Relations
                // ===================================================================
                final Set<Pair<Edge, Vertex>> adjacentUnaryRelationPairs = octopus.getAdjacentUnaryRelations(vertex);
                finalJsonObject.put("vertices", verticesJsonArray);
                finalJsonObject.put("connecions", conectionsJsonArray);
                if (CollectionUtils.isNotEmpty(adjacentUnaryRelationPairs)) {
                    adjacentUnaryRelationPairs.forEach((final Pair<Edge, Vertex> adjacentPair) -> {
                        final Vertex parentVertex = adjacentPair.getRight();
                        final Edge unaryEdge = adjacentPair.getLeft();
                        final Integer parentVertexId = octopus.getId(parentVertex);
                        addVertexEntry(verticesJsonArray, parentVertex);
                        addConnectionEntry(conectionsJsonArray, parentVertexId, vertexId, unaryEdge);
                    });
                }
                // ===================================================================
                // Retrieve the Binary Relations
                // ===================================================================
                final Set<Pair<Edge, Vertex>> adjacentBinaryRelationPairs = octopus.getAdjacentBinaryRelations(vertex);
                if (CollectionUtils.isNotEmpty(adjacentBinaryRelationPairs)) {
                    adjacentBinaryRelationPairs.forEach((final Pair<Edge, Vertex> adjacentPair) -> {
                        final Vertex peerVertex = adjacentPair.getRight();
                        final Edge binaryEdge = adjacentPair.getLeft();
                        final Integer peerVertexId = octopus.getId(peerVertex);
                        addVertexEntry(verticesJsonArray, peerVertex);
                        addConnectionEntry(conectionsJsonArray, vertexId, peerVertexId, binaryEdge);
                    });
                }
            }
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }

        nodeAssignmentOperation(finalJsonObject.getJSONArray("vertices"));

        return finalJsonObject.toString();
    }

    public String getRelatedVerticesByURI(final String uri) throws AppException {
        Validate.notNull(uri, "'vertexId' cannot be null!");
        final JSONObject finalJsonObject = new JSONObject();
        final JSONArray verticesJsonArray = new JSONArray();
        final JSONArray conectionsJsonArray = new JSONArray();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            // ===================================================================
            // Retrieve the Vertex
            // ===================================================================
            final Vertex vertex = octopus.getVertexByURI(graphDbNoTx, uri);
            if (null != vertex) {
                // ===================================================================
                // Retrieve the Unary Relations
                // ===================================================================
                final Set<Pair<Edge, Vertex>> adjacentUnaryRelationPairs = octopus.getAdjacentUnaryRelations(vertex);
                finalJsonObject.put("vertices", verticesJsonArray);
                finalJsonObject.put("connecions", conectionsJsonArray);
                if (CollectionUtils.isNotEmpty(adjacentUnaryRelationPairs)) {
                    adjacentUnaryRelationPairs.forEach((final Pair<Edge, Vertex> adjacentPair) -> {
                        final Vertex parentVertex = adjacentPair.getRight();
                        final Edge unaryEdge = adjacentPair.getLeft();
                        final String parentURI = octopus.getURI(parentVertex);
                        addVertexEntry(verticesJsonArray, parentVertex);
                        addConnectionEntryForURI(conectionsJsonArray, parentURI, uri, unaryEdge);
                    });
                }
                // ===================================================================
                // Retrieve the Binary Relations
                // ===================================================================
                final Set<Pair<Edge, Vertex>> adjacentBinaryRelationPairs = octopus.getAdjacentBinaryRelations(vertex);
                if (CollectionUtils.isNotEmpty(adjacentBinaryRelationPairs)) {
                    adjacentBinaryRelationPairs.forEach((final Pair<Edge, Vertex> adjacentPair) -> {
                        final Vertex peerVertex = adjacentPair.getRight();
                        final Edge binaryEdge = adjacentPair.getLeft();
                        final String peerURI = octopus.getURI(peerVertex);
                        addVertexEntry(verticesJsonArray, peerVertex);
                        addConnectionEntryForURI(conectionsJsonArray, uri, peerURI, binaryEdge);
                    });
                }
            }
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }

        nodeAssignmentOperation(finalJsonObject.getJSONArray("vertices"));

        return finalJsonObject.toString();
    }

    private void nodeAssignmentOperation(final JSONArray finalJsonOFVertices) throws AppException {
        for (int i = 0; i < finalJsonOFVertices.length(); i++) {
            final String assignment = AppUtils.getFileContent("json/assignment.json");
            //	JSONObject obj = new JSONObject(assignment);
            final JSONObject jsonObject = new JSONObject(assignment);
            final JSONObject objectVal = finalJsonOFVertices.getJSONObject(i);
            final String keyNodeName = objectVal.getString("name");
            if(jsonObject.has(keyNodeName)){
                final JSONObject jsonOBJ = jsonObject.getJSONObject(keyNodeName);
                JSONObject extraInfo = new JSONObject();
                if (objectVal.has("extraInfo")) {
                    extraInfo = objectVal.getJSONObject("extraInfo");
                } else {
                    objectVal.put("extraInfo", extraInfo);
                }
                extraInfo.put("definition", jsonOBJ.get("defination"));
                extraInfo.put("definitionLink", jsonOBJ.get("definationLink"));
            }
        }
    }


    public String findBaseVertexOf(final Integer vertexId) throws AppException {
        Validate.notNull(vertexId, "'vertexId' cannot be null!");
        Vertex baseVertex = null;
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            final Vertex vertex = octopus.getVertex(graphDbNoTx, vertexId);
            if (null != vertex) {
                baseVertex = octopus.getBaseVertex(vertex);
            }
            return (null == baseVertex) ? null : octopus.getName(baseVertex);
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
    }

    public String getVertexNameById(final Integer vertexId) {
        Validate.notNull(vertexId, "'vertexId' cannot be null!");
        System.out.println("Here --- >");
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            final Vertex vertex = octopus.getVertex(graphDbNoTx, vertexId);
            return (null == vertex) ? null : octopus.getName(vertex);
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
    }
    
    public String getVertexPropertyKeyById(final Integer vertexId) {
        Validate.notNull(vertexId, "'vertexId' cannot be null!");
        JSONObject vertexjsonObject = new JSONObject();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            final Vertex vertex = octopus.getVertex(graphDbNoTx, vertexId);
            vertex.getPropertyKeys().forEach((final String propertyKey) -> {
                if (!ignoredAttributes.contains(propertyKey)) {
                    vertexjsonObject.put(propertyKey, vertex.<Object> getProperty(propertyKey));
                    System.out.println(propertyKey + vertex.<Object> getProperty(propertyKey));
                }
            });
            return vertexjsonObject.toString();
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
    }

    public Set<Pair<Edge, Vertex>> getAllOutgoingVertices(final Integer vertexId) {
        Validate.notNull(vertexId, "'vertexId' cannot be null!");
        final Set<Pair<Edge, Vertex>> outputSet = new HashSet<Pair<Edge, Vertex>>();
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        try {
            final Vertex vertex = octopus.getVertex(graphDbNoTx, vertexId);
            if (null != vertex) {
                outputSet.addAll(octopus.getAdjacentBinaryRelations(vertex));
            }
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
        return outputSet;
    }

    private void addVertexEntry(final JSONArray verticesJsonArray, final Vertex vertex) {
        final JSONObject vertexjsonObject = new JSONObject();
        vertexjsonObject.put("id", octopus.getId(vertex));
        vertex.getPropertyKeys().forEach((final String propertyKey) -> {
            if (!ignoredAttributes.contains(propertyKey)) {
                vertexjsonObject.put(propertyKey, vertex.<Object> getProperty(propertyKey));
            }
        });
        verticesJsonArray.put(vertexjsonObject);
    }

    private void addConnectionEntry(final JSONArray conectionsJsonArray, final Integer fromVertexId, final Integer toVertexId, final Edge edge) {
        final JSONObject connectionJsonObject = new JSONObject();
        connectionJsonObject.put("from", fromVertexId);
        connectionJsonObject.put("to", toVertexId);
        connectionJsonObject.put("relType", octopus.getName(edge));
        edge.getPropertyKeys().forEach((final String propertyKey) -> {
            if (!ignoredAttributes.contains(propertyKey)) {
                connectionJsonObject.put(propertyKey, edge.<Object> getProperty(propertyKey));
            }
        });
        conectionsJsonArray.put(connectionJsonObject);
    }

    private void addConnectionEntryForURI(final JSONArray conectionsJsonArray, final String fromVertexURI, final String toVertexURI, final Edge edge) {
        final JSONObject connectionJsonObject = new JSONObject();
        connectionJsonObject.put("from", fromVertexURI);
        connectionJsonObject.put("to", toVertexURI);
        connectionJsonObject.put("relType", octopus.getName(edge));
        edge.getPropertyKeys().forEach((final String propertyKey) -> {
            if (!ignoredAttributes.contains(propertyKey)) {
                connectionJsonObject.put(propertyKey, edge.<Object> getProperty(propertyKey));
            }
        });
        conectionsJsonArray.put(connectionJsonObject);
    }

    public KGraph getFullKnowledgeGraph() throws AppException {
        return getKnowledgeGraph(octopus.considerAllEdges());
    }

    public KGraph getFullUnaryKnowledgeGraph() throws AppException {
        return getKnowledgeGraph(octopus.considerOnlyUnaryEdges());
    }

    public KGraph getFullBinaryKnowledgeGraph() throws AppException {
        return getKnowledgeGraph(octopus.considerOnlyUnaryEdges().negate());
    }

    public KGraph getFullBinaryKnowledgeGraphHavingSpecifiedEdges() throws AppException {
        return getKnowledgeGraph(octopus.considerOnlySpecifiedBinaryEdges());
    }

    public RGraph getFullReasonerGraph() throws AppException {
        return getReasonerGraph(octopus.considerAllEdges());
    }

    public RGraph getFullUnaryReasonerGraph() throws AppException {
        return getReasonerGraph(octopus.considerOnlyUnaryEdges());
    }

    public RGraph getFullBinaryReasonerGraph() throws AppException {
        return getReasonerGraph(octopus.considerOnlyUnaryEdges().negate());
    }

    public RGraph getFullBinaryReasonerGraphHavingSpecifiedEdges() throws AppException {
        return getReasonerGraph(octopus.considerOnlySpecifiedBinaryEdges());
    }

    private KGraph getKnowledgeGraph(final Predicate<Edge> predicate) throws AppException {
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        final KGraph graph = new KGraph();
        try {
            graphDbNoTx.getEdges().forEach((final Edge edge) -> {
                final Integer edgeId = octopus.getId(edge);
                if (null != edgeId) {
                    if (predicate.test(edge)) {
                        final Float edgeWeight = octopus.getWeight(edge);
                        final Vertex fromVertex = edge.getVertex(Direction.OUT);
                        final Integer fromVertexId = octopus.getId(fromVertex);
                        final Vertex toVertex = edge.getVertex(Direction.IN);
                        final Integer toVertexId = octopus.getId(toVertex);
                        graph.addEdge(edgeId, fromVertexId, toVertexId, edgeWeight);
                    }
                }
            });
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
        return graph;
    }

    private RGraph getReasonerGraph(final Predicate<Edge> predicate) throws AppException {
        final OrientGraphNoTx graphDbNoTx = octopus.getGraphConnectionNoTx();
        final RGraph hyperGraph = new RGraph();
        try {
            graphDbNoTx.getVertices().forEach((final Vertex vertex) -> {
                final Integer vertexId = octopus.getId(vertex);
                if (null != vertexId) {
                    hyperGraph.addNode(0, vertexId, vertexId);
                }
            });
            graphDbNoTx.getEdges().forEach((final Edge edge) -> {
                final Integer edgeId = octopus.getId(edge);
                if (null != edgeId) {
                    if (predicate.test(edge)) {
                        final Float edgeWeight = octopus.getWeight(edge);
                        final Vertex fromVertex = edge.getVertex(Direction.OUT);
                        final Integer fromVertexId = octopus.getId(fromVertex);
                        final Vertex toVertex = edge.getVertex(Direction.IN);
                        final Integer toVertexId = octopus.getId(toVertex);
                        hyperGraph.addEdge(0, edgeId, fromVertexId, toVertexId, edgeWeight);
                    }
                }
            });
        } finally {
            octopus.closeGraphConnection(graphDbNoTx);
        }
        return hyperGraph;
    }
}
