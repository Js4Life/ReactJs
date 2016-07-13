// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// KGraph.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.knowledge;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.parabole.feed.platform.BaseDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents Knowledge in terms of a Graph with Adjacency Lists.
 *
 * @author Subhasis Sanyal, Sandip Bhaumik
 * @since v1.0 
 */
public class KGraph extends BaseDTO {

    private static final long serialVersionUID = -1172613597422125119L;
    private final Multimap<Integer, KEdge> adjacencies = ArrayListMultimap.create();

    public int getNumofVertices() {
		return adjacencies.size();
	}
	
	public int getNumofEdges() {
		int numOfEdge = 0;
		for(int i = 0; i < adjacencies.size() ; i++) {
			numOfEdge += getEdges(i).size();
		}
		return numOfEdge;
	}
	
	
	public boolean containsVertex(final int vertex) {
        return adjacencies.containsKey(vertex);
    }

    public boolean addEdge(final int id, final int source, final int destination, final float weight) {
        final KEdge newEdge = new KEdge(id, destination, weight);
        return adjacencies.containsEntry(source, newEdge) ? false : adjacencies.put(source, newEdge);
    }

    public Collection<KEdge> getEdges(final int vertex) {
        return Collections.unmodifiableCollection(adjacencies.get(vertex));
    }

    public String toJson() {
        final JSONObject finalJsonObject = new JSONObject();
        final JSONArray verticesJsonArray = new JSONArray();
        adjacencies.keySet().forEach((final Integer vertex) -> {
            final JSONArray edgesJsonArray = new JSONArray();
            adjacencies.get(vertex).forEach((final KEdge edge) -> {
                final JSONObject edgeJsonObject = new JSONObject();
                edgeJsonObject.put("id", edge.getId());
                edgeJsonObject.put("dest", edge.getDestination());
                edgeJsonObject.put("weight", edge.getWeight());
                edgesJsonArray.put(edgeJsonObject);
            });
            final JSONObject vertexJsonObject = new JSONObject();
            vertexJsonObject.put("node", vertex);
            vertexJsonObject.put("edges", edgesJsonArray);
            verticesJsonArray.put(vertexJsonObject);
            finalJsonObject.put("total", adjacencies.size());
            finalJsonObject.put("vertices", verticesJsonArray);
        });
        return finalJsonObject.toString();
    }

}
