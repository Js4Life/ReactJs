package com.parabole.feed.platform.lineage.paraboleHyperGraph;

import com.parabole.feed.platform.knowledge.KEdge;
import com.parabole.feed.platform.knowledge.KGraph;
import com.parabole.feed.platform.lineage.GlossarytoDBMapper.glossarytodb;
import com.parabole.feed.platform.lineage.GlossarytoDBMapper.glossarytodb_map;
import com.parabole.feed.platform.lineage.bankInfoReader.BankInfoReader;
import com.parabole.feed.platform.lineage.businessRuleReader.RuleDef;
import com.parabole.feed.platform.lineage.businessRuleReader.RuleReader;
import com.parabole.feed.platform.lineage.dataSchematoGraph.DataSchemaParser;
import com.parabole.feed.platform.lineage.paraboleHyperGraph.HyperGraph.Lineage;
import net.sf.jsqlparser.JSQLParserException;

import java.util.List;

public class HyperGraphUtil {
	
	private	HyperGraph	pHGraph;
	private BankInfoReader pBankInfo;
	private List<RuleDef>	pRuleList;
	private final KGraph pKGraph;
	//HyperGraphUtil constructor
	//Generates a Hypergraph from KGraph
	/*
	public		HyperGraphUtil (KGraph kgraph){
			int num_of_vertex = 5;
			int num_of_edge = 7;
			String	C[] = {"C1", "C2", "C3", "C4", "C5"};
			String	R[] = {"R1", "R2", "R3", "R4", "R5", "R6", "R7"};
			int Src[] = {0, 0, 1, 1, 2, 2, 4};
			int Dst[] = {1, 2, 3, 4, 3, 4, 3};
			
			
			this.pKGraph = kgraph;
			//Add Ontology layer in Hypergraph test START
			System.out.println("Add Ontology Layer in HG START");
			
			//Create Hypergraph class
			pHGraph = new HyperGraph(0, 5, 7);
			
			//Add concept nodes first
			for(int i = 0 ; i < num_of_vertex ; i++){
				pHGraph.addConceptNode(i, C[i]);				
			}
			
			//Now add the relation nodes
			for(int i = 0 ; i < num_of_edge ; i ++)
			{	
				pHGraph.addRelationNode(Src[i], Dst[i], i, R[i], 0.5);
			}
			
			//Print Ontology layer of HyperGraph
			pHGraph.PrintHGraph();
			
			//Add Ontology Layer in Hypergraph test END
			System.out.println("Add Ontology Layer in HG END");			
	}
	*/
	
	public		HyperGraphUtil (KGraph KGraph){
			
			this.pKGraph = KGraph;
			
			int num_of_vertex = pKGraph.getNumofVertices();
			int num_of_edge = pKGraph.getNumofEdges();
			
			//Add Ontology layer in Hypergraph test START
			System.out.println("Add Ontology Layer in HG START");
			
			//Create Hypergraph class
			pHGraph = new HyperGraph(0, num_of_vertex, num_of_edge);
			
			int currRelation = 0;
			
			//Add concept nodes first
			for(int i = 0 ; i < num_of_vertex ; i++){
				//Add each node to HG
				pHGraph.addConceptNode(i, String.valueOf(i), null);				
				//Get the Edge List for i-th node 
				for (KEdge edge : pKGraph.getEdges(i)) {
					
						pHGraph.addRelationNode(i, edge.getDestination(), currRelation++, null, edge.getWeight());	
				}
			}
					
			//Print Ontology layer of HyperGraph
			//pHGraph.PrintHGraph();
			
			//Add Ontology Layer in Hypergraph test END
			System.out.println("Add Ontology Layer in HG END");			
	}	
	
	public	void AddGlossarySetInHG (final String FILE_PATH){
		System.out.println("AddGlossarySetInHG");
		pBankInfo = new BankInfoReader();
		pHGraph.AddGlossarySetInHG(pBankInfo.getGlossaryListFromExcel(FILE_PATH));
	}
	
	
	public void CreateGraphFromDDL(final String statement){	
		System.out.println("CreateGraphFromDDL");
		//Create a DB parser object
		DataSchemaParser pDBParser = new DataSchemaParser();
		//Call API to convert statement to graph
		
		try{
			pDBParser.GenerateGraphFromSQL(statement);
		}
		catch (JSQLParserException e) {
			//Do Something
		}
	}
	
	public List<glossarytodb_map> ReadSpecificDBMapfromXLS(final String FILE_PATH, String DB_name, String DB_table, String DB_col){
		//Read specific Glossary elements to DB map structure
		//Read Glossary to DB map information
		glossarytodb pDBMapReader = new glossarytodb();
		
		List<glossarytodb_map> pGlsDB_map = pDBMapReader.getGlossarytodbSpecificmapFromExcel(FILE_PATH, DB_name, DB_table, DB_col);	
		System.out.println("pGlsDB_map " + pGlsDB_map);
		pHGraph.SetGlossaryDBMap(pGlsDB_map);
		return pGlsDB_map;
	}
	
	public List<glossarytodb_map> ReadDBMapfromXLS(final String FIlE_PATH){
		//Read Glossary to DB map information
		glossarytodb	pDBMapReader = new glossarytodb();
		List<glossarytodb_map> pGlsDB_map = pDBMapReader.getGlossarytodbmapFromExcel(FIlE_PATH);	
		pHGraph.SetGlossaryDBMap(pGlsDB_map);
		return pGlsDB_map;
	}
	
	public List<RuleDef>	ReadRulefromXLS(final String FILE_PATH){
		//Read Rule info and print it 
		RuleReader	pRuleReader = new RuleReader();
		pRuleList = pRuleReader.GenerateRule(pRuleReader.getRuleListFromExcel(FILE_PATH));
	
		return pRuleList;
	}
	
	
	public List<RuleDef>	ReadSpecificRulefromXLS(final String FILE_PATH, int ruleId){
		//Read Rule info and print it 
		RuleReader	pRuleReader = new RuleReader();
		pRuleList = pRuleReader.GenerateSpecificRule(pRuleReader.getRuleListFromExcel(FILE_PATH), ruleId);
	
		return pRuleList;
	}
	
	public VisualDef GetLineageGraph (List<RuleDef> pRule){
			VisualDef pVDef = new VisualDef();
			System.out.println("Size :::::::::::::::::::::::::::::"+pRule.size());
			for(int i = 0 ; i < pRule.size() ; i++)	{
				//System.out.println("I am called");
				RuleDef pRuleElem = pRule.get(i);
				
				Lineage pLineage = pHGraph.GenerateLineage(pRuleElem);
				//System.out.println("printing");
				
				//populate visualdef from Lineage structure
				pVDef.AddRuleSnippets(pLineage.Rule_ID, pLineage.Rule_name, pLineage.Rule_Des);
				System.out.println("pLineage.Concept_Set.size()" + pLineage.Concept_Set.size());
				for(int j = 0 ; j < pLineage.Concept_Set.size(); j++)
				{	
					System.out.println("AddConceptSnippets");
					pVDef.AddConceptSnippets(pLineage.Concept_Set.get(j).getConcept_ID(), pLineage.Concept_Set.get(j).getConcept_Name(), null);
				}
				System.out.println("pLineage.Glossary_Set.size()" + pLineage.Glossary_Set.size());
				for(int k = 0 ; k < pLineage.Glossary_Set.size(); k++)
				{					
					System.out.println("AddGlossarySnippets");
					pVDef.AddGlossarySnippets(pLineage.Glossary_Set.get(k).getGlossary_Layer(), pLineage.Glossary_Set.get(k).getGlossary_ID() , pLineage.Glossary_Set.get(k).getGlossary_Name(), pLineage.Glossary_Set.get(k).getGlossary_Des());
				}				
				System.out.println("pLineage.DB_Set.size()" + pLineage.DB_Set.size());
				for(int k = 0 ; k < pLineage.DB_Set.size(); k++)
				{					
					System.out.println("AddDBSnippets");
					pVDef.AddDBSnippets(pLineage.DB_Set.get(k).getDB_Name(), pLineage.DB_Set.get(k).getTable_Name(), pLineage.DB_Set.get(k).getCol_Name());
				}				
			}	
			System.out.println("returning");
			return pVDef;
	}

	
	
	public VisualDef GetLineageGraphbyDB (List<RuleDef> pRule){
			VisualDef pVDef = new VisualDef();
			System.out.println("Size :::::::::::::::::::::::::::::"+pRule.size());
			for(int i = 0 ; i < pRule.size() ; i++)	{
				System.out.println("I am called");
				RuleDef pRuleElem = pRule.get(i);
				
				Lineage pLineage = pHGraph.GenerateLineageByDB(pRuleElem);
				if(pLineage == null){continue;}
				System.out.println("printing");
				
				//populate visualdef from Lineage structure
				pVDef.AddRuleSnippets(pLineage.Rule_ID, pLineage.Rule_name, pLineage.Rule_Des);
				System.out.println("pLineage.Concept_Set.size()" + pLineage.Concept_Set.size());
				for(int j = 0 ; j < pLineage.Concept_Set.size(); j++)
				{	
					System.out.println("AddConceptSnippets");
					pVDef.AddConceptSnippets(pLineage.Concept_Set.get(j).getConcept_ID(), pLineage.Concept_Set.get(j).getConcept_Name(), null);
				}
				System.out.println("pLineage.Glossary_Set.size()" + pLineage.Glossary_Set.size());
				for(int k = 0 ; k < pLineage.Glossary_Set.size(); k++)
				{					
					System.out.println("AddGlossarySnippets");
					pVDef.AddGlossarySnippets(pLineage.Glossary_Set.get(k).getGlossary_Layer(), pLineage.Glossary_Set.get(k).getGlossary_ID() , pLineage.Glossary_Set.get(k).getGlossary_Name(), pLineage.Glossary_Set.get(k).getGlossary_Des());
				}				
				System.out.println("pLineage.DB_Set.size()" + pLineage.DB_Set.size());
				for(int k = 0 ; k < pLineage.DB_Set.size(); k++)
				{					
					System.out.println("AddDBSnippets");
					pVDef.AddDBSnippets(pLineage.DB_Set.get(k).getDB_Name(), pLineage.DB_Set.get(k).getTable_Name(), pLineage.DB_Set.get(k).getCol_Name());
				}				
			}	
			System.out.println("returning");
			return pVDef;
	}
	
	public VisualDef GetLineageGraphbyConcept (List<RuleDef> pRule, int ConceptID){
			VisualDef pVDef = new VisualDef();
			System.out.println("Size :::::::::::::::::::::::::::::"+pRule.size());
			for(int i = 0 ; i < pRule.size() ; i++)	{
				//System.out.println("I am called");
				RuleDef pRuleElem = pRule.get(i);
				
				Lineage pLineage = pHGraph.GenerateLineageByConcept(pRuleElem, ConceptID);
				if(pLineage == null){continue;}
							
				//populate visualdef from Lineage structure
				pVDef.AddRuleSnippets(pLineage.Rule_ID, pLineage.Rule_name, pLineage.Rule_Des);
				//System.out.println("pLineage.Concept_Set.size()" + pLineage.Concept_Set.size());
				for(int j = 0 ; j < pLineage.Concept_Set.size(); j++)
				{	
					System.out.println("AddConceptSnippets");
					pVDef.AddConceptSnippets(pLineage.Concept_Set.get(j).getConcept_ID(), pLineage.Concept_Set.get(j).getConcept_Name(), null);
				}
				System.out.println("pLineage.Glossary_Set.size()" + pLineage.Glossary_Set.size());
				for(int k = 0 ; k < pLineage.Glossary_Set.size(); k++)
				{					
					System.out.println("AddGlossarySnippets");
					pVDef.AddGlossarySnippets(pLineage.Glossary_Set.get(k).getGlossary_Layer(), pLineage.Glossary_Set.get(k).getGlossary_ID() , pLineage.Glossary_Set.get(k).getGlossary_Name(), pLineage.Glossary_Set.get(k).getGlossary_Des());
				}				
				System.out.println("pLineage.DB_Set.size()" + pLineage.DB_Set.size());
				for(int k = 0 ; k < pLineage.DB_Set.size(); k++)
				{					
					System.out.println("AddDBSnippets");
					pVDef.AddDBSnippets(pLineage.DB_Set.get(k).getDB_Name(), pLineage.DB_Set.get(k).getTable_Name(), pLineage.DB_Set.get(k).getCol_Name());
				}				
			}	
			System.out.println("returning");
			return pVDef;
	}
	
	public VisualDef GetLineageGraphbyGlossary (List<RuleDef> pRule, int GlossaryID, int LayerID){
			VisualDef pVDef = new VisualDef();
			System.out.println("Size :::::::::::::::::::::::::::::"+pRule.size());
			for(int i = 0 ; i < pRule.size() ; i++)	{
				//System.out.println("I am called");
				RuleDef pRuleElem = pRule.get(i);
				
				Lineage pLineage = pHGraph.GenerateLineageByGlossary(pRuleElem, GlossaryID, LayerID);
				if(pLineage == null){continue;}
							
				//populate visualdef from Lineage structure
				pVDef.AddRuleSnippets(pLineage.Rule_ID, pLineage.Rule_name, pLineage.Rule_Des);
				//System.out.println("pLineage.Concept_Set.size()" + pLineage.Concept_Set.size());
				for(int j = 0 ; j < pLineage.Concept_Set.size(); j++)
				{	
					System.out.println("AddConceptSnippets");
					pVDef.AddConceptSnippets(pLineage.Concept_Set.get(j).getConcept_ID(), pLineage.Concept_Set.get(j).getConcept_Name(), null);
				}
				System.out.println("pLineage.Glossary_Set.size()" + pLineage.Glossary_Set.size());
				for(int k = 0 ; k < pLineage.Glossary_Set.size(); k++)
				{					
					System.out.println("AddGlossarySnippets");
					pVDef.AddGlossarySnippets(pLineage.Glossary_Set.get(k).getGlossary_Layer(), pLineage.Glossary_Set.get(k).getGlossary_ID() , pLineage.Glossary_Set.get(k).getGlossary_Name(), pLineage.Glossary_Set.get(k).getGlossary_Des());
				}				
				System.out.println("pLineage.DB_Set.size()" + pLineage.DB_Set.size());
				for(int k = 0 ; k < pLineage.DB_Set.size(); k++)
				{					
					System.out.println("AddDBSnippets");
					pVDef.AddDBSnippets(pLineage.DB_Set.get(k).getDB_Name(), pLineage.DB_Set.get(k).getTable_Name(), pLineage.DB_Set.get(k).getCol_Name());
				}				
			}	
			System.out.println("returning");
			return pVDef;
	}
	
}

