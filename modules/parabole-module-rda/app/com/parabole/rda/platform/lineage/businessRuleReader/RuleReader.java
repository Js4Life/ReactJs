package com.parabole.rda.platform.lineage.businessRuleReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class RuleReader {

	//define class constructor
	public RuleReader(){}

	   public List<rule_concept_map> getRuleListFromExcel(String FILE_PATH) {
	        String CellVal4;
	        String CellArray[];
	        int	   CellInt[] = new int[100];
	        List<rule_concept_map> rule_concept_list = new ArrayList<rule_concept_map>();
	        FileInputStream fis = null;
	        try {
	            fis = new FileInputStream(FILE_PATH);
	            // Using XSSF for xlsx format, for xls use HSSF
	            Workbook workbook = new XSSFWorkbook(fis);
	            int numberOfSheets = workbook.getNumberOfSheets();
	 
	            //looping over each workbook sheet
	            for (int i = 0; i < numberOfSheets; i++) {
	                Sheet sheet = workbook.getSheetAt(i);
	                Iterator<Row> rowIterator = sheet.iterator();
	                
	                //Skip the first row
	                if(!rowIterator.hasNext()){break;}
					Row headerrow = (Row) rowIterator.next();
				
	                
	                //iterating over each row
	                while (rowIterator.hasNext()) {
	                	rule_concept_map pRuleConceptMap = new rule_concept_map();
	                    Row row = (Row) rowIterator.next();
	                    
	                    Iterator<Cell> cellIterator = row.cellIterator();
	                    //Iterating over each cell (column wise)  in a particular row.
	                    //First one is the Rule ID (Type NUMERIC)
	                    Cell cell1 = (Cell) cellIterator.next();
	                    pRuleConceptMap.setRule_ID((int) cell1.getNumericCellValue());
	                    
	                    //Second one is the Rule name (Type STRING)
	                    Cell cell2 = (Cell) cellIterator.next();
	                    pRuleConceptMap.setRule_Name(cell2.getStringCellValue());
	                    
	                    //third one is the Rule description (Type STRING)
	                    Cell cell3 = (Cell) cellIterator.next();
	                    pRuleConceptMap.setRule_Description(cell3.getStringCellValue());
	                    
	                    //fourth one is the concept ID (Type NUMERIC[])
	                    Cell cell4 = (Cell) cellIterator.next();
	                    CellVal4 = cell4.getStringCellValue();
	                    CellArray = CellVal4.split(";");
	                    //Initialize CellInt values to "-1" (Invalid)
	                    for(int k = 0; k < 100 ; k++)
	                    	CellInt[k] = -1;
	                    //Assign CellInt from CellArray read
	                    for(int k = 0; k < CellArray.length ; k++) {
	                    	CellInt[k] = Integer.parseInt(CellArray[k]);
	                    }	                    	
	                    pRuleConceptMap.setConcept_ID((int[]) CellInt);
	                    
						pRuleConceptMap.setConcept_length(CellArray.length);	
	                    //end iterating a row, add all the elements of a row in list
	                    rule_concept_list.add(pRuleConceptMap);                    
	                	}

	                }
	            fis.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return rule_concept_list;
	   }
	   
	   public List<RuleDef> GenerateRule(List<rule_concept_map> rule_list){
		   rule_concept_map pRule_elem;
		   List<RuleDef>	pRuleList = (List<RuleDef>) new LinkedList<RuleDef>();
		   
		   for(int i = 0 ; i < rule_list.size() ; i++){
			   pRule_elem = rule_list.get(i);
			   //Create a RuleDef object
			   RuleDef	pRule = new RuleDef(pRule_elem.getRule_ID(), pRule_elem.getRule_Name(), pRule_elem.getRule_Description(), pRule_elem.getConcept_ID(), pRule_elem.getConcept_length());
			   //Add it to the RuleDef List
			   pRuleList.add(pRule);
		   }
		   
		   return pRuleList;
	   }
	   
	   public List<RuleDef> GenerateSpecificRule(List<rule_concept_map> rule_list, int ruleId){
		   rule_concept_map pRule_elem;
		   List<RuleDef>	pRuleList = (List<RuleDef>) new LinkedList<RuleDef>();
		   
		   for(int i = 0 ; i < rule_list.size() ; i++){
			   pRule_elem = rule_list.get(i);
			   
			   //Create a RuleDef object for ruleId
			   if(pRule_elem.getRule_ID() == ruleId) {
				   
				   //System.out.println("+++ RuleId = " + ruleId);
				   RuleDef	pRule = new RuleDef(pRule_elem.getRule_ID(), pRule_elem.getRule_Name(), pRule_elem.getRule_Description(), pRule_elem.getConcept_ID(), pRule_elem.getConcept_length());
				   //Add it to the RuleDef List
				   pRuleList.add(pRule);
			   }
		  }
		   
		   return pRuleList;
	   }

	   public void PrintRule (List<rule_concept_map> rule_list){
	        
	    	rule_concept_map pRule_elem;
	    	int	k;
	    	
	    	for(int i = 0; i < rule_list.size() ; i++){
	    		//System.out.println("printing Row = "+ i);
	    		pRule_elem = rule_list.get(i);
	    		//System.out.println("Rule ID = " + pRule_elem.Rule_ID);
	    		//System.out.println("Rule_Name = " + pRule_elem.Rule_Name);
	    		//System.out.println("Rule_Descriptor = " + pRule_elem.Rule_Description);
	    		k = 0;
	    		while(pRule_elem.Concept_ID[k] != -1)
	    		{	
	    			//System.out.println("Concept_ID = " + pRule_elem.Concept_ID[k]);
	    			k++;
	    		}	
	    	}
	    		
	    }
	   
	   private class rule_concept_map {
		   private int			Rule_ID;
		   private String		Rule_Name;
		   private String		Rule_Description;
		   private int			Concept_ID[]; // Map-ed concepts from ontology
		   private int			Concept_length;
		   public rule_concept_map() 
		   {
			   Concept_ID = new int[100];
		   }
		   
		   public rule_concept_map(int Rule_ID, String Rule_Name, String Rule_Description, int[] concept_ID){
			   this.Rule_ID = Rule_ID;
			   this.Rule_Name = Rule_Name;
			   this.Rule_Description = Rule_Description;
			   this.Concept_ID = concept_ID;
		   }

		   public void setConcept_length (int  length){
			   this.Concept_length = length;
		   }

			public int	getConcept_length (){
				return  Concept_length; 
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

			public String getRule_Description() {
				return Rule_Description;
			}

			public void setRule_Description(String rule_Description) {
				Rule_Description = rule_Description;
			}

			public int[] getConcept_ID() {
				return Concept_ID;
			}

			public void setConcept_ID(int concept_ID[]) {
				for(int i = 0 ; i < 100 ; i++) {
					Concept_ID[i] = concept_ID[i];
				}
			}
		
	   }

}
