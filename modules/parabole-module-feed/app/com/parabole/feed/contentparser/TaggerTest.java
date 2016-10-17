package com.parabole.feed.contentparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.feed.application.services.JenaTdbService;
import com.parabole.feed.contentparser.fasb.FASBDocIndexBuilder;
import com.parabole.feed.contentparser.glossary.ConceptGlossaryBuilder;
import com.parabole.feed.contentparser.glossary.FASBAccoutingGlossaryBuilder;
import com.parabole.feed.contentparser.models.fasb.DocumentData;
import com.parabole.feed.contentparser.models.fasb.FASBIndexedDocument;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 7/25/2016.
 */
public class TaggerTest {

    @Inject
    JenaTdbService jenaTdbService;
/*    public static void main( String[] args ) throws IOException
    {
        *//*Pattern pattern = Pattern.compile("\\w{1,3}[-]\\w{1,3}[-]\\w{1,3}[-]\\w{1,3}");
        Matcher matcher = pattern.matcher("445-310-35-3");
        if(matcher.find())
            System.out.println(matcher.group(0));*//*
        // suppress the Dock icon on OS X
        System.setProperty("apple.awt.UIElement", "true");

        String fPath = args[0];
        TaggerTest extractor = new TaggerTest();
        extractor.startExtraction(fPath);
    }*/

    public DocumentData getAllTopicsSubTopics(String fPath) throws IOException {

        /*PDDocument document = null;
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        document = PDDocument.load(new File( fPath ), "");
        AccessPermission ap = document.getCurrentAccessPermission();
        if( ! ap.canExtractContent() )
        {
            throw new IOException( "You do not have permission to extract text" );
        }

        PDFTextStripper stripper = new PDFContentTagger();
        stripper.setStartPage( startPage );
        stripper.setEndPage( endPage );

        stripper.writeText( document, null );
        */
        /*List<String> conceptList = new ArrayList<>();
        conceptList.add("Assets Held For Sale");
        conceptList.add("Credit Loss");
        conceptList.add("Accounting Adjustments");
        conceptList.add("Deferred Compensation Contracts");
        conceptList.add("Accrued Interest");
        conceptList.add("Contingent Items");
        FASBDocIndexBuilder builder = new FASBDocIndexBuilder(fPath,conceptList);*/
        com.parabole.feed.contentparser.IDocIndexBuilder indexBuilder = new com.parabole.feed.contentparser.GeneralParaBuilder(fPath);
        FASBAccoutingGlossaryBuilder builder = new FASBAccoutingGlossaryBuilder(indexBuilder);
        DocumentData data = builder.buildItemTree();

        ObjectMapper objectMapper = new ObjectMapper();
        return data;

    }

    public String startExtraction(String fPath) throws IOException {
        JSONObject jsonObject = jenaTdbService.getFilteredDataByCompName("ceclBaseNodeDetails","FASB Concept");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        // TODO
        JSONObject specificToFileTypeMetaData = new JSONObject();
        List<String> conceptList = new ArrayList<>();
        for (int i=0; i< jsonArray.length(); i++){
            String str = jsonArray.getJSONObject(i).getString("name");
            conceptList.add(str);
        }
        FASBDocIndexBuilder builder = new FASBDocIndexBuilder(fPath,conceptList, specificToFileTypeMetaData);
        System.out.println("builder---------------------------------------------------------------> = " + builder);
        FASBIndexedDocument fasbIndexedDocument = builder.buildFASBIndex();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("conceptList = " + conceptList);
        return objectMapper.writeValueAsString(fasbIndexedDocument);
    }
}
