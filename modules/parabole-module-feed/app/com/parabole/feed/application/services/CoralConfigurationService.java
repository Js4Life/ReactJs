package com.parabole.feed.application.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.application.global.CCAppConstants.ConfigurationType;
import com.parabole.feed.platform.exceptions.AppException;
import com.parabole.feed.platform.graphdb.Coral;
import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class CoralConfigurationService {

    @Inject
    private Coral coral;

  //  @Inject
  //  protected OctopusSemanticService octopusSemanticService;

    public Map<Integer, String> getConfigurationNames(final String userId, final String configurationType) throws AppException {
        Validate.notNull(userId, "'userId' cannot be null!");
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        return coral.getConfigurationNames(userId, configurationType);
    }

    public List<Map<String, String>> getConfigurationByUserId(final String userId, final String configurationType) throws AppException {
        Validate.notNull(userId, "'userId' cannot be null!");
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        return coral.getConfigurationByUserId(userId, configurationType);
    }
    
    public List<Map<String, String>> getConfigurationByName(final String configurationName) throws AppException {
        Validate.notNull(configurationName, "'configurationName' cannot be null!");
        return coral.getConfigurationByName(configurationName);
    }

    public List<Map<String, String>> getConfigurationByNameOnly(final String configurationName) throws AppException {
        Validate.notNull(configurationName, "'configurationName' cannot be null!");
        return coral.getConfigurationInfoByNameOnly(configurationName);
    }

    public List<Map<String, String>> getConfigurationByName(final String userId, final String configurationName, final boolean useLikePattern) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notBlank(configurationName, "'configurationType' cannot be empty!");
        return coral.getConfigurationByName(userId, configurationName, useLikePattern);
    }

    public String getConfigurationDetail(final Integer configurationId) throws AppException {
        Validate.notNull(configurationId, "'configurationId' cannot be null!");
        return coral.getConfigurationDetail(configurationId);
    }

    public void deleteConfiguration(final Integer configurationId) throws AppException {
        Validate.notNull(configurationId, "'configurationId' cannot be null!");
        coral.deleteConfiguration(configurationId);
    }

    public Integer saveConfigurationByNameAndDetails(final String userId, final JSONObject jsonObject, final ConfigurationType configurationType) throws AppException {
        Validate.notNull(jsonObject, "'json' cannot be null!");
        final String configurationName = jsonObject.getString("name");
        final String configurationDetails = jsonObject.getString("details");
        Validate.notBlank(configurationName, "'configurationName' cannot be empty!");
        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        return coral.saveConfiguration(userId, configurationType.toString(), configurationName, configurationDetails);
    }
    
    public Integer saveConfiguration(final String userId, final JsonNode json, final ConfigurationType configurationType) throws AppException {
	        Validate.notNull(json, "'json' cannot be null!");
	        final String configurationName = json.findPath("name").textValue();
	        final String configurationDetails = json.findPath("details").textValue();
	        Validate.notBlank(configurationName, "'configurationName' cannot be empty!");
	        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
	        Validate.notNull(configurationType, "'configurationType' cannot be null!");
	        return coral.saveConfiguration(userId, configurationType.toString(), configurationName, configurationDetails);
    }


    public Integer saveConfiguration(final String userId, final String configurationType, final String configurationName, final String configurationDetails) throws AppException {
        Validate.notNull(userId, "'json' cannot be null!");
        Validate.notBlank(configurationName, "'configurationName' cannot be empty!");
        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        return coral.saveConfiguration(userId, configurationType.toString(), configurationName, configurationDetails);
    }

    public Integer saveConfiguration(final String userId, final String configurationName, final String configurationDetails, final ConfigurationType configurationType, final byte[] fileBytes) throws AppException {
        Validate.notBlank(userId, "'userId' cannot be empty!");
        Validate.notBlank(configurationName, "'configurationName' cannot be empty!");
        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        final Integer configurationId = coral.saveConfiguration(userId, configurationType.toString(), configurationName, configurationDetails);
        if (fileBytes != null) {
            coral.saveFile(userId, configurationId, fileBytes);
        }
        return configurationId;
    }

    public String saveContextConceptMap(final String context_url, final String context_name, final String concept_url) throws AppException {
        Validate.notNull(context_url, "'configurationType' cannot be null!");
        Validate.notNull(context_name, "'configurationType' cannot be null!");
        Validate.notNull(concept_url, "'configurationType' cannot be null!");
        String retData = "{Status: true, message: saved }";
        try {
            final Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("CONTEXT_URI", concept_url);
            dataMap.put("CONTEXT_NAME", context_name);
            dataMap.put("CONCEPT_URI", concept_url);
            coral.saveContextConceptMap(CCAppConstants.APP_CONTEXT_CONCEPT_MAPPING, dataMap);
        }catch (Exception e){
            retData = "{Status: false, message: "+e.toString()+"}";
            e.printStackTrace();
        }
        return retData;
    }

    public List<String> getConceptsAgainstContextUri(String contextUri) throws AppException {
        Validate.notNull(contextUri, "'contextUri' cannot be null");
        return coral.getConceptsAgainstContextUri(contextUri);
    }

    public List<Map<String, String>> getContextsAgainstConceptUri(String conceptUri) throws AppException {
        Validate.notNull(conceptUri, "'contextUri' cannot be null");
        return coral.getContextsAgainstConceptUri(conceptUri);
    }

    public Integer saveViewConfiguration(final String userId, final JsonNode json) throws AppException {
        final String configurationName = json.findPath("name").textValue();
        final String configurationType = json.findPath("type").textValue();
        final String configurationDetails = json.findPath("details").textValue();
        Validate.notBlank(configurationName, "'configurationName' cannot be empty!");
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
        return coral.saveConfiguration(userId, configurationType, configurationName, configurationDetails);
    }

    public Integer saveAggregateMapping(final String userId, final JsonNode json, final ConfigurationType configurationType) throws AppException {
        final String configurationNameRaw = json.findPath("name").textValue();
        final String configurationDetails = json.findPath("details").textValue();
        Validate.notBlank(configurationNameRaw, "'configurationName' cannot be empty!");
        final StringBuffer configurationName = new StringBuffer(configurationNameRaw);
        configurationName.append("_Mapping");
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
        return coral.saveConfiguration(userId, configurationType.toString(), configurationName.toString(), configurationDetails);
    }

    public Integer saveRootNodeMapping(final String userId, final JsonNode json, final ConfigurationType configurationType) throws AppException {
        final String configurationNameRaw = json.findPath("name").textValue();
        final String configurationDetails = json.findPath("details").textValue();
        Validate.notBlank(configurationNameRaw, "'configurationName' cannot be empty!");
        final StringBuffer configurationName = new StringBuffer(configurationNameRaw);
        configurationName.append(CCAppConstants.BASENODEMAPPING_SUFFIX);
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
        return coral.saveConfiguration(userId, configurationType.toString(), configurationName.toString(), configurationDetails);
    }

    public Integer saveDataNodeMapping(final String userId, final JsonNode json, final ConfigurationType configurationType) throws AppException {
        final String configurationNameRaw = json.findPath("name").textValue();
        final String configurationDetails = json.findPath("details").textValue();
        Validate.notBlank(configurationNameRaw, "'configurationName' cannot be empty!");
        final StringBuffer configurationName = new StringBuffer(configurationNameRaw);
        configurationName.append(CCAppConstants.DATANODEMAPPING_SUFFIX);
        Validate.notNull(configurationType, "'configurationType' cannot be null!");
        Validate.notBlank(configurationDetails, "'configurationDetails' cannot be empty!");
        return coral.saveConfiguration(userId, configurationType.toString(), configurationName.toString(), configurationDetails);
    }

    public JSONObject getAllRootNodesMapping(final String userId) throws AppException {
        final JSONObject retJson = new JSONObject();
        final List<Map<String, String>> cfgList = coral.getConfigurationByUserId(userId, ConfigurationType.BASENODE_CFG.toString());
        for (final Map<String, String> cfg : cfgList) {
            String name = cfg.get(CCAppConstants.ATTR_DATASOURCE_MAPPING_NAME);
            final int idx = name.indexOf(CCAppConstants.BASENODEMAPPING_SUFFIX);
            if (idx != -1) {
                name = name.substring(0, idx);
            }
            final String details = cfg.get(CCAppConstants.ATTR_VIEWCREATION_DETAILS);
            retJson.put(name, new JSONArray(details));
        }
        return retJson;
    }

/*    public List<Map<String, String>> getDBViewsForANode(final String userId, final Integer nodeId) throws AppException {
        final String nodeName = octopusSemanticService.getVertexNameById(nodeId);
        final List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
        StringBuffer baseSql = new StringBuffer("SELECT * FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_NAME LIKE '");
        baseSql.append(nodeName);
        baseSql.append("-%'");
        final List<Map<String, Object>> dbViewList = coral.executeQuery(baseSql.toString());
        baseSql = new StringBuffer("SELECT * FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_NAME LIKE '%>");
        baseSql.append(nodeName);
        baseSql.append("'");
        final List<Map<String, Object>> dbViewList1 = coral.executeQuery(baseSql.toString());
        dbViewList.addAll(dbViewList1);
        if (dbViewList.size() > 0) {
            for (final Map<String, Object> detMap : dbViewList) {
                final String detailsStr = (String) detMap.get(AppConstants.ATTR_CFG_DETAILS);
                final Map<String, String> detailObj = new HashMap<String, String>();
                detailObj.put(CCAppConstants.ATTR_DATASOURCE_MAPPING_NAME, (String) detMap.get(AppConstants.ATTR_CFG_NAME));
                detailObj.put(CCAppConstants.ATTR_VIEWCREATION_DETAILS, detailsStr);
                retList.add(detailObj);
            }
        }
        return retList;
    }*/

    public List<Map<String, String>> getConfigurationByName(final String userId, final String configurationName) throws AppException {
        // return coral.getConfigurationBySpecificName(userId, configurationName);
    	return coral.getConfigurationByName(userId, configurationName, false);
    }

    public List<Map<String, String>> getConfigurationByPartialName(final String userId, final String cfgName) throws AppException {
        return coral.getConfigurationByName(userId, cfgName, true);
    }

    public void deleteConfigurationDetailsByName(final String userId, final String configurationName) throws AppException {
        coral.deleteConfigurationByName(userId, configurationName);
    }

    public void deleteAggergate(final String userId, final String aggregateName, final boolean deleteEdgeMappings) throws AppException {
        final List<String> sqls = new ArrayList<String>();
        final String baseSql = "DELETE FROM " + CCAppConstants.RDA_USER_CONFIGS + " WHERE CFG_NAME ";
        StringBuffer tmp = null;
        tmp = new StringBuffer(baseSql);
        tmp.append(" LIKE '%");
        tmp.append(aggregateName).append("'");
        sqls.add(tmp.toString());
        if (deleteEdgeMappings) {
            List<Map<String, String>> cfgList = coral.getConfigurationByName(userId, aggregateName + CCAppConstants.AGGREGATEMAPPING_SUFFIX, false);
            final List<String> classNames = new ArrayList<String>();
            JSONArray edgeList = null;
            if (cfgList.size() > 0) {
                final JSONObject mappingDetails = new JSONObject(cfgList.get(0).get(CCAppConstants.ATTR_VIEWCREATION_DETAILS));
                if (mappingDetails.has(CCAppConstants.ATTR_AGGREGATEMAPPING)) {
                    edgeList = mappingDetails.getJSONArray(CCAppConstants.ATTR_AGGREGATEMAPPING);
                }
            }
            if (edgeList != null) {
                for (int i = 0; i < edgeList.length(); i++) {
                    final String edgeName = edgeList.getString(i);
                    cfgList = coral.getConfigurationByName(userId, edgeName, false);
                    if (cfgList.size() > 0) {
                        classNames.add(new JSONObject(cfgList.get(0).get(CCAppConstants.ATTR_VIEWCREATION_DETAILS)).getString(CCAppConstants.ATTR_DATASOURCE_MAPPING_NAME));
                    }
                    tmp = new StringBuffer(baseSql);
                    tmp.append(" = '");
                    tmp.append(edgeList.getString(i)).append("'");
                    sqls.add(tmp.toString());
                }
                if (classNames.size() > 0) {
                    coral.dropTables(classNames);
                }
            }
        }
        tmp = new StringBuffer(baseSql);
        tmp.append(" = '");
        tmp.append(aggregateName).append(CCAppConstants.AGGREGATEMAPPING_SUFFIX).append("'");
        sqls.add(tmp.toString());
        tmp = new StringBuffer(baseSql);
        tmp.append(" = '");
        tmp.append(aggregateName).append("'");
        sqls.add(tmp.toString());
        coral.executeBatchUpdate(sqls);
    }
    
    public String saveConceptTags(final JsonNode json) throws AppException {
        Validate.notNull(json, "'json' cannot be null!");
        Map<String, Boolean> dataSet = new HashMap<String, Boolean>(); 
        final String nodeId = json.findPath("nodeId").textValue();
        final String nodeName = json.findPath("nodeName").textValue();
        dataSet.put("isBaseNode", Boolean.valueOf(json.findPath("isBaseNode").textValue()));
        dataSet.put("isImpactRoot", Boolean.valueOf(json.findPath("isImpactRoot").textValue()));
        dataSet.put("isImpactDestination", Boolean.valueOf(json.findPath("isImpactDestination").textValue()));
        return coral.saveConceptTags(nodeId, nodeName, dataSet);
    }

	public Map<Integer, String> getAllConceptByTag(String tag) throws AppException {
		Validate.notNull(tag, "'tag' cannot be null!");
		return coral.getAllConceptByTag(tag);

	}
}
