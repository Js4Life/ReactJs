package com.parabole.rda.platform.lineage.paraboleHyperGraph;

import java.util.ArrayList;
import java.util.List;

public class VisualDef {
	private	List<RuleSnippets>			lRule;
	private List<UCSnippets>			lUC;
	private List<GlossarySnippets>		lGlossary;
	private List<ConceptSnippets>		lConcept;
	private	List<DBSnippets>			lDB;
	
	//define the constructor
	public	VisualDef() {
		lRule = (List<RuleSnippets>)	new		ArrayList<RuleSnippets>();
		lGlossary = (List<GlossarySnippets>)	new		ArrayList<GlossarySnippets>();
		lConcept = (List<ConceptSnippets>)		new		ArrayList<ConceptSnippets>();
		lDB = (List<DBSnippets>)	new		ArrayList<DBSnippets>();
		lUC = (List<UCSnippets>) new ArrayList<UCSnippets>();
	}
	
	//Utilities for Rule Snippets
	public void	AddRuleSnippets (int ID, String Name, String Def)
	{
		RuleSnippets  newRule = new RuleSnippets();
		newRule.ID = ID;
		newRule.Name = Name;
		newRule.Def = Def;
		
		//Add this new Rule Snippets in the lRule
		lRule.add(newRule);
	}
	
	public int	GetRuleID (int index){
		return(lRule.get(index).ID);
	}

	public String GetRuleName (int index){
		System.out.println("Rule name ********* " + lRule.get(index).Name);
		return(lRule.get(index).Name);
	}
	
	public String GetRuleDef (int index){
		return(lRule.get(index).Def);
	}
	
	public	int	GetRuleListSize () {
		return(lRule.size());
	}
	
	//Utilities for UC Snippets
	public void	AddUCSnippets (int ID, String Name, String Def)
	{
		UCSnippets  newUC = new UCSnippets();
		newUC.ID = ID;
		newUC.Name = Name;
		newUC.Def = Def;
		
		//Add this new Rule Snippets in the lRule
		lUC.add(newUC);
	}

	//Utilities for Glossary Snippets AddGlossarySnippet
	public void AddGlossarySnippets (int Layer, int ID, String Name, String Def){		
		//Add the new Glossary to Glossary list only if it doesn't exist
		for(GlossarySnippets	pGlossary : lGlossary){
			if(pGlossary.ID == ID && pGlossary.Layer == Layer){ System.out.println("******* Glossary Snippets Already exists ********"); return;}
		}
		
		GlossarySnippets	newGlossary = new GlossarySnippets();
		
		newGlossary.Layer = Layer;
		newGlossary.ID = ID;
		newGlossary.Name = Name;
		newGlossary.Def = Def;

		/*
		if(lGlossary.contains(newGlossary) == true){
			System.out.println("*************** Glossary Snippets Already exists ***************");
		}
		else {
			lGlossary.add(newGlossary);
		}*/
		lGlossary.add(newGlossary);
	}
	
	//Utilities for Glossary Snippets AddGlossarySnippetwithOwner
	public void AddGlossarySnippetswithOwner (int Layer, int ID, String Name, String Def, int concept_id, String Generator, String Reviewer, String Approver){		
		//Add the new Glossary to Glossary list only if it doesn't exist
		for(GlossarySnippets	pGlossary : lGlossary){
			if(pGlossary.ID == ID && pGlossary.Layer == Layer){ System.out.println("******* Glossary Snippets Already exists ********"); return;}
		}
		
		GlossarySnippets	newGlossary = new GlossarySnippets();
		
		newGlossary.Layer = Layer;
		newGlossary.ID = ID;
		newGlossary.Name = Name;
		newGlossary.Def = Def;
		newGlossary.Concept_ID = concept_id;
		newGlossary.Generator = Generator;
		newGlossary.Reviewer = Reviewer;
		newGlossary.Approver = Approver;

		/*
		if(lGlossary.contains(newGlossary) == true){
			System.out.println("*************** Glossary Snippets Already exists ***************");
		}
		else {
			lGlossary.add(newGlossary);
		}*/
		lGlossary.add(newGlossary);
	}

	public int	GetGlossarySet (int index){
		return(lGlossary.get(index).Layer);
	}
	
	public int	GetGlossaryID (int index){
		return(lGlossary.get(index).ID);
	}
	
	public String	GetGlossaryName (int index){
		return(lGlossary.get(index).Name);
	}
	
	public String	GetGlossaryDef (int index){
		return(lGlossary.get(index).Def);
	}
	
	public int		GetGlossaryConcept_ID (int index){
		return(lGlossary.get(index).Concept_ID);
	}
	
	public String	GetGlossaryGenerator (int index){
		return(lGlossary.get(index).Generator);
	}

	public String	GetGlossaryReviewer (int index){
		return(lGlossary.get(index).Reviewer);
	}

	public String	GetGlossaryApprover (int index){
		return(lGlossary.get(index).Approver);
	}

	public	int		GetGlossaryListSize () {
		return(lGlossary.size());
	}
	
	//Utilities for Concept Snippets
	public void AddConceptSnippets (int ID, String Name, String Def){
		//Add the new Concept snippets to concept list only if it doesn't exist
		for(ConceptSnippets	pConcept : lConcept){
			if(pConcept.ID == ID){ System.out.println("******* Concept Snippets Already exists ********"); return;}
		}
		ConceptSnippets newConcept = new ConceptSnippets();
		newConcept.ID = ID;
		newConcept.Name = Name;
		newConcept.Def = Def;
		
		lConcept.add(newConcept);
	}
	
	public	int	GetConceptID (int index){
		return (lConcept.get(index).ID);
	}
	
	public String	GetConceptName (int index){
		return (lConcept.get(index).Name);
	}
	
	public	String	GetConceptDef (int index){
		return (lConcept.get(index).Def);
	}
	
	public	int		GetConcpetListSize() {
		return(lConcept.size());
	}
	
	//Utilities for DB snippets 
	public void	AddDBSnippets (String DB_Name, String DB_Table, String DB_Col, int Glossary_Set, int Glossary_ID){
		//Add the new DB snippet to DB list only if it doesn't exist
		for(DBSnippets	pDB : lDB){
			if(pDB.DB_Name == DB_Name && pDB.DB_Table == DB_Table  && pDB.Column == DB_Col){ System.out.println("******* DB Snippets Already exists ********"); return;}
		}
		DBSnippets	newDB = new DBSnippets();
		newDB.DB_Name = DB_Name;
		newDB.DB_Table = DB_Table;
		newDB.Column = DB_Col;
		newDB.Glossary_Set = Glossary_Set;
		newDB.Glossary_ID = Glossary_ID;
	
		lDB.add(newDB);
	}
	
	public String	GetDBName (int index){
		return (lDB.get(index).DB_Name);
	}
	
	public String	GetDBTable (int index){
		return (lDB.get(index).DB_Table);
	}
	
	public String	GetDBColumn (int index){
		return (lDB.get(index).Column);
	}
	
	public int   GetMappedGlossary_Set(int index){
		return (lDB.get(index).Glossary_Set);
	}
	
	public int   GetMappedGlossary_ID(int index){
		return (lDB.get(index).Glossary_ID);
	}

	public	int		GetDBListSize() {
		return (lDB.size());
	}
	
	
	
	//Class definition
	class	RuleSnippets {
		private	int		ID;
		private	String	Name;
		private String	Def;
	}
	
	class  UCSnippets {
		private int 	ID;
		private String	Name;
		private String  Def;
	}
	
	class 	GlossarySnippets {
		private	int		Layer;
		private	int		ID;
		private	String	Name;
		private	String	Def;
		private int		Concept_ID;
		private String  Generator;
		private String  Reviewer;
		private String  Approver;
	}

	class 	ConceptSnippets {
		private	int		ID;
		private	String	Name;
		private	String	Def;
	}
	
	class 	DBSnippets {
		private	String	DB_Name;
		private	String	DB_Table;
		private	String	Column;
		private int 	Glossary_Set;
		private	int	    Glossary_ID;
	}
}
