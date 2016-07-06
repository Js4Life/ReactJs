// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ViewColumn.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.assimilation;


/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class ViewColumn extends BaseColumn {

    private static final long serialVersionUID = 6154703397759919311L;
    private String edgeName;
    private boolean isLogical;
    private String viewCfgName;

    public String getEdgeName() {
        return edgeName;
    }

    public void setEdgeName(final String edgeName) {
        this.edgeName = edgeName;
    }

    public boolean isLogical() {
        return isLogical;
    }

    public void setLogical(final boolean isLogical) {
        this.isLogical = isLogical;
    }

    public String getViewCfgName() {
        return viewCfgName;
    }

    public void setViewCfgName(final String viewCfgName) {
        this.viewCfgName = viewCfgName;
    }
}
