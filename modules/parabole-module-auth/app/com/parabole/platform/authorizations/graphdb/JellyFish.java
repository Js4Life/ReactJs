// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// Octopus.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.platform.authorizations.graphdb;

import com.google.common.io.Files;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.exceptions.AppErrorCode;
import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import play.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Operational DB for Glossary Services
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class JellyFish extends GraphDb {

    public JellyFish() {
        final String graphDbUrl = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".jellyfish.graphdb.url");
        final String graphDbUser = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".jellyfish.graphdb.user");
        final String graphDbPassword = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".jellyfish.graphdb.password");
        final Integer graphDbPoolMinSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".jellyfish.graphdb.pool.min");
        final Integer graphDbPoolMaxSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".jellyfish.graphdb.pool.max");
        this.orientGraphFactory = new OrientGraphFactory(graphDbUrl, graphDbUser, graphDbPassword).setupPool(graphDbPoolMinSize, graphDbPoolMaxSize);
    }

    public void saveGlossaryConfigurations(final File file) throws AppException {
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        BufferedReader reader = null;
        try {
            dbTx.begin();
            reader = Files.newReader(file, Charset.forName("UTF-8"));
            final Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
            for (final CSVRecord record : records) {
                final ODocument document = new ODocument("GLOSSARY");
                document.field("SET_ID", record.get("SET_ID"));
                document.field("ITEM_ID", record.get("ITEM_ID"));
                document.field("ITEM_NAME", record.get("ITEM_NAME"));
                document.field("CONCEPT_ID", record.get("CONCEPT_ID"));
                document.field("CONCEPT_NAME", record.get("CONCEPT_NAME"));
                dbTx.save(document);
            }
            dbTx.commit();
        } catch (final IOException ioEx) {
            dbTx.rollback();
            Logger.error("IO Exception: ", ioEx);
            throw new AppException(AppErrorCode.SYSTEM_EXCEPTION);
        } finally {
            IOUtils.closeQuietly(reader);
            closeDocDBConnection(dbTx);
        }
    }

    public void saveReportConfigurations(final File file) throws AppException {
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        BufferedReader reader = null;
        try {
            dbTx.begin();
            reader = Files.newReader(file, Charset.forName("UTF-8"));
            final Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
            for (final CSVRecord record : records) {
                final ODocument document = new ODocument("REPORT");
                document.field("RPT_ID", record.get("RPT_ID"));
                document.field("RPT_NAME", record.get("RPT_NAME"));
                document.field("CONCEPT_ID", record.get("CONCEPT_ID"));
                document.field("CONCEPT_NAME", record.get("CONCEPT_NAME"));
                dbTx.save(document);
            }
            dbTx.commit();
        } catch (final IOException ioEx) {
            dbTx.rollback();
            Logger.error("IO Exception: ", ioEx);
            throw new AppException(AppErrorCode.SYSTEM_EXCEPTION);
        } finally {
            IOUtils.closeQuietly(reader);
            closeDocDBConnection(dbTx);
        }
    }
}