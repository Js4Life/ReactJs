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
import org.json.JSONObject;
import play.Logger;

import java.sql.Timestamp;
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
        dataMapForParagraph.put("DATA_ID", "12345678");
        dataMapForParagraph.put("TEXT", "This is a text of the paragraph");
        String rids = saveAnything(CCAppConstants.PARAGRAPH_DOCUMENT, dataMapForParagraph);

        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("DATA_ID", "12345678");
        dataMap.put("TEXT", "This is a text of the paragraph");
        dataMap.put("IS_MANDATORY", true);
        dataMap.put("PARAGRAPH_ID", rids);
        return saveAnything(CCAppConstants.QUESTION_DOCUMENT, dataMap);
    }

    public String saveOrUpdateCheckList(String checkListId, String checklistText) throws AppException {
        final Map<String, Object> dataMapForCheckList = new HashMap<String, Object>();
        dataMapForCheckList.put("DATA_ID", checkListId);
        dataMapForCheckList.put("TEXT", checklistText);
        String rids = saveAnything(CCAppConstants.APP_CHECKLIST, dataMapForCheckList);
        return  rids;
    }


    public String removeCheckList( String checkListId) throws AppException {
        final Map<String, Object> dataMapForCheckList = new HashMap<String, Object>();
        dataMapForCheckList.put("DATA_ID", checkListId);
        String rids = removeByProperty(CCAppConstants.APP_CHECKLIST, checkListId);
        return  rids;
    }


    public String getCheckListById(String checkListId) throws AppException {
        final Map<String, Object> dataMapForCheckList = new HashMap<String, Object>();
        List<Map<String, String>> rids = getByProperty(CCAppConstants.APP_CHECKLIST, checkListId);
        return  rids.toString();
    }

    public List<Map<String, String>> getByProperty(final String configurationObjectClass, final String checkListId) throws AppException {
        Validate.notBlank(checkListId, "'checkListId' cannot be empty!");
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        JSONObject jsonObject = null;
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT DATA_ID, TEXT  FROM " + configurationObjectClass + " WHERE DATA_ID = '" + checkListId + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            results.forEach((final ODocument result) -> {
                final Map<String, String> outputMap = new HashMap<String, String>();
                final String configurationNameCollected = result.field("DATA_ID");
                final String configurationDetails = result.field("TEXT");
                outputMap.put("name", configurationNameCollected);
                outputMap.put("details", configurationDetails);
                outputList.add(outputMap);
            });
            return (outputList);
        } catch (final Exception ex) {
            Logger.error("Could not retrieve configuration", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

    public String saveParagraph(String paragraphId, String paragraphText, String tag) throws AppException {
        //final Integer configurationId = generateId(CCAppConstants.RDA_USER_CONFIGS);

        final Map<String, Object> dataMapForParagraph = new HashMap<String, Object>();
        dataMapForParagraph.put("DATA_ID", paragraphId);
        dataMapForParagraph.put("TEXT", paragraphText);
        dataMapForParagraph.put("TAG", tag);
        return saveAnything(CCAppConstants.PARAGRAPH_DOCUMENT, dataMapForParagraph);

     /*   final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("DATA_ID", "12345678");
        dataMap.put("TEXT", "This is a text of the paragraph");
        dataMap.put("IS_MANDATORY", true);
        dataMap.put("PARAGRAPH_ID", rids);
        return saveAnything(CCAppConstants.QUESTION_DOCUMENT, dataMap);*/
    }

    public String removeByProperty(final String configurationObjectClass, final String checkListId) throws AppException {
            Validate.notNull(checkListId, "'configurationId' cannot be null!");
            executeUpdate("DELETE FROM " + configurationObjectClass + " WHERE DATA_ID = " + checkListId);

            return "Success !";

    }


    public String saveAnything(final String configurationObjectClass, final Map<String, Object> configurationDataMap) throws AppException {
        Validate.notBlank(configurationObjectClass, "'configurationObjectClass' cannot be empty!");
        Validate.notEmpty(configurationDataMap, "'configurationDataMap' cannot be empty!");
        boolean isNew = true;
        List<ODocument> results = null;
        String rid = null;
        final String data_id = (String) configurationDataMap.get("DATA_ID");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        ODocument document = null;
        try {
            dbTx.begin();
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM " + configurationObjectClass + " WHERE DATA_ID = '" + data_id + "'");
            results = dbTx.command(query).execute();
            if (results.size() > 0) {
                document = results.get(0);
                isNew = false;
            } else {
                document = new ODocument(configurationObjectClass);
            }
            for (final Map.Entry<String, Object> entry : configurationDataMap.entrySet()) {
                final String key = entry.getKey();
                if (key.equalsIgnoreCase("DATA_ID")) {
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


    public List<Map<String, String>> getAllParagraphs() throws AppException {
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT DATA_ID, TEXT, TAG FROM APP_PARAGRAPHS");
            final List<ODocument> results = dbNoTx.command(query).execute();
            for (final ODocument result : results) {
                final Map<String, String> outputMap = new HashMap<String, String>();
                final String data_id = result.field("DATA_ID");
                final String text = result.field("TEXT");
                final String tag = result.field("TAG");

                outputMap.put("data_id", data_id);
                outputMap.put("text", text);
                outputMap.put("tag", tag);

                outputList.add(outputMap);
            }
            return outputList;
        } catch (final Exception ex) {
            Logger.error("Could not retrieve All Users", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

    public List<Map<String, String>> getAllParagraphsByTag(String tagInput) throws AppException {
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionTx();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT DATA_ID, TEXT, TAG FROM APP_PARAGRAPHS WHERE TAG = '" + tagInput + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            for (final ODocument result : results) {
                final Map<String, String> outputMap = new HashMap<String, String>();
                final String data_id = result.field("DATA_ID");
                final String text = result.field("TEXT");
                final String tag = result.field("TAG");

                outputMap.put("data_id", data_id);
                outputMap.put("text", text);
                outputMap.put("tag", tag);

                outputList.add(outputMap);
            }
            return outputList;
        } catch (final Exception ex) {
            Logger.error("Could not retrieve All Users", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }

        public Map<String, String> getParagraphTagByParagraphid(String paragraphID) throws AppException {
        final List<Map<String, String>> outputList = new ArrayList<Map<String, String>>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionTx();
        final Map<String, String> outputMap = new HashMap<String, String>();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT DATA_ID, TEXT, TAG FROM APP_PARAGRAPHS WHERE DATA_ID = '" + paragraphID + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            for (final ODocument result : results) {

                final String data_id = result.field("DATA_ID");
                final String text = result.field("TEXT");
                final String tag = result.field("TAG");

                outputMap.put("data_id", data_id);
                outputMap.put("text", text);
                outputMap.put("tag", tag);

             //   outputList.add(outputMap);
            }
            return outputMap;
        } catch (final Exception ex) {
            Logger.error("Could not retrieve All Users", ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
    }



}
