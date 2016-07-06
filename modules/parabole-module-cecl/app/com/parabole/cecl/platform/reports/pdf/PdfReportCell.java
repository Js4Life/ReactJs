// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// PdfReportCell.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.reports.pdf;

import com.parabole.cecl.platform.BaseDTO;

/**
 * PDF Report Cell.
 *
 * @author Koushik Chatterjee
 * @since v1.0
 */
public class PdfReportCell extends BaseDTO {

    private static final long serialVersionUID = 6978851883680341841L;
    private int index;
    private String name;
    private String graphSvg;
    private String graphDesc;

    public int getIndex() {
        return index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getGraphSvg() {
        return graphSvg;
    }

    public void setGraphSvg(final String graphSvg) {
        this.graphSvg = graphSvg;
    }

    public String getGraphDesc() {
        return graphDesc;
    }

    public void setGraphDesc(final String graphDesc) {
        this.graphDesc = graphDesc;
    }
}