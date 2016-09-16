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
package com.parabole.feed.application.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.AppConstants;
import com.parabole.feed.platform.assimilation.*;
import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.graphdb.Biota;
import com.parabole.feed.platform.graphdb.GraphDbLinkDefinition;
import com.parabole.feed.platform.graphdb.LightHouse;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;

import java.io.IOException;
import java.util.*;

/**
 * LightHouse Service
 *
 * @author Sagir
 * @since v1.0
 */
@Singleton
public class LightHouseService {

    @Inject
    private LightHouse lightHouse;

    @Inject
    private StarfishServices starfishServices;

   // public String save

    public String createNewTopic(){

        Map<String, String> nodeDetailsToSave = new HashMap<String, String>();
        nodeDetailsToSave.put("elementID", "5001");
        nodeDetailsToSave.put("name", "TopicOne");
        nodeDetailsToSave.put("type", "topic");
        try {
            lightHouse.createNewVertex(nodeDetailsToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    public String createNewSubtopic(){

        Map<String, String> nodeDetailsToSave = new HashMap<String, String>();
        nodeDetailsToSave.put("elementID", "6001");
        nodeDetailsToSave.put("name", "STOne");
        nodeDetailsToSave.put("type", "subTopic");
        try {
            lightHouse.createNewVertex(nodeDetailsToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    public String createNewSection(){
        Map<String, String> nodeDetailsToSave = new HashMap<String, String>();
        nodeDetailsToSave.put("elementID", "7001");
        nodeDetailsToSave.put("name", "Section");
        nodeDetailsToSave.put("type", "section");
        try {
            lightHouse.createNewVertex(nodeDetailsToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }


    public String createRelationBetweenTwoNodes(){
        try {
            lightHouse.establishEdgeByVertexIDs("5001", "6001", "topicSubTopic", "topicSubTopic");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    public ArrayList<HashMap<String, String>> getAlltopic(){

        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getAlltopic();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<HashMap<String, String>> getAllConcepts(){

        String vertexType = "CONCEPT";

        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getAllvertex(vertexType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<HashMap<String, String>> getAllVertexesByType(String vertexType){

        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getAllvertex(vertexType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<HashMap<String, String>> getSubtopicsByTopicId(String topicId){

        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getSubtopicsByTopicId(topicId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<HashMap<String, String>> getParagraphBySectionId(String nodeId){
        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getParagraphBySectionId(nodeId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String addAnewVertexproperty(String vertexID, HashMap<String, String> mapOfProperties){
        String result=null;
        try {
            result = lightHouse.addAnewVertexproperty(vertexID, mapOfProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getVertexProperties(String vertexID, ArrayList<String> listOfKeys){
        String result=null;
        return result;
    }

    public HashMap<String, String> getComponentTypesByParagraphIds(ArrayList<String> listOfParagraphIDs) {

        HashMap<String, String> componentTypes = lightHouse.getComponentTypeFromParagraphsIDS(listOfParagraphIDs);

        return componentTypes;
    }

    public ArrayList<HashMap<String, String>> getChecklistByConcept(String conceptID) {

        ArrayList<String> listOfOfChecklist = new ArrayList<>();
        ArrayList<HashMap<String, String>> componentTypes = lightHouse.getChildVerticesByRootVertexId(conceptID);
        for (HashMap<String, String> componentType : componentTypes) {
            if(componentType.get("type").equals("COMPONENTTYPE"));
                listOfOfChecklist.add(componentType.get("elementID"));
                System.out.println("componentType.get(\"elementID\") = " + componentType.get("type"));
        }

        //getListOfChildComponentTypeVerticesByRootVertices(getListOfChildComponentVerticesByRootVertices(listOfOfChecklist));
        return getChecklistsByComponentTypes(listOfOfChecklist);

    }

    public ArrayList<HashMap<String, String>> getChecklistsByComponentTypes(ArrayList<String> conceptIDs) {

        ArrayList<String> listOfOfChecklist = new ArrayList<>();
        for (String conceptID : conceptIDs) {
            ArrayList<HashMap<String, String>> checklistIDs = lightHouse.getChildVerticesByRootVertexId(conceptID);
            for (HashMap<String, String> checklistID : checklistIDs) {
                listOfOfChecklist.add(checklistID.get("elementID"));
            }
        }
        ArrayList<HashMap<String, String>> finalResult = starfishServices.getChecklistByID(listOfOfChecklist);

        return finalResult;

    }

    public ArrayList<HashMap<String, String>> getChecklistByComponent(ArrayList<String> ids) {

        ArrayList<String> listOFComponentTypes = new ArrayList<>();
    //    ArrayList<String> listOfOfChecklist = new ArrayList<>();
        for (String id : ids) {
            ArrayList<HashMap<String, String>> checklistIDs = lightHouse.getRootVerticesByChildVertexId(id);
            for (HashMap<String, String> checklistID : checklistIDs) {
                System.out.println(checklistID.get("elementID"));
                listOFComponentTypes.add(checklistID.get("elementID"));
            }
        }

        ArrayList<HashMap<String, String>> finalResult = getChecklistsByComponentTypes(listOFComponentTypes);

        return finalResult;

    }

    ArrayList<String> getListOfChildComponentVerticesByRootVertices(ArrayList<String> listOfOfRootVertices){

        ArrayList<String> listOfChildVertices = new ArrayList<>();

        for (String rootVertex : listOfOfRootVertices) {
            ArrayList<HashMap<String, String>> componentTypes = lightHouse.getChildVerticesByRootVertexId(rootVertex);
            for (HashMap<String, String> componentType : componentTypes) {
                System.out.println("componentType.get(\"type\") = " + componentType.get("type"));
                if(componentType.get("type").equals("COMPONENT"))
                    listOfChildVertices.add(componentType.get("elementID"));
            }
        }

        return listOfChildVertices;
    }

    ArrayList<String> getListOfChildComponentTypeVerticesByRootVertices(ArrayList<String> listOfOfRootVertices){

        ArrayList<String> listOfChildVertices = new ArrayList<>();

        for (String rootVertex : listOfOfRootVertices) {
            ArrayList<HashMap<String, String>> componentTypes = lightHouse.getRootVerticesByChildVertexId(rootVertex);
            for (HashMap<String, String> componentType : componentTypes) {
                System.out.println("componentType.get(\"type\") = " + componentType.get("type"));
                if(componentType.get("type").equals("COMPONENTTYPE"))
                    listOfChildVertices.add(componentType.get("elementID"));
            }
        }

        return listOfChildVertices;
    }

    public ArrayList<HashMap<String, String>> getChecklistsByParagraphs(ArrayList<String> paragraphIDs) {
        ArrayList<String> listOfOfChecklist = new ArrayList<>();
        for (String paragraphID : paragraphIDs) {
            ArrayList<HashMap<String, String>> checklistIDs = lightHouse.getChildVerticesByRootVertexId(paragraphID);
            for (HashMap<String, String> checklistID : checklistIDs) {
                listOfOfChecklist.add(checklistID.get("elementID"));
            }
        }
        ArrayList<HashMap<String, String>> finalResult = starfishServices.getChecklistByID(listOfOfChecklist);

        return finalResult;
    }
}
