// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// Octopus.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.graphdb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.parabole.rda.platform.AppConstants;
import com.parabole.rda.platform.utils.AppUtils;
import com.parabole.rda.application.controllers.ActionAuthenticator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import com.parabole.rda.application.global.RdaAppConstants;
import com.tinkerpop.blueprints.Compare;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import play.mvc.Security;

/**
 * Semantic Rules Graph-Database.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Security.Authenticated(ActionAuthenticator.class)
@Singleton
public class Octopus extends GraphDb {

    public static final boolean filterEdge = CollectionUtils.isNotEmpty(RdaAppConstants.RDA_RELATIONSHIPS);

    public Octopus() {
        final String graphDbUrl = AppUtils.getApplicationProperty(RdaAppConstants.INDUSTRY + ".octopus.graphdb.url");
        final String graphDbUser = AppUtils.getApplicationProperty(RdaAppConstants.INDUSTRY + ".octopus.graphdb.user");
        final String graphDbPassword = AppUtils.getApplicationProperty(RdaAppConstants.INDUSTRY + ".octopus.graphdb.password");
        final Integer graphDbPoolMinSize = AppUtils.getApplicationPropertyAsInteger(RdaAppConstants.INDUSTRY + ".octopus.graphdb.pool.min");
        final Integer graphDbPoolMaxSize = AppUtils.getApplicationPropertyAsInteger(RdaAppConstants.INDUSTRY + ".octopus.graphdb.pool.max");
        this.orientGraphFactory = new OrientGraphFactory(graphDbUrl, graphDbUser, graphDbPassword).setupPool(graphDbPoolMinSize, graphDbPoolMaxSize);
    }

    public Predicate<Edge> considerAllEdges() {
        return edge -> true;
    }

    public Predicate<Edge> considerOnlyUnaryEdges() {
        return edge -> AppConstants.IsA_RELATIONSHIP.equalsIgnoreCase(getName(edge));
    }

    public Predicate<Edge> considerOnlySpecifiedBinaryEdges() {
        return edge -> filterEdge && RdaAppConstants.RDA_RELATIONSHIPS.contains(getName(edge));
    }

    public Vertex getVertex(final OrientGraphNoTx graphDbNoTx, final Integer vertexId) {
        Validate.notNull(graphDbNoTx, "'graphDbNoTx' cannot be null!");
        Validate.notNull(vertexId, "'vertexId' cannot be null!");
        final GraphQuery graphQuery = graphDbNoTx.query().has(AppConstants.ELEMENT_ID, Compare.EQUAL, vertexId);
        final List<Vertex> vertices = Lists.newArrayList(graphQuery.vertices());
        return vertices.isEmpty() ? null : vertices.get(0);
    }

    public Vertex getVertex(final OrientGraphNoTx graphDbNoTx, final String vertexName) {
        Validate.notNull(graphDbNoTx, "'graphDbNoTx' cannot be null!");
        Validate.notBlank(vertexName, "'vertexName' cannot be null!");
        for (final Vertex vertex : graphDbNoTx.getVertices(AppConstants.NAME, vertexName)) {
            return vertex;
        }
        return null;
    }

    public Edge getEdge(final OrientGraphNoTx graphDbNoTx, final Integer edgeId) {
        Validate.notNull(graphDbNoTx, "'graphDbNoTx' cannot be null!");
        Validate.notNull(edgeId, "'edgeId' cannot be null!");
        final GraphQuery graphQuery = graphDbNoTx.query().has(AppConstants.ELEMENT_ID, Compare.EQUAL, edgeId);
        final List<Edge> edges = Lists.newArrayList(graphQuery.edges());
        return edges.isEmpty() ? null : edges.get(0);
    }

    public String getName(final Element element) {
        Validate.notNull(element, "'element' cannot be null!");
        final Object returnObject = element.getProperty(AppConstants.NAME);
        return (null == returnObject) ? null : (String) returnObject;
    }

    public Integer getId(final Vertex vertex) {
        Validate.notNull(vertex, "'vertex' cannot be null!");
        final Object returnObject = vertex.getProperty(AppConstants.ELEMENT_ID);
        return (null == returnObject) ? null : (Integer) returnObject;
    }

    public String getURI(final Vertex vertex) {
        Validate.notNull(vertex, "'vertex' cannot be null!");
        final Object returnObject = vertex.getProperty(AppConstants.URI);
        return (null == returnObject) ? null : (String) returnObject;
    }

    public Integer getId(final Edge edge) {
        Validate.notNull(edge, "'edge' cannot be null!");
        final Object returnObject = edge.getProperty(AppConstants.ELEMENT_ID);
        return (null == returnObject) ? null : (Integer) returnObject;
    }

    public Float getWeight(final Edge edge) {
        Validate.notNull(edge, "'edge' cannot be null!");
        final Object returnObject = edge.getProperty(AppConstants.WEIGHT);
        return (null == returnObject) ? null : (Float) returnObject;
    }

    public Vertex getParentVertex(final Vertex vertex) {
        Validate.notNull(vertex, "'vertex' cannot be null!");
        final Iterable<Edge> edges = vertex.getEdges(Direction.OUT);
        for (final Edge edge : edges) {
            final String edgeName = getName(edge);
            if (AppConstants.IsA_RELATIONSHIP.equalsIgnoreCase(edgeName)) {
                return edge.getVertex(Direction.IN);
            }
        }
        return null;
    }

    public Vertex getBaseVertex(final Vertex vertex) {
        Validate.notNull(vertex, "'vertex' cannot be null!");
        Vertex baseVertex = vertex;
        while (null != baseVertex) {
            final Object value = baseVertex.getProperty(RdaAppConstants.ATTR_BASE_NODE);
            if ((null != value) && value.equals("true")) {
                return baseVertex;
            }
            baseVertex = getParentVertex(vertex);
        }
        return null;
    }

    public Set<Pair<Edge, Vertex>> getAdjacentUnaryRelations(final Vertex vertex) {
        return getAdjacentUnaryRelations(vertex, considerOnlyUnaryEdges());
    }

    public Set<Pair<Edge, Vertex>> getAdjacentBinaryRelations(final Vertex vertex) {
        return getAdjacentBinaryRelations(vertex, considerOnlySpecifiedBinaryEdges());
    }

    public Set<Vertex> getAdjacentUnaryRelationVertices(final Vertex vertex) {
        return getAdjacentUnaryVertices(vertex, considerOnlySpecifiedBinaryEdges());
    }

    public Set<Vertex> getAdjacentBinaryRelationVertices(final Vertex vertex) {
        return getAdjacentBinaryVertices(vertex, considerOnlySpecifiedBinaryEdges());
    }

    private Set<Pair<Edge, Vertex>> getAdjacentUnaryRelations(final Vertex vertex, final Predicate<Edge> predicate) {
        final Set<Pair<Edge, Vertex>> outputSet = new HashSet<Pair<Edge, Vertex>>();
        if (null != vertex) {
            vertex.getEdges(Direction.IN).forEach((final Edge edge) -> {
                if (predicate.test(edge)) {
                    outputSet.add(new ImmutablePair<Edge, Vertex>(edge, edge.getVertex(Direction.OUT)));
                }
            });
        }
        return outputSet;
    }

    private Set<Pair<Edge, Vertex>> getAdjacentBinaryRelations(final Vertex vertex, final Predicate<Edge> predicate) {
        final Set<Pair<Edge, Vertex>> outputSet = new HashSet<Pair<Edge, Vertex>>();
        if (null != vertex) {
            vertex.getEdges(Direction.OUT).forEach((final Edge edge) -> {
                if (predicate.test(edge)) {
                    outputSet.add(new ImmutablePair<Edge, Vertex>(edge, edge.getVertex(Direction.IN)));
                }
            });
        }
        return outputSet;
    }

    private Set<Vertex> getAdjacentUnaryVertices(final Vertex vertex, final Predicate<Edge> predicate) {
        final Set<Vertex> outputSet = new HashSet<Vertex>();
        if (null != vertex) {
            vertex.getEdges(Direction.IN).forEach((final Edge edge) -> {
                if (predicate.test(edge)) {
                    outputSet.add(edge.getVertex(Direction.OUT));
                }
            });
        }
        return outputSet;
    }

    private Set<Vertex> getAdjacentBinaryVertices(final Vertex vertex, final Predicate<Edge> predicate) {
        final Set<Vertex> outputSet = new HashSet<Vertex>();
        if (null != vertex) {
            vertex.getEdges(Direction.OUT).forEach((final Edge edge) -> {
                if (predicate.test(edge)) {
                    outputSet.add(edge.getVertex(Direction.IN));
                }
            });
        }
        return outputSet;
    }
}
