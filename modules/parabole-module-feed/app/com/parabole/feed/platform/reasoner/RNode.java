// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// RNode.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.reasoner;

import com.parabole.feed.platform.BaseDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Node in Reasoner Graph.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class RNode extends BaseDTO {

    private static final long serialVersionUID = -1172613597422125119L;
    private final boolean isConcept;
    private final int qualifier;
    private final float value;
    private final List<Integer> adjacencies = new ArrayList<Integer>();

    public RNode(final boolean isConcept, final int qualifier, final float value) {
        this.isConcept = isConcept;
        this.qualifier = qualifier;
        this.value = value;
    }

    public boolean isConcept() {
        return isConcept;
    }

    public int getQualifier() {
        return qualifier;
    }

    public float getValue() {
        return value;
    }

    public boolean addAdjacentNode(final Integer nodeId) {
        return adjacencies.contains(nodeId) ? false : adjacencies.add(nodeId);
    }

    public boolean deleteAdjacentNode(final Integer nodeId) {
        return adjacencies.contains(nodeId) ? adjacencies.remove(nodeId) : false;
    }

    public List<Integer> getAdjacencyList() {
        return Collections.unmodifiableList(adjacencies);
    }
}
