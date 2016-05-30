package com.parabole.rda.platform.lineage.GlossarytoDBMapper;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



		
	//Bank Glossary Element & its map to Concept
	public class glossarytodb_map {
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

		public String getDatabase_name() {
			return Database_name;
		}

		public void setDatabase_name(String DB_name) {
			Database_name = DB_name;
		}

		public String getDatabase_table() {
			return Database_table;
		}

		public void setDatabase_table(String DB_table) {
			Database_table = DB_table;
		}

		public String getDatabase_col() {
			return Database_col;
		}

		public void setDatabase_col(String DB_col) {
			Database_col = DB_col;
		}
		
		private int			Glossary_Set;
		private int			Glossary_ID;
		private String		Glossary_Name;
		private	String		Database_name;
		private String		Database_table;
		private String		Database_col;
		
		public 	glossarytodb_map(){}
		
		public 	glossarytodb_map (int Glossary_Set, int Glossary_ID, String Glossary_Name, String DB_name, String DB_table, String DB_col){
			this.Glossary_Set = Glossary_Set;
			this.Glossary_ID = Glossary_ID;
			this.Glossary_Name = Glossary_Name;
			this.Database_name = DB_name;
			this.Database_table = DB_table;
			this.Database_col = DB_col;
		}
}
