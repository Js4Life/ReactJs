package com.parabole.rda.platform.lineage.dataSchematoGraph;

import java.io.StringReader;

import com.parabole.rda.platform.lineage.paraboleGraph.DGraph;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.JSQLParserException;

//DataSchemaParser class
public class DataSchemaParser {

	private CCJSqlParserManager parserManager;
	public DataSchemaParser() {
		//Create a JSQLParser Manager
		parserManager = new CCJSqlParserManager();
	}
	
	public DGraph GenerateGraphFromSQL(String statement) throws JSQLParserException{
		//CreateTable object from the passed SQL statement
		CreateTable createTable = (CreateTable) parserManager.parse(new StringReader(statement));
		//create a graph of size = number of column + 1 (1 for table which is the primary concept)
		//graph edges = number of column 
		int number_of_column = createTable.getColumnDefinitions().size();
		DGraph	pDGraph = new DGraph ((number_of_column+1), number_of_column);
		//First Add the primary node which is the table
		pDGraph.addNode(0, createTable.getTable().getWholeTableName(), null);
		
		//Read column recursively
		double weight = 1.0;
		for(int i = 0; i < createTable.getColumnDefinitions().size(); i++){
			//First Get each column 
			//Add one node for each column
			pDGraph.addNode((i+1), createTable.getColumnDefinitions().get(i).toString(), null);
			//Add one edge between the primary node and the current column
			pDGraph.addEdge(0, (i+1), weight);
			//System.out.println("column " + createTable.getColumnDefinitions().get(i).getColumnName());
		}

		return pDGraph;
	}
}
