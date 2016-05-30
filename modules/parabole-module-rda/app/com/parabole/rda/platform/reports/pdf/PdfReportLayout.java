// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// PdfReportLayout.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.reports.pdf;

import java.util.ArrayList;
import java.util.List;
import com.parabole.rda.platform.BaseDTO;

/**
 * PDF Report Layout.
 *
 * @author Koushik Chatterjee
 * @since v1.0
 */
public class PdfReportLayout extends BaseDTO {

    private static final long serialVersionUID = 1318425661067887571L;
    private String name;
    private List<PdfReportCell> cells = new ArrayList<PdfReportCell>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<PdfReportCell> getCells() {
        return cells;
    }

    public void setCells(final List<PdfReportCell> cells) {
        this.cells = cells;
    }
}
