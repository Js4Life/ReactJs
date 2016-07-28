package com.parabole.feed.contentparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.feed.application.services.JenaTdbService;
import com.parabole.feed.contentparser.models.FASBIndexedDocument;
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




    public String startExtraction(String fPath) throws IOException {
        JSONObject jsonObject = jenaTdbService.getFilteredDataByCompName("ceclBaseNodeDetails","FASB Concept");
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        List<String> conceptList = new ArrayList<>();

        for (int i=0; i< jsonArray.length(); i++){
            String str = jsonArray.getJSONObject(i).getString("name");
            conceptList.add(str);
        }
        FASBDocIndexBuilder builder = new FASBDocIndexBuilder(fPath,conceptList);
        System.out.println("builder---------------------------------------------------------------> = " + builder);
        FASBIndexedDocument fasbIndexedDocument = builder.buildFASBIndex();

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("conceptList = " + conceptList);
        return objectMapper.writeValueAsString(fasbIndexedDocument);
    }


}
