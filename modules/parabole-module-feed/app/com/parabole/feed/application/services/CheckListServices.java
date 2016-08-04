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

    public String findAndAddAnswer() throws AppException, IOException {

        String sampleIncomingAnswer = AppUtils.getFileContent("feedJson\\answersToAdd.json");

        JSONObject jsonObject = new JSONObject(sampleIncomingAnswer);

        return addAnswer(jsonObject);
    }

    private String addAnswer(JSONObject answersToAdd) throws AppException, IOException {
        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject fullJson = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = fullJson.getJSONObject("FASBAccntStandards");
        JSONObject alreadyAddedAnswers = new JSONObject();

        if(fileMappedQuestionsfromFASBAccntStandards.has("answers")) {
            alreadyAddedAnswers = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("answers");
        }
        else{
            JSONObject answer = new JSONObject();
            fileMappedQuestionsfromFASBAccntStandards.put("answers", answer);
            alreadyAddedAnswers = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("answers");
        }


        JSONArray answersToAddArray = answersToAdd.getJSONArray("answers");
        // looping all the answers ----------------->
        if(answersToAddArray != null & answersToAddArray.length() > 0 )
            for (int i = 0; i < answersToAddArray.length(); i++) {
                if(!alreadyAddedAnswers.has(answersToAddArray.getString(i)))
                    alreadyAddedAnswers.put(answersToAddArray.getString(i), true);
            }

        // Saving ------------------------------------>
        AppUtils.writeFile(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedJson\\mappedQuestions.json", fullJson.toString());
        return fullJson.toString();

    }

    public JSONObject questionAgainstParagraphId(String paragraphId) throws AppException {

        JSONObject finalReturn = new JSONObject();

        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject jsonObject = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
        JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");
        JSONObject paragraphs = indexes.getJSONObject("paragraphs");
        JSONObject questions = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions");


        JSONObject allQuestions = new JSONObject();
        JSONObject status = new JSONObject();
        JSONArray questionIds = new JSONArray();

            if(paragraphs.has(paragraphId)) {
                questionIds = paragraphs.getJSONArray(paragraphId);
                status.put("haveData", true);
                status.put("message", "It has total of "+questionIds.length()+" paragraphs");
            }else {
                status.put("haveData", false);
                status.put("message", "No Question Present on this flow !");
            }


            if (questionIds != null && questionIds.length() > 0) {
                for (int i = 0; i < questionIds.length(); i++) {
                    allQuestions.put(questionIds.getString(i), questions.getString(questionIds.getString(i)));
                }
            }

        finalReturn.put("questions", allQuestions);
        finalReturn.put("status", status);
        return finalReturn;
    }

    public JSONObject questionAgainstConceptNameComponentTypeComponentName(String conceptName, String componentType,  String componentName) throws AppException {

        JSONObject finalReturn = new JSONObject();
        JSONObject status = new JSONObject();

        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject jsonObject = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
        JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");
        JSONObject questions = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions");

        JSONObject qByConcept = new JSONObject();

        if(indexes.has(conceptName)) {
            qByConcept = indexes.getJSONObject(conceptName);
            status.put("have concept Name", true);
            status.put("message", "conceptName : "+conceptName);
        }else {
            status.put("have concept Name", false);
            status.put("message", "no such concept Name:&: input error");
        }



        JSONObject qByComponentByType = new JSONObject();
        JSONArray qByComponentByName = new JSONArray();


        if(qByConcept.has(componentType)) {
            qByComponentByType = qByConcept.getJSONObject(componentType);
            status.put("have Component Type", true);
            status.put("message", "ComponentType : "+componentType);
        }else {
            status.put("have Component Type", false);
            status.put("message", "input error");
        }


        if(qByConcept.has(componentName)) {
            qByComponentByName = qByComponentByType.getJSONArray(componentName);
            status.put("have component Name", true);
            status.put("message", "componentName : "+componentName);
        }else {
            status.put("have Component Name", false);
            status.put("message", "Input Error");
        }


        JSONObject allQuestions = new JSONObject();

        for (int i = 0; i < qByComponentByName.length(); i++) {
            allQuestions.put(qByComponentByName.getString(i), questions.getString(qByComponentByName.getString(i)));
        }

        finalReturn.put("questions", allQuestions);
        finalReturn.put("status", status);
        return finalReturn;
    }


}
