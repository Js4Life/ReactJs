package com.parabole.feed.platform.graphdb;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.AppConstants;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;


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

    public boolean createNewVertex( Map<String, String> dataToSave) throws IOException {
        OrientGraph graph = this.orientGraphFactory.getTx();
        try {
            String id = dataToSave.get("elementID");
            System.out.println("id = " + id);
            Iterable<Vertex> particularNode = graph.getVertices("elementID", id);
            int size = Iterables.size(particularNode);
            System.out.println("size = " + size);
            if(size > 0){
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


    public boolean establishEdgeByVertexIDs(String vertexIDOne, String vertexIDTwo, String edgeName, String edgeType) throws IOException {

        OrientGraph graph = this.orientGraphFactory.getTx();
        Iterable<Vertex> vs = graph.getVertices("elementID", vertexIDOne);
        Vertex one = null;
        Vertex two = null;

        for (Vertex v : graph.getVertices("elementID", vertexIDOne)) {
            one = v;
        }

        for (Vertex v : graph.getVertices("elementID", vertexIDTwo)) {
            two = v;
        }

        Map<String, String> edgeProperty = new HashMap<String, String>();
        edgeProperty.put("type", edgeType);
        return saveGraphInstance(graph, one, two, edgeName, edgeProperty);

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
            graph.shutdown();
        }

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
                listOfFinalData.add(finalData);
            }

        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }

        return listOfFinalData;
    }



    public String getSubtopicsByTopicId(String topicid) throws  IOException{

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
                            HashMap<String, String> finalData = new HashMap<>();
                            //outputSet.add(edge.getVertex(Direction.IN));
                            finalData.put("elementID", edge.getVertex(Direction.IN).getProperty("elementID"));
                            finalData.put("name", edge.getVertex(Direction.IN).getProperty("name"));
                            listOfFinalData.add(finalData);
                    });
                }
            }
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }

        return listOfFinalData.toString();
    }


    public String getParagraphBySectionId(String topicid) throws  IOException{

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
                            HashMap<String, String> finalData = new HashMap<>();
                            //outputSet.add(edge.getVertex(Direction.IN));
                            finalData.put("elementID", edge.getVertex(Direction.IN).getProperty("elementID"));
                            finalData.put("name", edge.getVertex(Direction.IN).getProperty("name"));
                            finalData.put("bodyText", edge.getVertex(Direction.IN).getProperty("bodyText"));
                            listOfFinalData.add(finalData);
                    });
                }
            }
        }catch( Exception e ) {
            graph.rollback();
        } finally {
            graph.shutdown();
        }

        return listOfFinalData.toString();
    }


}
