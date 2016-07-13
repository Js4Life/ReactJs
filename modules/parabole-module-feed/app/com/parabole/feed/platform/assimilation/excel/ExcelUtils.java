// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ExcelUtils.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.assimilation.excel;

import com.google.inject.Singleton;
import com.parabole.feed.application.global.CCAppConstants.ExcelFormat;
import com.parabole.feed.platform.assimilation.*;
import com.parabole.feed.platform.exceptions.AppErrorCode;
import com.parabole.feed.platform.exceptions.AppException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Excel Utility.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Singleton
public class ExcelUtils implements IDataSourceUtils, QueryBuilderIDataSourceOperation {

    private ExcelModelDataSourceCardinals _cardinals;
    private Workbook workBook = null;

    public ExcelUtils() {
    }

    public ExcelUtils(final BaseDataSourceCardinals cardinals) {
        _cardinals = (ExcelModelDataSourceCardinals) cardinals;
    }

    @Override
    public JSONObject exploreDataSource() throws AppException {
        try {
            final JSONArray sheetsJsonArray = new JSONArray();
            this.openWorkBook();
            final int sheetsCount = workBook.getNumberOfSheets();
            for (int i = 0; i < sheetsCount; i++) {
                final Sheet sheet = workBook.getSheetAt(i);
                final String sheetName = sheet.getSheetName();
                final Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    continue;
                }
                final JSONArray columnsJsonArray = new JSONArray();
                final Iterator<Cell> iterator = headerRow.cellIterator();
                while (iterator.hasNext()) {
                    final Cell cell = iterator.next();
                    final String headerName = cell.getStringCellValue();
                    mapCellType(cell.getCellType());
                    final JSONObject columnJsonObject = new JSONObject();
                    columnJsonObject.put("text", headerName);
                    columnJsonObject.put("type", "column");
                    columnJsonObject.put("path", _cardinals.getDsName() + "/" + _cardinals.getDsName() + "/" + sheetName + "/" + headerName);
                    columnsJsonArray.put(columnJsonObject);
                }
                final JSONObject sheetJsonObject = new JSONObject();
                sheetJsonObject.put("text", sheetName);
                sheetJsonObject.put("type", "table");
                sheetJsonObject.put("children", columnsJsonArray);
                sheetsJsonArray.put(sheetJsonObject);
            }
            workBook.close();
            final JSONObject finalJsonObject = new JSONObject();
            finalJsonObject.put("text", _cardinals.getDsName());
            finalJsonObject.put("children", sheetsJsonArray);
            return finalJsonObject;
        } catch (final IOException ioEx) {
            Logger.error("Could not open Excel", ioEx);
            throw new AppException(AppErrorCode.EXCEL_OPERATION_ERROR);
        }
    }

    public JSONArray fetchData(final String tableName) throws AppException {
        try {
            final JSONArray rowsJsonArray = new JSONArray();
            this.openWorkBook();
            final int sheetsCount = workBook.getNumberOfSheets();
            for (int i = 0; i < sheetsCount; i++) {
                final Sheet sheet = workBook.getSheetAt(i);
                final String sheetName = sheet.getSheetName();
                if (sheetName.equalsIgnoreCase(tableName)) {
                    final int numRows = sheet.getLastRowNum();
                    for (int j = 1; j < numRows; j++) {
                        final Row dataRow = sheet.getRow(j);
                        final Iterator<Cell> iterator = dataRow.cellIterator();
                        final JSONArray aRow = new JSONArray();
                        while (iterator.hasNext()) {
                            final Cell cell = iterator.next();
                            final String dataValue = cell.getStringCellValue();
                            aRow.put(dataValue);
                        }
                        rowsJsonArray.put(aRow);
                    }
                    break;
                }
            }
            workBook.close();
            return rowsJsonArray;
        } catch (final IOException ioEx) {
            Logger.error("Could not fetch data  from Excel", ioEx);
            throw new AppException(AppErrorCode.EXCEL_OPERATION_ERROR);
        }
    }

    private void openWorkBook() throws AppException {
        try {
            final ExcelModelDataSourceCardinals excelCardinals = _cardinals;
            final String fileExtension = FilenameUtils.getExtension(excelCardinals.getFileName());
            final ExcelFormat format = ExcelFormat.valueOf(fileExtension.toUpperCase());
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(excelCardinals.getBytes());
            if (format == ExcelFormat.XLS) {
                workBook = new HSSFWorkbook(inputStream);
            } else if (format == ExcelFormat.XLSX) {
                workBook = new XSSFWorkbook(inputStream);
            }
        } catch (final IOException ioEx) {
            Logger.error("Could not open Excel", ioEx);
            throw new AppException(AppErrorCode.EXCEL_OPERATION_ERROR);
        }
    }

    private String mapCellType(final int cellType) {
        switch (cellType) {
            case Cell.CELL_TYPE_NUMERIC:
                return "Number";
            case Cell.CELL_TYPE_BOOLEAN:
                return "Boolean";
            default:
                return "String";
        }
    }

    @Override
    public QueryResultTable getViewFromQueryRequest(final QueryRequest queryData) throws AppException {
        final QueryResultTable ret = generateExcelView(queryData);
        ret.setRowIterator(ret.getTableData().iterator());
        return ret;
    }

    private QueryResultTable generateExcelView(final QueryRequest queryData) throws AppException {
        try {
            final QueryResultTable ret = new QueryResultTable();
            final ExcelQueryRequest excelQuery = (ExcelQueryRequest) queryData;
            this._cardinals = excelQuery.getCardinals();
            this.openWorkBook();
            final List<String> workBookNames = getTableNames(excelQuery.getColumns());
            final int sheetsCount = workBook.getNumberOfSheets();
            for (int i = 0; i < sheetsCount; i++) {
                final Sheet sheet = workBook.getSheetAt(i);
                final String sheetName = sheet.getSheetName();
                if (sheetName.equalsIgnoreCase(workBookNames.get(0))) {
                    final int numRows = sheet.getLastRowNum();
                    boolean isColNames = true;
                    final List<Integer> colIndexes = new ArrayList<Integer>();
                    for (int j = 0; j <= numRows; j++) {
                        final Row dataRow = sheet.getRow(j);
                        final Iterator<Cell> iterator = dataRow.cellIterator();
                        final List<String> aRow = new ArrayList<String>();
                        int idx = 0;
                        while (iterator.hasNext()) {
                            final Cell cell = iterator.next();
                            final String dataValue = getCellvalueAsString(cell);
                            if (isColNames) {
                                if (isSelectedColumn(excelQuery.getColumns(), sheetName, dataValue)) {
                                    ret.addColumnName(dataValue);
                                    colIndexes.add(idx);
                                }
                            } else {
                                if (colIndexes.contains(idx)) {
                                    aRow.add(dataValue);
                                }
                            }
                            idx++;
                        }
                        if (!isColNames) {
                            ret.addRow(aRow);
                        }
                        isColNames = false;
                    }
                }
            }
            workBook.close();
            return ret;
        } catch (final IOException ioEx) {
            Logger.error("Could not generate Excel view", ioEx);
            throw new AppException(AppErrorCode.EXCEL_OPERATION_ERROR);
        }
    }

    private List<String> getTableNames(final List<ExcelColumn> columns) {
        final List<String> tableNames = new ArrayList<String>();
        for (final ExcelColumn column : columns) {
            if (!tableNames.contains(column.getTableName())) {
                tableNames.add(column.getTableName());
            }
        }
        return tableNames;
    }

    private boolean isSelectedColumn(final List<ExcelColumn> columns, final String sheetName, final String colName) {
        for (final ExcelColumn excelColumn : columns) {
            if (excelColumn.getTableName().equalsIgnoreCase(sheetName) && excelColumn.getName().equalsIgnoreCase(colName)) {
                return true;
            }
        }
        return false;
    }

    private String getCellvalueAsString(final Cell cell) {
        final String type = mapCellType(cell.getCellType());
        String ret = null;
        if (type.equalsIgnoreCase("Number")) {
            ret = String.valueOf(cell.getNumericCellValue());
        } else if (type.equalsIgnoreCase("Boolean")) {
            ret = String.valueOf(cell.getBooleanCellValue());
        } else {
            ret = cell.getStringCellValue();
        }
        return ret;
    }
}
