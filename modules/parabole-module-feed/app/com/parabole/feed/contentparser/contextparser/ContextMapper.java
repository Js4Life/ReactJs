package com.parabole.feed.contentparser.contextparser;

import com.parabole.feed.contentparser.contextparser.models.Context;
import com.parabole.feed.contentparser.contextparser.models.Triplet;
import com.parabole.feed.contentparser.contextparser.parsers.ContextParser;
import com.parabole.feed.contentparser.contextparser.parsers.ParagraphParser;
import com.parabole.feed.contentparser.contextparser.providers.ConceptProvider;
import com.parabole.feed.contentparser.contextparser.providers.ContextProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by parabole on 1/13/2017.
 */
public class ContextMapper {
    private List<String> m_concepts;
    private List<Context> m_contexts;

    public ContextMapper(JSONArray concepts, JSONArray contexts) {
        loadConcepts(concepts);
        loadContexts(contexts);
    }

    public Map<String, List<String>> getContextsFromParagraphs(Map<String, String> paras){
        List<Triplet> triplets;
        List<String> paragraphContexts;
        Map<String, List<String>> map = new HashMap<>();

        try{
            ConceptProvider conceptProvider = new ConceptProvider(m_concepts);
            ParagraphParser paragraphParser = new ParagraphParser(conceptProvider);

            for (String key: paras.keySet()) {
                String bodyText = paras.get(key);
                triplets = paragraphParser.getAllTriplets(bodyText);
                ContextParser contextParser = new ContextParser(m_contexts);
                paragraphContexts = contextParser.getParagraphContexts(triplets);
                map.put(key, paragraphContexts);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return map;
    }

    private void loadConcepts(JSONArray concepts){
        m_concepts = new ArrayList<>();
        for (int i = 0; i < concepts.length(); i++){
            JSONObject obj = concepts.getJSONObject(i);
            m_concepts.add(obj.getString("label"));
        }
    }

    private void loadContexts(JSONArray contexts){
        m_contexts = new ArrayList<>();
        for (int i = 0; i < contexts.length(); i++){
            JSONObject obj = contexts.getJSONObject(i);
            Context context = new Context();
            context.setId(obj.getString("cl"));
            context.setSubject(obj.getString("subject"));
            context.setPredicate(obj.getString("predicate"));
            context.setObject(obj.getString("object"));
            context.setContextTag(obj.getString("context"));
            m_contexts.add(context);
        }
    }
}
