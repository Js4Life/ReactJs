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



    private String addQuestion(JSONObject incomingQuestion) throws AppException, IOException {


        String mappedQuestions = AppUtils.getFileContent("feedJson\\mappedQuestions.json");
        JSONObject jsonObject = new JSONObject(mappedQuestions);
        JSONObject fileMappedQuestionsfromFASBAccntStandards = jsonObject.getJSONObject("FASBAccntStandards");
        JSONObject indexes = fileMappedQuestionsfromFASBAccntStandards.getJSONObject("indexes");



        // looping all the questions ----------------->
        JSONArray allQuestions = incomingQuestion.getJSONArray("questions");
        for (int i = 0; i < allQuestions.length(); i++) {
            JSONObject singleQuestionJsonObject = allQuestions.getJSONObject(i);

            String QuestionId = getUniqueID();
            JSONArray paragraphAgainstId = new JSONArray();

            fileMappedQuestionsfromFASBAccntStandards.getJSONObject("questions").put(QuestionId, singleQuestionJsonObject.getString("text"));
            if (indexes.getJSONObject("paragraphs").has(incomingQuestion.getString("paragraphId")))
                paragraphAgainstId = indexes.getJSONObject("paragraphs").getJSONArray(incomingQuestion.getString("paragraphId"));

            // --------------> if paragraph id not present
            if(paragraphAgainstId != null && paragraphAgainstId.length() > 0){
                System.out.println("paragraphAgainstId = null " + paragraphAgainstId);
                paragraphAgainstId.put(QuestionId);
            }
            else{
                System.out.println("paragraphAgainstId = " + paragraphAgainstId);
                JSONArray listOfPid = new JSONArray();
                listOfPid.put(QuestionId);
                indexes.getJSONObject("paragraphs").put(incomingQuestion.getString("paragraphId"), listOfPid);
            }

        }

        // Saving ------------------------------------>
        AppUtils.writeFile(environment.rootPath() + "\\modules\\parabole-module-feed\\conf\\feedJson\\mappedQuestions.json", mappedQuestions.toString());
        return jsonObject.toString();

    }






    public String findAndAddQuestion() throws AppException, IOException {

        String sampleIncomingQuestion = AppUtils.getFileContent("feedJson\\sampleIncomingQuestion.json");

        JSONObject jsonObject = new JSONObject(sampleIncomingQuestion);

        return addQuestion(jsonObject);
    }


}
