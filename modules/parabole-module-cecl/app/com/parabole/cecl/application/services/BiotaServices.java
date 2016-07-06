// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// BiotaServices.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.application.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parabole.cecl.application.global.RdaAppConstants;
import com.parabole.cecl.platform.AppConstants;
import com.parabole.cecl.platform.assimilation.*;
import com.parabole.cecl.platform.exceptions.AppException;
import com.parabole.cecl.platform.graphdb.Biota;
import com.parabole.cecl.platform.graphdb.GraphDbLinkDefinition;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;

import java.io.IOException;
import java.util.*;

/**
 * RDA Data DB Utilities.
 *
 * @author Anish Chatterjee
 * @since v1.0
 */
@Singleton
public class BiotaServices {

    @Inject
    private Biota biota;

    @Inject
    private CoralConfigurationService coralConfigurationService;

    @Inject
    protected OctopusSemanticService octopusSemanticService;

    public void createView(final JSONObject viewJson) throws AppException {
        final JSONArray columnsJson = viewJson.getJSONArray(RdaAppConstants.ATTR_VIEWCREATION_COLS);
        final String viewName = viewJson.getString(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME);
        final List<String> colNames = new ArrayList<String>();
        final int colCount = columnsJson.length();
        for (int i = 0; i < colCount; i++) {
            final JSONObject aColumn = columnsJson.getJSONObject(i);
            colNames.add(aColumn.getString(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME));
        }
        biota.createTable(viewName, colNames);
    }

    public JSONObject getDataFromLogicalView(final String userId, final ViewQueryRequest viewQuery) throws AppException {
        final List<ColumnReference<ViewColumn>> references = viewQuery.getReferences();
        final List<ViewColumn> columns = viewQuery.getColumns();
        final Set<String> dumpedTableSet = new HashSet<String>();
        for (final ViewColumn aColumn : columns) {
            if (!dumpedTableSet.contains(aColumn.getEdgeName())) {
                dumpedTableSet.add(aColumn.getEdgeName());
            }
        }
        for (final Object element : dumpedTableSet) {
            final String edgeName = (String) element;
            final QueryResultTable currTableDataFrom = DataSourceUtilsFactory.getViewDataFromEdgeName(userId, edgeName, coralConfigurationService);
            DataSourceUtilsFactory.dumpToDocumentDb(currTableDataFrom, this);
        }
        for (final ColumnReference<ViewColumn> ref : references) {
            createLink(ref);
        }
        final JSONObject recData = biota.browseLogicalView(viewQuery);
        final JSONArray jsonArr = (JSONArray) recData.get(AppConstants.ATTR_CLASS_INSTANCES);
        HashMap<String, String> classLinks = null;
        if (recData.has(AppConstants.ATTR_CLASS_LINKS)) {
            classLinks = (HashMap<String, String>) recData.get(AppConstants.ATTR_CLASS_LINKS);
        }
        final JSONArray cols = createColumnSet(viewQuery.getColumns(), classLinks);
        final JSONObject retObj = new JSONObject();
        retObj.put(RdaAppConstants.ATTR_VIEWCREATION_COLS, cols);
        retObj.put(RdaAppConstants.ATTR_VIEWCREATION_DATA, jsonArr);
        return retObj;
    }

    public void updateLogicalWithDB(final String userId, final ViewQueryRequest viewQuery) throws AppException {
        String newRootClass = null;
        final String origRootClass = viewQuery.getRootClass();
        final List<ColumnReference<ViewColumn>> references = viewQuery.getReferences();
        final List<ViewColumn> columns = viewQuery.getColumns();
        final Map<String, ViewQueryRequest> vqryMap = new HashMap<String, ViewQueryRequest>();
        populateNewViewQueryMap(userId, viewQuery, vqryMap);
        for (final ViewColumn aColumn : columns) {
            if (aColumn.isLogical()) {
                final String cfgName = aColumn.getViewCfgName();
                final ViewQueryRequest vqr = vqryMap.get(cfgName);
                if (aColumn.getTableName().equalsIgnoreCase(origRootClass)) {
                    newRootClass = vqr.getRootClass();
                }
                replaceLogicalAttributes(aColumn, vqr);
            }
        }
        for (final ColumnReference<ViewColumn> ref : references) {
            final ViewColumn fromCol = ref.getColumnFrom();
            if (fromCol.isLogical()) {
                final String cfgName = fromCol.getViewCfgName();
                final ViewQueryRequest vqr = vqryMap.get(cfgName);
                replaceLogicalAttributes(fromCol, vqr);
            }
            final ViewColumn toCol = ref.getColumnTo();
            if (toCol.isLogical()) {
                final String cfgName = toCol.getViewCfgName();
                final ViewQueryRequest vqr = vqryMap.get(cfgName);
                replaceLogicalAttributes(toCol, vqr);
            }
        }
        // AddThe Extra References from Logical View
        final Iterator<String> iter = vqryMap.keySet().iterator();
        while (iter.hasNext()) {
            final ViewQueryRequest vqr = vqryMap.get(iter.next());
            for (final ColumnReference<ViewColumn> ref1 : vqr.getReferences()) {
                references.add(ref1);
            }
        }
        if (newRootClass != null) {
            viewQuery.setRootClass(newRootClass);
        }
        return;
    }

    private void replaceLogicalAttributes(final ViewColumn aColumn, final ViewQueryRequest vqr) {
        final ViewColumn targetColumn = vqr.findColumnByName(aColumn.getName(), null);
        if (targetColumn != null) {
            aColumn.setTableName(targetColumn.getTableName());
            aColumn.setEdgeName(targetColumn.getEdgeName());
        }
    }

    private void populateNewViewQueryMap(final String userId, final ViewQueryRequest viewQuery, final Map<String, ViewQueryRequest> vqryMap) throws AppException {
        for (final ViewColumn aColumn : viewQuery.getColumns()) {
            if (aColumn.isLogical()) {
                final String cfgName = aColumn.getViewCfgName();
                ViewQueryRequest vqr = null;
                if (!vqryMap.containsKey(cfgName)) {
                    vqr = DataSourceUtilsFactory.deserializeViewQueryRequest(userId, cfgName, coralConfigurationService);
                    vqryMap.put(cfgName, vqr);
                }
            }
        }
        for (final ColumnReference<ViewColumn> ref1 : viewQuery.getReferences()) {
            if (ref1.getColumnFrom().isLogical()) {
                final String cfgName = ref1.getColumnFrom().getViewCfgName();
                ViewQueryRequest vqr = null;
                if (!vqryMap.containsKey(cfgName)) {
                    vqr = DataSourceUtilsFactory.deserializeViewQueryRequest(userId, cfgName, coralConfigurationService);
                    vqryMap.put(cfgName, vqr);
                }
            }
        }
    }

    public JSONObject showAggregatedView(final String userId, final JSONObject jsonObj) throws AppException {
        final JSONObject joinObj = jsonObj.getJSONObject("joinColumn");
        final String rootClass = jsonObj.getString("rootCls");
        final String joinAttr = joinObj.getString(RdaAppConstants.ATTR_DATASOURCE_MAPPING_NAME);
        final int cardinality = joinObj.getInt("cardinality");
        final List<String> viewNames = new ArrayList<String>();
        final JSONArray viewArr = jsonObj.getJSONArray("views");
        for (int i = 0; i < viewArr.length(); i++) {
            viewNames.add(viewArr.getString(i));
        }
        JSONObject aggrViewJson = null;
        Map<String, String> cfgData = null;
        final ObjectMapper mapper = new ObjectMapper();
        final ViewQueryRequest finalQryReq = new ViewQueryRequest();
        final List<ViewColumn> aggrViewCols = new ArrayList<ViewColumn>();
        final List<ColumnReference<ViewColumn>> aggrRefCols = new ArrayList<ColumnReference<ViewColumn>>();
        final List<String> newJoinClasses = new ArrayList<String>();
        for (final String vName : viewNames) {
            final List<Map<String, String>> cfgDetailsList = coralConfigurationService.getConfigurationByName(userId, vName);
            cfgData = cfgDetailsList.get(0);
            final String cfgInfo = cfgData.get(RdaAppConstants.ATTR_VIEWCREATION_DETAILS);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ViewQueryRequest viewQuery = null;
            try {
                viewQuery = mapper.readValue(cfgInfo, ViewQueryRequest.class);
                final String tmp = viewQuery.getRootClass();
                if (!tmp.equalsIgnoreCase(rootClass)) {
                    newJoinClasses.add(tmp);
                }
                aggrViewCols.addAll(viewQuery.getColumns());
                aggrRefCols.addAll(viewQuery.getReferences());
            } catch (final IOException ioEx) {
                Logger.error("IOException", ioEx);
            }
        }
        final ViewColumn fromColumn = new ViewColumn();
        fromColumn.setName(joinAttr);
        fromColumn.setTableName(rootClass);
        for (final String toClsName : newJoinClasses) {
            final ColumnReference<ViewColumn> newColRef = new ColumnReference<ViewColumn>();
            final ViewColumn toColumn = new ViewColumn();
            toColumn.setName(joinAttr);
            toColumn.setTableName(toClsName);
            newColRef.setColumnFrom(fromColumn);
            newColRef.setColumnTo(toColumn);
            newColRef.setName(rootClass + "_" + toClsName);
            newColRef.setCardinality(cardinality);
            aggrRefCols.add(newColRef);
        }
        finalQryReq.setRootClass(rootClass);
        finalQryReq.setColumns(aggrViewCols);
        finalQryReq.setReferences(aggrRefCols);
        aggrViewJson = getDataFromLogicalView(userId, finalQryReq);
        return aggrViewJson;
    }

    public boolean insertData(final QueryResultTable resultTable, final boolean clearAll) {
        try {
            if (clearAll) {
                biota.deleteAllDataFromTable(resultTable.getName());
            }
            biota.loadBulkDataInTable(resultTable.getName(), resultTable.getColumnNames(), resultTable);
            return true;
        } catch (final Exception ex) {
            Logger.error("Insert Data Exception", ex);
            return false;
        }
    }

    public List<Map<String, String>> getAggregatedViewsForNode(final String userId, final Integer rootNodeId, final String aggrName) throws AppException {
        final List<Map<String, String>> cfgList = coralConfigurationService.getConfigurationByName(userId, aggrName, false);
        if (cfgList.size() == 0) {
            return null;
        }
        final Map<String, String> cfgMap = cfgList.get(0);
        final JSONObject detailObj = new JSONObject(cfgMap.get("details"));
        final JSONArray connections = detailObj.getJSONArray("connecions");
        final JSONArray edges = detailObj.getJSONArray("vertices");
        final HashMap<Integer, List<Integer>> childMap = formChildrenMap(connections, edges);
        final HashMap<Integer, JSONObject> nodeMap = formNodesMap(edges);
        final List<Map<String, String>> outCfgList = new ArrayList<Map<String, String>>();
        aggregateViews(rootNodeId, userId, aggrName, childMap, nodeMap, outCfgList);
        return outCfgList;
    }

    public List<Map<String, String>> getAllAggregatedViewsForNode(final String userId, final Integer rootNodeId, final String confName) throws AppException {
        final List<Map<String, String>> cfgList = coralConfigurationService.getConfigurationByName(confName);
        if (cfgList.size() == 0) {
            return null;
        }
        return cfgList;
    }

    private void createLink(final ColumnReference<ViewColumn> refColumn) throws AppException {
        final GraphDbLinkDefinition lDef = new GraphDbLinkDefinition();
        lDef.setName(refColumn.getName());
        lDef.setFromClass(refColumn.getColumnFrom().getTableName());
        lDef.setToClass(refColumn.getColumnTo().getTableName());
        lDef.setFromProperty(refColumn.getColumnFrom().getName());
        lDef.setToProperty(refColumn.getColumnTo().getName());
        lDef.setCardinality(refColumn.getCardinality());
        biota.createLinkToTable(lDef);
    }

    private HashMap<Integer, List<Integer>> formChildrenMap(final JSONArray connections, final JSONArray edges) {
        final HashMap<Integer, Integer> edgeMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < edges.length(); i++) {
            edgeMap.put(edges.getJSONObject(i).getInt("id"), edges.getJSONObject(i).getInt("id"));
        }
        final HashMap<Integer, List<Integer>> childListMap = new HashMap<Integer, List<Integer>>();
        for (int i = 0; i < connections.length(); i++) {
            final JSONObject aVertex = connections.getJSONObject(i);
            int from = aVertex.getInt("from");
            int to = aVertex.getInt("to");
            if (!edgeMap.containsKey(from) || !edgeMap.containsKey(to)) {
                continue;
            }
            final String relType = aVertex.getString("relType").trim();
            if (relType.equalsIgnoreCase("isA")) {
                final int tmp = to;
                to = from;
                from = tmp;
            }
            List<Integer> childIds = null;
            if (!childListMap.containsKey(from)) {
                childIds = new ArrayList<Integer>();
                childListMap.put(from, childIds);
            } else {
                childIds = childListMap.get(from);
            }
            childIds.add(to);
        }
        return childListMap;
    }

    private HashMap<Integer, JSONObject> formNodesMap(final JSONArray edges) {
        final HashMap<Integer, JSONObject> nodesMap = new HashMap<Integer, JSONObject>();
        for (int i = 0; i < edges.length(); i++) {
            final JSONObject obj = edges.getJSONObject(i);
            nodesMap.put(obj.getInt("id"), obj);
            obj.put("visited", false);
        }
        return nodesMap;
    }

    private void aggregateViews(final int nodeId, final String userId, final String aggrName, final HashMap<Integer, List<Integer>> childMap, final HashMap<Integer, JSONObject> nodeMap, final List<Map<String, String>> outCfgList) throws AppException {
        final JSONObject nodeObj = nodeMap.get(nodeId);
        if (nodeObj == null) {
            return;
        }
        if (nodeObj.getBoolean("visited")) {
            return;
        }
        final String nodeName = nodeObj.getString("name");
        // final List<Map<String, String>> cfgList = coralConfigurationService.getConfigurationByName(userId, nodeName, true);
        final List<Map<String, String>> cfgList = coralConfigurationService.getConfigurationByName(userId, nodeName + "_" + aggrName, false);
        outCfgList.addAll(cfgList);
        nodeObj.put("visited", true);
        if (childMap.containsKey(nodeId)) {
            final List<Integer> childList = childMap.get(nodeId);
            for (int i = 0; i < childList.size(); i++) {
                final int childId = childList.get(i);
                aggregateViews(childId, userId, aggrName, childMap, nodeMap, outCfgList);
            }
        }
    }

/*    private void aggregateViews(final int nodeId, final String userId, final String aggrName, final HashMap<Integer, List<Integer>> childMap, final HashMap<Integer, JSONObject> nodeMap, final List<Map<String, String>> outCfgList) throws AppException {
        final JSONObject nodeObj = nodeMap.get(nodeId);
        if (nodeObj == null) {
            return;
        }
        if (nodeObj.getBoolean("visited")) {
            return;
        }
        final String nodeName = nodeObj.getString("name");
        final List<Map<String, String>> cfgList = coralConfigurationService.getConfigurationByName(userId, nodeName + "_" + aggrName, false);
        outCfgList.addAll(cfgList);
        nodeObj.put("visited", true);
        if (childMap.containsKey(nodeId)) {
            final List<Integer> childList = childMap.get(nodeId);
            for (int i = 0; i < childList.size(); i++) {
                final int childId = childList.get(i);
                aggregateViews(childId, userId, aggrName, childMap, nodeMap, outCfgList);
            }
        }
    }*/

    private JSONArray createColumnSet(final List<ViewColumn> columns, final HashMap<String, String> classLinks) {
        final JSONArray colArray = new JSONArray();
        final HashMap<String, Integer> linkIndices = new HashMap<String, Integer>();
        for (final ViewColumn aColumn : columns) {
            final String name = aColumn.getName();
            final String className = aColumn.getTableName();
            if ((classLinks != null) && classLinks.containsKey(className)) {
                final String linkName = classLinks.get(className);
                int index = -1;
                if (linkIndices.containsKey(linkName)) {
                    index = linkIndices.get(linkName);
                } else {
                    index = colArray.length();
                    linkIndices.put(linkName, index);
                    final JSONObject lObj = new JSONObject();
                    lObj.put(linkName, new JSONArray());
                    colArray.put(index, lObj);
                }
                final JSONObject lObj = (JSONObject) colArray.get(index);
                ((JSONArray) lObj.get(linkName)).put(name);
            } else {
                colArray.put(name);
            }
        }
        return colArray;
    }
}
