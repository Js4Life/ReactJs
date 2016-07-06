// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// REdge.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.reasoner;

import com.parabole.cecl.platform.BaseDTO;

/**
 * Represents an Edge in Reasoner Graph.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class REdge extends BaseDTO {

    private static final long serialVersionUID = -1172613597422125119L;
    private final int level;
    private final int head;
    private final int tail;
    private final float weight;

    public REdge(final int level, final int head, final int tail, final float weight) {
        this.level = level;
        this.head = head;
        this.tail = tail;
        this.weight = weight;
    }

    public int getLevel() {
        return level;
    }

    public int getHead() {
        return head;
    }

    public int getTail() {
        return tail;
    }

    public float getWeight() {
        return weight;
    }
}
