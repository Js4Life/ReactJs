package com.parabole.feed.platform.graphdb;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.AppConstants;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
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




}
