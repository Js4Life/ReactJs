package com.parabole.feed.platform.lineage.bankInfoReader;

/**
 * Created by parabole on 10-01-2017.
 */
public class BankGlossaryElementMap {
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

    public BankGlossaryElementMap(){}

    public BankGlossaryElementMap(int Glossary_Set, int Glossary_ID, String Glossary_Name, String Glossary_Description, int concept_ID){
        this.Glossary_Set = Glossary_Set;
        this.Glossary_ID = Glossary_ID;
        this.Glossary_Name = Glossary_Name;
        this.Glossary_Description = Glossary_Description;
        this.concept_ID = concept_ID;
    }
}