// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ColumnReference.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.assimilation;

import com.parabole.rda.platform.BaseDTO;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class ColumnReference<Column> extends BaseDTO {

    private static final long serialVersionUID = 6154993397750619311L;
    private String name;
    private Column columnFrom;
    private Column columnTo;
    private int cardinality;

    public Column getColumnFrom() {
        return columnFrom;
    }

    public void setColumnFrom(final Column columnFrom) {
        this.columnFrom = columnFrom;
    }

    public Column getColumnTo() {
        return columnTo;
    }

    public void setColumnTo(final Column columnTo) {
        this.columnTo = columnTo;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(final int cardinality) {
        this.cardinality = cardinality;
    }
}
