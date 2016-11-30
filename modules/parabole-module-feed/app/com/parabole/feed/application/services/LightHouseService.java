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

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.platform.graphdb.LightHouse;
import com.parabole.feed.platform.utils.AppUtils;
import com.tinkerpop.blueprints.Vertex;
import org.json.JSONArray;
import org.json.JSONObject;

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

    @Inject
    private JenaTdbService jenaTdbService;

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

    public ArrayList<HashMap<String, String>> getAllBaselTopic(String filterValue){

        String filterName = "fromFileName";
        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getAllBaselTopic(filterName, filterValue);
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

    public ArrayList<HashMap<String, String>> getAllBusinessSegments(){

        String vertexType = "BUSINESSSEGMENT";

        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getAllvertex(vertexType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<HashMap<String, String>> getAllProducts(){

        String vertexType = "PRODUCT";

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

    public ArrayList<HashMap<String, String>> getSubtopicsByTopicId(String topicId, String filterType){
        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getConnectedNodesByNodeIdAndType(topicId, filterType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<HashMap<String, String>> getBaselSubtopicsByTopicId(String topicId, String filterType){
        ArrayList<HashMap<String, String>> result=null;
        try {
            result = lightHouse.getConnectedNodesByNodeIdAndType(topicId, filterType);
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

    public HashMap<String, String> getProductByBusinessSegmentIds(ArrayList<String> listOfBusinessSegmentIDs) {

        //HashMap<String, String> componentTypes = lightHouse.getProductByBusinessSegmentIds(listOfBusinessSegmentIDs);
        // TODO
        return null;
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

    public ArrayList<HashMap<String, String>> getChecklistBySection(ArrayList<String> sectionIDs) {
        ArrayList<String> listOfOfChecklist = new ArrayList<>();
        for (String sectionID : sectionIDs) {
            ArrayList<HashMap<String, String>> componentTypes = lightHouse.getChildVerticesByRootVertexId(sectionID);
            for (HashMap<String, String> componentType : componentTypes) {
                if(componentType.get("type").equals("PARAGRAPH"));
                listOfOfChecklist.add(componentType.get("elementID"));
            }
        }
        return getChecklistsByParagraphIDs(listOfOfChecklist);
    }

    public ArrayList<HashMap<String, String>> getChecklistBySubTopic(ArrayList<String> subTopicIDs) {
        ArrayList<String> listOfSections = new ArrayList<>();
        for (String subTopicID : subTopicIDs) {
            ArrayList<HashMap<String, String>> sections = lightHouse.getChildVerticesByRootVertexId(subTopicID);
            for (HashMap<String, String> section : sections) {
                if(section.get("type").equals("SECTION"));
                listOfSections.add(section.get("elementID"));
            }
        }
        return getChecklistBySection(listOfSections);
    }

    public ArrayList<HashMap<String, String>> getChecklistByTopic(ArrayList<String> topicIDs) {
        ArrayList<String> listOfSubTopic = new ArrayList<>();
        for (String topicID : topicIDs) {
            ArrayList<HashMap<String, String>> subTopics = lightHouse.getChildVerticesByRootVertexId(topicID);
            for (HashMap<String, String> subTopic : subTopics) {
                if(subTopic.get("type").equals("SUBTOPIC"));
                listOfSubTopic.add(subTopic.get("elementID"));
            }
        }
        return getChecklistBySubTopic(listOfSubTopic);
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

    public ArrayList<HashMap<String, String>> getChecklistsByParagraphIDs(ArrayList<String> paragraphIDs) {
        ArrayList<String> listOfOfChecklist = new ArrayList<>();
        for (String paragraphID : paragraphIDs) {
            ArrayList<HashMap<String, String>> checklistIDs = lightHouse.getChildVerticesByRootVertexId(paragraphID);
            for (HashMap<String, String> checklistID : checklistIDs) {
                if(checklistID.get("type").equalsIgnoreCase("CHECKLIST"))
                listOfOfChecklist.add(checklistID.get("elementID"));
            }
        }
        ArrayList<HashMap<String, String>> finalResult = starfishServices.getChecklistByID(listOfOfChecklist);
        return finalResult;
    }

    public ArrayList<HashMap<String, String>> getChecklistsByRootNodeIDs(ArrayList<String> rootNodeIDs) {
        ArrayList<String> listOfOfChecklist = new ArrayList<>();
        for (String rootNodeID : rootNodeIDs) {
            ArrayList<HashMap<String, String>> checklistIDs = lightHouse.getChildVerticesByRootVertexId(rootNodeID);
            for (HashMap<String, String> checklistID : checklistIDs) {
                if(checklistID.get("type").equalsIgnoreCase("CHECKLIST"))
                    listOfOfChecklist.add(checklistID.get("elementID"));
            }
        }
        ArrayList<HashMap<String, String>> finalResult = starfishServices.getChecklistByID(listOfOfChecklist);
        return finalResult;
    }

    public ArrayList<HashMap<String, String>> getChecklistByComponent(String ids) {
        ArrayList<String> listOfOfChecklist = new ArrayList<>();
        ArrayList<HashMap<String, String>> componentTypes = lightHouse.getChildVerticesByRootVertexId(ids);
        for (HashMap<String, String> componentType : componentTypes) {
            if(componentType.get("type").equals("CHECKLIST"));
            listOfOfChecklist.add(componentType.get("elementID"));
            System.out.println("componentType.get(\"elementID\") = " + componentType.get("type"));
        }
        ArrayList<HashMap<String, String>> finalResult = starfishServices.getChecklistByID(listOfOfChecklist);
        return finalResult;
    }

    public ArrayList<HashMap<String, String>> getChecklistByBusinessSegment(ArrayList<String> ids) {
        ArrayList<String> listOFComponentTypes = new ArrayList<>();
        ArrayList<HashMap<String, String>> finalResult = new ArrayList<>();
        for (String id : ids) {
            ArrayList<HashMap<String, String>> checklistIDs = lightHouse.getRootVerticesByChildVertexId(id);
            for (HashMap<String, String> checklistID : checklistIDs) {
                listOFComponentTypes.add(checklistID.get("elementID"));
            }
        }
        ArrayList<String> listOfOfChecklist = new ArrayList<>();
        for (String listOFComponentType : listOFComponentTypes) {
            ArrayList<HashMap<String, String>> components = lightHouse.getChildVerticesByRootVertexId(listOFComponentType);

            for (HashMap<String, String> componentType : components) {
                if (componentType.get("type").equals("CHECKLIST"))
                listOfOfChecklist.add(componentType.get("elementID"));
                System.out.println("componentType.get(\"elementID\") = " + componentType.get("type"));
            }
        }

        ArrayList<HashMap<String, String>> f_Result = starfishServices.getChecklistByID(listOfOfChecklist);
        return f_Result;
    }

    public ArrayList<HashMap<String, String>> getChecklistByProducts(ArrayList<String> ids) {
        ArrayList<String> listOFBusinessSegments = new ArrayList<>();
        for (String id : ids) {
            ArrayList<HashMap<String, String>> checklistIDs = lightHouse.getRootVerticesByChildVertexId(id);
            for (HashMap<String, String> checklistID : checklistIDs) {
                if(checklistID.get("type").equalsIgnoreCase("BUSINESSSEGMENT"))
                    listOFBusinessSegments.add(checklistID.get("elementID"));
            }
        }
        ArrayList<HashMap<String, String>> finalResult = getChecklistByBusinessSegment(listOFBusinessSegments);
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

    public ArrayList<HashMap<String, HashMap<String, String>>> getChecklistsByParagraphs(ArrayList<String> paragraphIDs) {

        ArrayList<HashMap<String, HashMap<String, String>>> finalReturn = new ArrayList<>();

        for (String paragraphID : paragraphIDs) {
            ArrayList<String> listOfOfChecklist = new ArrayList<>();
            HashMap<String, HashMap<String, HashMap<String, String>>> checklistTags = new HashMap<>();

            ArrayList<HashMap<String, String>> checklistIDs = lightHouse.getChildVerticesByRootVertexId(paragraphID);
            for (HashMap<String, String> checklistID : checklistIDs) {
                HashMap<String, HashMap<String, String>> tempTags = new HashMap<>();
                HashMap<String, String> relatedParagraphIDs = new HashMap<>();
                HashMap<String, String> relatedComponentTypeIDs = new HashMap<>();
                listOfOfChecklist.add(checklistID.get("elementID"));
                for (HashMap<String, String> paraID : lightHouse.getRootVerticesByChildVertexId(checklistID.get("elementID"))) {
                    if(paraID.get("type").equals("PARAGRAPH")) {
                        relatedParagraphIDs.put(paraID.get("elementID"), String.valueOf(true));
                    }
                }
                for (HashMap<String, String> paraID : lightHouse.getRootVerticesByChildVertexId(checklistID.get("elementID"))) {
                    if(paraID.get("type").equals("COMPONENTTYPE")) {
                        relatedComponentTypeIDs.put(paraID.get("elementID"), String.valueOf(true));
                    }
                }
                tempTags.put("PARAGRAPH", relatedParagraphIDs);
                tempTags.put("COMPONENTTYPE", relatedComponentTypeIDs);
                checklistTags.put(checklistID.get("elementID"), tempTags);
            }

            ArrayList<HashMap<String, String>> tempResult = starfishServices.getChecklistByID(listOfOfChecklist);
            for (HashMap<String, String> checklists : tempResult) {
                HashMap<String, HashMap<String, String>> chkList = new HashMap<>();
                String checklistID = checklists.get("DATA_ID");
                chkList.put("checklist", checklists);
                chkList.put("paragraphs", checklistTags.get(checklistID).get("PARAGRAPH"));
                chkList.put("componentTypes", checklistTags.get(checklistID).get("COMPONENTTYPE"));
                finalReturn.add(chkList);
            }

        }


        return finalReturn;
    }

    public ArrayList<HashMap<String,String>> getRelatedParagraphsByNames(ArrayList<String> listOfParagraphIDs) {
        ArrayList<HashMap<String,String>> paragraphByNameProperty = new ArrayList<>();
        ArrayList<HashMap<String, String>> alltheParagraphs = lightHouse.getParagraphsByParagraphIds(listOfParagraphIDs);
        for (HashMap<String, String> paragraph : alltheParagraphs) {
            String nameP = paragraph.get("name");
            ArrayList<HashMap<String, String>> paragraphOne = lightHouse.getParagraphByNameProperty(nameP);
            paragraphByNameProperty.addAll(paragraphOne);
        }
        return paragraphByNameProperty;
    }


    public ArrayList<HashMap<String,String>> getRelatedParagraphsByMaxConceptsMatch(String paragraphID, String paragraphFile) {

        Integer paragraphCountsThresHold = Integer.valueOf(AppUtils.getApplicationProperty("paragraphCountsThresHold"));
        Integer minConceptMatchingThreshold = Integer.valueOf(AppUtils.getApplicationProperty("minConceptMatchingThreshold"));
        HashMap<String, Integer> sortableParagraphExistanceCounts = new HashMap<>();
        ArrayList<String> relatedConcepts = new ArrayList<>();
        ArrayList<String> directlyRelatedConcepts = getRelatedConceptsByParagraphID(paragraphID);
        ArrayList<String> newConceptNames = new ArrayList<>();
        //getRelated()
        for (String directlyRelatedConcept : directlyRelatedConcepts) {
            JSONObject ontoDataRelatedConcepts = jenaTdbService.getFilteredDataByCompName("relatedConcepts", directlyRelatedConcept);
            JSONArray arrayOfConcepts = ontoDataRelatedConcepts.getJSONArray("data");
            for (int i = 0; i < arrayOfConcepts.length(); i++) {
                JSONObject conceptObj = arrayOfConcepts.getJSONObject(i);
                newConceptNames.add(conceptObj.getString("con"));
                newConceptNames.add(conceptObj.getString("con2"));
            }
        }
        relatedConcepts.addAll(directlyRelatedConcepts);
        relatedConcepts.addAll(newConceptNames);
        for (String relatedConcept : relatedConcepts) {
            ArrayList<HashMap<String, String>> paragraphsFromTheConcept = lightHouse.getChildVerticesByRootVertexId(relatedConcept);
            for (HashMap<String, String> paragraphFromTheConcept : paragraphsFromTheConcept) {
                String elementIDofAParagraph =  new String();
               /* if(paragraphFromTheConcept.containsKey("fromFileName")){
                    if(!paragraphFromTheConcept.get("fromFileName").contains(paragraphFile) && paragraphFromTheConcept.get("type").contains("BASELPARAGRAPH") ) {
                        elementIDofAParagraph = paragraphFromTheConcept.get("elementID");
                    }
               }else{*/
                    if(paragraphFromTheConcept.get("type").contains("BASELPARAGRAPH")  ) {
                        elementIDofAParagraph = paragraphFromTheConcept.get("elementID");
                    }
                //}

                System.out.println("elementIDofAParagraph = " + elementIDofAParagraph);
                if(!elementIDofAParagraph.equals(paragraphID) && !elementIDofAParagraph.isEmpty() && null != elementIDofAParagraph )
                if(sortableParagraphExistanceCounts.containsKey(elementIDofAParagraph)){
                    Integer eachCount = sortableParagraphExistanceCounts.get(elementIDofAParagraph);
                    System.out.println("eachCount = " + eachCount);
                    Integer newCount = eachCount + 1;
                    sortableParagraphExistanceCounts.put(elementIDofAParagraph, newCount);
                }else {
                    sortableParagraphExistanceCounts.put(elementIDofAParagraph, 1);
                    System.out.println("LightHouseService.getRelatedParagraphsByMaxConceptsMatch");
                }
            }
        }

        Predicate<Integer> thresholdFilter = new Predicate<Integer>() {
            public boolean apply(Integer i) {
                return (i >= minConceptMatchingThreshold);
            }
        };

        Map<String, Integer> limitedToThresholdValueMap = Maps.filterValues(sortableParagraphExistanceCounts, thresholdFilter);


        // in the following operation it will try to get the highest number of concept attached paragraph

        if(sortableParagraphExistanceCounts != null && sortableParagraphExistanceCounts.keySet().size() != 0) {
            ArrayList<String> paragraphIDs = new ArrayList<>();
            Map<String, Integer> sortedParagraphs = sortByValue(sortableParagraphExistanceCounts);
            int count = 0;
            for(int i=sortedParagraphs.size(); i >= 0 ; i--) {
                String para = (String) sortedParagraphs.keySet().toArray()[i-1];
                count++;
                if(count == paragraphCountsThresHold){
                    i = 0;
                }
                paragraphIDs.add(para);
            }
            return lightHouse.getParagraphsByParagraphIds(paragraphIDs);
        }else{
            return null;
        }
    }


    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    private ArrayList<String> getRelatedConceptsByParagraphID(String paragraphID){
        ArrayList<String> relatedConcepts = new ArrayList<>();
        ArrayList<HashMap<String, String>> concepts = lightHouse.getRootVerticesByChildVertexId(paragraphID);
        for (HashMap<String, String> concept : concepts) {
            if(concept.get("type").equalsIgnoreCase("CONCEPT"))
                relatedConcepts.add(concept.get("elementID"));
        }
        return relatedConcepts;
    }

    public List<Map<String, String>> getAllDocFileNamesByType(String fType) throws IOException {
        List<Map<String, String>> allFileNames = new ArrayList<>();
        ArrayList<HashMap<String, String>> allFilesWithAllProperties = lightHouse.getVertexByProperty("type", fType);
        for (HashMap<String, String> allFilesWithAllProperty : allFilesWithAllProperties) {
            Map<String, String> aFile = new HashMap<>();
            aFile.put("name", allFilesWithAllProperty.get("name"));
            aFile.put("genre", allFilesWithAllProperty.get("genre"));
            allFileNames.add(aFile);
        }

        return allFileNames;
    }

    public String tinkerPopTest() {
        ArrayList<Vertex> listOfRelatedVertices = lightHouse.getRelatedVerticesByProperty("elementID", "320-10-35-34B");
        return listOfRelatedVertices.toString();
    }
}
