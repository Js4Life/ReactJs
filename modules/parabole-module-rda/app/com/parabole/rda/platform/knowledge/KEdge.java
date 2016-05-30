// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// KEdge.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.knowledge;

import com.parabole.rda.platform.BaseDTO;

/**
 * Represents An Edge in Knowledge Graph.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class KEdge extends BaseDTO {

    private static final long serialVersionUID = -1172613597422125119L;
    private final int id;
    private final int destination;
    private final float weight;

    public KEdge(final int id, final int destination, final float weight) {
        this.id = id;
        this.destination = destination;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public int getDestination() {
        return destination;
    }

    public float getWeight() {
        return weight;
    }
}