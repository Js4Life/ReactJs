// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// RGraph.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.reasoner;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.parabole.feed.platform.BaseDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Represents a Reasoner Hypergraph.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class RGraph extends BaseDTO {

    private static final long serialVersionUID = -1172613597422125119L;
    int noOfLevels = 0;
    final Multimap<Integer, RNode> graphs = ArrayListMultimap.create();
    final Multimap<Integer, REdge> hyperEdges = ArrayListMultimap.create();
    final Table<Integer, Integer, Integer> equiConceptLine = HashBasedTable.create();
    final Table<Integer, Integer, Integer> equiRelationLine = HashBasedTable.create();

    public void addLevel() {
        noOfLevels++;
    }

    public void addNode(final int level, final int vertexId, final int mappedVertexId) {
        graphs.put(level, new RNode(true, vertexId, 0.0f));
        equiConceptLine.put(level, vertexId, mappedVertexId);
    }

    public void addEdge(final int level, final int edgeId, final int fromVertexId, final int toVertexId, final float edgeWeight) {
        // ===================================================================
        // Create Reasoner-Graph Node
        // ===================================================================
        final Optional<RNode> fromVertexNode = findNode(level, fromVertexId);
        if (fromVertexNode.isPresent()) {
            fromVertexNode.get().addAdjacentNode(edgeId);
        }
        final RNode node = new RNode(false, edgeId, edgeWeight);
        node.addAdjacentNode(toVertexId);
        graphs.put(level, node);
        // ===================================================================
        // Create Reasoner-Graph HyperEdge
        // ===================================================================
        hyperEdges.put(edgeId, new REdge(level, fromVertexId, toVertexId, edgeWeight));
        equiRelationLine.put(level, edgeId, edgeId);
    }

    public String toJson() {
        final JSONObject finalJsonObject = new JSONObject();
        finalJsonObject.put("total", noOfLevels);
        // ======================================================
        // Graphs
        // ======================================================
        final JSONArray graphsJsonArray = new JSONArray();
        graphs.keySet().forEach((final Integer level) -> {
            final JSONObject levelJsonObject = new JSONObject();
            levelJsonObject.put("level", level);
            final JSONArray nodesJsonArray = new JSONArray();
            graphs.get(level).forEach((final RNode node) -> {
                final JSONObject nodeJsonObject = new JSONObject();
                nodeJsonObject.put("id", node.getQualifier());
                nodeJsonObject.put("concept", node.isConcept());
                nodeJsonObject.put("value", node.getValue());
                final JSONArray adjacentListJsonArray = new JSONArray();
                node.getAdjacencyList().forEach((final Integer peerNodeId) -> {
                    adjacentListJsonArray.put(peerNodeId);
                });
                nodeJsonObject.put("adjList", adjacentListJsonArray);
                nodesJsonArray.put(nodeJsonObject);
            });
            levelJsonObject.put("nodes", nodesJsonArray);
            graphsJsonArray.put(levelJsonObject);
        });
        finalJsonObject.put("graphs", graphsJsonArray);
        // ======================================================
        // HyperEdges
        // ======================================================
        final JSONArray hyperEdgesJsonArray = new JSONArray();
        hyperEdges.keySet().forEach((final Integer edgeId) -> {
            final JSONObject hyperEdgeJsonObject = new JSONObject();
            hyperEdgeJsonObject.put("id", edgeId);
            final JSONArray edgesJsonArray = new JSONArray();
            hyperEdges.get(edgeId).forEach((final REdge edge) -> {
                final JSONObject edgeJsonObject = new JSONObject();
                edgeJsonObject.put("level", edge.getLevel());
                edgeJsonObject.put("head", edge.getHead());
                edgeJsonObject.put("tail", edge.getTail());
                edgeJsonObject.put("weight", edge.getWeight());
                edgesJsonArray.put(edgeJsonObject);
            });
            hyperEdgeJsonObject.put("edges", edgesJsonArray);
            hyperEdgesJsonArray.put(hyperEdgeJsonObject);
        });
        finalJsonObject.put("hyperedges", hyperEdgesJsonArray);
        // ======================================================
        // Equivalent Concepts
        // ======================================================
        final JSONArray equiConceptsJsonArray = new JSONArray();
        equiConceptLine.cellSet().forEach((final Table.Cell<Integer, Integer, Integer> cell) -> {
            final JSONArray equiConceptJsonArray = new JSONArray();
            equiConceptJsonArray.put(cell.getRowKey());
            equiConceptJsonArray.put(cell.getColumnKey());
            equiConceptJsonArray.put(cell.getValue());
            equiConceptsJsonArray.put(equiConceptJsonArray);
        });
        finalJsonObject.put("equi-concepts", equiConceptsJsonArray);
        // ======================================================
        // Equivalent Relations
        // ======================================================
        final JSONArray equiRelationsJsonArray = new JSONArray();
        equiRelationLine.cellSet().forEach((final Table.Cell<Integer, Integer, Integer> cell) -> {
            final JSONArray equiRelationJsonArray = new JSONArray();
            equiRelationJsonArray.put(cell.getRowKey());
            equiRelationJsonArray.put(cell.getColumnKey());
            equiRelationJsonArray.put(cell.getValue());
            equiRelationsJsonArray.put(equiRelationJsonArray);
        });
        finalJsonObject.put("equi-relations", equiRelationsJsonArray);
        return finalJsonObject.toString();
    }

    private Optional<RNode> findNode(final int level, final int vertexId) {
        return graphs.get(level).stream().filter(node -> node.getQualifier() == vertexId).findFirst();
    }
}
