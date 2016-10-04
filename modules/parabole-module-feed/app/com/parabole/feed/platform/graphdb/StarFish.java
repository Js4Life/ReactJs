package com.parabole.feed.platform.graphdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
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

    public String saveOrUpdateCheckList(Map<String, Object> toSave) {

        final Map<String, Object> dataMapForCheckList = new HashMap<String, Object>();
        String rids = null;
        try{
            rids = saveAnything(CCAppConstants.APP_CHECKLIST, toSave);
        }catch (AppException e){
            e.printStackTrace();
        }
        return  rids;
    }

    public String saveOrUpdateCheckListAttachment(Map<String, Object> toSave) {

        final Map<String, Object> dataMapForCheckList = new HashMap<String, Object>();
        String rids = null;
        try{
            rids = saveAnything(CCAppConstants.APP_CHECKLIST_ATTACHMENT, toSave);
        }catch (AppException e){
            e.printStackTrace();
        }
        return  rids;
    }


    public void removeCheckList( String checkListId) throws AppException {
            executeUpdate("DELETE FROM " + CCAppConstants.APP_CHECKLIST + " WHERE DATA_ID = '" + checkListId +"'");
    }

    public void removeCheckListAttachment( String checkListAttachmetId ) throws AppException {
            executeUpdate("DELETE FROM " + CCAppConstants.APP_CHECKLIST + " WHERE data_id = '" + checkListAttachmetId +"'");
    }


    public void executeUpdate(final String sqlQuery) throws AppException {
        Validate.notBlank(sqlQuery, "'sqlQuery' cannot be empty!");
        final ODatabaseDocumentTx dbTx = getDocDBConnectionTx();
        try {
            dbTx.command(new OCommandSQL(sqlQuery)).execute();
            dbTx.commit();
        } catch (final Exception ex) {
            dbTx.rollback();
            Logger.error("Could not execute SQL Query: " + sqlQuery, ex);
            throw new AppException(AppErrorCode.GRAPH_DB_OPERATION_EXCEPTION);
        } finally {
            closeDocDBConnection(dbTx);
        }
    }


    public String getCheckListById(String checkListId) throws AppException {
        final Map<String, Object> dataMapForCheckList = new HashMap<String, Object>();
        List<Map<String, String>> rids = getByProperty(CCAppConstants.APP_CHECKLIST, checkListId);
        return  rids.toString();
    }


    public String getCheckListAttachmentIdById(String CheckListAttachmentId) throws AppException {
        List<Map<String, String>> data = getByProperty(CCAppConstants.APP_CHECKLIST_ATTACHMENT, CheckListAttachmentId);
        return  data.toString();
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
        System.out.println("data_id = " + data_id);
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

    public HashMap<String, String> getChecklistByID(String checklistID){
        final List<HashMap<String, String>> outputList = new ArrayList<HashMap<String, String>>();
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionTx();
        final HashMap<String, String> outputMap = new HashMap<String, String>();
        try {
            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT * FROM APP_CHECKLIST WHERE DATA_ID = '" + checklistID + "'");
            final List<ODocument> results = dbNoTx.command(query).execute();
            for (final ODocument result : results) {
                String[] properties = result.fieldNames();
                for (String property : properties) {
                    outputMap.put(property, result.field(property));
                }
            }

        } catch (final Exception ex) {
            Logger.error("Could not retrieve All Users", ex);
        } finally {
            closeDocDBConnection(dbNoTx);
        }
        return outputMap;
    }

    public HashMap<String,String> getCompliedAndNotCompliedCounts(){

        HashMap<String, String> resultData = new HashMap<>();
        Integer checked = 0;
        Integer notChecked = 0;
        final ODatabaseDocumentTx dbNoTx = getDocDBConnectionNoTx();
        try {

            final OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT * FROM APP_CHECKLIST");
            final List<ODocument> results = dbNoTx.command(query).execute();
            for (final ODocument result : results) {
                System.out.println("result.field(\"IS_CHECKED\") = " + result.field("IS_CHECKED"));
                if(result.field("IS_CHECKED") == null){
                    notChecked ++;
                }else if(Boolean.parseBoolean(result.field("IS_CHECKED").toString()) == true){
                    checked ++;
                }else{
                    notChecked ++;
                }
            }
            System.out.println("StarFish.getCompliedAndNotCompliedCounts");
            resultData.put("Not Complied", notChecked.toString());
            resultData.put("Complied", checked.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        return resultData;

    }

}
