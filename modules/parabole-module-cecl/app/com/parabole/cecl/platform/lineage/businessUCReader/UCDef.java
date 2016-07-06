package com.parabole.cecl.platform.lineage.businessUCReader;

public class UCDef {
	private		int		UC_ID;
	private		String	UC_Name;
	private		String	UC_def;
	private		int[]	Concept_ID;
	private		int		Concept_length;
	
	public	UCDef() {
		Concept_ID = new int[100];
	}
	
	public UCDef(int UC_ID, String UC_Name, String UC_def, int[] Concept_ID, int length){
		this.Concept_ID = new int[100];
		
		this.UC_ID = UC_ID;
		this.UC_Name = UC_Name;
		this.UC_def = UC_def;
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
	
	public int getUC_ID() {
		return UC_ID;
	}

	public void setUC_ID(int UC_ID) {
		UC_ID = UC_ID;
	}

	public String getUC_Name() {
		return UC_Name;
	}

	public void setUC_Name(String UC_Name) {
		UC_Name = UC_Name;
	}

	public String getUC_def() {
		return UC_def;
	}

	public void setUC_def(String UC_def) {
		UC_def = UC_def;
	}

	public int[] getConcept_ID() {
		return Concept_ID;
	}

	public void setConcept_ID(int[] concept_ID) {
		for(int i = 0 ; i < 100 ; i++)
			Concept_ID[i] = concept_ID[i];
	}
	
	
	
}
