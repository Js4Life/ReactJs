package com.parabole.feed.platform.graphdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.exceptions.AppErrorCode;
import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.apache.commons.lang3.Validate;
import play.Logger;

import java.util.*;

/**
 * Created by Sagir on 23-08-2016.
 */
public class StarFish extends GraphDb {

    public StarFish() {
        final String graphDbUrl = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".starfish.graphdb.url");
        final String graphDbUser = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".starfish.graphdb.user");
        final String graphDbPassword = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".starfish.graphdb.password");
        final Integer graphDbPoolMinSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".starfish.graphdb.pool.min");
        final Integer graphDbPoolMaxSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".starfish.graphdb.pool.max");
        this.orientGraphFactory = new OrientGraphFactory(graphDbUrl, graphDbUser, graphDbPassword).setupPool(graphDbPoolMinSize, graphDbPoolMaxSize);
    }


    public String saveQuestion() throws AppException {
        //final Integer configurationId = generateId(CCAppConstants.RDA_USER_CONFIGS);

        final Map<String, Object> dataMapForParagraph = new HashMap<String, Object>();
        dataMapForParagraph.put("PARAGRAPH_ID", "12345678");
        dataMapForParagraph.put("TEXT", "This is a text of the paragraph");
        //saveQ(CCAppConstants.QUESTION_DOCUMENT, dataMap);
        String rids = saveParagraph(CCAppConstants.PARAGRAPH_DOCUMENT, dataMapForParagraph);


        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("QUESTION_ID", "12345678");
        dataMap.put("TEXT", "This is a text of the paragraph");
        dataMap.put("IS_MANDATORY", true);
        dataMap.put("PARAGRAPH_ID", rids);
        //saveQ(CCAppConstants.QUESTION_DOCUMENT, dataMap);
        return saveQ(CCAppConstants.QUESTION_DOCUMENT, dataMap);
    }


    public String saveQ(final String configurationObjectClass, final Map<String, Object> configurationDataMap) throws AppException {
        Validate.notBlank(configurationObjectClass, "'configurationObjectClass' cannot be empty!");
        Validate.notEmpty(configurationDataMap, "'configurationDataMap' cannot be empty!");
        boolean isNew = true;
        List<ODocument> results = null;
        String rid = null;
        final String cfgname = (String) configurationDataMap.get("QUESTION_ID");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        ODocument document = null;
        try {
            dbTx.begin();
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM " + configurationObjectClass + " WHERE QUESTION_ID = '" + cfgname + "'");
            results = dbTx.command(query).execute();
            if (results.size() > 0) {
                document = results.get(0);
                isNew = false;
            } else {
                document = new ODocument(configurationObjectClass);
            }
            for (final Map.Entry<String, Object> entry : configurationDataMap.entrySet()) {
                final String key = entry.getKey();
                if (key.equalsIgnoreCase("QUESTION_ID")) {
                    if (isNew) {
                        document.field(key, entry.getValue());
                    }
                } else {
                    document.field(key, entry.getValue());
                }
            }
            dbTx.save(document);
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not save configuration", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            rid = document.getIdentity().toString();
            closeDocDBConnection(dbTx);
        }

        return rid;
    }

    public Map<String, String> getAllQuestions() throws AppException {
        final Map<String, String> outputMap = new HashMap<String, String>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT * FROM " + CCAppConstants.QUESTION_DOCUMENT);
            final List<ODocument> results = dbNoTx.command(query).execute();
            results.forEach((final ODocument result) -> {
                final String id = result.field("QUESTION_ID");
                final String text = result.field("TEXT");
                outputMap.put(id, text);
                outputMap.put("paragraphs", result.field("PARAGRAPH_ID"));
            });
            return outputMap;
        } catch (final Exception ex) {
            Logger.error("Could not retrieve node tag details", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }



    public String saveParagraph(final String configurationObjectClass, final Map<String, Object> configurationDataMap) throws AppException {
        Validate.notBlank(configurationObjectClass, "'configurationObjectClass' cannot be empty!");
        Validate.notEmpty(configurationDataMap, "'configurationDataMap' cannot be empty!");
        boolean isNew = true;
        List<ODocument> results = null;
        String rid = null;
        final String cfgname = (String) configurationDataMap.get("QUESTION_ID");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        ODocument document = null;
        try {
            dbTx.begin();
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM " + configurationObjectClass + " WHERE QUESTION_ID = '" + cfgname + "'");
            results = dbTx.command(query).execute();
            if (results.size() > 0) {
                document = results.get(0);
                isNew = false;
            } else {
                document = new ODocument(configurationObjectClass);
            }
            for (final Map.Entry<String, Object> entry : configurationDataMap.entrySet()) {
                final String key = entry.getKey();
                if (key.equalsIgnoreCase("QUESTION_ID")) {
                    if (isNew) {
                        document.field(key, entry.getValue());
                    }
                } else {
                    document.field(key, entry.getValue());
                }
            }
            dbTx.save(document);
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not save configuration", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            rid = document.getIdentity().toString();
            closeDocDBConnection(dbTx);
        }

        return rid;
    }
}
