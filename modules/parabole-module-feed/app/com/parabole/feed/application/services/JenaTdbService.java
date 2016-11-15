package com.parabole.feed.application.services;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppErrorCode;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.application.utils.AppUtils;
import com.parabole.feed.platform.reasoner.BaseBindObj;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.jena.query.*;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Configuration;
import play.Logger;
import play.db.DB;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.parabole.feed.application.utils.AppUtils.writeFile;

public class JenaTdbService {

    @Inject
    private CoralConfigurationService coralConfigurationService;

    public JenaTdbService(){

    }

    public void insert( final String keyName){
        String cfgInfo;
        Dataset dataset = null;
        try {
            cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_BATCHINSRT_FILE);
            final JsonObject jsonCfg = new JsonParser().parse(cfgInfo).getAsJsonObject();
            final JsonObject keyObj = jsonCfg.get(keyName).getAsJsonObject();
            final String selectSparql = keyObj.get("selectSparqlFile").getAsString();
            String grpByField = null;
            if( keyObj.has("groupByField")) {
                grpByField = keyObj.get("groupByField").getAsString();
            }
            final JsonArray colArrJson = keyObj.get("selectColumns").getAsJsonArray();
            final String qryFileName = keyObj.get("sparqlFile").getAsString();
            final String qrysparqlTpl = keyObj.get("sparqlTpl").getAsString();
            final String sparqlUpdateString = AppUtils.getFileContent("sparql/" + qryFileName);
            final String directory = CCAppConstants.JENA_TDB_STOREROOM;
            dataset = TDBFactory.createDataset(directory);
            dataset.begin(ReadWrite.WRITE);

            final List<String> insertList = new ArrayList<String>();
            int rowSize = 0;
            final HashMap<String,Object[]> valMap = new HashMap<String,Object[]>();
            for (final JsonElement jsonElement : colArrJson) {
                final String colName = jsonElement.getAsString();
                final HashMap<String, Set<String>> valueMap = getValueFromQuery(selectSparql, colName, grpByField, dataset);
                final Set<String> values = valueMap.getOrDefault(colName, null);
                rowSize = values.size();
                valMap.put(colName, values.toArray());
            }
            for(int i = 0; i < rowSize; i++){
                String tmp = qrysparqlTpl;
                for (final JsonElement jsonElement : colArrJson) {
                    final String colName = jsonElement.getAsString();
                    final Object[] values = valMap.getOrDefault(colName, null);
                    String value = (String)values[i];
                    System.out.println("P"+value);
                    value = value.replace(" ", "_")
                            .replace(" - ", "_").replace("/", "_").replace("?", "").replace("(", "").replace(")","");
                    System.out.println("A"+value);
                    tmp = tmp.replace("$$" + colName + "$$", value);
                }
                insertList.add(tmp);
            }
            final String body = insertList.stream().collect(Collectors.joining("\r\n"));
            String tmp = sparqlUpdateString;
            //DO DELETE FIRST
            tmp = tmp.replace("$$STATEMENT$$", "DELETE WHERE");
            tmp = tmp.replace("$$BODY$$", body);
            jenaTDBUpdateDataset(tmp,dataset);
            //DO INSERT
            tmp = sparqlUpdateString;
            tmp = tmp.replace("$$STATEMENT$$", "INSERT DATA");
            tmp = tmp.replace("$$BODY$$", body);
            jenaTDBUpdateDataset(tmp,dataset);
            dataset.commit();
        } catch (final AppException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            dataset.close();
        }
    }

    private JSONObject getDataSource(final String nameOfTheSource) throws JSONException {
        List<Map<String, String>> data = null;
        JSONArray receivedData = new JSONArray();
        try {
            data = coralConfigurationService.getConfigurationByName("root", nameOfTheSource);
            receivedData = new JSONArray(data);
            Logger.info("|JenaTdbService|126|  datasource ----> "+receivedData +" nameOfTheSource -->"+ nameOfTheSource + " data --> "+ data);
        } catch (com.parabole.feed.platform.exceptions.AppException e) {
            e.printStackTrace();
        }
        final String details = receivedData.getJSONObject(0).getString("details").toString();
        final JSONObject finalDataSource = new JSONObject(details);
        return finalDataSource;
    }

    public void insertBatch(final String keyName) throws JSONException {
        String cfgInfo;
        Dataset dataset = null;

        final JSONObject dataSource = getDataSource(Configuration.root().getString("denodo.dataSourceToUse"));
        final String driver = dataSource.getString("driver");
        final String url = dataSource.getString("connectionstr");
        final String userId = dataSource.getString("userid");
        final String password = dataSource.getString("password");

        /*
         * final String driver =
         * Configuration.root().getString("denodo.driver"); final String url =
         * Configuration.root().getString("denodo.url"); final String userId =
         * Configuration.root().getString("denodo.username"); final String
         * password = Configuration.root().getString("denodo.password");
         */

        try {
            cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_BATCHINSRT_FILE);
            final JsonObject jsonCfg = new JsonParser().parse(cfgInfo).getAsJsonObject();
            final JsonObject keyObj = jsonCfg.get(keyName).getAsJsonObject();
            final String dbName = keyObj.get("dbName").getAsString();
            final String tblName = keyObj.get("tableName").getAsString();
            final String qryFileName = keyObj.get("sparqlFile").getAsString();
            final ArrayList<String> listOfQrysparqlTpl = new ArrayList<>();
            final JsonElement elem = keyObj.get("sparqlTpl");
            if(elem instanceof JsonArray){
                final JsonArray jsonArray = keyObj.getAsJsonArray("sparqlTpl");
                for (final JsonElement jsonElement : jsonArray) {
                    listOfQrysparqlTpl.add(jsonElement.getAsString());
                }
            }else {
                final String qrysparqlTpl = keyObj.get("sparqlTpl").getAsString();
                listOfQrysparqlTpl.add(qrysparqlTpl);
                Logger.info(qrysparqlTpl.toString());
            }
            final String where = keyObj.get("where").getAsString();

            final JsonArray columns = keyObj.getAsJsonArray("selectColumns");
            final Set<String> colSet = new HashSet<String>();
            columns.forEach((a) -> colSet.add(a.getAsString()));
            final String selColStr = colSet.stream().collect(Collectors.joining(" , "));
            final String sparqlUpdateString = AppUtils.getFileContent("sparql/" + qryFileName);
            final StringBuilder selectQuery = new StringBuilder("SELECT ");
            selectQuery.append(selColStr).append(" FROM ").append(dbName).append(".").append(tblName);
            final Connection connection = getConnection(driver,url,userId,password);//DB.getConnection();
            Statement statement;
            final String directory = CCAppConstants.JENA_TDB_STOREROOM;
            final List<String> insertList = new ArrayList<String>();
            try {
                dataset = TDBFactory.createDataset(directory);
                dataset.begin(ReadWrite.WRITE);
                statement = connection.createStatement();
                String qryStr = selectQuery.toString();
                if( where.trim().length() > 0 ) {
                    qryStr += " WHERE " + where;
                }
                System.out.println(qryStr);
                statement.executeQuery(qryStr);
                final java.sql.ResultSet result = statement.getResultSet();
                if (null != result) {
                    while (result.next()) {
                        for (final String  qrysparqlTpl: listOfQrysparqlTpl) {
                            String tmp = qrysparqlTpl;
                            for (final String colName : colSet) {
                                final Object columnValue = result.getObject(colName);
                                String value = columnValue.toString();
                                value = value.
                                        replace("—", "_").
                                        replace(".", ".").
                                        replace(" - ", "_").
                                        replace(" / ", "_").
                                        replace("-", "_").
                                        replace("/", "_").
                                        replace("#", "Number_of_").
                                        replace("Schedule", "Sch").
                                        replace("N/A", "Not Applicable").
                                        replace("$", "USD_").
                                        replace(" & ", "_").
                                        replace("&", "and").
                                        replace("?", "").
                                        replace("(", "").
                                        replace(":", "").
                                        replace("'", "").
                                        replace("\n", "").
                                        replace("[", "").
                                        replace("]", "").
                                        replace(")","").
                                        replace("'","").
                                        replace(",","").
                                        replace("Agm Attr", "Agreement Attribute").
                                        replace("Cust", "Customer").
                                        replace("Coll", "Collateral").
                                        replace("Agm-Fac-Bal", "Agreement Facility Balance").
                                        replace("Agm bal", "Agreement Balance").
                                        replace("guar", "Guarantor").
                                        replace(" ", "_");
                                tmp = tmp.replace("$$" + colName + "$$", value);
                                final String newColumnValue = new String ( columnValue.toString());
                                tmp = tmp.replace("##" + colName + "##", newColumnValue);
                            }
                            insertList.add( tmp );
                        }
                    }
                }
                final String body = insertList.stream().collect(Collectors.joining("\r\n"));
                String tmp = sparqlUpdateString;
                //DO DELETE FIRST
                tmp = tmp.replace("$$STATEMENT$$", "DELETE WHERE");
                tmp = tmp.replace("$$BODY$$", body);
                jenaTDBUpdateDataset(tmp,dataset);
                //DO INSERT
                tmp = sparqlUpdateString;
                tmp = tmp.replace("$$STATEMENT$$", "INSERT DATA");
                tmp = tmp.replace("$$BODY$$", body);
                jenaTDBUpdateDataset(tmp,dataset);
                dataset.commit();
            } catch (final SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (final AppException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            dataset.close();
        }
    }

    public void updateTest( final String fileIdentification ){
        final String directory = CCAppConstants.JENA_TDB_STOREROOM;
        Dataset dataset = null;
        try {
            final String sparqlUpdateTemplate = AppUtils.getFileContent("sparql/sparqlQuery" + fileIdentification + ".rq");
            dataset = TDBFactory.createDataset(directory);
            dataset.begin(ReadWrite.WRITE);
            jenaTDBUpdateDataset(sparqlUpdateTemplate,dataset);
            dataset.commit();
        }catch(final Exception e){
            e.printStackTrace();
        }finally{
            dataset.close();
        }
    }

    public void jenaTDBUpdate(final String fileIdentification) throws AppException {
        final String sqlQueryString = AppUtils.getFileContent("sql/sqlQuery" + fileIdentification + ".rq");
        final String sparqlUpdateTemplate = AppUtils.getFileContent("sparql/sparqlUpdate" + fileIdentification + ".rq");
        final String directory = CCAppConstants.JENA_TDB_STOREROOM;
        Dataset dataset = null;
        try {
            dataset = TDBFactory.createDataset(directory);
            final Connection connection = DB.getConnection();
            final Statement statement = connection.createStatement();
            statement.executeQuery(sqlQueryString);
            final java.sql.ResultSet result = statement.getResultSet();
            if (null != result) {
                final ResultSetMetaData metadata = result.getMetaData();
                final int columnsCount = metadata.getColumnCount();
                final Set<String> columnNames = new HashSet<String>();
                for (int i = 1; i <= columnsCount; i++) {
                    columnNames.add(metadata.getColumnLabel(i));
                }
                while (result.next()) {
                    String sparqlUpdateString = sparqlUpdateTemplate;
                    for (final String columnName : columnNames) {
                        final Object columnValue = result.getObject(columnName);
                        sparqlUpdateString = sparqlUpdateString.replace("$$" + columnName, columnValue.toString());
                    }
                    jenaTDBUpdateDataset(sparqlUpdateString, dataset);
                }
            }
        } catch (final SQLException sqlEx) {
            Logger.error("SQL JDBC Error", sqlEx);
        } finally {
            dataset.close();
        }
    }

    public String jenaTdbFetching(final String fileIdentification) throws AppException {
        final String directory = CCAppConstants.JENA_TDB_STOREROOM;
        final Dataset dataset = TDBFactory.createDataset(directory);
        dataset.begin(ReadWrite.READ);
        final String sparqlQueryString = AppUtils.getFileContent("sparql/sparqlQuery" + fileIdentification + ".rq");
        System.out.println("sparqlQueryString = " + sparqlQueryString);
        final Query query = QueryFactory.create(sparqlQueryString);
        final QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        final ResultSet resultsData = qexec.execSelect();
        dataset.close();
        return sparkleResultToJSON(resultsData);
    }

    public String jenaTdbFetchingByClassName(final String className) throws AppException, JSONException {

        final String fileString1 = AppUtils.getFileContent("json/mapClassNumber.json");
        final JSONObject jsonObject1 = new JSONObject(fileString1);
        final String finalClassName = (String) jsonObject1.get(className);
        final String directory = CCAppConstants.JENA_TDB_STOREROOM;
        final Dataset dataset = TDBFactory.createDataset(directory);
        dataset.begin(ReadWrite.READ);
        final String fileString = AppUtils.getFileContent("sparql/sparqlQuery1001.rq");
        final String sparqlQueryString = fileString.concat(" FILTER(?resource="+ finalClassName +")}");
        final Query query = QueryFactory.create(sparqlQueryString);
        final QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        final ResultSet resultsData = qexec.execSelect();
        dataset.close();
        String nameOfTheFile = null;
        final JSONObject jsonObject = new JSONObject(sparkleResultToJSON(resultsData));
        nameOfTheFile = (String) jsonObject.getJSONObject("results").getJSONArray("bindings").getJSONObject(0).getJSONObject("label").get("value");
        return nameOfTheFile;
    }

    public JsonArray loadWidgetDataByParams(final String param1, final String param2) throws AppException {
        final JsonArray jsArr = new JsonArray();
        final JSONObject data = new JSONObject();
        final JSONObject options = new JSONObject();
        HashMap<String, Set<String>> valueMap = null;
        try {
            final Dataset dataset = getDataset();
            dataset.begin(ReadWrite.READ);
            final String cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_WIDGETDEF_FILE);
            final JsonObject jsonCfg = new JsonParser().parse(cfgInfo).getAsJsonObject();
            final JsonObject widgetDefJson = jsonCfg.getAsJsonObject("HeatMapCellInfo");
            final String sparqlFile = widgetDefJson.get("sparqlFile").getAsString();
            final JsonArray selectColumns = widgetDefJson.get("selectColumns").getAsJsonArray();
            final String colName = widgetDefJson.get("name").getAsString();
            final String groupByField = widgetDefJson.get("groupByField").getAsString();
            final String body = widgetDefJson.get("where").getAsString();
            StringBuilder queryStr = new StringBuilder("SELECT");
            for (final JsonElement jsonElement : selectColumns) {
                queryStr.append(" ").append(jsonElement.getAsString()).append(" ");
            }
            final String selectClause = queryStr.append(" WHERE ").toString();
            queryStr = new StringBuilder(body);
            /*
             * queryStr.append(" ").append("FILTER (?label = \""
             * ).append(param1).append("\") ").append("FILTER (?label = \""
             * ).append(param2).append("\") ");
             */

            String sparqlQueryString = AppUtils.getFileContent("sparql/" + sparqlFile);
            sparqlQueryString = sparqlQueryString.replace("$$STATEMENT$$", selectClause);
            sparqlQueryString = sparqlQueryString.replace("$$BODY$$", queryStr.toString());

            valueMap = getValueFromQueryString(sparqlQueryString, colName, groupByField, dataset);
            if (valueMap != null && valueMap.size() > 0) {
                for (final String key : valueMap.keySet()) {
                    final Set<String> value = valueMap.getOrDefault(key, null);
                    if (value.size() > 0) {
                        final String name = value.iterator().next();
                        JsonObject jsObj;
                        jsObj = loadSingleSeriesWidgetData(name);
                        jsArr.add(jsObj);
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return jsArr;
    }

    public JsonObject loadWidgetData(final String compName) throws AppException {
        JsonObject finalJson = new JsonObject();
        Dataset dataset = null;
        try {
            final String directory = CCAppConstants.JENA_TDB_STOREROOM;
            dataset = TDBFactory.createDataset(directory);
            dataset.begin(ReadWrite.READ);
            final String cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_WIDGETDEF_FILE);
            final JsonObject jsonCfg = new JsonParser().parse(cfgInfo).getAsJsonObject();
            final JsonObject widgetDefJson = jsonCfg.getAsJsonObject(compName);
            if (widgetDefJson == null) {
                throw new AppException(AppErrorCode.SYSTEM_EXCEPTION);
            }
            final HashMap<String, HashMap<String, Set<String>>> rowColMap = new HashMap<String, HashMap<String, Set<String>>>();
            final JsonArray colArrJson = widgetDefJson.getAsJsonArray("columns");
            final String grpByField = widgetDefJson.get("groupByField").getAsString();
            final JsonArray colorRange = widgetDefJson.getAsJsonArray("colorRange");
            final String outputFormat = widgetDefJson.get("outputFormat").getAsString();
            final JsonElement description = widgetDefJson.get("description");

            if (outputFormat.equals(CCAppConstants.PROGRESS_CHART) || outputFormat.equals(CCAppConstants.COLUMN_CHART)) {
                finalJson = createProgressData(compName, grpByField, dataset, colArrJson, rowColMap, outputFormat);
            } else if (outputFormat.equals(CCAppConstants.BAR_CHART)) {
                finalJson = createChartData(compName, grpByField, dataset, colArrJson, rowColMap);
            } else {
                finalJson = createTableData(compName, grpByField, dataset, colArrJson, rowColMap, colorRange);
            }
            if (description != null) {
                createExtraInfo(description.getAsJsonObject(), finalJson, grpByField, dataset);
            }
        } catch (final AppException e) {
            e.printStackTrace();
        } finally {
            dataset.close();
        }
        return finalJson;
    }

    private JsonObject createProgressData(final String name, final String grpByField, final Dataset dataset, final JsonArray colArrJson, final HashMap<String, HashMap<String, Set<String>>> rowColMap, final String chartType) throws AppException {
        // TODO Auto-generated method stub
        //
        final JsonObject finalJson = new JsonObject();
        final JsonArray columns = new JsonArray();
        finalJson.addProperty("name", name);
        if (chartType != null) {
            finalJson.addProperty("chartType", chartType);
        }
        final JsonArray metricsArr = new JsonArray();
        finalJson.add("metrics", metricsArr);
        for (final JsonElement jsonElement : colArrJson) {
            final JsonObject aMetrics = new JsonObject();
            metricsArr.add(aMetrics);
            final JsonObject colObj = jsonElement.getAsJsonObject();
            final String colName = colObj.get("name").getAsString();
            final String colLabel = colObj.get("label").getAsString();
            columns.add(new JsonPrimitive(colLabel));
            final HashMap<String, Set<String>> valueMap = getValueFromQuery(colObj.get("fileName").getAsString(), colName, grpByField, dataset);
            aMetrics.addProperty("name", colLabel);
            final Set<String> values = valueMap.getOrDefault(colName, null);
            double dVal = 0;
            if(values != null ) {
                dVal = values.stream().findFirst().map(Double::parseDouble).get();
            }
            aMetrics.add("value", new JsonPrimitive(dVal));
        }
        return finalJson;
    }

    private void jenaTDBUpdateDataset(final String sparqlUpdateString, final Dataset dataset) throws AppException {
        System.out.println(sparqlUpdateString);
        final DatasetGraph dsGraph = DatasetGraphFactory.create(dataset.asDatasetGraph());
        final UpdateRequest request = UpdateFactory.create(sparqlUpdateString);
        final UpdateProcessor proc = UpdateExecutionFactory.create(request, dsGraph);
        proc.execute();
    }

    private JsonObject createTableData(final String name, final String grpByField, final Dataset dataset, final JsonArray colArrJson, final HashMap<String, HashMap<String, Set<String>>> rowColMap, final JsonArray colorRange) throws AppException {
        final JsonObject finalJson = new JsonObject();
        final JsonArray columns = new JsonArray();
        final JsonArray rows = new JsonArray();
        int count = 0;
        HashMap<String, Set<String>> tmp = null;
        for (final JsonElement jsonElement : colArrJson) {
            final JsonObject colObj = jsonElement.getAsJsonObject();
            final String colName = colObj.get("name").getAsString();
            final String colLabel = colObj.get("label").getAsString();
            columns.add(new JsonPrimitive(colLabel));
            final HashMap<String, Set<String>> valueMap = getValueFromQuery(colObj.get("fileName").getAsString(), colName, grpByField, dataset);
            rowColMap.put(colName, valueMap);
            if (valueMap.size() >= count) {
                count = valueMap.size();
                tmp = valueMap;
            }
        }
        final List<String> indexValueList = new ArrayList<String>();
        for (final String aVal : tmp.keySet()) {
            indexValueList.add(aVal);
        }
        for (final String indexVal : indexValueList) {
            final JsonArray aRow = new JsonArray();
            for (final JsonElement jsonElement : colArrJson) {
                final JsonObject colObj = jsonElement.getAsJsonObject();
                final String colName = colObj.get("name").getAsString();
                final Boolean hasColor = colObj.has("hasColor") ? true : false;
                final Boolean hasExponent = colObj.has("hasExponent") ? true : false;
                final JsonObject aColObj = new JsonObject();
                String value = "";
                if (rowColMap.containsKey(colName)) {
                    final HashMap<String, Set<String>> aRowMap = rowColMap.get(colName);
                    if (aRowMap.containsKey(indexVal)) {
                        final Set<String> valueList = aRowMap.get(indexVal);
                        value = valueList.stream().collect(Collectors.joining(", "));
                    } else {
                        value = "";
                    }
                }
                aColObj.addProperty("name", value);
                if (hasColor) {
                    calculateRag(aColObj, colorRange);
                }
                if (hasExponent) {
                    convertExponentToDecimal(aColObj);
                }
                aRow.add(aColObj);
            }
            rows.add(aRow);
        }
        finalJson.add("name", new JsonPrimitive(name));
        finalJson.add("columns", columns);
        finalJson.add("rows", rows);
        return finalJson;
    }

    private JsonObject createChartData(final String name, final String grpByField, final Dataset dataset, final JsonArray colArrJson, final HashMap<String, HashMap<String, Set<String>>> rowColMap) throws AppException {
        final JsonObject finalJson = new JsonObject();
        final JsonArray columns = new JsonArray();
        final JsonArray rows = new JsonArray();
        int count = 0;
        HashMap<String, Set<String>> tmp = null;
        for (final JsonElement jsonElement : colArrJson) {
            final JsonObject colObj = jsonElement.getAsJsonObject();
            final String colName = colObj.get("name").getAsString();
            final HashMap<String, Set<String>> valueMap = getValueFromQuery(colObj.get("fileName").getAsString(), colName, grpByField, dataset);
            rowColMap.put(colName, valueMap);
            if (valueMap.size() >= count) {
                count = valueMap.size();
                tmp = valueMap;
            }
        }
        final List<String> indexValueList = new ArrayList<String>();
        for (final String aVal : tmp.keySet()) {
            indexValueList.add(aVal);
            columns.add(new JsonPrimitive(aVal));
        }
        final HashMap<String, JsonObject> seriesMap = new HashMap<String, JsonObject>();
        for (final String indexVal : indexValueList) {
            final JsonArray aRow = new JsonArray();
            for (final JsonElement jsonElement : colArrJson) {
                final JsonObject colObj = jsonElement.getAsJsonObject();
                final String colName = colObj.get("name").getAsString();
                final JsonObject aColObj = new JsonObject();
                String value = "";
                if (rowColMap.containsKey(colName)) {
                    final String colLabel = colObj.get("label").getAsString();
                    final HashMap<String, Set<String>> aRowMap = rowColMap.get(colName);
                    if (aRowMap.containsKey(indexVal)) {
                        JsonObject o = new JsonObject();
                        if (seriesMap.containsKey(indexVal)) {
                            o = seriesMap.get(indexVal);
                        } else {
                            o = new JsonObject();
                            columns.add(new JsonPrimitive(indexVal));
                            o.addProperty("name", colLabel);
                            o.add("data", new JsonArray());
                            seriesMap.put(indexVal, o);
                        }
                        final Set<String> valueList = aRowMap.get(indexVal);
                        final double dVal = valueList.stream().findFirst().map(Double::parseDouble).get();
                        o.get("data").getAsJsonArray().add(new JsonPrimitive(dVal));
                    } else {
                        value = "";
                    }
                }
                aColObj.addProperty("name", indexVal + "Name");
                aColObj.addProperty("data", value);
                aRow.add(aColObj);
            }
            rows.add(aRow);
        }
        finalJson.add("name", new JsonPrimitive(name));
        finalJson.add("categories", columns);
        final JsonArray seriesArr = new JsonArray();
        for (final String string : seriesMap.keySet()) {
            seriesArr.add(seriesMap.get(string));
        }
        finalJson.add("series", seriesArr);
        return finalJson;
    }

    private HashMap<String, Set<String>> getValueFromQuery(final String fileName, final String attrName, final String groupByField, final Dataset dataset) throws AppException {
        final String sparqlQueryString = AppUtils.getFileContent("sparql/" + fileName);
        final HashMap<String, Set<String>> retList = getValueFromQueryString(sparqlQueryString, attrName, groupByField, dataset);
        return retList;
    }

    private HashMap<String, Set<String>> getValueFromQueryString(final String sparqlQueryString, final String attrName, final String groupByField, final Dataset dataset) throws AppException {
        final LinkedHashMap<String, Set<String>> retList = new LinkedHashMap<String, Set<String>>();
        final Query query = QueryFactory.create(sparqlQueryString);
        final QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        final ResultSet resultsData = qexec.execSelect();
        final JsonObject resultObj = new JsonParser().parse(sparkleResultToJSON(resultsData)).getAsJsonObject();
        final JsonArray bindings = resultObj.getAsJsonObject("results").getAsJsonArray("bindings");
        for (final JsonElement jsonElement : bindings) {
            final JsonObject bindObj = jsonElement.getAsJsonObject();
            final JsonObject targetObject = bindObj.getAsJsonObject(attrName);
            if (targetObject != null) {
                if( (groupByField != null) && (groupByField.trim().length() > 0) ){
                    final String groupByFieldVal = bindObj.getAsJsonObject(groupByField).get("value").getAsString();
                    Set<String> valueList = null;
                    if (!retList.containsKey(groupByFieldVal)) {
                        valueList = new HashSet<String>();
                        retList.put(groupByFieldVal, valueList);
                    } else {
                        valueList = retList.get(groupByFieldVal);
                    }
                    valueList.add(targetObject.get("value").getAsString());
                }else{
                    Set<String> valueList = null;
                    if (!retList.containsKey(attrName)){
                        valueList = new HashSet<String>();
                        retList.put(attrName, valueList);
                    } else {
                        valueList = retList.get(attrName);
                    }
                    valueList.add(targetObject.get("value").getAsString());
                }
            }
        }
        return retList;
    }

    private HashMap<String, Set<String>> executeSparql(final String fileName, final String attrName, final String groupByField, final Dataset dataset) throws AppException {
        final HashMap<String, Set<String>> retList = new HashMap<String, Set<String>>();
        final String sparqlQueryString = AppUtils.getFileContent("sparql/" + fileName);
        final Query query = QueryFactory.create(sparqlQueryString);
        final QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        final ResultSet resultsData = qexec.execSelect();
        final JsonObject resultObj = new JsonParser().parse(sparkleResultToJSON(resultsData)).getAsJsonObject();

        return retList;
    }

    private String sparkleResultToJSON(final ResultSet results) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, results);
        return new String(outputStream.toByteArray());
    }

    private void calculateRag(final JsonObject jsObj, final JsonArray colorRange) {
        final String name = jsObj.get("name").getAsString();
        if (NumberUtils.isNumber(name)) {
            final DecimalFormat df = new DecimalFormat("####.##");
            // final double value = Double.parseDouble(name) * 100;
            final double value = Double.parseDouble(name);
            String color = "";
            for (final JsonElement jsonElement : colorRange) {
                final JsonObject obj = jsonElement.getAsJsonObject();
                final double lowerLimit = obj.get("lowerLimit").getAsDouble();
                final double upperLimit = obj.get("upperLimit").getAsDouble();

                if (greaterThanEqual(lowerLimit).and(lessThanEqual(upperLimit)).test(value)) {
                    color = obj.get("value").getAsString();
                    break;
                }
            }
            jsObj.remove("name");
            jsObj.addProperty("name", df.format(value));
            jsObj.addProperty("color", color);
        }
    }

    private Predicate<Double> greaterThanEqual(final Double val) {
        return (a) -> a >= val;
    }

    private Predicate<Double> lessThanEqual(final Double val) {
        return (a) -> a <= val;
    }

    private void convertExponentToDecimal(final JsonObject jsObj) {
        final String name = jsObj.get("name").getAsString();
        if (NumberUtils.isNumber(name)) {
            final double value = Double.parseDouble(name);
            jsObj.remove("name");
            jsObj.addProperty("name", value);
        }
    }

    private Connection getConnection (final String driver , final String url , final String uid , final String pwd)
    {
        try {
            Class.forName(driver);
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return DriverManager.getConnection(url, uid, pwd);
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonArray loadSeriesWidgetData(final String compName, final String reportType) throws AppException {
        JsonArray jsArr = new JsonArray();
        Dataset dataset = null;
        final String cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_WIDGETDEF_FILE);
        final JsonObject jsonCfg = new JsonParser().parse(cfgInfo).getAsJsonObject();
        final JsonObject widgetDefJson = jsonCfg.getAsJsonObject(compName);
        try {
            final String directory = CCAppConstants.JENA_TDB_STOREROOM;
            dataset = TDBFactory.createDataset(directory);
            dataset.begin(ReadWrite.READ);
            switch (compName) {
                case CCAppConstants.REGULATORY_WIDGETS:
                case CCAppConstants.EDM_WIDGETS:
                case "FRY-14Q-Schedule":
                case CCAppConstants.FRY14Q_WIDGETS:
                    jsArr = fetchEwgReportData(widgetDefJson, dataset);
                    break;
                default:
                    if (reportType.equals(CCAppConstants.GRAPH_WIDGETS)) {
                        jsArr = fetchEwgReportData(widgetDefJson, dataset);
                    } else {
                        jsArr = fetchScheduleData(widgetDefJson, dataset);
                    }
                    break;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            dataset.close();
        }
        return jsArr;
    }

    private JsonArray fetchScheduleData(final JsonObject widgetDefJson, final Dataset dataset) {
        final JsonArray jsArr = new JsonArray();
        final JsonArray schedules = widgetDefJson.get("columns").getAsJsonArray();
        try {
            for (final JsonElement jsonElement : schedules) {
                final JsonObject jsObj = jsonElement.getAsJsonObject();
                final HashMap<String, HashMap<String, Set<String>>> rowColMap = new HashMap<String, HashMap<String, Set<String>>>();
                final JsonArray colArrJson = jsObj.getAsJsonArray("columns");
                final String grpByField = jsObj.get("groupByField").getAsString();
                final String outputFormat = jsObj.get("outputFormat").getAsString();
                final String label = jsObj.get("label").getAsString();
                final JsonElement description = jsObj.get("description");
                final JsonObject finalJson = createProgressData("", grpByField, dataset, colArrJson, rowColMap, null);
                finalJson.addProperty("name", label);
                if (description != null) {
                    createExtraInfo(description.getAsJsonObject(), finalJson, grpByField, dataset);
                }
                jsArr.add(finalJson);
            }
        } catch (final AppException e) {
            e.printStackTrace();
        }
        return jsArr;
    }

    private JsonArray fetchReportData(final JsonObject widgetDefJson, final Dataset dataset) {
        final JsonArray jsArr = new JsonArray();
        final String colName = widgetDefJson.get("name").getAsString();
        final String groupByField = widgetDefJson.get("groupByField").getAsString();
        final String fileName = widgetDefJson.get("fileName").getAsString();
        HashMap<String, Set<String>> valueMap = null;
        try {
            valueMap = getValueFromQuery(fileName, colName, groupByField, dataset);
            if (valueMap != null && valueMap.size() > 0) {
                for (final String key : valueMap.keySet()) {
                    final Set<String> value = valueMap.getOrDefault(key, null);
                    if (value.size() > 0) {
                        final String name = value.iterator().next();
                        JsonObject jsObj;
                        jsObj = loadWidgetData(name);
                        // jsObj.addProperty("name", name);
                        jsArr.add(jsObj);
                    }
                }
            }
        } catch (final AppException e) {
            e.printStackTrace();
        }
        return jsArr;
    }

    private JsonArray fetchEwgReportData(final JsonObject widgetDefJson, final Dataset dataset) {
        final JsonArray jsArr = new JsonArray();
        final String colName = widgetDefJson.get("name").getAsString();
        final String groupByField = widgetDefJson.get("groupByField").getAsString();
        final String fileName = widgetDefJson.get("fileName").getAsString();
        HashMap<String, Set<String>> valueMap = null;
        try {
            valueMap = getValueFromQuery(fileName, colName, groupByField, dataset);
            if (valueMap != null && valueMap.size() > 0) {
                for (final String key : valueMap.keySet()) {
                    final Set<String> value = valueMap.getOrDefault(key, null);
                    if (value.size() > 0) {
                        final String name = value.iterator().next();
                        JsonObject jsObj;
                        jsObj = loadSingleSeriesWidgetData(name);
                        jsArr.add(jsObj);
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return jsArr;
    }

    private void createExtraInfo(final JsonObject src, final JsonObject trg, final String grpByField, final Dataset dataset) {
        final String colName = src.get("name").getAsString();
        final String colLabel = src.get("label").getAsString();
        final String fileName = src.get("fileName").getAsString();
        HashMap<String, Set<String>> valueMap = null;
        try {
            valueMap = getValueFromQuery(fileName, colName, grpByField, dataset);
            final Set<String> values = valueMap.getOrDefault(colName, null);
            if (values != null) {
                final String dVal = values.stream().findFirst().get();
                trg.addProperty(colLabel, dVal);
            }
        } catch (final AppException e) {
            e.printStackTrace();
        }
    }



    public JsonObject loadSingleSeriesWidgetData(final String compName) throws AppException, JSONException {
        final JSONObject finalResult = new JSONObject();
        JsonObject finalJson = new JsonObject();
        JSONObject data = new JSONObject();
        final JSONObject options = new JSONObject();
        final Dataset dataset = getDataset();
        dataset.begin(ReadWrite.READ);
        final String cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_WIDGETDEF_FILE);
        final JsonObject jsonCfg = new JsonParser().parse(cfgInfo).getAsJsonObject();
        final JsonObject widgetDefJson = jsonCfg.getAsJsonObject(compName);

        final String label = widgetDefJson.getAsJsonObject().get("label").getAsString();
        final JsonElement description = widgetDefJson.get("description");
        final String chartType = widgetDefJson.get("outputFormat").getAsString();
        final JsonElement xAxisLabel = widgetDefJson.get("xAxisLabel");
        final JsonElement yAxisLabel = widgetDefJson.get("yAxisLabel");

        finalResult.put("key", compName);
        if (chartType.equals(CCAppConstants.COMBINE_CHART)) {
            try {
                data = buildCombineChart(widgetDefJson);
                final JSONObject xAxis = new JSONObject();
                final JSONObject yAxis = new JSONObject();
                xAxis.put("title", xAxisLabel == null ? "" : xAxisLabel.getAsString());
                yAxis.put("title", yAxisLabel == null ? "" : yAxisLabel.getAsString());
                data.put("xAxis", xAxis);
                data.put("yAxis", yAxis);

                options.put("Title", label);
                options.put("GraphType", "");

                finalResult.put("name", label);
                finalResult.put("options", options);
                finalResult.put("data", data);

                finalJson = new JsonParser().parse(finalResult.toString()).getAsJsonObject();
                if (description != null) {
                    createExtraInfo(description.getAsJsonObject(), finalJson, "", dataset);
                }
                return finalJson;
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }
        final String groupByField = widgetDefJson.get("groupByField").getAsString();
        final JsonElement optionalGroupByField = widgetDefJson.get("optionalGroupByField");
        final String fileName = widgetDefJson.get("fileName").getAsString();
        final JsonArray cols = widgetDefJson.getAsJsonArray("columns");
        final HashMap<String, String> attrLabels = new HashMap<String, String>();
        for (final JsonElement col : cols) {
            final String attr = col.getAsJsonObject().get("name").getAsString();
            final String colLabel = col.getAsJsonObject().get("label").getAsString();
            attrLabels.put(attr, colLabel);
        }

        HashMap<String, HashMap<String, String>> valueMap = null;
        HashMap<String, HashMap<String, HashMap<String, String>>> multiValueMap = null;
        try {
            if (optionalGroupByField != null) {
                multiValueMap = getValueFromQueryByAttrs(fileName, attrLabels.keySet(), groupByField, optionalGroupByField.getAsString(), dataset);
                if (multiValueMap != null && multiValueMap.size() > 0) {
                    if (chartType.equals(CCAppConstants.LINE_CHART)) {
                        data = buildMultiGroupLineChart(multiValueMap, attrLabels);
                    }
                }
            } else {
                valueMap = getValueFromQueryByAttrs(fileName, attrLabels.keySet(), groupByField, dataset);
                if (valueMap != null && valueMap.size() > 0) {
                    if (chartType.equals(CCAppConstants.COLUMN_CHART)) {
                        data = buildColumnChart(valueMap);
                    } else if (chartType.equals(CCAppConstants.LINE_CHART)) {
                        data = buildLineChart(valueMap, attrLabels);
                    } else if (chartType.equals(CCAppConstants.PIE_CHART)) {
                        data = buildPieChart(valueMap);
                    } else if (chartType.equals(CCAppConstants.DONUT_CHART)) {
                        data = buildPieChart(valueMap);
                    } else if (chartType.equals(CCAppConstants.TIMELINE_CHART)) {
                        data = buildTimeLineData(valueMap, attrLabels);
                    }
                }
            }

            final JSONObject xAxis = new JSONObject();
            final JSONObject yAxis = new JSONObject();
            xAxis.put("title", xAxisLabel == null ? "" : xAxisLabel.getAsString());
            yAxis.put("title", yAxisLabel == null ? "" : yAxisLabel.getAsString());
            data.put("xAxis", xAxis);
            data.put("yAxis", yAxis);

            options.put("Title", label);
            options.put("GraphType", chartType);

            finalResult.put("name", label);
            finalResult.put("options", options);
            finalResult.put("data", data);

            finalJson = new JsonParser().parse(finalResult.toString()).getAsJsonObject();
            if (description != null) {
                createExtraInfo(description.getAsJsonObject(), finalJson, "", dataset);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            dataset.close();
        }
        return finalJson;
    }


    private JSONObject buildPieChart(final HashMap<String, HashMap<String, String>> valueMap) throws JSONException {
        final JSONObject data = new JSONObject();
        final JSONArray categories = new JSONArray();
        final JSONArray series = new JSONArray();
        final JSONArray subSeries = new JSONArray();
        data.put("categories", categories);
        data.put("series", series);
        final Iterator<String> it = valueMap.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            final HashMap<String, String> entries = valueMap.get(key);
            final JSONObject element = new JSONObject();
            element.put("name", key);
            final Iterator<String> keys = entries.keySet().iterator();
            element.put("y", Double.parseDouble(entries.get(keys.next())));
            subSeries.put(element);
        }
        final JSONObject obj = new JSONObject();
        obj.put("data", subSeries);
        series.put(obj);
        return data;
    }

    private JSONObject buildColumnChart(final HashMap<String, HashMap<String, String>> valueMap) throws JSONException {
        final JSONObject data = new JSONObject();
        final JSONArray categories = new JSONArray();
        final JSONArray series = new JSONArray();
        data.put("categories", categories);
        data.put("series", series);

        /*
         * final Set<String> baseKeys = valueMap.keySet(); final List<String>
         * sortedList = new ArrayList<String>(baseKeys);
         * Collections.sort(sortedList);
         */
        final Iterator<String> it = valueMap.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            final HashMap<String, String> entries = valueMap.get(key);
            final JSONObject element = new JSONObject();
            element.put("name", key);
            final JSONArray seriesData = new JSONArray();
            final Iterator<String> keys = entries.keySet().iterator();
            while (keys.hasNext()) {
                final String aKey = keys.next();
                seriesData.put(Double.parseDouble(entries.get(aKey)));
            }
            element.put("data", seriesData);
            series.put(element);
        }
        return data;
    }

    private JSONObject buildTimeLineData(final HashMap<String, HashMap<String, String>> valueMap, final HashMap<String, String> attrLabels) throws JSONException {
        final JSONObject data = new JSONObject();
        final JSONArray seriesData = new JSONArray();
        data.put("data", seriesData);
        final Iterator<String> it = valueMap.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            final HashMap<String, String> entries = valueMap.get(key);
            final JSONObject element = new JSONObject();
            final Iterator<String> keys = entries.keySet().iterator();
            while (keys.hasNext()) {
                final String aKey = keys.next();
                element.put(attrLabels.get(aKey), entries.get(aKey));
            }
            seriesData.put(element);
        }
        return data;
    }

    private JSONObject buildLineChart(final HashMap<String, HashMap<String, String>> valueMap, final HashMap<String, String> attrLabels) throws JSONException {
        final JSONObject data = new JSONObject();
        final JSONArray categories = new JSONArray();
        final JSONArray series = new JSONArray();
        data.put("categories", categories);
        data.put("series", series);

        final LinkedHashMap<String, LinkedHashMap<String, String>> temp = new LinkedHashMap<String, LinkedHashMap<String, String>>();
        Iterator<String> it = valueMap.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            categories.put(key);
            final HashMap<String, String> val = valueMap.get(key);
            final Iterator<String> it2 = val.keySet().iterator();
            LinkedHashMap<String, String> map = null;
            while (it2.hasNext()) {
                final String key2 = it2.next();
                map = temp.get(key2);
                if (map == null) {
                    map = new LinkedHashMap<String, String>();
                    temp.put(key2, map);
                }
                map.put(key, val.get(key2));
            }
        }

        it = temp.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            final HashMap<String, String> allVals = temp.get(key);
            final JSONObject obj = new JSONObject();
            final JSONArray arr = new JSONArray();
            key = attrLabels.get(key);
            obj.put("name", key);
            allVals.values().forEach(a -> {
                try {
                    arr.put(Double.parseDouble(a));
                } catch (final Exception e) {
                    System.out.println("Couldn't parse " + a + " to Double");
                    e.printStackTrace();
                }
            });
            obj.put("data", arr);
            series.put(obj);
        }

        return data;
    }

    private JSONObject buildMultiGroupLineChart(final HashMap<String, HashMap<String, HashMap<String, String>>> valueMap, final HashMap<String, String> attrLabels) throws JSONException {
        final JSONObject data = new JSONObject();
        final JSONArray categories = new JSONArray();
        final JSONArray series = new JSONArray();
        data.put("categories", categories);
        data.put("series", series);

        final HashMap<String, HashMap<String, HashMap<String, String>>> temp = new HashMap<String, HashMap<String, HashMap<String, String>>>();
        Iterator<String> it = valueMap.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            categories.put(key);
            final HashMap<String, HashMap<String, String>> val = valueMap.get(key);
            final Iterator<String> it2 = val.keySet().iterator();
            HashMap<String, HashMap<String, String>> map = null;
            while (it2.hasNext()) {
                final String key2 = it2.next();
                map = temp.get(key2);
                if (map == null) {
                    map = new HashMap<String, HashMap<String, String>>();
                    temp.put(key2, map);
                }
                map.put(key, val.get(key2));
            }
        }

        it = temp.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            final HashMap<String, HashMap<String, String>> allVals = temp.get(key);
            final JSONObject obj = new JSONObject();
            final JSONArray arr = new JSONArray();
            obj.put("name", key);
            allVals.values().forEach(a -> {
                try {
                    arr.put(Double.parseDouble(a.get(a.keySet().toArray()[0])));
                } catch (final Exception e) {
                    System.out.println("Couldn't parse " + a + " to Double");
                    e.printStackTrace();
                }
            });
            obj.put("data", arr);
            series.put(obj);
        }

        return data;
    }

    private JSONObject buildCombineChart(final JsonObject widgetDefJson) throws JSONException {
        final JSONObject chartData = new JSONObject();
        JSONArray categories = new JSONArray();
        final JSONArray series = new JSONArray();

        final String groupByField = widgetDefJson.get("groupByField").getAsString();
        final JsonArray cols = widgetDefJson.getAsJsonArray("columns");
        for (final JsonElement col : cols) {
            final String chartType = col.getAsJsonObject().get("outputFormat").getAsString();
            final String fileName = col.getAsJsonObject().get("fileName").getAsString();
            final JsonArray innerCols = col.getAsJsonObject().getAsJsonArray("columns");
            final HashMap<String, String> attrLabels = new HashMap<String, String>();
            for (final JsonElement innerCol : innerCols) {
                final String attr = innerCol.getAsJsonObject().get("name").getAsString();
                final String colLabel = innerCol.getAsJsonObject().get("label").getAsString();
                attrLabels.put(attr, colLabel);
            }
            Dataset dataset = null;
            HashMap<String, HashMap<String, String>> valueMap = null;
            JSONObject data = new JSONObject();
            try {
                dataset = getDataset();
                dataset.begin(ReadWrite.READ);
                valueMap = getValueFromQueryByAttrs(fileName, attrLabels.keySet(), groupByField, dataset);
                if (valueMap != null && valueMap.size() > 0) {

                    if (chartType.equals(CCAppConstants.PIE_CHART)) {
                        data = buildPieChart(valueMap);
                    } else {
                        data = buildLineChart(valueMap, attrLabels);
                    }
                    final JSONArray innerSeries = data.getJSONArray("series");
                    for (int i = 0; i < innerSeries.length(); i++) {
                        final JSONObject obj = innerSeries.getJSONObject(i);
                        obj.put("type", chartType);
                        series.put(obj);
                    }
                    if (categories.length() == 0) {
                        categories = data.getJSONArray("categories");
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                dataset.close();
            }
        }
        chartData.put("categories", categories);
        chartData.put("series", series);
        return chartData;
    }

    private Dataset getDataset() {
        final String directory = CCAppConstants.JENA_TDB_STOREROOM;
        return TDBFactory.createDataset(directory);
    }


    private HashMap<String, HashMap<String, String>> getValueFromQueryByAttrs(final String fileName, final Set<String> attrs, final String groupByField, final Dataset dataset) throws AppException {
        LinkedHashMap<String, HashMap<String, String>> retList = new LinkedHashMap<String, HashMap<String, String>>();
        final String sparqlQueryString = AppUtils.getFileContent("sparql/" + fileName);
        retList = getValueFromQueryStringByAttrs(sparqlQueryString, attrs, groupByField, dataset);
        return retList;
    }

    private LinkedHashMap<String, HashMap<String, String>> getValueFromQueryStringByAttrs(final String sparqlQueryString, final Set<String> attrs, final String groupByField, final Dataset dataset) throws AppException {
        final LinkedHashMap<String, HashMap<String, String>> retList = new LinkedHashMap<String, HashMap<String, String>>();
        final Query query = QueryFactory.create(sparqlQueryString);
        final QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        final ResultSet resultsData = qexec.execSelect();
        final JsonObject resultObj = new JsonParser().parse(sparkleResultToJSON(resultsData)).getAsJsonObject();
        final JsonArray bindings = resultObj.getAsJsonObject("results").getAsJsonArray("bindings");

        final List<HashMap<String, BaseBindObj>> bindSet = deserializeBindings(bindings);
        for (final HashMap<String, BaseBindObj> bindMap : bindSet) {
            for (final String attrName : attrs) {
                final BaseBindObj bindObj = bindMap.get(attrName);
                if (bindObj != null) {
                    if ((groupByField != null) && (groupByField.trim().length() > 0)) {
                        final String groupByFieldVal = bindMap.get(groupByField).getValue();
                        HashMap<String, String> valueList = null;
                        if (!retList.containsKey(groupByFieldVal)) {
                            valueList = new HashMap<String, String>();
                            retList.put(groupByFieldVal, valueList);
                        } else {
                            valueList = retList.get(groupByFieldVal);
                        }
                        valueList.put(attrName, bindObj.getValue());
                    } else {
                        HashMap<String, String> valueList = null;
                        if (!retList.containsKey(attrName)) {
                            valueList = new HashMap<String, String>();
                            retList.put(attrName, valueList);
                        } else {
                            valueList = retList.get(attrName);
                        }
                        valueList.put(attrName, bindObj.getValue());
                    }
                }
            }
        }
        return retList;
    }

    private HashMap<String, HashMap<String, HashMap<String, String>>> getValueFromQueryByAttrs(final String fileName, final Set<String> attrs, final String groupByField1, final String groupByField2, final Dataset dataset) throws AppException {
        final LinkedHashMap<String, HashMap<String, HashMap<String, String>>> retList = new LinkedHashMap<String, HashMap<String, HashMap<String, String>>>();
        final String sparqlQueryString = AppUtils.getFileContent("sparql/" + fileName);
        final Query query = QueryFactory.create(sparqlQueryString);
        final QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        final ResultSet resultsData = qexec.execSelect();
        final JsonObject resultObj = new JsonParser().parse(sparkleResultToJSON(resultsData)).getAsJsonObject();
        final JsonArray bindings = resultObj.getAsJsonObject("results").getAsJsonArray("bindings");

        final List<HashMap<String, BaseBindObj>> bindSet = deserializeBindings(bindings);
        for (final HashMap<String, BaseBindObj> bindMap : bindSet) {
            final BaseBindObj bindObj = bindMap.get(groupByField1);
            final String groupByField1Val = bindMap.get(groupByField1).getValue();
            HashMap<String, HashMap<String, String>> valueList = null;
            if (!retList.containsKey(groupByField1Val)) {
                valueList = new HashMap<String, HashMap<String, String>>();
                retList.put(groupByField1Val, valueList);
            } else {
                valueList = retList.get(groupByField1Val);
            }
            for (final String attrName : attrs) {
                final BaseBindObj targetObject = bindMap.get(attrName);
                if (targetObject != null) {
                    final String groupByField2Val = bindMap.get(groupByField2).getValue();
                    HashMap<String, String> inValueList = null;
                    if (!valueList.containsKey(groupByField2Val)) {
                        inValueList = new HashMap<String, String>();
                        valueList.put(groupByField2Val, inValueList);
                    } else {
                        inValueList = valueList.get(groupByField2Val);
                    }
                    inValueList.put(attrName, targetObject.getValue());
                }
            }
        }

        return retList;
    }



    private List<HashMap<String, BaseBindObj>> deserializeBindings(final JsonArray bindings) {
        List<HashMap<String, BaseBindObj>> bindSet = new ArrayList<HashMap<String, BaseBindObj>>();
        bindSet = new Gson().fromJson(bindings, new TypeToken<ArrayList<HashMap<String, BaseBindObj>>>() {
        }.getType());
        return bindSet;
    }

    public byte[] downloadFileByName(final String fileName) throws IOException {
        final Path srcFilePath = Paths.get(CCAppConstants.CONF_FILE_PATH, fileName);
        final byte[] data = Files.readAllBytes(srcFilePath);
        return data;
    }

    public JSONObject getLiquidityFilters(final String countryCode) {
        JSONObject filterDefJson = null;
        final String filterName = "filterDef";
        try {
            final Dataset dataset = getDataset();
            dataset.begin(ReadWrite.READ);
            final String cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_FILTERDEF_FILE);
            final JSONObject jsonCfg = new JSONObject(cfgInfo);
            filterDefJson = jsonCfg.getJSONObject(filterName);
            final Iterator<?> keys = filterDefJson.keys();
            while (keys.hasNext()) {
                final String key = keys.next().toString();
                final JSONObject obj = filterDefJson.getJSONObject(key);
                final String sourceType = obj.getString("sourceType");
                if (sourceType.equalsIgnoreCase("sparql")) {
                    final String fileIdentification = obj.getString("source");
                    final String groupByField = obj.getString("groupByField");
                    final JSONArray attrArr = obj.getJSONArray("columns");
                    final HashMap<String, String> attrLabels = new HashMap<String, String>();
                    for (int i = 0; i < attrArr.length(); i++) {
                        final JSONObject colObj = attrArr.getJSONObject(i);
                        final String attr = colObj.getString("name");
                        final String colLabel = colObj.getString("label");
                        attrLabels.put(attr, colLabel);
                    }

                    String sparqlQueryString = AppUtils.getFileContent("sparql/sparqlQuery" + fileIdentification + ".rq");
                    sparqlQueryString = sparqlQueryString.replace("$$COUNTRY_CODE$$", countryCode);
                    final HashMap<String, HashMap<String, String>> branches = getValueFromQueryStringByAttrs(sparqlQueryString, attrLabels.keySet(), groupByField, dataset);
                    final Iterator<?> branchKeys = branches.keySet().iterator();
                    final JSONArray jsArr = new JSONArray();
                    while (branchKeys.hasNext()) {
                        final String branchKey = branchKeys.next().toString();
                        final JSONObject jsObj = new JSONObject();
                        final HashMap<String, String> aBranch = branches.get(branchKey);
                        final Iterator<?> attrKeys = attrLabels.keySet().iterator();
                        while (attrKeys.hasNext()) {
                            final String attrKey = attrKeys.next().toString();
                            jsObj.put(attrLabels.get(attrKey), aBranch.get(attrKey));
                        }
                        jsArr.put(jsObj);
                    }
                    obj.put("source", jsArr);
                }
            }
            dataset.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return filterDefJson;
    }

    public JSONObject getLiquidityDataByFilters(final JSONArray filters) {
        StringBuilder sb = new StringBuilder();
        final JSONObject finalJson = new JSONObject();
        JSONObject filterDefJson = null;
        final String filterName = "liquidityBranch";
        try {
            final Dataset dataset = getDataset();
            dataset.begin(ReadWrite.READ);
            final String cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_FILTERDEF_FILE);
            final JSONObject jsonCfg = new JSONObject(cfgInfo);
            filterDefJson = jsonCfg.getJSONObject(filterName);
            final String outputFormat = filterDefJson.getString("outputFormat");
            finalJson.put("outputFormat", outputFormat);
            final String fileIdentification = filterDefJson.getString("source");
            final String groupByField = filterDefJson.getString("groupByField");
            final JSONArray attrArr = filterDefJson.getJSONArray("columns");
            final HashMap<String, String> attrLabels = new HashMap<String, String>();
            for (int i = 0; i < attrArr.length(); i++) {
                final JSONObject colObj = attrArr.getJSONObject(i);
                final String attr = colObj.getString("name");
                final String colLabel = colObj.getString("label");
                attrLabels.put(attr, colLabel);
            }

            for (int i = 0; i < filters.length(); i++) {
                final JSONObject aFilter = filters.getJSONObject(i);
                final String filterVariable = aFilter.getString("filterVariable");
                final String type = aFilter.getString("type");
                if (!aFilter.has("value")) {
                    continue;
                }
                switch (type) {
                    case "text": {
                        final String value = aFilter.getString("value");
                        sb.append("filter (?").append(filterVariable).append(">").append(value).append(") ");
                    }
                    break;
                    case "dropdown": {
                        final String value = aFilter.getString("value");
                        sb.append("filter (?").append(filterVariable).append("=").append("\"").append(value).append("\"").append(") ");
                    }
                    break;
                    case "multiselect": {
                        final JSONArray value = aFilter.getJSONArray("value");
                        String prefix = null;
                        if (aFilter.has("prefix")) {
                            prefix = aFilter.getString("prefix");
                        }
                        sb.append("filter (");
                        for (int j = 0; j < value.length(); j++) {
                            final String val = value.getString(j);
                            if (prefix != null) {
                                sb.append("?").append(filterVariable).append("=").append(prefix).append(val).append(" || ");
                            } else {
                                sb.append("?").append(filterVariable).append("=").append("\"").append(val).append("\"").append(" || ");
                            }
                        }
                        String tmp = sb.toString();
                        if (sb.lastIndexOf(" || ") != -1) {
                            tmp = sb.substring(0, sb.lastIndexOf(" || "));
                        }
                        sb = new StringBuilder(tmp);
                        sb.append(") ");
                    }
                    break;
                }
            }
            String sparqlQueryString = AppUtils.getFileContent("sparql/sparqlQuery" + fileIdentification + ".rq");
            sparqlQueryString = sparqlQueryString.replace("$$FILTERS$$", sb.toString());
            final HashMap<String, HashMap<String, String>> branches = getValueFromQueryStringByAttrs(sparqlQueryString, attrLabels.keySet(), groupByField, dataset);
            final Iterator<?> branchKeys = branches.keySet().iterator();
            final JSONArray jsArr = new JSONArray();
            finalJson.put("data", jsArr);
            while (branchKeys.hasNext()) {
                final String branchKey = branchKeys.next().toString();
                final JSONObject jsObj = new JSONObject();
                final HashMap<String, String> aBranch = branches.get(branchKey);
                final Iterator<?> attrKeys = attrLabels.keySet().iterator();
                while (attrKeys.hasNext()) {
                    final String attrKey = attrKeys.next().toString();
                    jsObj.put(attrLabels.get(attrKey), aBranch.get(attrKey));
                }
                jsArr.put(jsObj);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return finalJson;
    }

    public void getLiquidityHierarchyData() {
        final JSONObject filterDefJson = null;
        final String filterName = "liquidityHierarchyBranch";

    }


    public JSONObject getFilteredSparqlResultDataByCompName(final String compName, String filterStr){
        final JSONObject finalJson = new JSONObject();
        JSONObject filterDefJson = null;
        try {
            final Dataset dataset = getDataset();
            dataset.begin(ReadWrite.READ);
            final String cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_FILTERDEF_FILE);
            final JSONObject jsonCfg = new JSONObject(cfgInfo);
            filterDefJson = jsonCfg.getJSONObject(compName);
            final String outputFormat = filterDefJson.getString("outputFormat");
            finalJson.put("outputFormat", outputFormat);
            final String fileIdentification = filterDefJson.getString("source");
            final String groupByField = filterDefJson.getString("groupByField");
            final JSONArray attrArr = filterDefJson.getJSONArray("columns");
            final HashMap<String, String> attrLabels = new HashMap<String, String>();
            for (int i = 0; i < attrArr.length(); i++) {
                final JSONObject colObj = attrArr.getJSONObject(i);
                final String attr = colObj.getString("name");
                final String colLabel = colObj.getString("label");
                attrLabels.put(attr, colLabel);
            }
            String sparqlQueryString = AppUtils.getFileContent("sparql/sparqlQuery" + fileIdentification + ".rq");


            if(filterStr != null && filterStr.trim().length() != 0) {
                filterStr = "\"" + filterStr + "\"";
                sparqlQueryString = sparqlQueryString.replace("$$FILTERS$$", filterStr);
            }
            final HashMap<String, HashMap<String, String>> branches = getValueFromQueryStringByAttrs(sparqlQueryString, attrLabels.keySet(), null, dataset);

            final Iterator<?> branchKeys = branches.keySet().iterator();
            final JSONArray jsArr = new JSONArray();
            finalJson.put("data", jsArr);
            while (branchKeys.hasNext()) {
                final String branchKey = branchKeys.next().toString();
                final JSONObject jsObj = new JSONObject();
                final HashMap<String, String> aBranch = branches.get(branchKey);
                final Iterator<?> attrKeys = attrLabels.keySet().iterator();
                /*while (attrKeys.hasNext()) {*/
                    final String attrKey = attrKeys.next().toString();
                    jsObj.put(attrLabels.get(attrKey), aBranch.get(attrKey));
                /*}*/
                jsArr.put(jsObj);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return finalJson;
    }

    public JSONObject getFilteredDataByCompName(final String compName, String filterStr){
        final JSONObject finalJson = new JSONObject();
        JSONObject filterDefJson = null;
        try {
            final Dataset dataset = getDataset();
            dataset.begin(ReadWrite.READ);
            final String cfgInfo = AppUtils.getFileContent("json/" + CCAppConstants.JENA_FILTERDEF_FILE);
            final JSONObject jsonCfg = new JSONObject(cfgInfo);
            filterDefJson = jsonCfg.getJSONObject(compName);
            final String outputFormat = filterDefJson.getString("outputFormat");
            finalJson.put("outputFormat", outputFormat);
            final String fileIdentification = filterDefJson.getString("source");
            final String groupByField = filterDefJson.getString("groupByField");
            final JSONArray attrArr = filterDefJson.getJSONArray("columns");
            final HashMap<String, String> attrLabels = new HashMap<String, String>();
            for (int i = 0; i < attrArr.length(); i++) {
                final JSONObject colObj = attrArr.getJSONObject(i);
                final String attr = colObj.getString("name");
                final String colLabel = colObj.getString("label");
                attrLabels.put(attr, colLabel);
            }
            String sparqlQueryString = AppUtils.getFileContent("sparql/sparqlQuery" + fileIdentification + ".rq");

            System.out.println("sparqlQueryString = " + sparqlQueryString);

            if(filterStr != null && filterStr.trim().length() != 0) {
                filterStr = "\"" + filterStr + "\"";
                sparqlQueryString = sparqlQueryString.replace("$$FILTERS$$", filterStr);
            }
            final HashMap<String, HashMap<String, String>> branches = getValueFromQueryStringByAttrs(sparqlQueryString, attrLabels.keySet(), groupByField, dataset);

            final Iterator<?> branchKeys = branches.keySet().iterator();
            final JSONArray jsArr = new JSONArray();
            finalJson.put("data", jsArr);
            while (branchKeys.hasNext()) {
                final String branchKey = branchKeys.next().toString();
                final JSONObject jsObj = new JSONObject();
                final HashMap<String, String> aBranch = branches.get(branchKey);
                final Iterator<?> attrKeys = attrLabels.keySet().iterator();
                while (attrKeys.hasNext()) {
                    final String attrKey = attrKeys.next().toString();
                    jsObj.put(attrLabels.get(attrKey), aBranch.get(attrKey));
                }
                jsArr.put(jsObj);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return finalJson;
    }

    public String getRawBindingDataValues( final String fileName ) throws AppException {

        final String sparqlQueryString = AppUtils.getFileContent("sparql/" + fileName);
        System.out.println("sparqlQueryString = " + sparqlQueryString);
        Dataset dataset = getDataset();
        final Query query = QueryFactory.create(sparqlQueryString);
        System.out.println("query.toString() = " + query.toString());
        final QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        final ResultSet resultsData = qexec.execSelect();
        final JsonObject resultObj = new JsonParser().parse(sparkleResultToJSON(resultsData)).getAsJsonObject();
        final JsonArray variables = resultObj.getAsJsonObject("head").getAsJsonArray("vars");
        final JsonArray bindings = resultObj.getAsJsonObject("results").getAsJsonArray("bindings");

        JsonArray returnOblList = new JsonArray();
        bindings.forEach( aBinding -> {
            JsonObject aObject = new JsonObject();
            variables.forEach(a -> {
                aObject.addProperty( a.getAsString(),
                        aBinding.getAsJsonObject().get(a.getAsString()).getAsString());
                returnOblList.add(aObject);
            });
        });
        return returnOblList.toString();
    }


}