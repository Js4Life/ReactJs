// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// PdfReportPage.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.reports.pdf;

import com.parabole.cecl.platform.BaseDTO;

/**
 * PDF Report Page.
 *
 * @author Koushik Chatterjee
 * @since v1.0
 */
public class PdfReportPage extends BaseDTO {

    private static final long serialVersionUID = 8378015262564806597L;
    private String id;
    private PdfReportLayout layout;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public PdfReportLayout getLayout() {
        return layout;
    }

    public void setLayout(final PdfReportLayout layout) {
        this.layout = layout;
    }
}
