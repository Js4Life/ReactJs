package com.parabole.ccar.platform.lineage.bankInfoReader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BankInfoReader {
	
	public BankInfoReader() {}

    public List<bankglossary_element_map> getGlossaryListFromExcel(String FILE_PATH) {
        List<bankglossary_element_map> bank_element_list = new ArrayList<bankglossary_element_map>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);
            // Using XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new XSSFWorkbook(fis);
            //System.out.println(workbook.toString());
            int numberOfSheets = workbook.getNumberOfSheets();
 
            //looping over each workbook sheet
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();
                
                //Skip the first row
                //rowIterator.
                
                //iterating over each row
                while (rowIterator.hasNext()) {
                	//System.out.println("iteration");
                	
                	bankglossary_element_map pGlossary_elem = new bankglossary_element_map();
                    Row row = (Row) rowIterator.next();
					
					//System.out.println("Row " + row);
                    
                    Iterator<Cell> cellIterator = row.cellIterator();
                    //Iterating over each cell (column wise)  in a particular row.
                    //First one is the Glossary Set (Type NUMERIC)
                    Cell cell1 = (Cell) cellIterator.next();
                    pGlossary_elem.setGlossary_Set((int)cell1.getNumericCellValue());
					//System.out.println("Glossary Set" + (int)cell1.getNumericCellValue());
                    
                    //Second one is the Glossary ID (Type NUMERIC)
                    Cell cell2 = (Cell) cellIterator.next();
                    pGlossary_elem.setGlossary_ID((int)cell2.getNumericCellValue());
                    
                    //third one is the Glossary Name (Type STRING)
                    Cell cell3 = (Cell) cellIterator.next();
                    pGlossary_elem.setGlossary_Name(cell3.getStringCellValue());
                    
                    //fourth one is the Glossary Description (Type STRING)
                    Cell cell4 = (Cell) cellIterator.next();
                    pGlossary_elem.setGlossary_Des(cell4.getStringCellValue());
                    
                    //fifth one is the Mapped concept id
                    Cell cell5 = (Cell) cellIterator.next();
                    pGlossary_elem.setConcept_ID((int)cell5.getNumericCellValue());
         
                    
                    //end iterating a row, add all the elements of a row in list
                    bank_element_list.add(pGlossary_elem);       
                	}
                }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(bank_element_list);
        return bank_element_list;
    }
	
    public void PrintGlossary (List<bankglossary_element_map> glossary_list){
    
    	bankglossary_element_map pGlossary_elem;
    	
    	for(int i = 0; i < glossary_list.size() ; i++){
    		//System.out.println("printing Row = "+ i);
    		pGlossary_elem = glossary_list.get(i);
    		//System.out.println("Set = " + pGlossary_elem.Glossary_Set);
    		//System.out.println("Glossary_ID = " + pGlossary_elem.Glossary_ID);
    		//System.out.println("Glossary Name = " + pGlossary_elem.Glossary_Name);
    		//System.out.println("Glossary Definition = " + pGlossary_elem.Glossary_Description);
    		//System.out.println("concept ID = " + pGlossary_elem.concept_ID);
    	}
    		
    }
	
	//Bank Glossary Element & its map to Concept
	public class bankglossary_element_map {
		public int getGlossary_Set() {
			return Glossary_Set;
		}

		public void setGlossary_Set(int glossary_Set) {
			Glossary_Set = glossary_Set;
		}

		public int getGlossary_ID() {
			return Glossary_ID;
		}

		public void setGlossary_ID(int glossary_ID) {
			Glossary_ID = glossary_ID;
		}

		public String getGlossary_Name() {
			return Glossary_Name;
		}

		public void setGlossary_Name(String glossary_Name) {
			Glossary_Name = glossary_Name;
		}

		public String getGlossary_Des() {
			return Glossary_Description;
		}

		public void setGlossary_Des(String glossary_Description) {
			Glossary_Description = glossary_Description;
		}

		public int getConcept_ID() {
			return concept_ID;
		}

		public void setConcept_ID(int concept_ID) {
			this.concept_ID = concept_ID;
		}

		private int			Glossary_Set;
		private int			Glossary_ID;
		private String		Glossary_Name;
		private	String		Glossary_Description;
		private	int			concept_ID;
		
		public 	bankglossary_element_map(){}
		
		public 	bankglossary_element_map (int Glossary_Set, int Glossary_ID, String Glossary_Name, String Glossary_Description, int concept_ID){
			this.Glossary_Set = Glossary_Set;
			this.Glossary_ID = Glossary_ID;
			this.Glossary_Name = Glossary_Name;
			this.Glossary_Description = Glossary_Description;
			this.concept_ID = concept_ID;
		}
	}
}
