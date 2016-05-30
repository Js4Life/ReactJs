// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// OctopusImpactService.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.application.services;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.platform.exceptions.AppException;
import com.parabole.ccar.platform.graphdb.Octopus;
import com.parabole.ccar.platform.knowledge.KGraph;
import com.parabole.ccar.platform.lineage.GlossarytoDBMapper.glossarytodb_map;
import com.parabole.ccar.platform.lineage.businessRuleReader.RuleDef;
import com.parabole.ccar.platform.lineage.paraboleHyperGraph.HyperGraphUtil;
import com.parabole.ccar.platform.lineage.paraboleHyperGraph.VisualDef;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Octopus Lineage Services.
 *
 * @author Sandip Bhaumik
 * @since v1.0
 */

@Singleton
public class OctopusLineageService {

    @Inject
    protected Octopus octopus;

    @Inject
    protected OctopusSemanticService octopusSemanticService;

    private static final List<String> ignoredAttributes = Lists.newArrayList(CCAppConstants.TAG, CCAppConstants.ELEMENT_ID);
    
    public String getGlobalLineage() throws AppException {
    	
	
    	//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil pHGUtil = new HyperGraphUtil(pKGraph);
		
		//Read the Glossary Set
		final String GLOSSARY_FILE_PATH = "Glossary_Resource.xlsx";	
    	//Create other layers of HG using Glossary Set
		pHGUtil.AddGlossarySetInHG(GLOSSARY_FILE_PATH);
		
		//Read DDL
		//Sample Script
		//String statement = "CREATE TABLE MyGuests (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,firstname VARCHAR(30) NOT NULL,lastname VARCHAR(30) NOT NULL,email VARCHAR(50),reg_date TIMESTAMP)";
		//Create Graph from DDL
		//pHGUtil.CreateGraphFromDDL(statement);
		//Read Map of Glossary and Graph
		//??
		//Read Glossary to DB Map
		final String DB_MAP_FILE_PATH = "GlossarytoDB_Map.xlsx";		
		List<glossarytodb_map> pGlossaryDB_map = pHGUtil.ReadDBMapfromXLS(DB_MAP_FILE_PATH);
		
		//Read Rule Def file
		final String RULE_FILE_PATH = "Rule_Concept_Map.xlsx";	
		List<RuleDef> pRule = pHGUtil.ReadRulefromXLS(RULE_FILE_PATH);
		//System.out.println(pRule.toString());
		//Get Lineage Graph for this rule set
		VisualDef pVDef = pHGUtil.GetLineageGraph(pRule);
		//Create JSON from pVDef	
		return createVisGraphFormatJson(pVDef);
	}
    
	public String getLineageForSpecificRules(int RuleId) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil pHGUtil = new HyperGraphUtil(pKGraph);
		
		//Read the Glossary Set
		final String GLOSSARY_FILE_PATH = "Glossary_Resource.xlsx";	
    	//Create other layers of HG using Glossary Set
		pHGUtil.AddGlossarySetInHG(GLOSSARY_FILE_PATH);
		
		//Read Glossary to DB Map
		final String DB_MAP_FILE_PATH = "GlossarytoDB_Map.xlsx";		
		List<glossarytodb_map> pGlossaryDB_map = pHGUtil.ReadDBMapfromXLS(DB_MAP_FILE_PATH);		
		
		//Read DDL
		//Sample Script
		//String statement = "CREATE TABLE MyGuests (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,firstname VARCHAR(30) NOT NULL,lastname VARCHAR(30) NOT NULL,email VARCHAR(50),reg_date TIMESTAMP)";
		//Create Graph from DDL
		//pHGUtil.CreateGraphFromDDL(statement);
		//Read Map of Glossary and Graph
	
		//Read Rule Def file
		final String RULE_FILE_PATH = "Rule_Concept_Map.xlsx";	
		List<RuleDef> pRule = pHGUtil.ReadSpecificRulefromXLS(RULE_FILE_PATH, RuleId);

	
		
		//Get Lineage Graph for this rule set
		VisualDef pVDef = pHGUtil.GetLineageGraph(pRule);
		//Create JSON from pVDef	
		return createRuleSpecificVisGraphFormatJson(pVDef);
	}
	
	public String getLineageForSpecificConcepts(int ConceptId) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil pHGUtil = new HyperGraphUtil(pKGraph);
		
		//Read the Glossary Set
		final String GLOSSARY_FILE_PATH = "Glossary_Resource.xlsx";	
    	//Create other layers of HG using Glossary Set
		pHGUtil.AddGlossarySetInHG(GLOSSARY_FILE_PATH);
		
	
		//Read Glossary to DB Map
		final String DB_MAP_FILE_PATH = "GlossarytoDB_Map.xlsx";		
		List<glossarytodb_map> pGlossaryDB_map = pHGUtil.ReadDBMapfromXLS(DB_MAP_FILE_PATH);
		//Read DDL
		//Sample Script
		//String statement = "CREATE TABLE MyGuests (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,firstname VARCHAR(30) NOT NULL,lastname VARCHAR(30) NOT NULL,email VARCHAR(50),reg_date TIMESTAMP)";
		//Create Graph from DDL
		//pHGUtil.CreateGraphFromDDL(statement);
		//Read Map of Glossary and Graph
	
		//Read Rule Def file
		final String RULE_FILE_PATH = "Rule_Concept_Map.xlsx";	
		List<RuleDef> pRule = pHGUtil.ReadRulefromXLS(RULE_FILE_PATH);
	
		
		//Get Lineage Graph for this rule set
		VisualDef pVDef = pHGUtil.GetLineageGraphbyConcept(pRule, ConceptId);
		//Create JSON from pVDef	
		return createVisGraphFormatJson(pVDef);
	}
	
	public String getLineageForSpecificGlossary(int GlossaryId, int LayerId) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil pHGUtil = new HyperGraphUtil(pKGraph);
		
		//Read the Glossary Set
		final String GLOSSARY_FILE_PATH = "Glossary_Resource.xlsx";	
    	//Create other layers of HG using Glossary Set
		pHGUtil.AddGlossarySetInHG(GLOSSARY_FILE_PATH);
		
		//Read Glossary to DB Map
		final String DB_MAP_FILE_PATH = "GlossarytoDB_Map.xlsx";		
		List<glossarytodb_map> pGlossaryDB_map = pHGUtil.ReadDBMapfromXLS(DB_MAP_FILE_PATH);		//Read DDL
		//Sample Script
		//String statement = "CREATE TABLE MyGuests (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,firstname VARCHAR(30) NOT NULL,lastname VARCHAR(30) NOT NULL,email VARCHAR(50),reg_date TIMESTAMP)";
		//Create Graph from DDL
		//pHGUtil.CreateGraphFromDDL(statement);
		//Read Map of Glossary and Graph
	
		//Read Rule Def fi
		final String RULE_FILE_PATH = "Rule_Concept_Map.xlsx";	
		List<RuleDef> pRule = pHGUtil.ReadRulefromXLS(RULE_FILE_PATH);
	
		
		//Get Lineage Graph for this rule set
		VisualDef pVDef = pHGUtil.GetLineageGraphbyGlossary(pRule, GlossaryId, LayerId);
		//Create JSON from pVDef	
		return createVisGraphFormatJson(pVDef);
	}
		
	public String getLineageForSpecificDB(String DB_name, String DB_table, String DB_col) throws AppException {
		
    	//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil pHGUtil = new HyperGraphUtil(pKGraph);
		
		//Read the Glossary Set
		final String GLOSSARY_FILE_PATH = "Glossary_Resource.xlsx";	
    	//Create other layers of HG using Glossary Set
		pHGUtil.AddGlossarySetInHG(GLOSSARY_FILE_PATH);
		
		
		//Read DDL
		//Sample Script
		//String statement = "CREATE TABLE MyGuests (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,firstname VARCHAR(30) NOT NULL,lastname VARCHAR(30) NOT NULL,email VARCHAR(50),reg_date TIMESTAMP)";
		//Create Graph from DDL
		//pHGUtil.CreateGraphFromDDL(statement);
		//Read Map of Glossary and Graph
		//??
		//Read Glossary to DB Map
		final String DB_MAP_FILE_PATH = "GlossarytoDB_Map.xlsx";		
		List<glossarytodb_map> pGlossaryDB_map = pHGUtil.ReadSpecificDBMapfromXLS(DB_MAP_FILE_PATH, DB_name, DB_table, DB_col);
		
		//Read Rule Def file
		final String RULE_FILE_PATH = "Rule_Concept_Map.xlsx";	
		List<RuleDef> pRule = pHGUtil.ReadRulefromXLS(RULE_FILE_PATH);
		//System.out.println(pRule.toString());
		//Get Lineage Graph for this rule set
		VisualDef pVDef = pHGUtil.GetLineageGraphbyDB(pRule);
		//Create JSON from pVDef	
		return createVisGraphFormatJson(pVDef);
	}


	//Create rule specific Lineage JSON
	private String createRuleSpecificVisGraphFormatJson(VisualDef pVDef) {
		final JSONObject outputJson = new JSONObject();
		final JSONObject lineagedata = new JSONObject();
		
		//Add rule list to JSON
		final JSONArray ruleArray = new JSONArray();
		final JSONObject ruleJsonObj = new JSONObject();
		ruleJsonObj.put("id", pVDef.GetRuleID(0));
		ruleJsonObj.put("name", pVDef.GetRuleName(0));
		ruleJsonObj.put("des", pVDef.GetRuleDef(0));
		JSONObject newjson = new JSONObject(octopusSemanticService.getVertexPropertyKeyById(pVDef.GetRuleID(0)));
		JSONObject merged = new JSONObject(ruleJsonObj, JSONObject.getNames(ruleJsonObj));
		for(String key : JSONObject.getNames(newjson))
		{
		  merged.put(key, newjson.get(key));
		}

		ruleArray.put(merged);
		outputJson.put("rules", ruleArray);
		
		//Add concept list to JSON
		final JSONObject conceptFullJsonObj = new JSONObject();
		final JSONArray conceptJsonArray = new JSONArray();
		for(int i = 0 ; i < pVDef.GetConcpetListSize() ; i++) {
			final JSONObject conceptJsonObj = new JSONObject();
        	conceptJsonObj.put("id", pVDef.GetConceptID(i));
        	conceptJsonObj.put("type", "concept");
			conceptJsonObj.put("level", 0);

			if( octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)) != null ){
				conceptJsonObj.put("name", octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)));
			}else{
				JenaTdbService jenaTdbService = new JenaTdbService();
				String nodeData= null;
				try {
					nodeData = jenaTdbService.jenaTdbFetchingByClassName(pVDef.GetConceptName(i));
				} catch (com.parabole.ccar.application.exceptions.AppException e) {
					e.printStackTrace();
				}
			//	conceptJsonObj.put("name", pVDef.GetConceptName(i));
				conceptJsonObj.put("name", pVDef.GetConceptName(i));

			}
			
			conceptJsonObj.put("color", "green");
			conceptJsonArray.put(conceptJsonObj);
		}		
		conceptFullJsonObj.put("vertices", conceptJsonArray);
		conceptFullJsonObj.put("connecions", new JSONArray());
		//Add concept List to rule object
		outputJson.put("graphData", conceptFullJsonObj);
		
		//Add glossary list to JSON
		int	writen_cnt = 0;
		int layer_index = 1;
		
		final JSONObject glossaryFullJsonobject = new JSONObject();
		
		while(true)	
		{
			final JSONArray glossaryJsonArray = new JSONArray();
			for(int i = 0 ; i < pVDef.GetGlossaryListSize() ; i++) {
				if( pVDef.GetGlossarySet(i) == layer_index) {
				final JSONObject glossaryJsonObj = new JSONObject();
					//glossaryJsonObj.put("layer", pVDef.GetGlossarySet(i));
					glossaryJsonObj.put("id", pVDef.GetGlossaryID(i));
					glossaryJsonObj.put("name", pVDef.GetGlossaryName(i));
					glossaryJsonObj.put("des", pVDef.GetGlossaryDef(i));
					glossaryJsonArray.put(glossaryJsonObj);
					writen_cnt = writen_cnt + 1;
				}
			}		
			switch(layer_index){
					case 1: 	glossaryFullJsonobject.put("set-1", glossaryJsonArray);
								break;
					case 2: 	glossaryFullJsonobject.put("set-2", glossaryJsonArray);
								break;
					case 3: 	glossaryFullJsonobject.put("set-3", glossaryJsonArray);
								break;
					case 4: 	glossaryFullJsonobject.put("set-4", glossaryJsonArray);
								break;
					case 5: 	glossaryFullJsonobject.put("set-5", glossaryJsonArray);
								break;
					case 6: 	glossaryFullJsonobject.put("set-6", glossaryJsonArray);
								break;
					case 7: 	glossaryFullJsonobject.put("set-7", glossaryJsonArray);
								break;
					case 8: 	glossaryFullJsonobject.put("set-8", glossaryJsonArray);
								break;
			}
			
			layer_index++;	
			if(writen_cnt == pVDef.GetGlossaryListSize()) break;
		}	
		
		outputJson.put("glossarySets", glossaryFullJsonobject);
		
		
		//Add db list to JSON
		final JSONArray dbJsonArray = new JSONArray();
		for(int i = 0 ; i < pVDef.GetDBListSize() ; i++) {
			final JSONObject dbJsonObj = new JSONObject();
        	dbJsonObj.put("dbname", pVDef.GetDBName(i));
			dbJsonObj.put("table", pVDef.GetDBTable(i));
			dbJsonObj.put("column", pVDef.GetDBColumn(i));
			dbJsonObj.put("des", "Sample Data");
			dbJsonArray.put(dbJsonObj);
		}		
		outputJson.put("db", dbJsonArray);
		
		//Add all arrays together to output
       // lineagedata.put("rule", ruleJsonObj);
        return outputJson.toString();		
	}
	
	//Create Global Lineage JSON
	private String createVisGraphFormatJson(VisualDef pVDef) {
		
		final JSONObject outputJson = new JSONObject();
		final JSONObject lineagedata = new JSONObject();
		
		//Add rule list to JSON
		
		final JSONArray ruleJsonArray = new JSONArray();
		for(int i = 0 ; i < pVDef.GetRuleListSize() ; i++) {
			final JSONObject ruleJsonObj = new JSONObject();
        	ruleJsonObj.put("id", pVDef.GetRuleID(i));
			ruleJsonObj.put("name", pVDef.GetRuleName(i));
			ruleJsonObj.put("des", pVDef.GetRuleDef(i));
			JSONObject newjson = new JSONObject(octopusSemanticService.getVertexPropertyKeyById(pVDef.GetRuleID(i)));
			JSONObject merged = new JSONObject(ruleJsonObj, JSONObject.getNames(ruleJsonObj));
			for(String key : JSONObject.getNames(newjson))
			{
			  merged.put(key, newjson.get(key));
			}
			ruleJsonArray.put(merged);
		}		
		
		//Add concept list to JSON
		final JSONObject conceptFullJsonObj = new JSONObject();
		final JSONArray conceptJsonArray = new JSONArray();
		for(int i = 0 ; i < pVDef.GetConcpetListSize() ; i++) {
			final JSONObject conceptJsonObj = new JSONObject();
        	conceptJsonObj.put("id", pVDef.GetConceptID(i));
			conceptJsonObj.put("level", 0);
			conceptJsonObj.put("type", "concept");
			//finding nodeName against Node ID
			// conceptJsonObj.put("name", pVDef.GetConceptName(i));
			
			if( octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)) != null ){
				//conceptJsonObj.put("name", octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)));
                conceptJsonObj.put("name", pVDef.GetConceptName(i));
                JenaTdbService jenaTdbService1 = new JenaTdbService();
                String nodeData= null;
                try {
                    nodeData = jenaTdbService1.jenaTdbFetchingByClassName(pVDef.GetConceptName(i));
                    //nodeData = jenaTdbService1.jenaTdbFetchingByClassName("mpb-fin-main-ccar-14q:Sub_Sch_H1_CUSIP");
                } catch (com.parabole.ccar.application.exceptions.AppException e) {
                    e.printStackTrace();
                }
                //	conceptJsonObj.put("name", pVDef.GetConceptName(i));
                conceptJsonObj.put("name", nodeData);
			}else{
				conceptJsonObj.put("name", pVDef.GetConceptName(i));
			}
			conceptJsonObj.put("color", "green");
 			conceptJsonArray.put(conceptJsonObj);
		}		
		conceptFullJsonObj.put("vertices", conceptJsonArray);
		conceptFullJsonObj.put("connecions", new JSONArray());
        
		//Add glossary list to JSON
		
		int	writen_cnt = 0;
		int layer_index = 1;

		final JSONObject glossaryFullJsonobject = new JSONObject();
		
		while(true)	
		{
			final JSONArray glossaryJsonArray = new JSONArray();
			for(int i = 0 ; i < pVDef.GetGlossaryListSize() ; i++) {
				if( pVDef.GetGlossarySet(i) == layer_index) {
				final JSONObject glossaryJsonObj = new JSONObject();
					//glossaryJsonObj.put("layer", pVDef.GetGlossarySet(i));
					glossaryJsonObj.put("id", pVDef.GetGlossaryID(i));
					glossaryJsonObj.put("name", pVDef.GetGlossaryName(i));
					glossaryJsonObj.put("des", pVDef.GetGlossaryDef(i));
					glossaryJsonArray.put(glossaryJsonObj);
					writen_cnt = writen_cnt + 1;
				}
			}	
			switch(layer_index){
					case 1: 	glossaryFullJsonobject.put("set-1", glossaryJsonArray);
								break;
					case 2: 	glossaryFullJsonobject.put("set-2", glossaryJsonArray);
								break;
					case 3: 	glossaryFullJsonobject.put("set-3", glossaryJsonArray);
								break;
					case 4: 	glossaryFullJsonobject.put("set-4", glossaryJsonArray);
								break;
					case 5: 	glossaryFullJsonobject.put("set-5", glossaryJsonArray);
								break;
					case 6: 	glossaryFullJsonobject.put("set-6", glossaryJsonArray);
								break;
					case 7: 	glossaryFullJsonobject.put("set-7", glossaryJsonArray);
								break;
					case 8: 	glossaryFullJsonobject.put("set-8", glossaryJsonArray);
								break;
			}
			
			layer_index++;	
			if(writen_cnt == pVDef.GetGlossaryListSize()) break;
		}	
		//Add db list to JSON
		final JSONArray dbJsonArray = new JSONArray();
		for(int i = 0 ; i < pVDef.GetDBListSize() ; i++) {
			final JSONObject dbJsonObj = new JSONObject();
        	dbJsonObj.put("dbname", pVDef.GetDBName(i));
			dbJsonObj.put("table", pVDef.GetDBTable(i));
			dbJsonObj.put("column", pVDef.GetDBColumn(i));
			dbJsonArray.put(dbJsonObj);
		}		
		
		//Add all arrays together to output
        lineagedata.put("rules", ruleJsonArray);
        lineagedata.put("glossarySets", glossaryFullJsonobject);
        lineagedata.put("graphData", conceptFullJsonObj);
		//lineagedata.putOpt("concept", conceptJsonArray);
        //lineagedata.putOpt("glossary", glossaryJsonArray);
        lineagedata.putOpt("db", dbJsonArray);		
        //outputJson.put("graphData", lineagedata);
        //outputJson.put(lineagedata);
        return lineagedata.toString();
        //return outputJson.toString();
   }
	
}
