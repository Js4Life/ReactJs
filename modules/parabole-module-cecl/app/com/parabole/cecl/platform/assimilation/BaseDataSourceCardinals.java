// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// BaseDataSourceCardinals.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.assimilation;

import com.parabole.cecl.platform.BaseDTO;

/**
 * TODO
 *
 * @author Atanu Mallick
 * @since v1.0
 */
public abstract class BaseDataSourceCardinals extends BaseDTO {

    private static final long serialVersionUID = 8599633118762912372L;
    protected String dsName;

    public String getDsName() {
        return dsName;
    }

    public void setDsName(final String dsName) {
        this.dsName = dsName;
    }
}
