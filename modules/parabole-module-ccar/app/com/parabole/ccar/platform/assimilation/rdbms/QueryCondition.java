// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// QueryCondition.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.assimilation.rdbms;

import com.parabole.ccar.platform.assimilation.BaseValue;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class QueryCondition extends QueryNCondition {

    private static final long serialVersionUID = 6554703397750619311L;
    private RdbColumn valueL;
    private String operator;
    private BaseValue valueR;

    public RdbColumn getValueL() {
        return valueL;
    }

    public void setValueL(final RdbColumn valueL) {
        this.valueL = valueL;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(final String operator) {
        this.operator = operator;
    }

    public BaseValue getValueR() {
        return valueR;
    }

    public void setValueR(final BaseValue valueR) {
        this.valueR = valueR;
    }
}
