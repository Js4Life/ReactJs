package com.parabole.feed.application.services;

import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.utils.AppUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Environment;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Sagir on 02-08-2016.
 */
public class CheckListServices {

    @Inject
    private Environment environment;



    private String getUniqueID() {
        UUID uniqueKey = UUID.randomUUID();
        return uniqueKey.toString();
    }

    public String addQuestion(JSONObject incomingQuestion) throws AppException, IOException {

        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject jsonObject = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
        JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");

        System.out.println("indexes.toString() = " + indexes.toString());

        // looping all the questions ----------------->
        JSONArray allQuestions = incomingQuestion.getJSONArray("questions");
        for (int i = 0; i < allQuestions.length(); i++) {
            JSONObject singleQuestionJsonObject = allQuestions.getJSONObject(i);

            String QuestionId = getUniqueID();
            JSONArray paragraphAgainstId = new JSONArray();

            fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions").put(QuestionId, singleQuestionJsonObject.getString("text"));
            if (indexes.getJSONObject("paragraphs").has(incomingQuestion.getString("paragraphId")))
                paragraphAgainstId = indexes.getJSONObject("paragraphs").getJSONArray(incomingQuestion.getString("paragraphId"));

            //if paragraph id not present  ----------->
            if(paragraphAgainstId != null && paragraphAgainstId.length() > 0){
                paragraphAgainstId.put(QuestionId);
            }
            else{
                JSONArray listOfPid = new JSONArray();
                listOfPid.put(QuestionId);
                indexes.getJSONObject("paragraphs").put(incomingQuestion.getString("paragraphId"), listOfPid);
            }

            // adding other components------------------>
            //// loop against all the components----->
            for (int j = 0; j < singleQuestionJsonObject.getJSONArray("components").length(); j++) {
                JSONObject component = singleQuestionJsonObject.getJSONArray("components").getJSONObject(j);
                JSONArray containArrayOfcomponentName = new JSONArray();

                Boolean continueationOfThisFlow = true;

                if(indexes.has(incomingQuestion.getString("conceptName")))
                    if (indexes.getJSONObject(incomingQuestion.getString("conceptName")).has(component.getString("type")))
                    {
                        if (indexes.getJSONObject(incomingQuestion.getString("conceptName")).getJSONObject(component.getString("type")).has(component.getString("name"))) {
                            containArrayOfcomponentName = indexes.getJSONObject(incomingQuestion.getString("conceptName")).getJSONObject(component.getString("type")).getJSONArray(component.getString("name"));
                        }else{
                            JSONArray componentName = new JSONArray();
                            componentName.put(QuestionId);
                            indexes.getJSONObject(incomingQuestion.getString("conceptName")).getJSONObject(component.getString("type")).put(component.getString("name"), componentName);
                            continueationOfThisFlow = false;
                        }
                    }else{
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(QuestionId);
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put(component.getString("name"), jsonArray);
                        indexes.getJSONObject(incomingQuestion.getString("conceptName")).put(component.getString("type"), jsonObject1);
                        continueationOfThisFlow = false;
                    }

                // handling component name list
                if(continueationOfThisFlow)
                    if(containArrayOfcomponentName != null && containArrayOfcomponentName.length() > 0){
                        System.out.println("paragraphAgainstId = null " + containArrayOfcomponentName);
                        containArrayOfcomponentName.put(QuestionId);
                    }
                    else{
                        System.out.println("paragraphAgainstId = " + containArrayOfcomponentName);
                        JSONArray listOfConceptName = new JSONArray();
                        listOfConceptName.put(QuestionId);
                        JSONObject componentType = new JSONObject();
                        componentType.put(component.getString("name"), listOfConceptName);
                        JSONObject newConcept = new JSONObject();
                        newConcept.put(component.getString("type"), componentType);
                        indexes.put(incomingQuestion.getString("conceptName"), newConcept);
                    }
            }

        }

        // Saving ------------------------------------>
        AppUtils.writeFile(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedJson\\mappedQuestions.json", jsonObject.toString());
        return jsonObject.toString();

    }

    public String findAndAddQuestion() throws AppException, IOException {

        String sampleIncomingQuestion = AppUtils.getFileContent("feedJson\\sampleIncomingQuestion.json");

        JSONObject jsonObject = new JSONObject(sampleIncomingQuestion);

        return addQuestion(jsonObject);
    }

    public JSONObject questionAgainstParagraphId(String paragraphId) throws AppException {
        JSONObject allQuestions = new JSONObject();
        if(paragraphId != null) {
            String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
            JSONObject jsonObject = new JSONObject(mappedQuestions);
            JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
            JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");
            JSONObject paragraphs = indexes.getJSONObject("paragraphs");
            JSONObject questions = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions");

            JSONArray questionIds = paragraphs.getJSONArray(paragraphId);



            if (questionIds != null && questionIds.length() > 0) {
                for (int i = 0; i < questionIds.length(); i++) {
                    allQuestions.put(questionIds.getString(i), questions.getString(questionIds.getString(i)));
                }
            }


        }else{
            allQuestions.put("message", "No Question Present on this flow !");
        }
        return allQuestions;
    }

    public JSONObject questionAgainstConceptNameComponentTypeComponentName(String conceptName, String componentType,  String componentName) throws AppException {
        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject jsonObject = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
        JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");
        JSONObject questions = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions");

        JSONObject qByConcept = indexes.getJSONObject(conceptName);
        JSONObject qByComponentByType = qByConcept.getJSONObject(componentType);
        JSONArray qByComponentByName = qByComponentByType.getJSONArray(componentName);

        JSONObject allQuestions = new JSONObject();

        for (int i = 0; i < qByComponentByName.length(); i++) {
            allQuestions.put(qByComponentByName.getString(i), questions.getString(qByComponentByName.getString(i)));
        }

        return allQuestions;
    }


}
