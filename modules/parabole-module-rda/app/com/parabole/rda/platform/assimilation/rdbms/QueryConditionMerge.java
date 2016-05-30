// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// QueryConditionMerge.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.assimilation.rdbms;


/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class QueryConditionMerge extends QueryNCondition {

    private static final long serialVersionUID = 6354703397750619311L;
    private QueryNCondition conditionL;
    private String mergeOperator;
    private QueryNCondition conditionR;

    public QueryNCondition getConditionL() {
        return conditionL;
    }

    public void setConditionL(final QueryNCondition conditionL) {
        this.conditionL = conditionL;
    }

    public String getMergeOperator() {
        return mergeOperator;
    }

    public void setMergeOperator(final String mergeOperator) {
        this.mergeOperator = mergeOperator;
    }

    public QueryNCondition getConditionR() {
        return conditionR;
    }

    public void setConditionR(final QueryNCondition conditionR) {
        this.conditionR = conditionR;
    }
}
