// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ExcelQueryRequest.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.assimilation.excel;

import com.parabole.feed.platform.assimilation.QueryRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */
public class ExcelQueryRequest extends QueryRequest {

    private static final long serialVersionUID = -2780400615556121291L;
    private List<ExcelColumn> columns = new ArrayList<ExcelColumn>();
    private ExcelModelDataSourceCardinals cardinals;

    public List<ExcelColumn> getColumns() {
        return columns;
    }

    public void setColumns(final List<ExcelColumn> columns) {
        this.columns = columns;
    }

    public ExcelModelDataSourceCardinals getCardinals() {
        return cardinals;
    }

    public void setCardinals(final ExcelModelDataSourceCardinals cardinals) {
        this.cardinals = cardinals;
    }
}
