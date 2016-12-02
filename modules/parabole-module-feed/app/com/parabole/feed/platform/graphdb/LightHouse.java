package com.parabole.feed.platform.graphdb;

import com.google.common.collect.Iterables;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.orient.*;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import org.apache.commons.collections.CollectionUtils;


import java.io.IOException;
import java.util.*;

/**
 * This is a graph database for all the paragraph related operations.
 *
 * Created by Sagir on 24-08-2016.
 *
 */

public class LightHouse extends GraphDb {

    public static final boolean filterEdge = CollectionUtils.isNotEmpty(CCAppConstants.RDA_RELATIONSHIPS);

    public LightHouse() {
        final String graphDbUrl = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".lightHouse.graphdb.url");
        final String graphDbUser = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".lightHouse.graphdb.user");
        final String graphDbPassword = AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".lightHouse.graphdb.password");
        final Integer graphDbPoolMinSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".lightHouse.graphdb.pool.min");
        final Integer graphDbPoolMaxSize = AppUtils.getApplicationPropertyAsInteger(CCAppConstants.INDUSTRY + ".lightHouse.graphdb.pool.max");
        this.orientGraphFactory = new OrientGraphFactory(graphDbUrl, graphDbUser, graphDbPassword).setupPool(graphDbPoolMinSize, graphDbPoolMaxSize);
        //ODatabaseDocumentTx db = new ODatabaseDocumentTx("local:<path>/<db-name>").create();

    }

    /*******************************************************************
    // Everytime you need a graph instance
    /*******************************************************************

     OrientGraph graph = this.orientGraphFactory.getTx();
        try {

    } finally {
        graph.shutdown();
    }

     ******************************************************************/

    public boolean createLightHouse() throws IOException {

        //Vertex v = new OrientVertex();

        OrientGraph graph = this.orientGraphFactory.getTx();
        Vertex luca = graph.addVertex(null);
        luca.setProperty( "dataId", "Topic" );
        luca.setProperty( "name", "Topic" );
        luca.setProperty( "type", "Topic" );
        Vertex marko = graph.addVertex(null);
        marko.setProperty( "name", "subTopic" );
        luca.setProperty( "name", "subTopic" );
        luca.setProperty( "type", "subTopic" );
        String edgeName = "anEdge";
        Map<String, String> edgeProperty = new HashMap<String, String>();
        edgeProperty.put("color", "Red");
        return saveGraphInstance(graph, luca, marko, edgeName, edgeProperty);
    }

    public boolean createNewVertex_depricated( Map<String, String> dataToSave) throws IOException {
        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
            String id = dataToSave.get("elementID");
            System.out.println("id = " + id);
            Iterable<Vertex> particularNode = graph.getVertices("elementID", id);
            int size = Iterables.size(particularNode);
            System.out.println("size = " + size);
            if(size > 0){
                for (Vertex v : particularNode) {
                    Iterator dts = dataToSave.entrySet().iterator();
                    while (dts.hasNext()) {
                        Map.Entry oneDataToSave = (Map.Entry)dts.next();
                        v.setProperty(oneDataToSave.getKey().toString(), oneDataToSave.getValue());
                    }
                }
                System.out.println("Already exists = ");
            }else{
                Vertex v = graph.addVertex(null);
                Iterator dts = dataToSave.entrySet().iterator();
                while (dts.hasNext()) {
                    Map.Entry oneDataToSave = (Map.Entry)dts.next();
                    v.setProperty(oneDataToSave.getKey().toString(), oneDataToSave.getValue());
                }
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
            System.out.println("e = " + e);
        } finally {
            graph.shutdown();
        }
        return true;
    }

    public Boolean establishEdgeByVertexIDs_depricated(String vertexIDOne, String vertexIDTwo, String edgeName, String edgeType) {

        OrientGraph graph = this.orientGraphFactory.getTx();
        Iterable<Vertex> vs = graph.getVertices("elementID", vertexIDOne);
        Vertex one = null;
        Vertex two = null;
        Boolean res = false;
        try {
            for (Vertex v : graph.getVertices("elementID", vertexIDOne)) {
                one = v;
            }

            for (Vertex v : graph.getVertices("elementID", vertexIDTwo)) {
                two = v;
            }

            Map<String, String> edgeProperty = new HashMap<String, String>();
            edgeProperty.put("type", edgeType);
            edgeProperty.put("elementID", vertexIDOne + "_" + vertexIDTwo);
            if (two != null) {
                Iterable<Edge> result = graph.getEdges("elementID", vertexIDOne + "_" + vertexIDTwo);
                int size = Iterables.size(result);
                System.out.println("Edge size = " + size);
                if(size < 1){
                    res = saveGraphInstance(graph, one, two, edgeName, edgeProperty);
                    System.out.println("Edge does not exists = " + "null");
                } else{

                    for (Edge e : result) {
                        String id = e.getProperty("elementID");
                        System.out.println("id = " + id);
                        if (id.equalsIgnoreCase(vertexIDOne + "_" + vertexIDTwo)) {
                            System.out.println("Edge already exists = " + vertexIDOne + "_" + vertexIDTwo);
                        } else{
                            res = saveGraphInstance(graph, one, two, edgeName, edgeProperty);
                            System.out.println("Edge does not exists = " + vertexIDOne + "_" + vertexIDTwo);
                        }
                    }
                }
                /*for (Edge e : result) {
                    if (e.getVertex(Direction.OUT).getProperty("elementID").equals(one.getProperty("elementID"))) {
                        System.out.println("Edge already exists = ");
                    } else{
                        //res = saveGraphInstance(graph, one, two, edgeName, edgeProperty);
                        System.out.println("Edge does not exists = " + e.getVertex(Direction.OUT).getProperty("type") + e.getVertex(Direction.OUT).getProperty("name"));
                    }
                }*/
            }
        } catch(Exception e ) {
            graph.rollback();
            System.out.println("e = " + e);
        } finally {
            graph.shutdown();
        }
        return res;

    }

    public Boolean establishEdgeByVertexIDs(String vertexIDOne, String vertexIDTwo, String edgeName, String edgeType) {

        Graph g = getGraphConnectionNoTx();
        Iterable<Vertex> vs = g.getVertices("elementID", vertexIDOne);
        Vertex one = null;
        Vertex two = null;
        Boolean res = false;
        try {
            for (Vertex v : g.getVertices("elementID", vertexIDOne)) {
                one = v;
            }

            for (Vertex v : g.getVertices("elementID", vertexIDTwo)) {
                two = v;
            }
            Map<String, String> edgeProperty = new HashMap<String, String>();
            edgeProperty.put("type", edgeType);
            edgeProperty.put("elementID", vertexIDOne + "_" + vertexIDTwo);
            if (two != null) {
                Iterable<Edge> result = g.getEdges("elementID", vertexIDOne + "_" + vertexIDTwo);
                int size = Iterables.size(result);
                if (size < 1) {
                    res = saveGraphInstanceUsingTinkerpop(g, one, two, edgeName, edgeProperty);
                } else {
                    for (Edge e : result) {
                        String id = e.getProperty("elementID");
                        if (id.equalsIgnoreCase(vertexIDOne + "_" + vertexIDTwo)) {
                        } else {
                            res = saveGraphInstanceUsingTinkerpop(g, one, two, edgeName, edgeProperty);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;

    }

    public boolean deleteEdgeByVertexIDs(String vertexIDOne, String vertexIDTwo) {
        System.out.println(">>>>>>>>>>>>> vertexIDOne = " + vertexIDOne);
        System.out.println(">>>>>>>>>>>>> vertexIDTwo = " + vertexIDTwo);
        OrientGraph graph = this.orientGraphFactory.getTx();

            Vertex vOne = null;
            Vertex vTwo = null;

        try {
            for (Vertex v : graph.getVertices("elementID", vertexIDOne)) {
                vOne = v;
            }

            for (Vertex v : graph.getVertices("elementID", vertexIDTwo)) {
                vTwo = v;
            }

            if (vTwo != null) {
                Iterable<Edge> result = vOne.getEdges(Direction.OUT);
                System.out.println("I am here = " );
                for (Edge e : result) {
                    if (e.getVertex(Direction.IN).equals(vTwo)) {
                        System.out.println("I am here 2 = " + e.getVertex(Direction.IN).getProperty("type"));
                        graph.removeEdge(e);
                    }
                }
            } else {
                System.out.println("Not connected");
            }
            graph.commit();
        }catch(Exception e ) {
            graph.rollback();
            System.out.println("e = " + e);
        } finally {
            graph.shutdown();
        }
        return true;

    }

    public boolean saveListOfVertices(List<String> listOfvertices) throws IOException {
        OrientGraph graph = this.orientGraphFactory.getTx();

        try {
            listOfvertices.forEach((k)-> {

                Iterable<Vertex> abc = graph.getVertices("elementID", k);
                int size = Iterables.size(abc);
                System.out.println("size = " + size);

                if(size>0){
                    for (Vertex v : graph.getVertices("elementID", k)) {
                        System.out.println("already exists = " + v);
                        if (v.getProperty("elementID") == k && v.getProperty("type") == "Topic") {
                            System.out.println("already exists = " + v);
                        } /*else {
                            Vertex newV = graph.addVertex(null);
                            System.out.println("Created vertex: " + v.getId());
                            newV.setProperty("elementID", k);
                            newV.setProperty("type", "Topic");
                            newV.setProperty("name", k);
                            System.out.println("new Vertex added :: " + k);
                        }*/
                    }
                }else{
                    Vertex v = graph.addVertex(null);
                    System.out.println("Created vertex: " + v.getId());
                    v.setProperty("elementID", k);
                    v.setProperty("type", "Topic");
                    v.setProperty("name", k);
                    System.out.println("Vertex added :: " + k);
                }


             /*   if(graph.getVertices("elementId",k) == null) {
                    System.out.println(" = Already exists !");
                }else{
                    // graph.getVertices("elementID",k);

                    Vertex v = (Vertex) graph.createVertexType("Topic");
                    System.out.println("Created vertex: " + v.getId());
                    v.setProperty("elementID", k);
                    v.setProperty("type", "Topic");
                    System.out.println("k =================================================>>> " + k);
                }*/

            });
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
            System.out.println("e = " + e);
        } finally {
            graph.shutdown();
        }

        return true;

    }

    /*******************************************************************
     // Generic Method to save a graph component
     /*******************************************************************

     This method will dynamically collect two vertices and name of the
     edge and all the edge properties

     ******************************************************************/

    public boolean saveGraphInstance(OrientGraph graph, Vertex vertexOne, Vertex vertexTwo, String edgeName, Map<String, String> edgeProperties) throws  IOException{


        try {
            Edge edge = graph.addEdge(null, vertexOne, vertexTwo, edgeName);
            if(edgeProperties != null)
                edgeProperties.forEach((key, value)->{
                    edge.setProperty(key, value);
                });
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            //graph.shutdown();
        }

        return true;
    }

    public boolean saveGraphInstanceUsingTinkerpop(Graph graph, Vertex vertexOne, Vertex vertexTwo, String edgeName, Map<String, String> edgeProperties) throws  IOException{
            Edge edge = graph.addEdge(null, vertexOne, vertexTwo, edgeName);
            if(edgeProperties != null)
                edgeProperties.forEach((key, value)->{
                    edge.setProperty(key, value);
                });
        return true;
    }

    public ArrayList<HashMap<String, String>> getAlltopic() throws  IOException{

        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String, String>> listOfFinalData = new ArrayList<HashMap<String, String>>();

        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
            verticesData = graph.getVertices("type", "TOPIC");

            for (Vertex v : verticesData) {
                HashMap<String, String> finalData = new HashMap<>();
                finalData.put("elementID", v.getProperty("elementID"));
                finalData.put("name", v.getProperty("name"));
                finalData.put("type", v.getProperty("type"));
                listOfFinalData.add(finalData);
            }

        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }

        return listOfFinalData;
    }

    public ArrayList<HashMap<String, String>> getAllBaselTopic(String key, String fromFileName) throws  IOException{

        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String, String>> listOfFinalData = new ArrayList<HashMap<String, String>>();

        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
            verticesData = graph.getVertices("type", "BASELTOPIC");
            for (Vertex v : verticesData) {
                if(null != fromFileName || null != key) {
                    if (v.getProperty(key).equals(fromFileName)) {
                        HashMap<String, String> finalData1 = new HashMap<>();
                        for (String s : v.getPropertyKeys()) {
                            finalData1.put(s, v.getProperty(s));
                        }
                        listOfFinalData.add(finalData1);
                    }
                }else{
                    HashMap<String, String> finalData = new HashMap<>();
                    for (String pkey : v.getPropertyKeys()) {
                        finalData.put(pkey, v.getProperty(pkey));
                    }
                    listOfFinalData.add(finalData);
                }

            }

        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }

        return listOfFinalData;
    }

    public ArrayList<HashMap<String, String>> getAllvertex(String vertexType) throws  IOException{
        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String, String>> listOfFinalData = new ArrayList<HashMap<String, String>>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
            verticesData = graph.getVertices("type", vertexType);
            for (Vertex v : verticesData) {
                Set<String> propertyKeys = v.getPropertyKeys();
                HashMap<String, String> finalData = new HashMap<>();
                for (String propertyKey : propertyKeys) {
                    finalData.put(propertyKey, v.getProperty(propertyKey));
                }
                listOfFinalData.add(finalData);
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return listOfFinalData;
    }

    public HashMap<String, String> getVertexByVertexID(String vertexID) throws  IOException{
        Iterable<Vertex> verticesData = null;
        HashMap<String, String> finalData = new HashMap<>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
            verticesData = graph.getVertices("elementID", vertexID);
            for (Vertex v : verticesData) {
                Set<String> propertyKeys = v.getPropertyKeys();
                for (String propertyKey : propertyKeys) {
                    finalData.put(propertyKey, v.getProperty(propertyKey));
                }
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return finalData;
    }

    public HashMap<String, String> getVertexByVertexName(String vName) throws  IOException{
        Iterable<Vertex> verticesData = null;
        HashMap<String, String> finalData = new HashMap<>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
            verticesData = graph.getVertices("name", vName);
            for (Vertex v : verticesData) {
                Set<String> propertyKeys = v.getPropertyKeys();
                for (String propertyKey : propertyKeys) {
                    finalData.put(propertyKey, v.getProperty(propertyKey));
                }
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return finalData;
    }

    public ArrayList<HashMap<String, String>> getConnectedNodesByNodeIdAndType(String topicid, String filterType) throws  IOException{
        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String, String>> listOfFinalData = new ArrayList<HashMap<String, String>>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        System.out.println("topicid = " + topicid);
        verticesData = graph.getVertices("elementID", topicid);
        System.out.println("verticesData.toString() = " + verticesData.toString());
        try {
            for (Vertex v : verticesData) {
                final Set<Vertex> outputSet = new HashSet<Vertex>();
                System.out.println("v.getProperty(\"name\") = " + v.getProperty("name"));
                if (null != v) {
                    v.getEdges(Direction.OUT).forEach((final Edge edge) -> {
                            System.out.println((String) edge.getVertex(Direction.IN).getProperty("elementID"));
                        if(filterType != null){
                            if(edge.getVertex(Direction.IN).getProperty("type").toString().contains(filterType)){
                                HashMap<String, String> finalData = new HashMap<>();
                                finalData.put("elementID", edge.getVertex(Direction.IN).getProperty("elementID"));
                                finalData.put("name", edge.getVertex(Direction.IN).getProperty("name"));
                                finalData.put("type", edge.getVertex(Direction.IN).getProperty("type"));
                                listOfFinalData.add(finalData);
                            }
                        }else{
                            if(edge.getVertex(Direction.IN).getProperty("type").toString().contains("BASELSECTION") ||
                                    edge.getVertex(Direction.IN).getProperty("type").toString().contains("BASELPARAGRAPH") ||
                                        edge.getVertex(Direction.IN).getProperty("type").toString().contains("BASELSUBTOPIC")){


                                System.out.println("I am in all type of element field consideration !");
                                HashMap<String, String> anotherFinalData = new HashMap<>();
                                anotherFinalData.put("elementID", edge.getVertex(Direction.IN).getProperty("elementID"));
                                anotherFinalData.put("name", edge.getVertex(Direction.IN).getProperty("name"));
                                anotherFinalData.put("type", edge.getVertex(Direction.IN).getProperty("type"));
                                if(edge.getVertex(Direction.IN).getProperty("type").toString().contains("BASELPARAGRAPH")) {
                                    anotherFinalData.put("bodyText", edge.getVertex(Direction.IN).getProperty("bodyText"));
                                    anotherFinalData.put("fromFileName", edge.getVertex(Direction.IN).getProperty("fromFileName"));
                                    anotherFinalData.put("tag", edge.getVertex(Direction.IN).getProperty("tag"));
                                }
                                listOfFinalData.add(anotherFinalData);
                            }
                        }
                        //outputSet.add(edge.getVertex(Direction.IN));

                    });
                }
            }
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }

        return listOfFinalData;
    }

    public ArrayList<HashMap<String, String>> getConnectedNodesByNodeIdAndTypeUsingTinkerPop(String topicid, String filterType) throws  IOException{
        ArrayList<HashMap<String, String>> listOfHM = new ArrayList<>();
        ArrayList<Vertex> listOfRelatedVertices = getRelatedVerticesByProperty("elementID", topicid);
        for (Vertex relatedVertice : listOfRelatedVertices) {
            if(relatedVertice.getProperty("type").equals(filterType)) {
                HashMap<String, String> finalData = new HashMap<>();
                for (String s : relatedVertice.getPropertyKeys()) {
                    finalData.put(s, relatedVertice.getProperty(s));
                }
                listOfHM.add(finalData);
            }
        }
        return listOfHM;
    }

    public ArrayList<HashMap<String, String>> getChildVerticesByRootVertexId(String rootVertexID){
        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String, String>> listOfFinalData = new ArrayList<HashMap<String, String>>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        verticesData = graph.getVertices("elementID", rootVertexID);
        try {
            for (Vertex v : verticesData) {
                final Set<Vertex> outputSet = new HashSet<Vertex>();
                if (null != v) {
                    v.getEdges(Direction.OUT).forEach((final Edge edge) -> {
                            HashMap<String, String> finalData = new HashMap<>();
                            for (String s : edge.getVertex(Direction.IN).getPropertyKeys()) {
                                finalData.put(s, edge.getVertex(Direction.IN).getProperty(s));
                            }
                            listOfFinalData.add(finalData);
                    });
                }
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return listOfFinalData;
    }

    public ArrayList<HashMap<String, String>> getRootVerticesByChildVertexId(String rootVertexID){

        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String, String>> listOfFinalData = new ArrayList<HashMap<String, String>>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        verticesData = graph.getVertices("elementID", rootVertexID);
        try {
            for (Vertex v : verticesData) {
                final Set<Vertex> outputSet = new HashSet<Vertex>();
                if (null != v) {
                    v.getEdges(Direction.IN).forEach((final Edge edge) -> {
                            HashMap<String, String> finalData = new HashMap<>();
                            for (String s : edge.getVertex(Direction.OUT).getPropertyKeys()) {
                                finalData.put(s, edge.getVertex(Direction.OUT).getProperty(s));
                            }
                            listOfFinalData.add(finalData);
                    });
                }
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }

        return listOfFinalData;
    }

    public ArrayList<HashMap<String, String>> getParagraphBySectionId(String topicid) throws  IOException{

        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String, String>> listOfFinalData = new ArrayList<HashMap<String, String>>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        System.out.println("topicid = " + topicid);
        verticesData = graph.getVertices("elementID", topicid);
        System.out.println("verticesData.toString() = " + verticesData.toString());
        try {
            for (Vertex v : verticesData) {
                final Set<Vertex> outputSet = new HashSet<Vertex>();
                System.out.println("v.getProperty(\"name\") = " + v.getProperty("name"));
                if (null != v) {
                    v.getEdges(Direction.OUT).forEach((final Edge edge) -> {
                        if(edge.getVertex(Direction.IN).getProperty("type").toString().contains("PARAGRAPH")) {
                            HashMap<String, String> finalData = new HashMap<>();
                            //outputSet.add(edge.getVertex(Direction.IN));
                            finalData.put("elementID", edge.getVertex(Direction.IN).getProperty("elementID"));
                            finalData.put("name", edge.getVertex(Direction.IN).getProperty("name"));
                            finalData.put("type", edge.getVertex(Direction.IN).getProperty("type"));
                            finalData.put("fromFileName", edge.getVertex(Direction.IN).getProperty("fromFileName"));
                            finalData.put("startPage", edge.getVertex(Direction.IN).getProperty("startPage"));
                            finalData.put("endPage", edge.getVertex(Direction.IN).getProperty("endPage"));
                            finalData.put("firstLine", edge.getVertex(Direction.IN).getProperty("firstLine"));
                            finalData.put("bodyText", edge.getVertex(Direction.IN).getProperty("bodyText"));
                            finalData.put("tag", edge.getVertex(Direction.IN).getProperty("tag"));
                            finalData.put("willIgnore", edge.getVertex(Direction.IN).getProperty("willIgnore"));
                            listOfFinalData.add(finalData);
                        }
                    });
                }
            }
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }

        return listOfFinalData;
    }

    public String addAnewVertexproperty(String vertexID, HashMap<String, String> mapOfProperties) throws  IOException{
        OrientGraph graph = this.orientGraphFactory.getTx();
        Iterable<Vertex> verticesData = null;
        verticesData = graph.getVertices("elementID", vertexID);
        try {
            for (Vertex v : verticesData) {
                final Set<Vertex> outputSet = new HashSet<Vertex>();
                if (null != v) {
                    for (Map.Entry<String, String> entry : mapOfProperties.entrySet())
                    {
                        v.setProperty(entry.getKey(), entry.getValue());
                    }

                }
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return "{ status: saved } ";
    }

    // ------------------------------------------------------------------------------------------------
    // The following two methods will fetch all the document types against given list of paragraph IDs
    // ------------------------------------------------------------------------------------------------

    public HashMap<String, String> getComponentTypeFromParagraphsIDS(ArrayList<String> paragraphIDs){
        HashMap<String, String> componentTypes = new HashMap<>();
        try {
            for (String paragraphID : paragraphIDs) {
                getConceptsFromParagraphs(paragraphID, componentTypes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return componentTypes;
    }

    public boolean createNewVertex( Map<String, String> dataToSave) throws IOException {
        //GremlinPipeline pipe = new GremlinPipeline();
        Graph g = getGraphConnectionNoTx();
        Iterable<Vertex> abc = g.getVertices("elementID", dataToSave.get("elementID"));
        //Vertex v = new OrientVertex();
        int size = Iterables.size(abc);
        System.out.println("size = " + size);
        if(size > 0){
            for (Vertex vertex1 : abc) {
                Iterator dts = dataToSave.entrySet().iterator();
                while (dts.hasNext()) {
                    Map.Entry oneDataToSave = (Map.Entry)dts.next();
                    vertex1.setProperty(oneDataToSave.getKey().toString(), oneDataToSave.getValue());
                }
            }
            System.out.println("Already exists = ");
        }else{
            Vertex vertex2 = g.addVertex(null);
            Iterator dts = dataToSave.entrySet().iterator();
            while (dts.hasNext()) {
                Map.Entry oneDataToSave = (Map.Entry)dts.next();
                vertex2.setProperty(oneDataToSave.getKey().toString(), oneDataToSave.getValue());
            }
        }
        return true;
    }

    public ArrayList<Vertex> getRelatedVerticesByProperty(String KeyName, String keyValue){
        GremlinPipeline pipe = new GremlinPipeline();
        ArrayList<String> dataToReturn = new ArrayList<>();
        ArrayList<Vertex> listOfVertex = new ArrayList<>();

        Vertex temVert;
        Graph g = getGraphConnectionNoTx();
        Iterable vertices = g.getVertices(KeyName, keyValue);
        if(vertices.iterator().hasNext())
        {
            temVert = (Vertex) vertices.iterator().next();
            GremlinPipeline PathO = pipe.start(temVert).bothE().bothV();
            List<Vertex> pathList = PathO.toList();
            for(int i=0; i<pathList.size(); i++){
                listOfVertex.add(pathList.get(i));
                System.out.println("listOfVertex = " + listOfVertex);
            }
        }
        return listOfVertex;
    }

    public void getConceptsFromParagraphs(String paragraphID, HashMap<String, String> resultantComponentTypes) throws  IOException{

        Iterable<Vertex> verticesData = null;

        OrientGraph graph = this.orientGraphFactory.getTx();
        verticesData = graph.getVertices("elementID", paragraphID);
        try {
            for (Vertex v : verticesData) {
                if (null != v) {
                    v.getEdges(Direction.IN).forEach((final Edge edge) -> {
                        Iterable<Vertex> verticesDataTwo = null;
                        //System.out.println("edge.getVertex(Direction.IN).getProperty(\"type\") = " + edge.getVertex(Direction.OUT).getProperty("type"));
                        if (edge.getVertex(Direction.OUT).getProperty("type").equals("CONCEPT")) {
                            verticesDataTwo = graph.getVertices("elementID", edge.getVertex(Direction.OUT).getProperty("elementID"));
                            for (Vertex v2 : verticesDataTwo) {
                                if (null != v2) {
                                    v2.getEdges(Direction.OUT).forEach((final Edge edgeTwo) -> {
                                        if (edgeTwo.getVertex(Direction.IN).getProperty("type").equals("COMPONENTTYPE"))
                                            resultantComponentTypes.put(edgeTwo.getVertex(Direction.IN).getProperty("elementID"), edgeTwo.getVertex(Direction.IN).getProperty("name"));
                                    });
                                }
                            }
                        }
                    });
                }
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public void deleteAVertexByID(String checkListId){
        OrientGraph graph = this.orientGraphFactory.getTx();
        Iterable<Vertex> verticesData = null;
        verticesData = graph.getVertices("elementID", checkListId);
        try {
            if(verticesData != null)
                for (Vertex v : verticesData) {
                if(v!=null)
                    v.remove();
                }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public HashMap<String,String> getParagraphCountGroupByTag(){

        HashMap<String, String> paragraphCountGroupByTag = new HashMap<>();

        OObjectDatabaseTx db = new OObjectDatabaseTx(AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".lightHouse.graphdb.url")).open(
                AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".lightHouse.graphdb.user"),
                AppUtils.getApplicationProperty(CCAppConstants.INDUSTRY + ".lightHouse.graphdb.password")
        );
        
        List<ODocument> results = db.query(new OSQLSynchQuery<ODocument>("SELECT tag, COUNT(*) FROM V where type = 'PARAGRAPH' group by tag"));
        for (ODocument aDoc : results) {
            String tag = (aDoc.field("tag") != null) ? aDoc.field("tag").toString() : "Untagged";
            paragraphCountGroupByTag.put(tag, aDoc.field("COUNT").toString());
            System.out.println(aDoc.field("COUNT").toString());
        }

        return paragraphCountGroupByTag;

    }

    public ArrayList<HashMap<String,String>> getParagraphByNameProperty(String name){

        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String,String>> finalData = new ArrayList<>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        try {

                verticesData = graph.getVertices("name", name);
                for (Vertex v : verticesData) {
                    if(v.getProperty("type").toString().contains("BASELPARAGRAPH")) {
                        HashMap<String, String> tempData = new HashMap<>();
                        Set<String> propertyKeys = v.getPropertyKeys();
                        for (String propertyKey : propertyKeys) {
                            tempData.put(propertyKey, v.getProperty(propertyKey));
                        }
                        finalData.add(tempData);
                    }
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return finalData;
    }

    public ArrayList<HashMap<String,String>> getParagraphsByParagraphType(String paragraphType){

        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String,String>> finalData = new ArrayList<>();
        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
                verticesData = graph.getVertices("type", paragraphType);
                for (Vertex v : verticesData) {
                        HashMap<String, String> tempData = new HashMap<>();
                        Set<String> propertyKeys = v.getPropertyKeys();
                        for (String propertyKey : propertyKeys) {
                            tempData.put(propertyKey, v.getProperty(propertyKey));
                        }
                        finalData.add(tempData);
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return finalData;
    }

    public ArrayList<HashMap<String,String>> getParagraphsByParagraphIds(ArrayList<String> listOfParagraphIDs){
        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String,String>> finalData = new ArrayList<>();
        OrientGraph graph = this.orientGraphFactory.getTx();
            try {
                for (String paragraphID : listOfParagraphIDs) {
                    HashMap<String, String> tempData = new HashMap<>();
                    verticesData = graph.getVertices("elementID", paragraphID);
                    for (Vertex v : verticesData) {
                        Set<String> propertyKeys = v.getPropertyKeys();
                        for (String propertyKey : propertyKeys) {
                            tempData.put(propertyKey, v.getProperty(propertyKey));
                        }
                    }
                    finalData.add(tempData);
                }
                graph.commit();
            }catch( Exception e ) {
                graph.rollback();
            } finally {
                graph.shutdown();
            }
        return finalData;
    }

    public ArrayList<HashMap<String, String>> getVertexByProperty(String propertyName, String propertyType) throws  IOException{
        Iterable<Vertex> verticesData = null;
        ArrayList<HashMap<String, String>> allFinalData = new ArrayList<>();

        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
            verticesData = graph.getVertices(propertyName, propertyType);
            for (Vertex v : verticesData) {
                HashMap<String, String> finalData = new HashMap<>();
                Set<String> propertyKeys = v.getPropertyKeys();
                for (String propertyKey : propertyKeys) {
                    finalData.put(propertyKey, v.getProperty(propertyKey));
                }
                allFinalData.add(finalData);
            }
            graph.commit();
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }
        return allFinalData;
    }
}
