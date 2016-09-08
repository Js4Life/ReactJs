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
        } catch (IOException e) {
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




}
