package com.parabole.rda.platform.lineage.datareader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class DataReader {

	//define class constructor
	public DataReader(){}

	   public int getDataArrayListFromExcel(String FILE_PATH, double[][] x, double[] y, HashMap <Integer, ArrayList<String>> pDataArray, int startyear, int endyear) {
	        String CellVal4;	        
	        FileInputStream fis = null;
			Iterator<Row> rowIterator;
			Row row;
			Iterator<Cell> cellIterator;
			ArrayList	lLineageList;
			int temp_target_col;
			int target_col;
			int		indx = 0;
			int 	date_col = 0;
			int		current_year = 0;
	        
			try {
	            fis = new FileInputStream(FILE_PATH);
	            // Using XSSF for xlsx format, for xls use HSSF
	            Workbook workbook = new XSSFWorkbook(fis);
	            int numberOfSheets = workbook.getNumberOfSheets();
	 
	            //looping over each workbook sheet
	            for (int i = 0; i < numberOfSheets; i++) {
	                Sheet sheet = workbook.getSheetAt(i);
					
	                rowIterator = sheet.iterator();
					
					
					//First identify the date column
					//check which column name matches with "Date"
	                //Read the first row which is header
					if(!rowIterator.hasNext()) { break;}
	                row = (Row) rowIterator.next();
					//create a cell iterator
					cellIterator = row.cellIterator();
					temp_target_col = 0;
					
					while(cellIterator.hasNext()){
						//compare the column heading with rule
						Cell	cell_value = (Cell) cellIterator.next();
						System.out.println("cell_value.getStringCellValue() " + cell_value.getStringCellValue());
						System.out.println("cell_value.getStringCellValue() " + cell_value.getStringCellValue());
						if((cell_value.getStringCellValue()).equals("Year")){
							date_col = temp_target_col;
							break;
						}
						temp_target_col = temp_target_col + 1;
					}
					
					//Now start with identify y
	                rowIterator = sheet.iterator();					
					//Chcek what is the name of Rule at 0 Key of hashmap
					lLineageList = pDataArray.get(0);
					//System.out.println("lLineageList.get(0)" + lLineageList.get(0));
					//check which column name matches with rule name
	                //Read the first row which is header
					if(!rowIterator.hasNext()) { break;}
	                row = (Row) rowIterator.next();
					//create a cell iterator
					cellIterator = row.cellIterator();
					temp_target_col = 0;
					target_col = 0;
					
					//System.out.println("check the column for Y START");
					while(cellIterator.hasNext()){
						//compare the column heading with rule
						Cell	cell_value = (Cell) cellIterator.next();
						System.out.println("lLineageList.get(0) " + lLineageList.get(0));
						System.out.println("cell_value.getStringCellValue() " + cell_value.getStringCellValue());
						if(lLineageList.get(0).equals(cell_value.getStringCellValue())){
							target_col = temp_target_col;
							break;
						}
						temp_target_col = temp_target_col + 1;
					}
					System.out.println("check the column for Y End Column num " + target_col);
					
					//Now read the Y values one-by-one
					indx = 0;
					
					//System.out.println("Y[] reading START");					
					while(rowIterator.hasNext()) {
						row = (Row) rowIterator.next();
	
						//create a cell iterator
						cellIterator = row.cellIterator();
						temp_target_col = 0;
						while(cellIterator.hasNext())
						{
							if(temp_target_col == date_col){
								current_year = (int)(((Cell) cellIterator.next()).getNumericCellValue());
								break;
							}
							Cell cell_value = (Cell) cellIterator.next();
							temp_target_col = temp_target_col + 1;
						}			

						//System.out.println("current_year " + current_year + " startyear " + startyear + " endyear " + endyear);
						//check if the time line within the range set
						if((current_year >= startyear) && (current_year <= endyear)){
							//reset the cell iterator
							cellIterator = row.cellIterator();
							temp_target_col = 0;
				
							//System.out.println("reading next row for Y[]");
							while(cellIterator.hasNext())
							{
								if(temp_target_col == target_col){
									y[indx] = (double)(((Cell) cellIterator.next()).getNumericCellValue());
									//System.out.println(" Reading Y[index] " + y[indx]);	
									indx = indx + 1;
									break;
								}
								Cell cell_value = (Cell) cellIterator.next();
								temp_target_col = temp_target_col + 1;
							}
						}		
					}		

					//System.out.println("Y[] reading END");					
	                
					//Now read each parameter one by one
					for(int j = 0 ; j < (pDataArray.size()-1); j++){
							rowIterator = sheet.iterator();
							//Chcek what is the name of column at 0 Key of hashmap
							lLineageList = pDataArray.get(j+1);
							//check which column name matches with rule name
							//Read the first row which is header
							if(!rowIterator.hasNext()) { break;}
							row = (Row) rowIterator.next();
							//create a cell iterator
							cellIterator = row.cellIterator();
							temp_target_col = 0;
							target_col = 0;
							
							//System.out.println("Reading of x[][] START");
							while(cellIterator.hasNext()){
								//compare the column heading with rule
								Cell	cell_value = (Cell) cellIterator.next();
								//4th element in the ArrayList contains the column name
								if(lLineageList.get(3).equals(cell_value.getStringCellValue())){
									target_col = temp_target_col;
									System.out.println("Cell_value.getStringCellValue() :" + cell_value.getStringCellValue() + " target_col " + target_col);
									break;
								}
								temp_target_col = temp_target_col + 1;
							}
							
							//Now read the X values one-by-one
							indx = 0;
							while(rowIterator.hasNext()) {
								row = (Row) rowIterator.next();
								//create a cell iterator
								cellIterator = row.cellIterator();
								temp_target_col = 0;
								while(cellIterator.hasNext())
								{
									if(temp_target_col == date_col){
										current_year = (int)(((Cell) cellIterator.next()).getNumericCellValue());
										break;
									}
									temp_target_col = temp_target_col + 1;
								}			


								//check if the time line within the range set
								if((current_year >= startyear) && (current_year <= endyear)){
									//reset the cell iterator
									cellIterator = row.cellIterator();

									temp_target_col = 0;
									//System.out.println("target_col " + target_col);
									while(cellIterator.hasNext())
									{
										if(temp_target_col == target_col){
											x[indx][j] = (double)(((Cell) cellIterator.next()).getNumericCellValue());
											//System.out.println("j " + j + " indx " + indx + " x[indx][j] " + x[indx][j]);
											indx = indx + 1;
											break;
										}
										Cell cell_value = (Cell) cellIterator.next();
										temp_target_col = temp_target_col + 1;
									}	
								}	
							}		
							//System.out.println("Reading of x[][] END");

					}
				}	
	            fis.close();
	        } catch (FileNotFoundException e) {
				System.out.println("  Cannot open Data file");
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return indx;
	   }	   
}
