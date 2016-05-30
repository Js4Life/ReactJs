// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
package com.parabole.rda.application.services;

import com.google.inject.Inject;
import com.parabole.rda.application.global.RdaAppConstants.ConfigurationType;
import com.parabole.rda.platform.aggregator.AssimilatorsAggregator;
import com.parabole.rda.platform.aggregator.LogicalViewFinder;
import com.parabole.rda.platform.exceptions.AppException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Play Authentication mechanism using Annotation.
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */

public class AssimilationServices {
	
	@Inject
    protected CoralConfigurationService coralConfigurationService;
	
	@Inject
	protected OctopusSemanticService octopusSemanticService;

	
	public String AssimilationAggregation(JSONObject jsonFileContent){
		JSONObject rootGraph = (JSONObject) jsonFileContent.get("estuary");
		JSONObject graphTomerge = (JSONObject) jsonFileContent.get("river");
		AssimilatorsAggregator newReport = new AssimilatorsAggregator(rootGraph);
		newReport.compose(graphTomerge);
		return newReport.getFinalAggregatedGraph().toString();	
	}
	
	public String AssimilationAggregationByListOfAggregate(JSONArray listOfAggregate) throws AppException, JSONException{
		AssimilatorsAggregator newReport = null;
		for (int i = 0; i < listOfAggregate.length(); i++) {	
			if(i==0){
				JSONObject rootGraph = (JSONObject) coralConfigurationService.getConfigurationByName(listOfAggregate.get(i).toString());
				newReport = new AssimilatorsAggregator(rootGraph);
			}else{
				JSONObject graphTomerge = (JSONObject) coralConfigurationService.getConfigurationByName(listOfAggregate.get(i).toString());
				newReport.compose(graphTomerge);
			}
		}
		return newReport.getFinalAggregatedGraph().toString();	
	}
	
	public String getLogicalViewForCompositeAggregatedGraphNode(Integer nodeId) throws AppException{
		System.out.println(nodeId);
		String name = "";
		String nodeName = octopusSemanticService.getVertexNameById(nodeId);
		System.out.println(nodeName.toString());
		List<Map<String, String>> combinedLogicalView = coralConfigurationService.getConfigurationByName(nodeName);
		LogicalViewFinder logicalViewFinder = new LogicalViewFinder();
		JSONObject jsonObject = new JSONObject();
		for (Map<String, String> map : combinedLogicalView) {
			System.out.println("hello " +map.get("name"));
			name = map.get("name");
		}
		jsonObject.put("name", name);
		jsonObject.put("details",  logicalViewFinder.findLogicalViewAssociatedWithNode(combinedLogicalView));
		JSONArray jsonArray = new JSONArray();
		String result = jsonArray.put(jsonObject).toString();
		return result;
	}

	public String AssimilationAggregationByFetchingGraphById(JSONArray arrayOfGraphNames) throws AppException {
		List<Map<String, String>> outputList = coralConfigurationService.getConfigurationByNameOnly(arrayOfGraphNames.getString(0));
		JSONObject rootGraph	 = new JSONObject(outputList.get(0).get("details"));
		AssimilatorsAggregator newReport = new AssimilatorsAggregator(rootGraph);
		for (int i = 1; i < arrayOfGraphNames.length(); i++) {
			List<Map<String, String>> newOutputList = coralConfigurationService.getConfigurationByNameOnly(arrayOfGraphNames.getString(i));
			JSONObject graphTomerge	 = new JSONObject(newOutputList.get(0).get("details"));
			newReport.compose(graphTomerge);
		}
		return newReport.getFinalAggregatedGraph().toString();	
	}

	public String getNewGraphAsPerAggregationType(String userId, String configarationName, String configarationType) throws AppException, JSONException {
		String finalOutput = new String();
		if(configarationType == ConfigurationType.AGGREGATION.toString()){
        	List<Map<String, String>> output = coralConfigurationService.getConfigurationByName(userId, configarationName);
        	finalOutput = output.get(0).get("details");
        }else if(configarationType.equalsIgnoreCase(ConfigurationType.COMPOSITEAGGREGATION.toString()) ){
        	List<Map<String, String>> output = coralConfigurationService.getConfigurationByName(userId, configarationName);
        	finalOutput = output.get(0).get("details");
        }
		return finalOutput;
	}
	
}
