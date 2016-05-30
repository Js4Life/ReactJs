// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// Biota.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parabole.rda.platform.AppConstants;
import com.parabole.rda.platform.utils.AppUtils;
import com.parabole.rda.platform.assimilation.ViewQueryRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.parabole.rda.application.global.RdaAppConstants;
import com.parabole.rda.platform.assimilation.ColumnReference;
import com.parabole.rda.platform.assimilation.ViewColumn;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

/**
 * Biota is a Data Assimilation Graph-Database.
 * 
 * @author Subhasis Sanyal
 * @since v1.0
 */
@Singleton
public class Biota extends GraphDb {

    private final HashMap<String, String> toCheck = new HashMap<String, String>();

    public Biota() {
        final String graphDbUrl = AppUtils.getApplicationProperty(RdaAppConstants.INDUSTRY + ".biota.graphdb.url");
        final String graphDbUser = AppUtils.getApplicationProperty(RdaAppConstants.INDUSTRY + ".biota.graphdb.user");
        final String graphDbPassword = AppUtils.getApplicationProperty(RdaAppConstants.INDUSTRY + ".biota.graphdb.password");
        final Integer graphDbPoolMinSize = AppUtils.getApplicationPropertyAsInteger(RdaAppConstants.INDUSTRY + ".biota.graphdb.pool.min");
        final Integer graphDbPoolMaxSize = AppUtils.getApplicationPropertyAsInteger(RdaAppConstants.INDUSTRY + ".biota.graphdb.pool.max");
        this.orientGraphFactory = new OrientGraphFactory(graphDbUrl, graphDbUser, graphDbPassword).setupPool(graphDbPoolMinSize, graphDbPoolMaxSize);
    }

    public JSONObject browseLogicalView(final ViewQueryRequest reqObj) {
        final JSONObject obj = new JSONObject();
        final String rootClass = reqObj.getRootClass();
        final List<Object> planData = createFetchPlan(rootClass, reqObj.getReferences());
        final String fetchPlanStr = (String) planData.get(1);
        obj.put(AppConstants.ATTR_CLASS_LINKS, planData.get(0));
        obj.put(AppConstants.ATTR_CLASS_INSTANCES, browseByPlan(rootClass, fetchPlanStr));
        return obj;
    }

    private JSONArray browseByPlan(final String rootClass, final String fetchPlanStr) {
        final StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("select @this.toJSON('");
        sBuffer.append(fetchPlanStr).append("') from ").append(rootClass);
        final ODatabaseDocumentTx dbNoTx = orientGraphFactory.getNoTx().getRawGraph();
        final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sBuffer.toString());
        final JSONArray records = new JSONArray();
        try {
            final List<ODocument> results = dbNoTx.command(query).execute();
            for (final ODocument oDoc : results) {
                final JSONObject jsonRec = new JSONObject(oDoc.field("this").toString());
                records.put(jsonRec);
            }
        } finally {
            dbNoTx.close();
        }
        return records;
    }

    private List<Object> createFetchPlan(final String rootClass, final List<ColumnReference<ViewColumn>> references) {
        final HashMap<String, List<ColumnReference<ViewColumn>>> linkMap = new HashMap<String, List<ColumnReference<ViewColumn>>>();
        for (final ColumnReference<ViewColumn> aRef : references) {
            final String className = aRef.getColumnFrom().getTableName();
            List<ColumnReference<ViewColumn>> links = null;
            if (linkMap.containsKey(className)) {
                links = linkMap.get(className);
            } else {
                links = new ArrayList<ColumnReference<ViewColumn>>();
                linkMap.put(className, links);
            }
            links.add(aRef);
        }
        final GraphDbLinkDefinition linkDefRoot = new GraphDbLinkDefinition();
        linkDefRoot.setParentLink(null);
        linkDefRoot.setName("");
        if (linkMap.size() > 0) {
            createLinkDefinitionObjects(rootClass, linkMap, linkDefRoot);
        }
        final StringBuffer strBuffer = new StringBuffer("");
        final HashMap<String, String> tableAndLinks = formFetchPlanString(linkDefRoot, strBuffer);
        String fetchPlanStr = "";
        if (strBuffer.length() == 0) {
            strBuffer.append("*:0");
        }
        fetchPlanStr = AppConstants.FETCH_PLAN_PREFIX + strBuffer.toString().trim();
        final List<Object> planData = new ArrayList<Object>();
        planData.add(tableAndLinks);
        planData.add(fetchPlanStr);
        return planData;
    }

    private HashMap<String, String> formFetchPlanString(final GraphDbLinkDefinition linkDefRoot, final StringBuffer strBuffer) {
        final List<GraphDbLinkDefinition> childLinks = linkDefRoot.getChildLinks();
        final HashMap<String, String> tableAndLinks = new HashMap<String, String>();
        if (childLinks == null) {
            return tableAndLinks;
        } else {
            for (final GraphDbLinkDefinition lDef : childLinks) {
                tableAndLinks.put(lDef.getToClass(), lDef.getName());
                strBuffer.append(lDef.getLinkPath()).append(":0").append(" ");
                formFetchPlanString(lDef, strBuffer);
            }
            return tableAndLinks;
        }
    }

    private void createLinkDefinitionObjects(final String className, final HashMap<String, List<ColumnReference<ViewColumn>>> linkMap, final GraphDbLinkDefinition linkDefParent) {
        final List<ColumnReference<ViewColumn>> colRefs = linkMap.get(className);
        toCheck.put(className, className);
        for (final ColumnReference<ViewColumn> aRef : colRefs) {
            final GraphDbLinkDefinition childLink = new GraphDbLinkDefinition();
            childLink.setName(aRef.getName());
            childLink.setFromClass(aRef.getColumnFrom().getTableName());
            childLink.setToClass(aRef.getColumnTo().getTableName());
            linkDefParent.addChildLink(childLink);
            final String toClass = aRef.getColumnTo().getTableName();
            if (linkMap.containsKey(toClass)) {
                createLinkDefinitionObjects(toClass, linkMap, childLink);
            }
        }
    }
}
