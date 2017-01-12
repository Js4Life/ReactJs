package com.parabole.feed.platform.lineage.paraboleHyperGraph;

import com.parabole.feed.platform.lineage.GlossarytoDBMapper.glossarytodb_map;
import com.parabole.feed.platform.lineage.bankInfoReader.BankGlossaryElementMap;
import com.parabole.feed.platform.lineage.businessRuleReader.RuleDef;
import com.parabole.feed.platform.lineage.paraboleGraph.DGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


//HyperGraph class
public class HyperGraph {
		private int						num_of_layer; //number of layer/set in the HyperGraph
		private int						num_of_concept_node;	//Layer 0 nodes
		private int						num_of_concept_edge;	//Layer 0 edges
		private	int						constructed_layer;      //Stores the highest layer already constructed
		private List<DGraph>			DGraph_list; //DGraph list
		private	List<EquiConceptLine_element>[]	EquiConcept_list; //Equi-concept line list
		private List<EquiRelationLine_element>[] EquiRelation_list; //Equi-relation line list
		private List<glossarytodb_map>  pGlossaryDB_Map;
		
		//Define HyperGraph constructor
		public	HyperGraph(int LayerNum, int conceptNum, int relationNum){
			//initialize class members
			num_of_layer = LayerNum;
			num_of_concept_node = conceptNum;
			num_of_concept_edge = relationNum;
			constructed_layer = 0;
			
			//create memory for Graph layers
			DGraph_list = (List<DGraph>) new LinkedList<DGraph>();
			
			//create memory for GlossarytoDB Map array
			pGlossaryDB_Map = (List<glossarytodb_map>) new LinkedList<glossarytodb_map>();
		
			//create the first graph layer (concept layer) and add it to DGraph_list
			//this is a bi-partide graph. All concepts become one part and
			//all relations become another part. 
			//number of vertices = concept + relation
			//number of edges = 2 * relation
			DGraph pDGgraph = new DGraph((num_of_concept_node+num_of_concept_edge), 2 * num_of_concept_edge);
			
			//Now add it to the DGraph_list
			DGraph_list.add(pDGgraph);
		
			//Create memory for EquiConcept_list
			EquiConcept_list = (List<EquiConceptLine_element>[]) new List[num_of_concept_node];
			for(int i = 0; i < num_of_concept_node ; i++) {
				EquiConcept_list[i] = (List<EquiConceptLine_element>) new LinkedList<EquiConceptLine_element>();
			}
			
			//Create memory for EquiRelation_list
			EquiRelation_list = (List<EquiRelationLine_element>[]) new List[num_of_concept_edge];
			for(int i = 0; i < num_of_concept_edge ; i++) {
				EquiRelation_list[i] = (List<EquiRelationLine_element>) new LinkedList<EquiRelationLine_element>();
			}
		}
		
		//utility function to add concept 
		public void addConceptNode(int node_id, String node_name, String node_desc){
			//Add this node to the layer 0
			DGraph_list.get(0).addNode(node_id, node_name, node_desc);

			//update EquiConceptLine 
			//Create an EquiConceptLine_element for the current concept node
			EquiConceptLine_element pConceptElement = new EquiConceptLine_element(0, node_id, node_name, node_desc);
			//Add this element to the line
			EquiConcept_list[node_id].add(pConceptElement);
		}
		
		//utility function to add relation node
		public void addRelationNode(int src_id, int dest_id, int relation_id, String relation_name, double weight){
			//Add the relation as the relation node
			//It will be added after all concepts are added
			DGraph_list.get(0).addNode((num_of_concept_node + relation_id), relation_name, null);
			//Now add 2 edges
			//First src_id to relation_id
			DGraph_list.get(0).addEdge(src_id, (relation_id + num_of_concept_node), (2*weight));
			//Second relation_id to dest_id
			DGraph_list.get(0).addEdge((relation_id + num_of_concept_node), dest_id, (2*weight));
			
			//update EquiRelationLine 
			//Create an EquiRelationLine_element for the current relation node
			EquiRelationLine_element pRelationElement = new EquiRelationLine_element(0, relation_id, relation_name);
			//Add this element to the line
			EquiRelation_list[relation_id].add(pRelationElement);
		}
		
		public void AddGlossarySetInHG(List<BankGlossaryElementMap> glossary_list){
			//Scan through the glossary_list and add layers as needed
			//Read the glossary list within the loop
			for(int i = 0; i < glossary_list.size() ; i++){
				System.out.println("glossary_list.get(i).getGlossary_Set() " + glossary_list.get(i).getGlossary_Set());
				if(glossary_list.get(i).getGlossary_Set() > constructed_layer){
					//A new glossary set has been reached
					//create the next graph layer (concept layer) and add it to DGraph_list
					//this is also a bipartide graph. All concepts become one part and
					//all relations become another part. 
					//number of vertices = concept + relation
					//number of edges = 2 * relation
					System.out.println("Graph layer constructured " +  constructed_layer);
					DGraph pDGgraph = new DGraph((num_of_concept_node+num_of_concept_edge), 2 * num_of_concept_edge);
					
					//Now add this layer to the DGraph_list
					DGraph_list.add(pDGgraph);
					
					//increment the constructed_layer
					constructed_layer++;
				}
				// Add the new glossary element to the latest constructed layer
				DGraph_list.get(constructed_layer).addNode(glossary_list.get(i).getGlossary_ID(), glossary_list.get(i).getGlossary_Name(),
														   glossary_list.get(i).getGlossary_Des());
				//update the EquiConceptLine
				//Create an EquiConceptLine_element for the current concept node
				EquiConceptLine_element pConceptElement = new EquiConceptLine_element(glossary_list.get(i).getGlossary_Set(), glossary_list.get(i).getGlossary_ID(), glossary_list.get(i).getGlossary_Name(),
																					  glossary_list.get(i).getGlossary_Des());
				//Add this element to the line
				EquiConcept_list[glossary_list.get(i).getConcept_ID()].add(pConceptElement);
			}
		}
		
		public void SetGlossaryDBMap (List<glossarytodb_map>  pGlsDB_Map) {
			//this.pGlossaryDB_Map = pGlsDB_Map;
			System.out.println("+++++++++++++++++ SetGlossaryDBMap ++++++++++++++++++");
			
			for(glossarytodb_map	pGlstoDB_elem : pGlsDB_Map){
				pGlossaryDB_Map.add(pGlstoDB_elem);
			}
		}
		
		//class EquiConceptLine_element
		public class EquiConceptLine_element {
			private int		layer_id;
			private int		concept_id;
			private String	concept_name;
			private String	Desc;
			
			//define class constructor
			public EquiConceptLine_element(int layer_ID, int concept_ID, String concept_NAME, String desc){
				layer_id = layer_ID;
				concept_id = concept_ID;
				concept_name = concept_NAME;
				Desc = desc;
			}
			public int getlayer_id(){
				return layer_id;
			}
			public int getconcept_id(){
				return concept_id;
			}
			public String getconcept_name() {
				return concept_name;
			}
			public String get_Desc() {
				return	Desc;
			}
		}
		
		//class EquiRelationLine_element
		public class EquiRelationLine_element {
			private int		layer_id;
			private int		relation_id;
			private String	relation_name;	
			
			//define class constructor
			public EquiRelationLine_element(int layer_ID, int relation_ID, String relation_NAME){
				layer_id = layer_ID;
				relation_id = relation_ID;
				relation_name = relation_NAME;
			}
		}
		
		//utility class for HyperGraph printing
		public void PrintHGraph () {
			System.out.println("Print Hyper Graph");
			//Print Graph at Layer 0
			System.out.println("Print Graph at Layer 0");
			//Print Graph
			DGraph_list.get(0).PrintGraph();
			
		}
		
		public Lineage	GenerateLineage (RuleDef rule) {
			Lineage 	pLineage = new Lineage();
			int[]		ConceptList;
			//Load parameters from rule to Lineage
			pLineage.Rule_ID = rule.getRule_ID();
			pLineage.Rule_name = rule.getRule_Name();
			pLineage.Rule_Des = rule.getRule_def();
			ConceptList = rule.getConcept_ID();
			System.out.println("rule.getConcept_length " + rule.getConcept_length());
			for(int i = 0 ; i < rule.getConcept_length() ; i++) {
				Concept_Elem	pConcept = new Concept_Elem();
				pConcept.Concept_ID = ConceptList[i];
				pConcept.Concept_Name = EquiConcept_list[ConceptList[i]].get(0).concept_name;
				pConcept.Concept_Des = null;
				pLineage.Concept_Set.add(pConcept);
				
				//Check if the corresponding EquiConceptLine 
				System.out.println("EquiConcept_list[ConceptList[i]].size() " + EquiConcept_list[ConceptList[i]].size());
				for(int k = 1 ; k < EquiConcept_list[ConceptList[i]].size(); k++) {
					EquiConceptLine_element	pGlossaryElem = EquiConcept_list[ConceptList[i]].get(k);
					Glossary_Elem	pGlossary = new Glossary_Elem();
					pGlossary.Glossary_ID = pGlossaryElem.concept_id;
					pGlossary.Glossary_Layer = pGlossaryElem.layer_id;
					pGlossary.Glossary_Name = pGlossaryElem.concept_name;
					pGlossary.Glossary_Des = pGlossaryElem.Desc;
					pLineage.Glossary_Set.add(pGlossary);
					//System.out.println("pGlstoDB_elem.size() " + pGlossaryDB_Map.size());
					//Now check for these glossary items which are the connected DB items
					for(glossarytodb_map   pGlstoDB_elem : pGlossaryDB_Map){
						//System.out.println("pGlstoDB_elem.getGlossary_Set() ");
						//System.out.println("pGlstoDB_elem.getGlossary_Set() " + pGlstoDB_elem.getGlossary_Set() );
						if((pGlstoDB_elem.getGlossary_Set() == pGlossary.Glossary_Layer) && 
							(pGlstoDB_elem.getGlossary_ID()  == pGlossary.Glossary_ID)){
								DB_Elem		pDB_elem = new DB_Elem();
								pDB_elem.Glossary_set = pGlstoDB_elem.getGlossary_Set();
								pDB_elem.Glossary_ID = pGlstoDB_elem.getGlossary_ID();
								pDB_elem.DB_Name = pGlstoDB_elem.getDatabase_name();
								pDB_elem.Table_Name = pGlstoDB_elem.getDatabase_table();
								pDB_elem.Table_Col = pGlstoDB_elem.getDatabase_col();
								pLineage.DB_Set.add(pDB_elem);
							}
					}
				}
			}			
			
			return pLineage;
		}
		
		public Lineage	GenerateLineageByConcept (RuleDef rule, int Concept_Id) {
			Lineage 	pLineage = new Lineage();
			int[]		ConceptList;
			int			Concept_Occur = 0;
			ConceptList = rule.getConcept_ID();

			for(int i = 0 ; i < rule.getConcept_length() ; i++) { 
				if(ConceptList[i] == Concept_Id){	
				Concept_Occur++; 
				break;
			}
			}
			if(Concept_Occur == 0) {return null;}
			//Load parameters from rule to Lineage
			pLineage.Rule_ID = rule.getRule_ID();
			pLineage.Rule_name = rule.getRule_Name();
			pLineage.Rule_Des = rule.getRule_def();
			System.out.println("rule.getConcept_length " + rule.getConcept_length());
			for(int i = 0 ; i < rule.getConcept_length() ; i++) {
				if(ConceptList[i] == Concept_Id){
				Concept_Elem	pConcept = new Concept_Elem();
				pConcept.Concept_ID = ConceptList[i];
				pConcept.Concept_Name = EquiConcept_list[ConceptList[i]].get(0).concept_name;
				//pConcept.Glossary_Des = pGlossaryElem.Glossary_Des;
				pLineage.Concept_Set.add(pConcept);
				
				//Check if the corresponding EquiConceptLine 
				System.out.println("EquiConcept_list[ConceptList[i]].size() " + EquiConcept_list[ConceptList[i]].size());
				for(int k = 1 ; k < EquiConcept_list[ConceptList[i]].size(); k++) {
					EquiConceptLine_element	pGlossaryElem = EquiConcept_list[ConceptList[i]].get(k);
					Glossary_Elem	pGlossary = new Glossary_Elem();
					pGlossary.Glossary_ID = pGlossaryElem.concept_id;
					pGlossary.Glossary_Layer = pGlossaryElem.layer_id;
					pGlossary.Glossary_Name = pGlossaryElem.concept_name;
					pGlossary.Glossary_Des = pGlossaryElem.Desc;
					pLineage.Glossary_Set.add(pGlossary);
					//Now check for these glossary items which are the connected DB items
					for(glossarytodb_map   pGlstoDB_elem : pGlossaryDB_Map){
						//System.out.println("pGlstoDB_elem.getGlossary_Set() ");
						System.out.println("pGlstoDB_elem.getGlossary_Set() " + pGlstoDB_elem.getGlossary_Set() );
						if((pGlstoDB_elem.getGlossary_Set() == pGlossary.Glossary_Layer) && 
							(pGlstoDB_elem.getGlossary_ID()  == pGlossary.Glossary_ID)){
								DB_Elem		pDB_elem = new DB_Elem();
								pDB_elem.Glossary_set = pGlstoDB_elem.getGlossary_Set();
								pDB_elem.Glossary_ID = pGlstoDB_elem.getGlossary_ID();
								pDB_elem.DB_Name = pGlstoDB_elem.getDatabase_name();
								pDB_elem.Table_Name = pGlstoDB_elem.getDatabase_table();
								pDB_elem.Table_Col = pGlstoDB_elem.getDatabase_col();
								pLineage.DB_Set.add(pDB_elem);
							}
						}
					}
				}	
			}			
			
			return pLineage;
		}
		
		public Lineage	GenerateLineageByGlossary (RuleDef rule, int Glossary_Id, int Layer_Id) {
			Lineage 	pLineage = new Lineage();
			int[]		ConceptList;
			int			Glossary_Occur = 0;
			ConceptList = rule.getConcept_ID();


			for(int i = 0 ; i < rule.getConcept_length() ; i++) {
				Concept_Elem	pConcept = new Concept_Elem();
				EquiConceptLine_element	pGlossaryElem = EquiConcept_list[ConceptList[i]].get(Layer_Id);
				if(pGlossaryElem.concept_id == Glossary_Id){
					//Load parameters from rule to Lineage
					pLineage.Rule_ID = rule.getRule_ID();
					pLineage.Rule_name = rule.getRule_Name();
					pLineage.Rule_Des = rule.getRule_def();
	
					pConcept.Concept_ID = ConceptList[i];
					pConcept.Concept_Name = EquiConcept_list[ConceptList[i]].get(0).concept_name;
					//pGlossary.Glossary_Des = pGlossaryElem.Glossary_Des;
					pLineage.Concept_Set.add(pConcept);
					
					Glossary_Elem	pGlossary = new Glossary_Elem();
					pGlossary.Glossary_ID = pGlossaryElem.concept_id;
					pGlossary.Glossary_Layer = pGlossaryElem.layer_id;
					pGlossary.Glossary_Name = pGlossaryElem.concept_name;
					pGlossary.Glossary_Des = pGlossaryElem.Desc;
					pLineage.Glossary_Set.add(pGlossary);
	
					//Now check for this glossary item which are the connected DB items
					for(glossarytodb_map   pGlstoDB_elem : pGlossaryDB_Map){
						//System.out.println("pGlstoDB_elem.getGlossary_Set() ");
						//System.out.println("pGlstoDB_elem.getGlossary_Set() " + pGlstoDB_elem.getGlossary_Set() );
						if((pGlstoDB_elem.getGlossary_Set() == pGlossary.Glossary_Layer) && 
							(pGlstoDB_elem.getGlossary_ID()  == pGlossary.Glossary_ID)){
								DB_Elem		pDB_elem = new DB_Elem();
								pDB_elem.Glossary_set = pGlstoDB_elem.getGlossary_Set();
								pDB_elem.Glossary_ID = pGlstoDB_elem.getGlossary_ID();
								pDB_elem.DB_Name = pGlstoDB_elem.getDatabase_name();
								pDB_elem.Table_Name = pGlstoDB_elem.getDatabase_table();
								pDB_elem.Table_Col = pGlstoDB_elem.getDatabase_col();
								pLineage.DB_Set.add(pDB_elem);
							}
						}
						Glossary_Occur++;
				}
			}			
			
			if(Glossary_Occur == 0){return null;}
			return pLineage;
		}
		
		
		public Lineage	GenerateLineageByDB (RuleDef rule) {
			Lineage 	pLineage = new Lineage();
			int[]		ConceptList;
			int			Glossary_Occur = 0;
			int			Layer_Occur = 0;
			ConceptList = rule.getConcept_ID();


			for(glossarytodb_map   pGlstoDB_elem : pGlossaryDB_Map){
				System.out.println("Inside Glossary to DB map loop");
				System.out.println("rule.getConcept_length() " + rule.getConcept_length());
				for(int i = 0 ; i < rule.getConcept_length() ; i++) {
					Concept_Elem	pConcept = new Concept_Elem();
					Layer_Occur = 0;
					for(EquiConceptLine_element pGlossaryElem: EquiConcept_list[ConceptList[i]]){
						if(pGlossaryElem.getlayer_id() == pGlstoDB_elem.getGlossary_Set()){
								System.out.println("Got the layer");
								System.out.println("pGlossaryElem.getconcept_id() " + pGlossaryElem.getconcept_id());
								System.out.println("pGlstoDB_elem.getGlossary_ID() " + pGlstoDB_elem.getGlossary_ID());
								
								if(pGlossaryElem.getconcept_id() == pGlstoDB_elem.getGlossary_ID()){
									//Load parameters from rule to Lineage
									pLineage.Rule_ID = rule.getRule_ID();
									pLineage.Rule_name = rule.getRule_Name();
									pLineage.Rule_Des = rule.getRule_def();
					
									pConcept.Concept_ID = ConceptList[i];
									pConcept.Concept_Name = EquiConcept_list[ConceptList[i]].get(0).concept_name;
									pConcept.Concept_Des = null;
									pLineage.Concept_Set.add(pConcept);
									
									Glossary_Elem	pGlossary = new Glossary_Elem();
									pGlossary.Glossary_ID = pGlossaryElem.getconcept_id();
									pGlossary.Glossary_Layer = pGlossaryElem.getlayer_id();
									pGlossary.Glossary_Name = pGlossaryElem.getconcept_name();
									pGlossary.Glossary_Des = pGlossaryElem.get_Desc();
									pLineage.Glossary_Set.add(pGlossary);
					
									DB_Elem		pDB_elem = new DB_Elem();
									pDB_elem.Glossary_set = pGlstoDB_elem.getGlossary_Set();
									pDB_elem.Glossary_ID = pGlstoDB_elem.getGlossary_ID();
									pDB_elem.DB_Name = pGlstoDB_elem.getDatabase_name();
									pDB_elem.Table_Name = pGlstoDB_elem.getDatabase_table();
									pDB_elem.Table_Col = pGlstoDB_elem.getDatabase_col();
									pLineage.DB_Set.add(pDB_elem);
									
									Glossary_Occur++;
								}
						}
					}
				}			
			}		
			if(Glossary_Occur == 0){return null;}
			return pLineage;
		}
		
		public void PrintLineage (Lineage		pLineage) {
			System.out.println("Rule Number " + pLineage.Rule_ID);
			System.out.println("Rule name " + pLineage.Rule_name);
			System.out.println("Rule Description"  + pLineage.Rule_Des);
			for(int i = 0 ; i < pLineage.Concept_Set.size(); i++)
			{
				System.out.println("Concept " + pLineage.Concept_Set.get(i).Concept_ID);
			}
			for(int i = 0 ; i < pLineage.Glossary_Set.size(); i++)
			{
				System.out.println("Glossary Layer " + pLineage.Glossary_Set.get(i).Glossary_Layer);
				System.out.println("Glossary ID " + pLineage.Glossary_Set.get(i).Glossary_ID);
			}
		}
		
		public class	Lineage {
			public int			Rule_ID;
			public	String		Rule_name;
			public	String		Rule_Des;
			public  List<Concept_Elem>	Concept_Set;
			public  List<Glossary_Elem>	Glossary_Set;
			public  List<DB_Elem>		DB_Set;
			
			public Lineage() 
			{
				Concept_Set = (List<Concept_Elem>) new ArrayList<Concept_Elem>();
				Glossary_Set = (List<Glossary_Elem>) new ArrayList<Glossary_Elem>();
				DB_Set = (List<DB_Elem>) new ArrayList<DB_Elem>();
			}			
		}
		
		public class DB_Elem {
			public String getTable_Name() {
				return Table_Name;
			}
			public void setTable_Name(String table_Name) {
				this.Table_Name = table_Name;
			}
			public String getCol_Name() {
				return Table_Col;
			}
			public void setCol_Name(String table_Col) {
				this.Table_Col = table_Col;
			}
			public String getDB_Name() {
				return DB_Name;
			}
			public void setDB_Name (String db_Name){
				this.DB_Name = db_Name;
			}
			public int getGlossary_ID() {
				return Glossary_ID;
			}
			public void setGlossary_ID (int id){
				this.Glossary_ID = id;
			}
			public int getGlossary_set() {
				return Glossary_set;
			}
			public void setGlossary_set (int set){
				this.Glossary_set = set;
			}
			private int		Glossary_set;
			private int		Glossary_ID;
			private	String	Table_Name;
			private	String	Table_Col;
			private String  DB_Name;
		}
		
		public class Glossary_Elem {
			public String getGlossary_Name() {
				return Glossary_Name;
			}
			public void setGlossary_Name(String glossary_Name) {
				Glossary_Name = glossary_Name;
			}
			public String getGlossary_Des() {
				return Glossary_Des;
			}
			public void setGlossary_Des(String glossary_Des) {
				Glossary_Des = glossary_Des;
			}
			public int getGlossary_Layer() {
				return Glossary_Layer;
			}
			public void setGlossary_Layer(int glossary_Layer) {
				Glossary_Layer = glossary_Layer;
			}
			public int getGlossary_ID() {
				return Glossary_ID;
			}
			public void setGlossary_ID(int glossary_ID) {
				Glossary_ID = glossary_ID;
			}
			private	int		Glossary_Layer;
			private	int		Glossary_ID;
			private	String	Glossary_Name;
			private	String	Glossary_Des;
		}
		
		public class Concept_Elem {
			public String getConcept_Des() {
				return Concept_Des;
			}
			public void setConcept_Des(String concept_Des) {
				Concept_Des = concept_Des;
			}
			public int getConcept_ID() {
				return Concept_ID;
			}
			public void setConcept_ID(int concept_ID) {
				Concept_ID = concept_ID;
			}
			public String getConcept_Name() {
				return Concept_Name;
			}
			public void setConcept_Name(String concept_Name) {
				Concept_Name = concept_Name;
			}
			private	int		Concept_ID;
			private String	Concept_Name;
			private String	Concept_Des;
		}
		
}

