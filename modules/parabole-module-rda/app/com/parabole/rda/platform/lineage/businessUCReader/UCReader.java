package com.parabole.rda.platform.lineage.businessUCReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.parabole.rda.platform.graphdb.OctopusIdMapper;
import com.parabole.rda.platform.lineage.paraboleGraph.DGraph;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import play.Play;


public class UCReader {

	protected OctopusIdMapper octopusIdMapper = Play.application().injector().instanceOf(OctopusIdMapper.class);

	//define class constructor
	public UCReader(){}

	   public List<uc_concept_map> getUCListFromExcel(String FILE_PATH, DGraph pDGraph) {
	        String CellVal4;
	        String CellArray[];
		    int	   Concept_ID;
		    int	   Relation_ID;
	        int	   CellInt[] = new int[100];
	        List<uc_concept_map> uc_concept_list = new ArrayList<uc_concept_map>();
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
	                
	                //Skip the first row which is header
					if(!rowIterator.hasNext()) { break;}
	                Row row = (Row) rowIterator.next();
	                
	                //iterating over each row
	                while (rowIterator.hasNext()) {
	                	uc_concept_map pUCConceptMap = new uc_concept_map();
	                    row = (Row) rowIterator.next();
						System.out.println("  Reading rows of UC data ");	                    
	                    Iterator<Cell> cellIterator = row.cellIterator();
	                    //Iterating over each cell (column wise)  in a particular row.
	                    //First one is the UC ID (Type NUMERIC)
	                    Cell cell1 = (Cell) cellIterator.next();
	                    pUCConceptMap.setUC_ID((int) cell1.getNumericCellValue());

						//Second one is the UC name (Type STRING)
						Cell cell2 = (Cell) cellIterator.next();
						pUCConceptMap.setUC_Name(cell2.getStringCellValue());

						//Third one is the UC URL
						Cell cell3 = (Cell) cellIterator.next();
						String url = cell3.getStringCellValue();
						Concept_ID = octopusIdMapper.getId(url);
						Relation_ID = 0;

						//Now go through the all relation paths for this ID
						for(int j = 0 ; j < pDGraph.AdjList[Concept_ID].size() ; j++){
							//First get the relation node
							Relation_ID = (pDGraph.AdjList[Concept_ID].get(j)).dest_ID;
							//Then find the other edge of the relation node
							CellInt[j] = pDGraph.AdjList[Relation_ID].get(0).dest_ID;
							System.out.println("CellInt[j] +++++++++ " + CellInt[j]);
						}

	                    pUCConceptMap.setConcept_ID((int[]) CellInt);
	                    
						pUCConceptMap.setConcept_length(pDGraph.AdjList[Concept_ID].size());
	                    //end iterating a row, add all the elements of a row in list
	                    uc_concept_list.add(pUCConceptMap);                    
	                	}

	                }
	            fis.close();
	        } catch (FileNotFoundException e) {
				System.out.println("  Cannot open UC file");
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return uc_concept_list;
	   }
	   
	   public List<uc_concept_map> getUCListFromExcelSpecificNodeId(String FILE_PATH, DGraph pDGraph, int NodeId) {
	        String CellVal4;
	        String CellArray[];
		    int	   Concept_ID;
		    int	   Relation_ID;
	        int	   CellInt[] = new int[100];
	        List<uc_concept_map> uc_concept_list = new ArrayList<uc_concept_map>();
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
	                
	                //Skip the first row which is header
					if(!rowIterator.hasNext()) { break;}
	                Row row = (Row) rowIterator.next();
	                
	                //iterating over each row
	                while (rowIterator.hasNext()) {
	                	uc_concept_map pUCConceptMap = new uc_concept_map();
	                    row = (Row) rowIterator.next();
						System.out.println("  Reading rows of UC data ");	                    
	                    Iterator<Cell> cellIterator = row.cellIterator();
	                    //Iterating over each cell (column wise)  in a particular row.
	                    //First one is the UC ID (Type NUMERIC)
	                    Cell cell1 = (Cell) cellIterator.next();
	                    pUCConceptMap.setUC_ID((int) cell1.getNumericCellValue());

						//Second one is the UC name (Type STRING)
						Cell cell2 = (Cell) cellIterator.next();
						pUCConceptMap.setUC_Name(cell2.getStringCellValue());

						//Third one is the UC URL
						Cell cell3 = (Cell) cellIterator.next();
						String url = cell3.getStringCellValue();
						Concept_ID = octopusIdMapper.getId(url);
						Relation_ID = 0;

						//Now go through the all relation paths for this ID
						for(int j = 0 ; j < pDGraph.AdjList[Concept_ID].size() ; j++){
							//First get the relation node
							Relation_ID = (pDGraph.AdjList[Concept_ID].get(j)).dest_ID;
							//Then find the other edge of the relation node
							CellInt[j] = pDGraph.AdjList[Relation_ID].get(0).dest_ID;
							System.out.println("CellInt[j] +++++++++ " + CellInt[j]);
							System.out.println("Adjcency List Size() " + pDGraph.AdjList[CellInt[j]].size());
						}

	                    pUCConceptMap.setConcept_ID((int[]) CellInt);
	                    
						pUCConceptMap.setConcept_length(pDGraph.AdjList[Concept_ID].size());
	                    //end iterating a row, add all the elements of a row in list
	                    uc_concept_list.add(pUCConceptMap);                    
	                	}

	                }
	            fis.close();
	        } catch (FileNotFoundException e) {
				System.out.println("  Cannot open UC file");
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return uc_concept_list;
	   }


	   public List<UCDef> GenerateUC(List<uc_concept_map> uc_list){
		   uc_concept_map pUC_elem;
		   List<UCDef>	pUCList = (List<UCDef>) new LinkedList<UCDef>();
		   
		   for(int i = 0 ; i < uc_list.size() ; i++){
			   pUC_elem = uc_list.get(i);
			   //Create a UCDef object
			   UCDef	pUC = new UCDef(pUC_elem.getUC_ID(), pUC_elem.getUC_Name(), pUC_elem.getUC_Description(), pUC_elem.getConcept_ID(), pUC_elem.getConcept_length());
			   //Add it to the UCDef List
			   pUCList.add(pUC);
		   }
		   
		   return pUCList;
	   }
	   
	   public List<UCDef> GenerateSpecificUC(List<uc_concept_map> uc_list, int ucId){
		   uc_concept_map pUC_elem;
		   List<UCDef>	pUCList = (List<UCDef>) new LinkedList<UCDef>();
		   System.out.println("GenerateSpecificUC START ");
		   
		   for(int i = 0 ; i < uc_list.size() ; i++){
			   pUC_elem = uc_list.get(i);
			   
			   System.out.println("+++ scanning through UC # " + i);
			   //Create a UCDef object for ucId
			   if(pUC_elem.getUC_ID() == ucId) {
				   
				   System.out.println("+++ UCId = " + ucId);
				   UCDef	pUC = new UCDef(pUC_elem.getUC_ID(), pUC_elem.getUC_Name(), pUC_elem.getUC_Description(), pUC_elem.getConcept_ID(), pUC_elem.getConcept_length());
				   //Add it to the UCDef List
				   pUCList.add(pUC);
			   }
		  }
		   
		   return pUCList;
	   }

	   public void PrintUC (List<uc_concept_map> uc_list){
	        
	    	uc_concept_map pUC_elem;
	    	int	k;
	    	
	    	for(int i = 0; i < uc_list.size() ; i++){
	    		//System.out.println("printing Row = "+ i);
	    		pUC_elem = uc_list.get(i);
	    		k = 0;
	    		while(pUC_elem.Concept_ID[k] != -1)
	    		{	
	    			//System.out.println("Concept_ID = " + pUC_elem.Concept_ID[k]);
	    			k++;
	    		}	
	    	}
	    		
	    }
	   
	   private class uc_concept_map {
		   private int			UC_ID;
		   private String		UC_Name;
		   private String		UC_Description;
		   private int			Concept_ID[]; // Map-ed concepts from ontology
		   private int			Concept_length;
		   public uc_concept_map() 
		   {
			   Concept_ID = new int[100];
		   }
		   
		   public uc_concept_map(int UC_ID, String UC_Name, String UC_Description, int[] concept_ID){
			   this.UC_ID = UC_ID;
			   this.UC_Name = UC_Name;
			   this.UC_Description = UC_Description;
			   this.Concept_ID = concept_ID;
		   }

		   public void setConcept_length (int  length){
			   this.Concept_length = length;
		   }

			public int	getConcept_length (){
				return  Concept_length; 
			}		
		   
			public int getUC_ID() {
				return UC_ID;
			}

			public void setUC_ID(int uc_ID) {
				UC_ID = uc_ID;
			}

			public String getUC_Name() {
				return UC_Name;
			}

			public void setUC_Name(String uc_Name) {
				UC_Name = uc_Name;
			}

			public String getUC_Description() {
				return UC_Description;
			}

			public void setUC_Description(String uc_Description) {
				UC_Description = uc_Description;
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
