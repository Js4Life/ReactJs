// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ViewQueryRequest.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.assimilation;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class ViewQueryRequest extends QueryRequest {

    private static final long serialVersionUID = 2345678936545L;
    private String name;
    private String rootClass;
    private List<ViewColumn> columns = new ArrayList<ViewColumn>();
    private List<ColumnReference<ViewColumn>> references = new ArrayList<ColumnReference<ViewColumn>>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<ViewColumn> getColumns() {
        return columns;
    }

    public void setColumns(final List<ViewColumn> columns) {
        this.columns = columns;
    }

    public List<ColumnReference<ViewColumn>> getReferences() {
        return references;
    }

    public void setReferences(final List<ColumnReference<ViewColumn>> references) {
        this.references = references;
    }

    public String getRootClass() {
        return rootClass;
    }

    public void setRootClass(final String rootClass) {
        this.rootClass = rootClass;
    }

    public ViewColumn findColumnByName(final String colName, final String tableName) {
        ViewColumn retColumn = null;
        for (final ViewColumn vc : this.columns) {
            retColumn = matchColumn(vc, colName, tableName);
            if (retColumn != null) {
                break;
            }
        }
        if (retColumn == null) {
            for (final ColumnReference<ViewColumn> ref : this.references) {
                retColumn = matchColumn(ref.getColumnFrom(), colName, tableName);
                if (retColumn != null) {
                    break;
                }
                retColumn = matchColumn(ref.getColumnTo(), colName, tableName);
                if (retColumn != null) {
                    break;
                }
            }
        }
        return retColumn;
    }

    private ViewColumn matchColumn(final ViewColumn vc, final String colName, final String tableName) {
        ViewColumn retColumn = null;
        if (vc.getName().equalsIgnoreCase(colName)) {
            if (tableName != null) {
                if (vc.getTableName().equalsIgnoreCase(tableName)) {
                    retColumn = vc;
                }
            } else {
                retColumn = vc;
            }
        }
        return retColumn;
    }
}