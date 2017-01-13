package com.parabole.feed.platform.lineage.bankInfoReader;

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

    public List<BankGlossaryElementMap> getGlossaryListFromExcel(String FILE_PATH) {
        List<BankGlossaryElementMap> bank_element_list = new ArrayList<BankGlossaryElementMap>();
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
                	
                	BankGlossaryElementMap pGlossary_elem = new BankGlossaryElementMap();
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
	
    public void PrintGlossary (List<BankGlossaryElementMap> glossary_list){
    
    	BankGlossaryElementMap pGlossary_elem;
    	
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

}
