package com.parabole.feed.platform.lineage.businessRuleReader;

public class RuleDef {
	private		int		Rule_ID;
	private		String	Rule_Name;
	private		String	Rule_def;
	private		int[]	Concept_ID;
	private		int		Concept_length;
	
	public	RuleDef() {
		Concept_ID = new int[100];
	}
	
	public RuleDef(int Rule_ID, String Rule_Name, String Rule_def, int[] Concept_ID, int length){
		this.Concept_ID = new int[100];
		
		this.Rule_ID = Rule_ID;
		this.Rule_Name = Rule_Name;
		this.Rule_def = Rule_def;
		for(int i = 0 ; i < 100 ; i++)
			this.Concept_ID[i] = Concept_ID[i];
		this.Concept_length = length;
	}

	public void setConcept_length (int length){
		Concept_length = length;
	}
	
	public int getConcept_length (){
		return Concept_length;
	}
	
	public int getRule_ID() {
		return Rule_ID;
	}

	public void setRule_ID(int rule_ID) {
		Rule_ID = rule_ID;
	}

	public String getRule_Name() {
		return Rule_Name;
	}

	public void setRule_Name(String rule_Name) {
		Rule_Name = rule_Name;
	}

	public String getRule_def() {
		return Rule_def;
	}

	public void setRule_def(String rule_def) {
		Rule_def = rule_def;
	}

	public int[] getConcept_ID() {
		return Concept_ID;
	}

	public void setConcept_ID(int[] concept_ID) {
		for(int i = 0 ; i < 100 ; i++)
			Concept_ID[i] = concept_ID[i];
	}
	
	
	
}
