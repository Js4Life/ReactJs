// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// RdbQueryRequest.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.assimilation.rdbms;

import com.parabole.ccar.platform.assimilation.ColumnReference;
import com.parabole.ccar.platform.assimilation.QueryRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class RdbQueryRequest extends QueryRequest {

    private static final long serialVersionUID = 6959703397750619316L;
    private List<RdbColumn> columns = new ArrayList<RdbColumn>();
    private RdbAccessCardinals cardinals;
    private QueryNCondition condition;
    private List<ColumnReference<RdbColumn>> references = new ArrayList<ColumnReference<RdbColumn>>();
    private String fixedClause;

    public List<RdbColumn> getColumns() {
        return columns;
    }

    public void setColumns(final List<RdbColumn> columns) {
        this.columns = columns;
    }

    public RdbAccessCardinals getCardinals() {
        return cardinals;
    }

    public void setCardinals(final RdbAccessCardinals cardinals) {
        this.cardinals = cardinals;
    }

    public QueryNCondition getCondition() {
        return condition;
    }

    public void setCondition(final QueryNCondition condition) {
        this.condition = condition;
    }

    public List<ColumnReference<RdbColumn>> getReferences() {
        return references;
    }

    public void setReferences(final List<ColumnReference<RdbColumn>> references) {
        this.references = references;
    }

    public String getFixedClause() {
        return fixedClause;
    }

    public void setFixedClause(final String fixedClause) {
        this.fixedClause = fixedClause;
    }
}
