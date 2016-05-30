package com.parabole.ccar.platform.lineage.GlossarytoDBMapper;

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


public class glossarytodb {
	
	public glossarytodb() {}

    public List<glossarytodb_map> getGlossarytodbmapFromExcel(String FILE_PATH) {
        List<glossarytodb_map> glossarytodb_list = new ArrayList<glossarytodb_map>();
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
                	
                	glossarytodb_map pGlossarytodb_elem = new glossarytodb_map();
                    Row row = (Row) rowIterator.next();
					
					//System.out.println("Row " + row);
                    
                    Iterator<Cell> cellIterator = row.cellIterator();
                    //Iterating over each cell (column wise)  in a particular row.
                    //First one is the Glossary Set (Type NUMERIC)
                    Cell cell1 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setGlossary_Set((int)cell1.getNumericCellValue());
					//System.out.println("Glossary Set" + (int)cell1.getNumericCellValue());
                    
                    //Second one is the Glossary ID (Type NUMERIC)
                    Cell cell2 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setGlossary_ID((int)cell2.getNumericCellValue());
                    
                    //third one is the Glossary Name (Type STRING)
                    Cell cell3 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setGlossary_Name(cell3.getStringCellValue());
                    
                    //fourth one is the Mapped Database name (Type STRING)
                    Cell cell4 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setDatabase_name(cell4.getStringCellValue());
                    
                    //fifth one is the Mapped Database table (Type STRING)
                    Cell cell5 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setDatabase_table(cell5.getStringCellValue());
         
                    //sixth one is the Mapped Database column (Type STRING)
                    Cell cell6 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setDatabase_col(cell6.getStringCellValue());
					
					//System.out.println("pGlossarytodb col name " + pGlossarytodb_elem.getDatabase_col());
                    
                    //end iterating a row, add all the elements of a row in list
                    glossarytodb_list.add(pGlossarytodb_elem);       
                	}
                }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
        //System.out.println("glossarytodb_list " + glossarytodb_list);
        return glossarytodb_list;
    }
	
	
    public List<glossarytodb_map> getGlossarytodbSpecificmapFromExcel(String FILE_PATH, String DB_name, String DB_table, String DB_col) {
        List<glossarytodb_map> glossarytodb_list = new ArrayList<glossarytodb_map>();
        FileInputStream fis = null;
		
		System.out.println("DB Name  " + DB_name);
		System.out.println("DB table  " + DB_table);
		System.out.println("DB col  " + DB_col);
		
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
                	System.out.println("iteration");
                	
                	glossarytodb_map pGlossarytodb_elem = new glossarytodb_map();
                    Row row = (Row) rowIterator.next();
					
					//System.out.println("Row " + row);
                    
                    Iterator<Cell> cellIterator = row.cellIterator();
                    //Iterating over each cell (column wise)  in a particular row.
                    //First one is the Glossary Set (Type NUMERIC)
                    Cell cell1 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setGlossary_Set((int)cell1.getNumericCellValue());
					
					//System.out.println("Glossary Set" + (int)cell1.getNumericCellValue());
                    
                    //Second one is the Glossary ID (Type NUMERIC)
                    Cell cell2 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setGlossary_ID((int)cell2.getNumericCellValue());
                    
                    //third one is the Glossary Name (Type STRING)
                    Cell cell3 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setGlossary_Name(cell3.getStringCellValue());
                    
                    //fourth one is the Mapped Database name (Type STRING)
                    Cell cell4 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setDatabase_name(cell4.getStringCellValue());
                    if(!pGlossarytodb_elem.getDatabase_name().equals(DB_name))
					//if(pGlossarytodb_elem.getDatabase_name() != DB_name) 
					{ System.out.println("1 " + pGlossarytodb_elem.getDatabase_name()); System.out.println("2 " + DB_name); continue; }
					
                    //fifth one is the Mapped Database table (Type STRING)
                    Cell cell5 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setDatabase_table(cell5.getStringCellValue());
                    if(!pGlossarytodb_elem.getDatabase_table().equals(DB_table))
					{ System.out.println("1 " + pGlossarytodb_elem.getDatabase_table()); System.out.println("2 " + DB_table); continue; }
         
                    //sixth one is the Mapped Database column (Type STRING)
                    Cell cell6 = (Cell) cellIterator.next();
                    pGlossarytodb_elem.setDatabase_col(cell6.getStringCellValue());
                    if(!pGlossarytodb_elem.getDatabase_col().equals(DB_col))
					{ System.out.println("1 " + pGlossarytodb_elem.getDatabase_col()); System.out.println("2 " + DB_col); continue; }
					
					//System.out.println("pGlossarytodb col name " + pGlossarytodb_elem.getDatabase_col());
                    
                    //end iterating a row, add all the elements of a row in list
                    glossarytodb_list.add(pGlossarytodb_elem);       
                	}
                }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
        //System.out.println("glossarytodb_list " + glossarytodb_list);
        return glossarytodb_list;
    }		
}
