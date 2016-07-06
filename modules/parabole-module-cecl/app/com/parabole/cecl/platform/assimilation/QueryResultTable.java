// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// InvalidColumnException.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.assimilation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.parabole.cecl.platform.BaseDTO;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class QueryResultTable extends BaseDTO implements Iterable<List<String>> {

    private static final long serialVersionUID = 6154703397750619392L;
    private String name;
    private List<String> columnNames = new ArrayList<String>();
    private List<List<String>> tableData = new ArrayList<List<String>>();
    private transient Iterator<List<String>> rowIterator = null;

    public void setRowIterator(final Iterator<List<String>> rowIterator) {
        this.rowIterator = rowIterator;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(final List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<List<String>> getTableData() {
        return tableData;
    }

    public void setTableData(final List<List<String>> tableData) {
        this.tableData = tableData;
    }

    public String getColumnName(final int index) {
        return columnNames.get(index);
    }

    public void setColumnName(final int index, final String columnName) {
        columnNames.set(index, columnName);
    }

    public void addColumnName(final String columnName) {
        this.columnNames.add(columnName);
    }

    public List<String> getRow(final int index) {
        return tableData.get(index);
    }

    public void setTableRow(final int index, final List<String> row) {
        this.tableData.set(index, row);
    }

    public void addRow(final List<String> row) {
        this.tableData.add(row);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public Iterator<List<String>> iterator() {
        return this.rowIterator;
    }
}
