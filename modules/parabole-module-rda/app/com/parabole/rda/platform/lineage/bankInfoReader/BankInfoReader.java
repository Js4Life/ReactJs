package com.parabole.rda.platform.lineage.bankInfoReader;

import com.parabole.rda.platform.graphdb.Octopus;
import com.parabole.rda.platform.graphdb.OctopusIdMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import play.Play;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//@InjectSupport
public class BankInfoReader {

    @Inject
    protected Octopus octopus;
	
	//@Inject
    //protected OctopusIdMapper octopusIdMapper;
	protected OctopusIdMapper octopusIdMapper = Play.application().injector().instanceOf(OctopusIdMapper.class);
	
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
				if(!rowIterator.hasNext()){break;}
				Row	headerrow = (Row) rowIterator.next();
					
                
                //iterating over each row
                while (rowIterator.hasNext()) {
                	System.out.println("iteration");
                	
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
	

    public List<bankglossary_element_ownership_map> getGlossaryOwnershipListFromExcel(String FILE_PATH) {
        List<bankglossary_element_ownership_map> bank_element_ownership_list = new ArrayList<bankglossary_element_ownership_map>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);
            // Using XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new XSSFWorkbook(fis);
            //System.out.println(workbook.toString());
            int numberOfSheets = workbook.getNumberOfSheets();
 
            //looping over each workbook sheet
            for (int i = 0; i < numberOfSheets; i++) {
				
				//System.out.println(" Reading Sheet " + i);
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();
                
                //Skip the first row which is header row
				if(!rowIterator.hasNext()){ break; }
				Row row = (Row) rowIterator.next();
				
                
                //iterating over each row
                while (rowIterator.hasNext()) {
                	//System.out.println("iteration");
                	
                	bankglossary_element_ownership_map pGlossary_elem = new bankglossary_element_ownership_map();
                    row = (Row) rowIterator.next();
					
					//System.out.println("One more Row Read");
                    
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
					String url = cell5.getStringCellValue();
					int id = octopusIdMapper.getId(url);
                    pGlossary_elem.setGlossaryConcept_ID((id));
         
                    
                    //sixth one is the Generator person name
                    Cell cell6 = (Cell) cellIterator.next();
                    pGlossary_elem.setGenerator(cell6.getStringCellValue());
					//System.out.println("Generator " + cell6.getStringCellValue());					

                    //fifth one is the Reviewer Name
                    Cell cell7 = (Cell) cellIterator.next();
                    pGlossary_elem.setReviewer(cell7.getStringCellValue());
					//System.out.println("Reviewer " + cell7.getStringCellValue());
					
                    //fifth one is the Approver Name
                    Cell cell8 = (Cell) cellIterator.next();
                    pGlossary_elem.setApprover(cell8.getStringCellValue());
					//System.out.println("Approver " + cell8.getStringCellValue());
					
                    //end iterating a row, add all the elements of a row in list
                    bank_element_ownership_list.add(pGlossary_elem);   
					//System.out.println("One more row parsed");		
                	}
                }
			//System.out.println("File Read finished");		
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading of glossary over");
        return bank_element_ownership_list;
    }
		
	
	//Bank Glossary Element Ownership & its map to Concept
	public class bankglossary_element_ownership_map {
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

		public int getGlossaryConcept_ID() {
			return concept_ID;
		}

		public void setGlossaryConcept_ID(int concept_ID) {
			this.concept_ID = concept_ID;
		}
		
		public String getGenerator () {
			return Generator;
		}
		
		public void setGenerator (String Generator) {
			this.Generator = Generator;
		}
		
		public String getReviewer () {
			return Reviewer;
		}
		
		public void setReviewer (String Reviewer) {
			this.Reviewer = Reviewer;
		}
		
		public String getApprover () {
			return Approver;
		}
		
		public void setApprover (String Approver) {
			this.Approver = Approver;
		}

		private int			Glossary_Set;
		private int			Glossary_ID;
		private String		Glossary_Name;
		private	String		Glossary_Description;
		private	int			concept_ID;
		private String		Generator;
		private String 		Reviewer;
		private String 		Approver;
		
		public 	bankglossary_element_ownership_map(){}
		
		public 	bankglossary_element_ownership_map (int Glossary_Set, int Glossary_ID, String Glossary_Name, String Glossary_Description, int concept_ID, String Generator, String Reviewer, String Approver){
			this.Glossary_Set = Glossary_Set;
			this.Glossary_ID = Glossary_ID;
			this.Glossary_Name = Glossary_Name;
			this.Glossary_Description = Glossary_Description;
			this.concept_ID = concept_ID;
			this.Generator = Generator;
			this.Reviewer = Reviewer;
			this.Approver = Approver;
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

