// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// DataValue.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.assimilation;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class DataValue<T> extends BaseValue {

    private static final long serialVersionUID = 6154705557750619311L;
    private List<T> values;

    public List<T> getValues() {
        return new ArrayList<T>(values);
    }

    public void setValues(final List<T> values) {
        this.values = values;
    }
}
