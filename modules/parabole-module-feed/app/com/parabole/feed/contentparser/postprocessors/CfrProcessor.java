package com.parabole.feed.contentparser.postprocessors;

import com.parabole.feed.contentparser.models.cfr.CfrDocMeta;
import com.parabole.feed.contentparser.models.cfr.DocumentElement;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.*;

/**
 * Created by parabole on 12/8/2016.
 */
public class CfrProcessor {
    private List<DocumentElement> toc;
    private Map<String, List<DocumentElement>> body;
    private String filePath;
    private DocumentBuilderFactory builderFactory;
    private DocumentBuilder builder;
    private CfrDocMeta docMeta;
    HashMap<String, Set<String>> conceptParaMap = new HashMap<>();
    List<String> concepts = new ArrayList<>();
    private StringBuilder levelId;

    public CfrProcessor(String filePath, List<String> concepts, JSONObject glossaryMetaData) {
        this.toc = new ArrayList<>();
        this.body = new LinkedHashMap<>();
        this.filePath = filePath;
        this.concepts = concepts;
        if(glossaryMetaData != null){
            this.docMeta = getGlossaryMetadata(glossaryMetaData);
        } else {
            this.docMeta = getGlossaryMetadata();
        }
        this.builderFactory = DocumentBuilderFactory.newInstance();
        this.builder = null;
    }

    public void buildItemTree(){
        try{
            builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(filePath);
            String expression = "/RULE/SUPLINF";
            XPath xPath =  XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

            Element root =(Element) nodeList.item(0);
            NodeList hds = root.getChildNodes();
            List<DocumentElement> paraList = null;
            Element prevHead = null;
            DocumentElement prevDocElement = null;
            for (int i = 0; i < hds.getLength(); i++) {
                Node nNode = hds.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = nNode.getNodeName();
                    Element element = (Element) nNode;
                    if(nodeName.equals("HD")){
                        if(prevHead != null && !prevHead.getAttribute("SOURCE").equals("HED")){
                            for (DocumentElement aPara:paraList) {
                                String levelId = prevDocElement.getLevelId() + "-P" + aPara.getIndex();
                                aPara.setLevelId(levelId);
                                aPara.setName(levelId);
                                postProcessParagraph(aPara);
                            }
                            body.put(prevDocElement.getLevelId(), paraList);
                        }
                        paraList = new ArrayList<>();
                        if(prevHead != null) {
                            String prevHeadLevelStr = prevHead.getAttribute("SOURCE").replaceAll("\\D+", "");
                            String currHeadLevelStr = element.getAttribute("SOURCE").replaceAll("\\D+", "");
                            if (!prevHeadLevelStr.isEmpty() && !currHeadLevelStr.isEmpty()) {
                                int prevHeadLevel = Integer.parseInt(prevHeadLevelStr);
                                int currHeadLevel = Integer.parseInt(currHeadLevelStr);
                                if(currHeadLevel > prevHeadLevel) {
                                    DocumentElement currDocElement = buildDocElement(element, currHeadLevel, false);
                                    currDocElement.setIndex(prevDocElement.getChildren().size()+1);
                                    currDocElement.setLevelId(prevDocElement.getLevelId() + "-" + currDocElement.getIndex());
                                    prevDocElement.addChildren(currDocElement);
                                    prevDocElement = currDocElement;
                                } else {
                                    List<DocumentElement> siblingList = getSiblingsAtPreviousLevel(toc, currHeadLevel);
                                    DocumentElement currDocElement = buildDocElement(element, currHeadLevel, false);
                                    currDocElement.setIndex(siblingList.size()+1);
                                    levelId.append(currDocElement.getIndex());
                                    currDocElement.setLevelId(levelId.toString());
                                    siblingList.add(currDocElement);
                                    prevDocElement = currDocElement;
                                }
                            } else if(!currHeadLevelStr.isEmpty()){
                                int currHeadLevel = Integer.parseInt(currHeadLevelStr);
                                prevDocElement = buildDocElement(element, currHeadLevel, false);
                                prevDocElement.setIndex(1);
                                prevDocElement.setLevelId(docMeta.getMetaDocName() + "-1");
                                toc.add(prevDocElement);
                            }
                        }
                        prevHead = element;
                    } else if(nodeName.equals("P")){
                        String parentLevelStr = prevHead.getAttribute("SOURCE").replaceAll("\\D+", "");
                        if(!parentLevelStr.isEmpty()){
                            int parentLevel = Integer.parseInt(parentLevelStr);
                            DocumentElement aPara = buildDocElement(element, parentLevel+1, true);
                            aPara.setIndex(paraList.size()+1);
                            paraList.add(aPara);
                        }
                    }
                }
            }
            if(prevHead != null){
                for (DocumentElement aPara:paraList) {
                    String levelId = prevDocElement.getLevelId() + "-P" + aPara.getIndex();
                    aPara.setLevelId(levelId);
                    aPara.setName(levelId);
                }
                body.put(prevDocElement.getLevelId(), paraList);
            }
        } catch (ParserConfigurationException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<DocumentElement> getSiblingsAtPreviousLevel(List<DocumentElement> tempTreeData, int level){
        levelId = new StringBuilder(docMeta.getMetaDocName()).append("-");
        if (tempTreeData.size() > 0) {
            for (int i = 1; i < level; i++) {
                try {
                    DocumentElement documentElement = tempTreeData.get(tempTreeData.size() - 1);
                    List<DocumentElement> childeren = documentElement.getChildren();
                    tempTreeData = childeren;
                    levelId.append(documentElement.getIndex()).append("-");
                }catch (Exception e){
                    return tempTreeData;
                }
            }
        }
        return tempTreeData;
    }

    private DocumentElement buildDocElement(Element element, int level, Boolean isParagraph){
        DocumentElement documentElement = new DocumentElement();
        if(isParagraph)
            documentElement.setElementType(DocumentElement.ElementTypes.PARAGRAPH.toString());
        else
            documentElement.setElementType("Level-" + level);

        StringBuilder sb = new StringBuilder();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node nNode = children.item(i);
            if (nNode.getNodeType() == Node.TEXT_NODE) {
                sb.append(nNode.getTextContent());
            }
        }

        documentElement.setContent(sb.toString());
        documentElement.setLevel(level);
        return documentElement;
    }

    private void indexParagraphByConcepts(String concept, DocumentElement paragraph){
        Set<String> paraIds = null;
        if( this.conceptParaMap.containsKey(concept))
            paraIds = this.conceptParaMap.get(concept);
        else{
            paraIds = new TreeSet<>();
            this.conceptParaMap.put(concept,paraIds);
        }
        if(paragraph.getContent().toUpperCase().indexOf(concept.toUpperCase()) != -1) {
            System.out.println("paraIds = " + paraIds);
            if(!paraIds.contains(paragraph.getLevelId()))
                paraIds.add(paragraph.getLevelId());
        }
    }

    private void postProcessParagraph(DocumentElement para){
        //Is Ihe Para Obsolete
        for(String concept : this.concepts){
            indexParagraphByConcepts(concept, para) ;
        }
    }

    public List<DocumentElement> getToc() {
        return toc;
    }

    public Map<String, List<DocumentElement>> getBody() {
        return body;
    }

    public HashMap<String, Set<String>> getConceptParaMap() {
        return conceptParaMap;
    }

    private CfrDocMeta getGlossaryMetadata() {
        CfrDocMeta cfrDocMeta = new CfrDocMeta();
        cfrDocMeta.setMetaDocName("cfr1");
        return cfrDocMeta;
    }

    private CfrDocMeta getGlossaryMetadata(JSONObject glossaryMetaData) {
        CfrDocMeta cfrDocMeta = new CfrDocMeta();
        cfrDocMeta.setMetaDocName(glossaryMetaData.getString("levelIdPrefix"));
        return cfrDocMeta;
    }
}
