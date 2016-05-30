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
package com.parabole.rda.application.services;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parabole.rda.application.global.RdaAppConstants;
import com.parabole.rda.platform.adaptivelearning.MultipleLinearRegression;
import com.parabole.rda.platform.exceptions.AppException;
import com.parabole.rda.platform.graphdb.Octopus;
import com.parabole.rda.platform.knowledge.KGraph;
import com.parabole.rda.platform.lineage.GlossarytoDBMapper.glossarytodb_map;
import com.parabole.rda.platform.lineage.businessRuleReader.RuleDef;
import com.parabole.rda.platform.lineage.businessUCReader.UCDef;
import com.parabole.rda.platform.lineage.datareader.DataReader;
import com.parabole.rda.platform.lineage.paraboleHyperGraph.HyperGraphUtil;
import com.parabole.rda.platform.lineage.paraboleHyperGraph.VisualDef;
import com.parabole.rda.platform.utils.ParaboleTree;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	protected ParaboleTree pPTree = null;

    private static final List<String> ignoredAttributes = Lists.newArrayList(RdaAppConstants.TAG, RdaAppConstants.ELEMENT_ID);
	
	//create a hashmap for <arraynumber>ArrayList of string containing (<concept/rule name><glossaryname><tablename><columnname><arraynumber>)
	//Data reading for csv will update the arraynumber
	//Data is used for Adaptive Learning
	private	HashMap <Integer, ArrayList<String>> pDataArray = new HashMap<Integer, ArrayList<String>>();
	private int		MaxDataSize = 1000; 	
	private int		MaxElemSize = 50;
    private double[]		NodeImpact = new double[1000];
	private	double		NodeImpactSum;
	
	/*public  OctopusLineageService() {
		System.out.println("creating ParaboleTree Start");
		pPTree = new ParaboleTree();
		System.out.println("creating ParaboleTree end");
	}*/
	
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
    
	public String getLineageForSpecificUseCases(int UseCaseId) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil		pHGUtil = new HyperGraphUtil(pKGraph);
		
		//Read the Glossary Set
		final String GLOSSARY_FILE_PATH = "Glossary_Resource_Ownership.xlsx";	
    	//Create other layers of HG using Glossary Set
		pHGUtil.AddGlossaryOwnershipSetInHG(GLOSSARY_FILE_PATH);
		
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
		final String UC_FILE_PATH = "UC_Concept_Map.xlsx";	
		List<UCDef> pUC = pHGUtil.ReadSpecificUCfromXLS(UC_FILE_PATH, UseCaseId);

	
		
		//Get Lineage Graph for the requested Use Case
		VisualDef	pVDef = pHGUtil.GetLineageGraphbyUC(pUC);
		//Create JSON from pVDef	
		return createUCSpecificVisGraphFormatJson(pVDef, pUC.get(0).getUC_Name());
	}

	public String getLineageForSpecificUseCaseswithAL(int UseCaseId, String datafile, int startyear, int endyear) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil		pHGUtil = new HyperGraphUtil(pKGraph);
		
		//Read the Glossary Set
		final String GLOSSARY_FILE_PATH = "Glossary_Resource_Ownership.xlsx";	
    	//Create other layers of HG using Glossary Set
		pHGUtil.AddGlossaryOwnershipSetInHG(GLOSSARY_FILE_PATH);
		
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
		final String UC_FILE_PATH = "UC_Concept_Map.xlsx";	
		List<UCDef> pUC = pHGUtil.ReadSpecificUCfromXLS(UC_FILE_PATH, UseCaseId);

	
		
		//Get Lineage Graph for the requested Use Case
		VisualDef	pVDef = pHGUtil.GetLineageGraphbyUC(pUC);
		
		//Read the data from datafile and run the multiple regression logic
		double[][] x = new double[MaxDataSize][MaxElemSize];
		double[] y = new double[MaxDataSize];
		
		DataReader pDataRD = new DataReader();
		
		System.out.println("Reading Data File Start");
		System.out.println("pDataArray.size() " + pDataArray.size());
		int DataLength = pDataRD.getDataArrayListFromExcel(datafile, x, y, pDataArray, startyear, endyear);
		System.out.println("Reading Data File End");
		
		double[][] a = new double[DataLength][(pDataArray.size()-1)];
		double[] b = new double[DataLength];
		
		//Copy Y
		for(int k = 0; k < DataLength ; k++){
			b[k] = y[k];
			//System.out.println("b[k] " + b[k] );
		}
		
		//Copy X
		for(int k = 0; k < (pDataArray.size()-1) ; k++){
			for(int l = 0 ; l < DataLength ; l++){
				a[l][k] = x[l][k];
				//System.out.println("a[l][k] " + a[l][k] );
			}		
		}
			
		
		System.out.println("MultipleLinearRegression Start");		
		MultipleLinearRegression regression = new MultipleLinearRegression(a, b, DataLength, (pDataArray.size()-1));
		System.out.println("MultipleLinearRegression end");
		
		
		NodeImpactSum = 0;
		for(int i = 0 ; i < (pDataArray.size()-1); i++){			
			NodeImpact[i] = (regression.beta(i) > 0) ? regression.beta(i) : -regression.beta(i);
			System.out.println("NodeImpact[i] : " + NodeImpact[i]);
			NodeImpactSum = NodeImpactSum + NodeImpact[i];
		}				
		System.out.println("NodeImpactSum " + NodeImpactSum);
		
		//Create JSON from pVDef	
		return createUCSpecificVisGraphFormatJsonwithAL(pVDef, pUC.get(0).getUC_Name());
	}

	public int IsEnoughImpactbyConcept(final String Concept_name){
			int	impact = 0;
			ArrayList<String> lLineageList;
			
			//check if NodeImpactSum is very low
			//In case of very low NodeImpactSum drop all parameters
			//if(NodeImpactSum < 1.0) {return impact;}
			
			for(int i=1 ; i < pDataArray.size(); i++){
				lLineageList = pDataArray.get(i);
				if(Concept_name.equals(lLineageList.get(0)))
				{
					//System.out.println("NodeImpact[i]/NodeImpactSum : " + NodeImpact[i-1]/NodeImpactSum);
					if((NodeImpact[i-1]/NodeImpactSum) > 0.1) {impact = 1;}
					break;
				}		
			}
			return impact;
	}

	public int IsEnoughImpactbyGlossary(final String Glossary_name){
			int impact = 0;
			ArrayList<String> lLineageList;
			
			//check if NodeImpactSum is very low
			//In case of very low NodeImpactSum drop all parameters
			//if(NodeImpactSum < 1.0) {return impact;}			
			
			for(int i=1 ; i < pDataArray.size(); i++){
				lLineageList = pDataArray.get(i);
				if(Glossary_name.equals(lLineageList.get(1)))
				{
					//System.out.println("NodeImpact[i]/NodeImpactSum : " + NodeImpact[i-1]/NodeImpactSum);
					if((NodeImpact[i-1]/NodeImpactSum) > 0.1) {impact = 1;}
					break;
				}		
			}
			
			return impact;
	}

	public String getLineageForNodeIdSpecificUseCases(int UseCaseId, int nodeId) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil		pHGUtil = new HyperGraphUtil(pKGraph);
		
		//Read the Glossary Set
		final String GLOSSARY_FILE_PATH = "Glossary_Resource_Ownership.xlsx";	
    	//Create other layers of HG using Glossary Set
		pHGUtil.AddGlossaryOwnershipSetInHG(GLOSSARY_FILE_PATH);
		
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
		final String UC_FILE_PATH = "UC_Concept_Map.xlsx";	
		List<UCDef> pUC = pHGUtil.ReadSpecificUCfromXLS(UC_FILE_PATH, UseCaseId);

	
		
		//Get Lineage Graph for the requested Use Case
		VisualDef	pVDef = pHGUtil.GetLineageGraphbyUC(pUC);
		//Create JSON from pVDef	
		return createUCNodeSpecificVisGraphFormatJson(pVDef, pUC.get(0).getUC_Name(), nodeId);
	}

	public String getLineageForSpecificRules(int RuleId) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil		pHGUtil = new HyperGraphUtil(pKGraph);
		
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
		VisualDef	pVDef = pHGUtil.GetLineageGraph(pRule);
		//Create JSON from pVDef	
		return createRuleSpecificVisGraphFormatJson(pVDef);
	}
	
	public String getLineageForSpecificConcepts(int ConceptId) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil		pHGUtil = new HyperGraphUtil(pKGraph);
		
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
		VisualDef	pVDef = pHGUtil.GetLineageGraphbyConcept(pRule, ConceptId);
		//Create JSON from pVDef	
		return createVisGraphFormatJson(pVDef);
	}
	
	public String getLineageForSpecificGlossary(int GlossaryId, int LayerId) throws AppException {
		
		//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil		pHGUtil = new HyperGraphUtil(pKGraph);
		
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
		VisualDef	pVDef = pHGUtil.GetLineageGraphbyGlossary(pRule, GlossaryId, LayerId);
		//Create JSON from pVDef	
		return createVisGraphFormatJson(pVDef);
	}
		
	public String getLineageForSpecificDB(String DB_name, String DB_table, String DB_col) throws AppException {
		
    	//Build Knowledge graph from Ontology
		final KGraph pKGraph = octopusSemanticService.getFullBinaryKnowledgeGraph();
		
		//Build the layer zero of hypergraph using the knowledge graph
		HyperGraphUtil 	pHGUtil = new HyperGraphUtil(pKGraph);
		
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
		VisualDef	pVDef = pHGUtil.GetLineageGraphbyDB(pRule);
		//Create JSON from pVDef	
		return createVisGraphFormatJson(pVDef);
	}

	//Create UC specific Lineage JSON
	private String createUCSpecificVisGraphFormatJson(VisualDef pVDef, String UC_Name) {
		final JSONObject outputJson = new JSONObject();
		final JSONObject lineagedata = new JSONObject();
		int	  vertex_cnt;
		int	  concept_vertex_cnt;
		int   glossary_vertex_cnt;
		int	  root_vertex_cnt;
		int   connection_cnt = 1;	//Connection ID starts from 1000000
		int	  pDataArrayIndex = 0; 

		
		//Add vertices array
		final JSONObject ucFullJsonObj = new JSONObject();
		final JSONArray verticesJsonArray = new JSONArray();
		final JSONArray connectionsJsonArray = new JSONArray();
		
		//Start Adding Concept, Glossary, User, DB to vertices arrays
		//Add connections also to connection arrays
		
		// 1-9 ---> vertex count reserved for rule / use cases
		root_vertex_cnt = UC_Name.hashCode();
		
		//First add the UC
		final JSONObject  ucJsonObj = new JSONObject();
		ucJsonObj.put("id", root_vertex_cnt);
		ucJsonObj.put("level", 1);
		ucJsonObj.put("name", UC_Name);
		ucJsonObj.put("type", "rule");
		//add the UC vertices to the vertices array
		verticesJsonArray.put(ucJsonObj);
		
		//Add the first element to "pDataArray"
		ArrayList<String> lLineageList = new ArrayList<String>();
		lLineageList.add(UC_Name);
		lLineageList.add(UC_Name);
		lLineageList.add(UC_Name);
		lLineageList.add(UC_Name);
		pDataArray.put(pDataArrayIndex, lLineageList);	
		pDataArrayIndex = pDataArrayIndex + 1;
		
		//add the first node to ParaboleTree
		pPTree = new ParaboleTree();
		pPTree.addNode(0, root_vertex_cnt);
		//System.out.println("Adding first Node to ParaboleTree End");
		
		//First Add concepts
		for(int i = 0 ; i < pVDef.GetConcpetListSize() ; i++) {
			
			//Add Concepts in vertices Array
			final JSONObject conceptJsonObj = new JSONObject();        	
			concept_vertex_cnt = octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)).hashCode();
			conceptJsonObj.put("id", concept_vertex_cnt);
			conceptJsonObj.put("level", 2);
			if( octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)) != null ){
				System.out.println("Concept ID " + pVDef.GetConceptID(i));
				conceptJsonObj.put("name", octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)));
			}else{
				conceptJsonObj.put("name", pVDef.GetConceptName(i));
			}
        	conceptJsonObj.put("type", "concept");
			verticesJsonArray.put(conceptJsonObj);
			
			
			//Add connections between rule vertex and current concept vertext
			final JSONObject connectionJsonObj = new JSONObject();        	
			connectionJsonObj.put("id", connection_cnt);
			connectionJsonObj.put("from", root_vertex_cnt);
			connectionJsonObj.put("to", concept_vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(root_vertex_cnt, concept_vertex_cnt);			

			connectionsJsonArray.put(connectionJsonObj);
			
			//Add the concept node to the pDataArray
			ArrayList<String> tLineageList = new ArrayList<String>();
			tLineageList.add(octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)));
			pDataArray.put(pDataArrayIndex, tLineageList);	
			pDataArrayIndex = pDataArrayIndex + 1;
			
			
			//Increase connection_cnt
			connection_cnt = connection_cnt + 1;
		}

		
		//Then add the glossary items
		for(int i = 0 ; i < pVDef.GetGlossaryListSize() ; i++) {

			final JSONObject glossaryJsonObj = new JSONObject();
			glossary_vertex_cnt = pVDef.GetGlossaryName(i).hashCode(); 
			
			glossaryJsonObj.put("id", glossary_vertex_cnt);
			glossaryJsonObj.put("level", pVDef.GetGlossarySet(i)+2);
			glossaryJsonObj.put("name", pVDef.GetGlossaryName(i));
			glossaryJsonObj.put("type", "glossary");
			//add the glossary node to vertex array
			verticesJsonArray.put(glossaryJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject connectionJsonObj = new JSONObject();        	
			connectionJsonObj.put("id", connection_cnt);
			//connectionJsonObj.put("from", 1);
			concept_vertex_cnt = octopusSemanticService.getVertexNameById(pVDef.GetGlossaryConcept_ID(i)).hashCode();
			connectionJsonObj.put("from", concept_vertex_cnt);
			connectionJsonObj.put("to", glossary_vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(concept_vertex_cnt, glossary_vertex_cnt);				
			connectionsJsonArray.put(connectionJsonObj);
			
			
			//update pDataArray with glossary node
			for(int k = 0; k < pDataArrayIndex; k++){
				ArrayList<String>	tmpLineageList = new ArrayList<String>();
				tmpLineageList = pDataArray.get(k);
				if((tmpLineageList.get(0)).equals(octopusSemanticService.getVertexNameById(pVDef.GetGlossaryConcept_ID(i)))){
					tmpLineageList.add(pVDef.GetGlossaryName(i));
					pDataArray.put(k, tmpLineageList);		
				}
			}
			
			//update vertex_cnt + 1;
			vertex_cnt = pVDef.GetGlossaryGenerator(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now add all the owners for this glossary
			//Start with Generator
			final JSONObject  generatorJsonObj = new JSONObject();
			generatorJsonObj.put("id", vertex_cnt);
			generatorJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			generatorJsonObj.put("name", pVDef.GetGlossaryGenerator(i));
			generatorJsonObj.put("type", "user");
			//add the Generator user node to vertex array
			verticesJsonArray.put(generatorJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject generatorconnectionJsonObj = new JSONObject();        	
			generatorconnectionJsonObj.put("id", connection_cnt);
			generatorconnectionJsonObj.put("from", glossary_vertex_cnt);
			generatorconnectionJsonObj.put("to", vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(glossary_vertex_cnt, vertex_cnt);				
			
			connectionsJsonArray.put(generatorconnectionJsonObj);
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetGlossaryReviewer(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now do for Reviewer
			final JSONObject  reviewJsonObj = new JSONObject();
			reviewJsonObj.put("id", vertex_cnt);
			reviewJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			reviewJsonObj.put("name", pVDef.GetGlossaryReviewer(i));
			reviewJsonObj.put("type", "user");
			//add the Generator user node to vertex array
			verticesJsonArray.put(reviewJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject reviewconnectionJsonObj = new JSONObject();        	
			reviewconnectionJsonObj.put("id", connection_cnt);
			reviewconnectionJsonObj.put("from", glossary_vertex_cnt);
			reviewconnectionJsonObj.put("to", vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(glossary_vertex_cnt, vertex_cnt);				
			connectionsJsonArray.put(reviewconnectionJsonObj);
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetGlossaryApprover(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now do it for Approver
			final JSONObject  approverJsonObj = new JSONObject();
			approverJsonObj.put("id", vertex_cnt);
			approverJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			approverJsonObj.put("name", pVDef.GetGlossaryApprover(i));
			approverJsonObj.put("type", "user");
			//add the Generator user node to vertex array
			verticesJsonArray.put(approverJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject approverconnectionJsonObj = new JSONObject();        	
			approverconnectionJsonObj.put("id", connection_cnt);
			approverconnectionJsonObj.put("from", glossary_vertex_cnt);
			approverconnectionJsonObj.put("to", vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(glossary_vertex_cnt, vertex_cnt);				
			connectionsJsonArray.put(approverconnectionJsonObj);
			
			//update connection cnt
			connection_cnt = connection_cnt + 1;									
			
		}		
		
		//Now Add the DB items
		//Add db list to JSON
		
		final JSONArray dbJsonArray = new JSONArray();
		for(int i = 0 ; i < pVDef.GetDBListSize() ; i++) {
			final JSONObject dbJsonObj = new JSONObject();
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetDBName(i).hashCode();

			dbJsonObj.put("id", pVDef.GetDBName(i).hashCode());
			dbJsonObj.put("level", 5);
			dbJsonObj.put("name", pVDef.GetDBName(i));
			dbJsonObj.put("type", "db");
			//add the Generator user node to vertex array
			verticesJsonArray.put(dbJsonObj);
			
			//add the Conncection between this glossary and concept
			//calcualte corresponding glossary item
			glossary_vertex_cnt = pVDef.GetGlossaryName(i).hashCode();
			final JSONObject connectionJsonObj = new JSONObject();  	
			connectionJsonObj.put("id", connection_cnt);
			connectionJsonObj.put("from", glossary_vertex_cnt);
			connectionJsonObj.put("to", vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(glossary_vertex_cnt, vertex_cnt);				
			connectionsJsonArray.put(connectionJsonObj);
			
			//update pDataArray with glossary node
			for(int k = 0; k < pDataArrayIndex; k++){
				ArrayList<String>	tmpLineageList = new ArrayList<String>();
				tmpLineageList = pDataArray.get(k);
				if((tmpLineageList.get(1)).equals(pVDef.GetGlossaryName(i))){
					tmpLineageList.add(pVDef.GetDBTable(i));
					tmpLineageList.add(pVDef.GetDBColumn(i));
					pDataArray.put(k, tmpLineageList);		
				}
			}
			
			//update connection cnt
			connection_cnt = connection_cnt + 1;									
		}		
		
			
		
		//add vertices array to the final object
		ucFullJsonObj.put("vertices", verticesJsonArray);
		//add connection array to the final object
		ucFullJsonObj.put("connecions", connectionsJsonArray);
		

	   System.out.println(ucFullJsonObj.toString());
       return ucFullJsonObj.toString();		
	}

	//Create UC specific Lineage JSON highlighting sub tree with effect of data driven adaptive learning
	private String createUCSpecificVisGraphFormatJsonwithAL(VisualDef pVDef, String UC_Name) {
		final JSONObject outputJson = new JSONObject();
		final JSONObject lineagedata = new JSONObject();
		int	  vertex_cnt;
		int	  concept_vertex_cnt;
		int   glossary_vertex_cnt;
		int	  root_vertex_cnt;
		int   connection_cnt = 1;	//Connection ID starts from 1000000

		
		//Add vertices array
		final JSONObject ucFullJsonObj = new JSONObject();
		final JSONArray verticesJsonArray = new JSONArray();
		final JSONArray connectionsJsonArray = new JSONArray();
		
		//Start Adding Concept, Glossary, User, DB to vertices arrays
		//Add connections also to connection arrays
		
		// 1-9 ---> vertex count reserved for rule / use cases
		root_vertex_cnt = UC_Name.hashCode();
		
		//First add the UC
		final JSONObject  ucJsonObj = new JSONObject();
		ucJsonObj.put("id", root_vertex_cnt);
		ucJsonObj.put("level", 1);
		ucJsonObj.put("name", UC_Name);
		ucJsonObj.put("type", "rule");
		//add the UC vertices to the vertices array
		verticesJsonArray.put(ucJsonObj);
		
		//System.out.println("Adding first Node to ParaboleTree Start");
		
		//add the first node to ParaboleTree
		pPTree = new ParaboleTree();
		pPTree.addNode(0, root_vertex_cnt);
		//System.out.println("Adding first Node to ParaboleTree End");
		
		//First Add concepts
		for(int i = 0 ; i < pVDef.GetConcpetListSize() ; i++) {
			
			//check if this concept has enough impact
			//if impact is not enough go to the next concept
			if(IsEnoughImpactbyConcept(octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i))) == 0) {continue;}		
			
					//Add Concepts in vertices Array
			final JSONObject conceptJsonObj = new JSONObject();
			concept_vertex_cnt = octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)).hashCode();
			conceptJsonObj.put("id", concept_vertex_cnt);
			conceptJsonObj.put("level", 2);
			if( octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)) != null ){
				System.out.println("Concept ID " + pVDef.GetConceptID(i));
				conceptJsonObj.put("name", octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)));
			}else{
				conceptJsonObj.put("name", pVDef.GetConceptName(i));
			}
        	conceptJsonObj.put("type", "concept");
			verticesJsonArray.put(conceptJsonObj);
			
			
			//Add connections between rule vertex and current concept vertext
			final JSONObject connectionJsonObj = new JSONObject();        	
			connectionJsonObj.put("id", connection_cnt);
			connectionJsonObj.put("from", root_vertex_cnt);
			connectionJsonObj.put("to", concept_vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(root_vertex_cnt, concept_vertex_cnt);			

			connectionsJsonArray.put(connectionJsonObj);
			
			
			//Increase connection_cnt
			connection_cnt = connection_cnt + 1;
		}

		
		//Then add the glossary items
		for(int i = 0 ; i < pVDef.GetGlossaryListSize() ; i++) {
				
			//check if the corresponding concept has enough impact
			//if impact is not enough go to the next glossary
			if(IsEnoughImpactbyConcept(octopusSemanticService.getVertexNameById(pVDef.GetGlossaryConcept_ID(i))) == 0) {continue;}		

			final JSONObject glossaryJsonObj = new JSONObject();
			glossary_vertex_cnt = pVDef.GetGlossaryName(i).hashCode(); 
			
			glossaryJsonObj.put("id", glossary_vertex_cnt);
			glossaryJsonObj.put("level", pVDef.GetGlossarySet(i)+2);
			glossaryJsonObj.put("name", pVDef.GetGlossaryName(i));
			glossaryJsonObj.put("type", "glossary");
			//add the glossary node to vertex array
			verticesJsonArray.put(glossaryJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject connectionJsonObj = new JSONObject();        	
			connectionJsonObj.put("id", connection_cnt);
			//connectionJsonObj.put("from", 1);
			concept_vertex_cnt = octopusSemanticService.getVertexNameById(pVDef.GetGlossaryConcept_ID(i)).hashCode();
			connectionJsonObj.put("from", concept_vertex_cnt);
			connectionJsonObj.put("to", glossary_vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(concept_vertex_cnt, glossary_vertex_cnt);				
			connectionsJsonArray.put(connectionJsonObj);
			
			
			//update vertex_cnt + 1;
			vertex_cnt = pVDef.GetGlossaryGenerator(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now add all the owners for this glossary
			//Start with Generator
			final JSONObject  generatorJsonObj = new JSONObject();
			generatorJsonObj.put("id", vertex_cnt);
			generatorJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			generatorJsonObj.put("name", pVDef.GetGlossaryGenerator(i));
			generatorJsonObj.put("type", "user");
			//add the Generator user node to vertex array
			verticesJsonArray.put(generatorJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject generatorconnectionJsonObj = new JSONObject();        	
			generatorconnectionJsonObj.put("id", connection_cnt);
			generatorconnectionJsonObj.put("from", glossary_vertex_cnt);
			generatorconnectionJsonObj.put("to", vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(glossary_vertex_cnt, vertex_cnt);				
			
			connectionsJsonArray.put(generatorconnectionJsonObj);
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetGlossaryReviewer(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now do for Reviewer
			final JSONObject  reviewJsonObj = new JSONObject();
			reviewJsonObj.put("id", vertex_cnt);
			reviewJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			reviewJsonObj.put("name", pVDef.GetGlossaryReviewer(i));
			reviewJsonObj.put("type", "user");
			//add the Generator user node to vertex array
			verticesJsonArray.put(reviewJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject reviewconnectionJsonObj = new JSONObject();        	
			reviewconnectionJsonObj.put("id", connection_cnt);
			reviewconnectionJsonObj.put("from", glossary_vertex_cnt);
			reviewconnectionJsonObj.put("to", vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(glossary_vertex_cnt, vertex_cnt);				
			connectionsJsonArray.put(reviewconnectionJsonObj);
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetGlossaryApprover(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now do it for Approver
			final JSONObject  approverJsonObj = new JSONObject();
			approverJsonObj.put("id", vertex_cnt);
			approverJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			approverJsonObj.put("name", pVDef.GetGlossaryApprover(i));
			approverJsonObj.put("type", "user");
			//add the Generator user node to vertex array
			verticesJsonArray.put(approverJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject approverconnectionJsonObj = new JSONObject();        	
			approverconnectionJsonObj.put("id", connection_cnt);
			approverconnectionJsonObj.put("from", glossary_vertex_cnt);
			approverconnectionJsonObj.put("to", vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(glossary_vertex_cnt, vertex_cnt);				
			connectionsJsonArray.put(approverconnectionJsonObj);
			
			//update connection cnt
			connection_cnt = connection_cnt + 1;									
			
		}		
		
		//Now Add the DB items
		//Add db list to JSON
		
		final JSONArray dbJsonArray = new JSONArray();
		for(int i = 0 ; i < pVDef.GetDBListSize() ; i++) {
			//check if the corresponding concept has enough impact
			//if impact is not enough go to the next glossary
			
			if(IsEnoughImpactbyGlossary(pVDef.GetGlossaryName(i)) == 0) {continue;}		
			
			final JSONObject dbJsonObj = new JSONObject();
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetDBName(i).hashCode();

			dbJsonObj.put("id", pVDef.GetDBName(i).hashCode());
			dbJsonObj.put("level", 5);
			dbJsonObj.put("name", pVDef.GetDBName(i));
			dbJsonObj.put("type", "db");
			//add the Generator user node to vertex array
			verticesJsonArray.put(dbJsonObj);
			
			//add the Conncection between this glossary and concept
			//calcualte corresponding glossary item
			glossary_vertex_cnt = pVDef.GetGlossaryName(i).hashCode();
			final JSONObject connectionJsonObj = new JSONObject();  	
			connectionJsonObj.put("id", connection_cnt);
			connectionJsonObj.put("from", glossary_vertex_cnt);
			connectionJsonObj.put("to", vertex_cnt);
			//add the  node to ParaboleTree
			pPTree.addNode(glossary_vertex_cnt, vertex_cnt);				
			connectionsJsonArray.put(connectionJsonObj);
			
			//update connection cnt
			connection_cnt = connection_cnt + 1;									
		}		
		
			
		
		//add vertices array to the final object
		ucFullJsonObj.put("vertices", verticesJsonArray);
		//add connection array to the final object
		ucFullJsonObj.put("connecions", connectionsJsonArray);
		

		//Add concept List to rule object
		//outputJson.put(ucFullJsonObj);
		
	   System.out.println(ucFullJsonObj.toString());
       return ucFullJsonObj.toString();		
	}
	
	
	//Create UC specific Lineage JSON highlighting sub tree associated with specific node
	private String createUCNodeSpecificVisGraphFormatJson(VisualDef pVDef, String UC_Name, int search_nodeId) {
		final JSONObject outputJson = new JSONObject();
		final JSONObject lineagedata = new JSONObject();
		int	  vertex_cnt;
		int	  concept_vertex_cnt;
		int   glossary_vertex_cnt;
		int	  root_vertex_cnt;
		int   connection_cnt = 1;	//Connection ID starts from 1000000
		boolean		edge_highlight_status = false;
		HashMap<Integer, Integer>	node_highligt_map = new HashMap<Integer, Integer>();	
		
		//reset Tree Traversal status
		pPTree.ResetTraversal();

		
		//Add vertices array
		final JSONObject ucFullJsonObj = new JSONObject();
		final JSONArray verticesJsonArray = new JSONArray();
		final JSONArray connectionsJsonArray = new JSONArray();
		
		//Start Adding Concept, Glossary, User, DB to vertices arrays
		//Add connections also to connection arrays
		
		// 1-9 ---> vertex count reserved for rule / use cases
		root_vertex_cnt = UC_Name.hashCode();
		
		//First add the UC
		final JSONObject  ucJsonObj = new JSONObject();
		ucJsonObj.put("id", root_vertex_cnt);
		ucJsonObj.put("level", 1);
		ucJsonObj.put("name", UC_Name);
		ucJsonObj.put("type", "selectedRule");
		//add the UC vertices to the vertices array
		verticesJsonArray.put(ucJsonObj);
				
		
		//First Add concepts
		for(int i = 0 ; i < pVDef.GetConcpetListSize() ; i++) {
			
			//Add Concepts in vertices Array
			final JSONObject conceptJsonObj = new JSONObject();        	
			concept_vertex_cnt = octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)).hashCode();
			conceptJsonObj.put("id", concept_vertex_cnt);
			conceptJsonObj.put("level", 2);
			if( octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)) != null ){
				System.out.println("Concept ID " + pVDef.GetConceptID(i));
				conceptJsonObj.put("name", octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)));
			}else{
				conceptJsonObj.put("name", pVDef.GetConceptName(i));
			}
			//check if it comes in the connection of node in interest	
			if((pPTree.IsChild(search_nodeId, concept_vertex_cnt)) || (pPTree.IsParent(search_nodeId, concept_vertex_cnt))){
					conceptJsonObj.put("type", "selectedConcept");
					node_highligt_map.put(concept_vertex_cnt, 1); }
			else {	
					conceptJsonObj.put("type", "unSelectedConcept");
					node_highligt_map.put(concept_vertex_cnt, 0);	
			}		
			verticesJsonArray.put(conceptJsonObj);
			
			
			//Add connections between rule vertex and current concept vertext
			final JSONObject connectionJsonObj = new JSONObject();        	
			connectionJsonObj.put("id", connection_cnt);
			connectionJsonObj.put("from", root_vertex_cnt);
			connectionJsonObj.put("to", concept_vertex_cnt);
			if(node_highligt_map.get(concept_vertex_cnt) == 1){
				connectionJsonObj.put("color", "rgb(255, 165, 0)");
			}else{
				connectionJsonObj.put("color", "rgb(192,192,192)");				
			}
			
			connectionsJsonArray.put(connectionJsonObj);

			
			//Increase connection_cnt
			connection_cnt = connection_cnt + 1;
		}

		
		//Then add the glossary items
		for(int i = 0 ; i < pVDef.GetGlossaryListSize() ; i++) {

			final JSONObject glossaryJsonObj = new JSONObject();
			glossary_vertex_cnt = pVDef.GetGlossaryName(i).hashCode(); 
			
			glossaryJsonObj.put("id", glossary_vertex_cnt);
			glossaryJsonObj.put("level", pVDef.GetGlossarySet(i)+2);
			glossaryJsonObj.put("name", pVDef.GetGlossaryName(i));
			if((pPTree.IsChild(search_nodeId, glossary_vertex_cnt)) || (pPTree.IsParent(search_nodeId, glossary_vertex_cnt))){	
				glossaryJsonObj.put("type", "selectedGlossary");
				node_highligt_map.put(glossary_vertex_cnt, 1);
			}	
			else{
				glossaryJsonObj.put("type", "unSelectedGlossary");
				node_highligt_map.put(glossary_vertex_cnt, 0);
			}		
			//add the glossary node to vertex array
			verticesJsonArray.put(glossaryJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject connectionJsonObj = new JSONObject();        	
			connectionJsonObj.put("id", connection_cnt);
			//connectionJsonObj.put("from", 1);
			concept_vertex_cnt = octopusSemanticService.getVertexNameById(pVDef.GetGlossaryConcept_ID(i)).hashCode();
			connectionJsonObj.put("from", concept_vertex_cnt);
			connectionJsonObj.put("to", glossary_vertex_cnt);
			if((node_highligt_map.get(concept_vertex_cnt) == 1) && (node_highligt_map.get(glossary_vertex_cnt) == 1)){
				connectionJsonObj.put("color", "rgb(255, 165, 0)");
			}else{
				connectionJsonObj.put("color", "rgb(192,192,192)");				
			}
			
			connectionsJsonArray.put(connectionJsonObj);
			
			//update vertex_cnt + 1;
			vertex_cnt = pVDef.GetGlossaryGenerator(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now add all the owners for this glossary
			//Start with Generator
			final JSONObject  generatorJsonObj = new JSONObject();
			generatorJsonObj.put("id", vertex_cnt);
			generatorJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			generatorJsonObj.put("name", pVDef.GetGlossaryGenerator(i));
			if((pPTree.IsChild(search_nodeId, vertex_cnt)) || (pPTree.IsParent(search_nodeId, vertex_cnt))){
				generatorJsonObj.put("type", "selectedUser");
				node_highligt_map.put(vertex_cnt, 1);
			}	
			else {
				generatorJsonObj.put("type", "unSelectedUser");
				node_highligt_map.put(vertex_cnt, 0);
			}	
			//add the Generator user node to vertex array
			verticesJsonArray.put(generatorJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject generatorconnectionJsonObj = new JSONObject();        	
			generatorconnectionJsonObj.put("id", connection_cnt);
			generatorconnectionJsonObj.put("from", glossary_vertex_cnt);
			generatorconnectionJsonObj.put("to", vertex_cnt);
			if((node_highligt_map.get(vertex_cnt) == 1) && (node_highligt_map.get(glossary_vertex_cnt) == 1)){
				generatorconnectionJsonObj.put("color", "rgb(255, 165, 0)");
			}else{
				generatorconnectionJsonObj.put("color", "rgb(192,192,192)");				
			}
			
			connectionsJsonArray.put(generatorconnectionJsonObj);
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetGlossaryReviewer(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now do for Reviewer
			final JSONObject  reviewJsonObj = new JSONObject();
			reviewJsonObj.put("id", vertex_cnt);
			reviewJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			reviewJsonObj.put("name", pVDef.GetGlossaryReviewer(i));
			if((pPTree.IsChild(search_nodeId, vertex_cnt)) || (pPTree.IsParent(search_nodeId, vertex_cnt))){
				reviewJsonObj.put("type", "selectedUser");
				node_highligt_map.put(vertex_cnt, 1);
			}	
			else {
				reviewJsonObj.put("type", "unSelectedUser");
				node_highligt_map.put(vertex_cnt, 0);
			}	
			//add the Generator user node to vertex array
			verticesJsonArray.put(reviewJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject reviewconnectionJsonObj = new JSONObject();        	
			reviewconnectionJsonObj.put("id", connection_cnt);
			reviewconnectionJsonObj.put("from", glossary_vertex_cnt);
			reviewconnectionJsonObj.put("to", vertex_cnt);
			if((node_highligt_map.get(vertex_cnt) == 1) && (node_highligt_map.get(glossary_vertex_cnt) == 1)){
				reviewconnectionJsonObj.put("color", "rgb(255, 165, 0)");
			}else{
				reviewconnectionJsonObj.put("color", "rgb(192,192,192)");				
			}
			
			connectionsJsonArray.put(reviewconnectionJsonObj);
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetGlossaryApprover(i).hashCode();
			//update connection cnt
			connection_cnt = connection_cnt + 1;
			
			//Now do it for Approver
			final JSONObject  approverJsonObj = new JSONObject();
			approverJsonObj.put("id", vertex_cnt);
			approverJsonObj.put("level", pVDef.GetGlossarySet(i)+3);
			approverJsonObj.put("name", pVDef.GetGlossaryApprover(i));
			if((pPTree.IsChild(search_nodeId, vertex_cnt)) || (pPTree.IsParent(search_nodeId, vertex_cnt))){
				approverJsonObj.put("type", "selectedUser");
				node_highligt_map.put(vertex_cnt, 1);
			}	
			else {
				approverJsonObj.put("type", "unSelectedUser");
				node_highligt_map.put(vertex_cnt, 0);
			}	
			//add the Generator user node to vertex array
			verticesJsonArray.put(approverJsonObj);
			
			//add the Conncection between this glossary and concept
			final JSONObject approverconnectionJsonObj = new JSONObject();        	
			approverconnectionJsonObj.put("id", connection_cnt);
			approverconnectionJsonObj.put("from", glossary_vertex_cnt);
			approverconnectionJsonObj.put("to", vertex_cnt);
			if((node_highligt_map.get(vertex_cnt) == 1) && (node_highligt_map.get(glossary_vertex_cnt) == 1)){
				approverconnectionJsonObj.put("color", "rgb(255, 165, 0)");
			}else{
				approverconnectionJsonObj.put("color", "rgb(192,192,192)");				
			}
			
			connectionsJsonArray.put(approverconnectionJsonObj);
			
			//update connection cnt
			connection_cnt = connection_cnt + 1;									
			
		}		
		
		//Now Add the DB items
		//Add db list to JSON
		
		final JSONArray dbJsonArray = new JSONArray();
		for(int i = 0 ; i < pVDef.GetDBListSize() ; i++) {
			final JSONObject dbJsonObj = new JSONObject();
			
			//update vertex_cnt
			vertex_cnt = pVDef.GetDBName(i).hashCode();

			dbJsonObj.put("id", vertex_cnt);
			dbJsonObj.put("level", 5);
			dbJsonObj.put("name", pVDef.GetDBName(i));
			if((pPTree.IsChild(search_nodeId, vertex_cnt)) || (pPTree.IsParent(search_nodeId, vertex_cnt))){
				dbJsonObj.put("type", "selectedDb");
				node_highligt_map.put(vertex_cnt, 1);
			}	
			else {
				dbJsonObj.put("type", "unSelectedDb");
				node_highligt_map.put(vertex_cnt, 0);
			}	
			
			//add the Generator user node to vertex array
			verticesJsonArray.put(dbJsonObj);
			
			
			//add the Conncection between this glossary and concept
			//calcualte corresponding glossary item
			glossary_vertex_cnt = pVDef.GetGlossaryName(i).hashCode();
			final JSONObject connectionJsonObj = new JSONObject();  	
			connectionJsonObj.put("id", connection_cnt);
			connectionJsonObj.put("from", glossary_vertex_cnt);
			connectionJsonObj.put("to", vertex_cnt);
			if((node_highligt_map.get(vertex_cnt) == 1) && (node_highligt_map.get(glossary_vertex_cnt) == 1)){
				connectionJsonObj.put("color", "rgb(255, 165, 0)");
			}else{
				connectionJsonObj.put("color", "rgb(192,192,192)");				
			}
			
			connectionsJsonArray.put(connectionJsonObj);
			
			//update connection cnt
			connection_cnt = connection_cnt + 1;									
		}		
		
			
		
		//add vertices array to the final object
		ucFullJsonObj.put("vertices", verticesJsonArray);
		//add connection array to the final object
		ucFullJsonObj.put("connecions", connectionsJsonArray);
		

		//Add concept List to rule object
		//outputJson.put(ucFullJsonObj);
		
	   System.out.println(ucFullJsonObj.toString());
       return ucFullJsonObj.toString();		
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
		//ruleJsonObj.put("des", pVDef.GetRuleDef(0));
		//JSONObject newjson = new JSONObject(octopusSemanticService.getVertexPropertyKeyById(pVDef.GetRuleID(0)));
		//JSONObject merged = new JSONObject(ruleJsonObj, JSONObject.getNames(ruleJsonObj));
		//for(String key : JSONObject.getNames(newjson))
		//{
		//  merged.put(key, newjson.get(key));
		//}

		//ruleArray.put(merged);
		ruleArray.put(ruleJsonObj);
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
				conceptJsonObj.put("name", pVDef.GetConceptName(i));
			}
			
			conceptJsonObj.put("color", "green");
			conceptJsonArray.put(conceptJsonObj);
		}		
		conceptFullJsonObj.put("vertices", conceptJsonArray);
		//conceptFullJsonObj.put("connecions", new JSONArray());
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
	   System.out.println(outputJson.toString());
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
			//ruleJsonObj.put("des", pVDef.GetRuleDef(i));
			//ruleJsonObj.put("type", "rule");
			
			//JSONObject newjson = new JSONObject(octopusSemanticService.getVertexPropertyKeyById(pVDef.GetRuleID(i)));
			//JSONObject merged = new JSONObject(ruleJsonObj, JSONObject.getNames(ruleJsonObj));
			//for(String key : JSONObject.getNames(newjson))
			//{
			//  merged.put(key, newjson.get(key));
			//}
			ruleJsonArray.put(ruleJsonObj);
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
				conceptJsonObj.put("name", octopusSemanticService.getVertexNameById(pVDef.GetConceptID(i)));
			}else{
				conceptJsonObj.put("name", pVDef.GetConceptName(i));
			}
		
					
			conceptJsonObj.put("color", "green");
 			conceptJsonArray.put(conceptJsonObj);
		}		
		conceptFullJsonObj.put("vertices", conceptJsonArray);
		//conceptFullJsonObj.put("connecions", new JSONArray());
        
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
        lineagedata.putOpt("db", dbJsonArray);		

        System.out.println(lineagedata.toString());
		return lineagedata.toString();
   }
	
}
