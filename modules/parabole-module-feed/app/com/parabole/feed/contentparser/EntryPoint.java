package com.parabole.feed.contentparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.feed.application.services.JenaTdbService;
import com.parabole.feed.contentparser.fasb.FASBDocIndexBuilder;
import com.parabole.feed.contentparser.models.fasb.FASBIndexedDocument;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagiruddin Mondal on 17-10-2016.
 */
public class EntryPoint {


    @Inject
    JenaTdbService jenaTdbService;

    public String entrance(String fPath, JSONObject metaDaTA, String fileTYPE){
        String result = new String();
        if(fileTYPE.equals("FASB")) {
            JSONObject specificToFileTypeMetaData = metaDaTA.getJSONObject(fileTYPE);
            try {
                result = extractFASB(fPath, specificToFileTypeMetaData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            result = "Yet to dev";
        }
        return result;
    }


    public String extractFASB(String fPath, JSONObject specificToFileTypeMetaData) throws IOException {
        JSONObject jsonObject = jenaTdbService.getFilteredDataByCompName("ceclBaseNodeDetails","FASB Concept");
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        List<String> conceptList = new ArrayList<>();
        for (int i=0; i< jsonArray.length(); i++){
            String str = jsonArray.getJSONObject(i).getString("name");
            conceptList.add(str);
        }
        FASBDocIndexBuilder builder = new FASBDocIndexBuilder(fPath,conceptList, specificToFileTypeMetaData);
        System.out.println("builder--> = " + builder);
        FASBIndexedDocument fasbIndexedDocument = builder.buildFASBIndex();

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("conceptList = " + conceptList);
        return objectMapper.writeValueAsString(fasbIndexedDocument);
    }

}
