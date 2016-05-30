// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// PdfReport.java
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
 * PDF Report Object.
 *
 * @author Koushik Chatterjee
 * @since v1.0
 */
public class PdfReport extends BaseDTO {

    private static final long serialVersionUID = 435446096158826905L;
    private String id;
    private String name;
    private List<PdfReportPage> pages = new ArrayList<PdfReportPage>();

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<PdfReportPage> getPages() {
        return pages;
    }

    public void setPages(final List<PdfReportPage> pages) {
        this.pages = pages;
    }
}