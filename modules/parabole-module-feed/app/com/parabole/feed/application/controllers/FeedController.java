package com.parabole.feed.application.controllers;

import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.services.LightHouseService;
import com.parabole.feed.platform.graphdb.LightHouse;
import play.mvc.Result;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by Sagir on 19-07-2016.
 */

public class FeedController extends BaseController{

    @Inject
    LightHouseService lightHouseService;


    public Result saveAllConcepts() {
        return ok(taggingUtilitiesServices.saveAllConcepts());
    }

    public Result getRawBindingDataValues(String fileName) throws AppException {

        final String configurationId = jenaTdbService.getRawBindingDataValues(fileName);

        return ok(configurationId);

    }

    public Result saveListOfSentenceLocationsAgainstAllConcepts() throws com.parabole.feed.platform.exceptions.AppException {

        final Integer configurationId = taggingUtilitiesServices.saveListOfSentenceLocationsAgainstAllConcepts();

        return ok(configurationId.toString());

    }

    public Result getTheassignments() throws AppException {

        final String configurationId = taggingUtilitiesServices.getConfigurationDetailWithnodeinfo();

        return ok(configurationId);

    }

    public Result startContentParser(String file) throws AppException, IOException {

        final String result = taggingUtilitiesServices.startContentParser(file);
        return ok(result);

    }

    public Result getAllTopicsSubTopics(String file) throws AppException, Exception {
        final String result = taggingUtilitiesServices.getAllTopicsSubTopics(file);
        return ok(result);

    }

    public Result saveBaselTopicToSubtopic(String file) throws AppException, Exception {
        final String result = taggingUtilitiesServices.saveBaselTopicToSubtopic(file);
        return ok(result);

    }

    public Result saveParagraphsAndAssociateItWithBaselSubTopic(String file) throws AppException, Exception {
        final String result = taggingUtilitiesServices.saveParagraphsAndAssociateItWithBaselSubTopic(file);
        return ok(result);

    }

    public Result saveBaselConcepts(String file) throws Exception {
        final String result = taggingUtilitiesServices.saveBaselConcepts(file);
        return ok(result);

    }

    public Result getParagraphsByContent(String concept) throws AppException, com.parabole.feed.platform.exceptions.AppException, IOException {

        final String result = taggingUtilitiesServices.getParagraphsByContent(concept);
        return ok(result);

    }

    public Result saveSectionsFromParagraphJSon(String filename) throws Exception {
        String fileType = "FASB";
        final String result = taggingUtilitiesServices.saveSectionsFromParagraphJSon(filename, fileType);
        return ok(result);
    }

    public Result createConceptNodesFromParagraph(String filename) throws Exception {
        final String result = taggingUtilitiesServices.createConceptNodesFromParagraph(filename);
        return ok(result);
    }

    public Result createComponentTypseAndAssignToConcept() throws Exception {
        final String result = taggingUtilitiesServices.createComponentTypseAndAssignToConcept();
        return ok(result);
    }

    public Result createComponentAndAssignToComponentType() throws Exception {
        final String result = taggingUtilitiesServices.createComponentAndAssignToComponentType();
        return ok(result);
    }

    public Result createBusinesSegmentAndAssignComponent() throws Exception {
        final String result = taggingUtilitiesServices.createBusinesSegmentAndAssignComponent();
        return ok(result);
    }

    public Result createProductAndAssignToBusinessSegment() throws Exception {
        final String resultData = taggingUtilitiesServices.createProductAndAssignToBusinessSegment();
        return ok(resultData);
    }

    /*public Result createConceptComponent() throws Exception {

        final String result = taggingUtilitiesServices.saveSectionsFromParagraphJSon();
        return ok(result);

    }*/


    public Result getAllParagraphInTextFile(String fileType) throws Exception {
        final String resultData = taggingUtilitiesServices.getAllParagraphInTextFile(fileType);
        return ok(resultData);
    }

    public Result getAllFileExcept() throws Exception {
        List<Map<String, Object>> resultData = lightHouseService.getAllFileExcept("BANKFILE");
        return ok(resultData.toString());
    }

    public Result getFeedFileNames() throws Exception {
        final String resultData = documentCfgService.getFeedFileNames("BASEL").toString();
        return ok(resultData);
    }
}
