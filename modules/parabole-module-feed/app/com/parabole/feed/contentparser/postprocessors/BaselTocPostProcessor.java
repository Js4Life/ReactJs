package com.parabole.feed.contentparser.postprocessors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parabole.feed.contentparser.IDocIndexBuilder;
import com.parabole.feed.contentparser.models.basel.BaselDocMeta;
import com.parabole.feed.contentparser.models.basel.DocumentElement;
import com.parabole.feed.contentparser.models.common.LineElement;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Created by parabole on 10/19/2016.
 */
public class BaselTocPostProcessor implements IPostProcessor {
    IDocIndexBuilder docIndexBuilder;
    BaselDocMeta docMeta;
    List<DocumentElement> treeData;
    List<DocumentElement> flatParaList;
    DocumentElement lastInsertedParentNode;
    DocumentElement lastInsertedChildNode;
    StringBuilder levelId;

    public BaselTocPostProcessor(IDocIndexBuilder docIndexBuilder, JSONObject glossaryMetaData) {
        this.docIndexBuilder = docIndexBuilder;
        if(glossaryMetaData != null){
            this.docMeta = getGlossaryMetadata(glossaryMetaData);
        } else {
            this.docMeta = getGlossaryMetadata();
        }
        this.treeData = new ArrayList<>();
        this.flatParaList = new ArrayList<>();
    }

    public List<DocumentElement> buildItemTree() throws IOException {
        List<ParagraphElement> paraList = docIndexBuilder.startProcessing(docMeta);
        return buildTree(paraList);
    }

    /*private List<DocumentElement> buildTree(List<ParagraphElement> paraList) {            //coordinate wise indentation
        for(ParagraphElement aPara : paraList) {
            LineElement lineElement = aPara.getLines().get(0);
            float currentStartX = lineElement.getLineStart();
            DocumentElement temp = buildDocElement(aPara, currentStartX);

            flatParaList.add(temp);
            if(lastInsertedParentNode != null){
                if(currentStartX > lastInsertedParentNode.getStartX()){
                    if((currentStartX > lastInsertedChildNode.getStartX()) && (lastInsertedParentNode.getStartX() != lastInsertedChildNode.getStartX())){
                        lastInsertedChildNode.addChildren(temp);
                        lastInsertedParentNode = lastInsertedChildNode;
                        lastInsertedChildNode = temp;
                    } else {
                        lastInsertedChildNode = temp;
                        lastInsertedParentNode.addChildren(lastInsertedChildNode);
                    }
                } else {
                    List<DocumentElement> siblingList = getSiblingsAtPreviousLevel(treeData, currentStartX);
                    if(siblingList != null){
                        lastInsertedChildNode = lastInsertedParentNode = temp;
                        siblingList.add(lastInsertedParentNode);
                    }
                }
            } else {
                lastInsertedChildNode = lastInsertedParentNode = temp;
                treeData.add(lastInsertedParentNode);
            }
        }
        return treeData;
    }   */

    private List<DocumentElement> buildTree(List<ParagraphElement> paraList) {            //predefined pattern match wise indentation
        Map<Integer, String> levelSelector = docMeta.getLevelSelector();
        Boolean startParsing = false;
        for(ParagraphElement aPara : paraList) {
            for(LineElement aSentence : aPara.getLines()){
                if(aSentence.toString().startsWith(docMeta.getStartText())){
                    startParsing = true;
                }
                if(startParsing) {
                    DocumentElement temp = buildDocElement(aSentence);
                    if(temp == null)
                        continue;
                    for (Integer level : levelSelector.keySet()) {
                        Pattern p = Pattern.compile(levelSelector.get(level));
                        Matcher m = p.matcher(temp.getContent());
                        if (m.find()) {
                            if(isParagraphSelectorLevel(level))
                                flatParaList.add(temp);
                            temp.setLevel(level);
                            if(level == 1){
                                temp.setElementType(DocumentElement.ElementTypes.TOPIC);
                                int index = temp.getContent().indexOf(':');
                                if(index != -1) {
                                    String name = temp.getContent().substring(0, index);
                                    temp.setName(name);
                                }
                            }
                            else if(level == 2){
                                temp.setElementType(DocumentElement.ElementTypes.SUBTOPIC);
                                int index = temp.getContent().indexOf('.');
                                if(index != -1) {
                                    String name = temp.getContent().substring(0, index);
                                    temp.setName(name);
                                }
                            }
                            else if(level == 3){
                                temp.setElementType(DocumentElement.ElementTypes.SECTION);
                                int index = temp.getContent().indexOf('.');
                                if(index != -1) {
                                    String name = temp.getContent().substring(0, index);
                                    temp.setName(name);
                                }
                            }
                            //else temp.setElementType(DocumentElement.ElementTypes.OTHER);
                            else
                                break;                                                                      //Concidering upto level 3 (SECTION)
                            List<DocumentElement> siblingList = getSiblingsAtPreviousLevel(treeData, level);
                            temp.setIndex(siblingList.size()+1);
                            levelId.append(temp.getIndex());
                            temp.setLevelId(levelId.toString());
                            siblingList.add(temp);
                            break;
                        }
                    }
                }
                if(aSentence.toString().startsWith(docMeta.getEndText())) {
                    return treeData;
                }
            }
        }
        return treeData;
    }

    private Boolean isParagraphSelectorLevel(int level){
        int[] paraSelectorLevels = docMeta.getParagraphSelectorLevel();
        return IntStream.of(paraSelectorLevels).anyMatch(x -> x == level);
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

    /*private List<DocumentElement> getSiblingsAtPreviousLevel(List<DocumentElement> tempTreeData, float currentStartX){
        for (DocumentElement documentElement : tempTreeData) {
            if (currentStartX == documentElement.getStartX()) {
                return tempTreeData;
            } else {
                if(documentElement.getChildren().size() > 0)
                    return getSiblingsAtPreviousLevel(documentElement.getChildren(), currentStartX);
            }
        }
        return tempTreeData;
    }

    private DocumentElement buildDocElement(ParagraphElement paragraphElement, float currentStartX){
        DocumentElement documentElement = new DocumentElement();
        documentElement.setStartX(currentStartX);
        documentElement.setName(paragraphElement.toString().replaceAll(docMeta.getParaEndRegEx(), ""));
        return documentElement;
    }*/

    String tempContent = "";
    private DocumentElement buildDocElement(LineElement lineElement){
        DocumentElement documentElement = new DocumentElement();
        Pattern p = Pattern.compile(docMeta.getParaEndRegEx());
        Matcher m = p.matcher(lineElement.toString());
        if(!m.find()){
            tempContent = tempContent + lineElement.toString() + " ";
            return null;
        }
        tempContent += lineElement.toString();
        documentElement.setContent(tempContent.replaceAll(docMeta.getParaEndRegEx(), ""));
        tempContent = "";
        return documentElement;
    }

    //------------------------------------------------------
    // For CounterpartyCreditRisk-Standardized-Apr2014
    //------------------------------------------------------
    private BaselDocMeta getGlossaryMetadata() {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setMetaDocName("bank-document-1");
        baselDocMeta.setStartPage(2);
        baselDocMeta.setEndPage(2);
        baselDocMeta.setStartText("I. Background");
        baselDocMeta.setEndText("C. Time risk horizon");

        int[] paraSelectorLevels = {1, 2};
        baselDocMeta.setParagraphSelectorLevel(paraSelectorLevels);
        baselDocMeta.setParaEndRegEx("[.]{5,}[0-9]{1,}|[.]{5,}|\\d+$");

        Map<Integer, String> levelSelector = new HashMap<>();
        levelSelector.put(1, "^(I{1,}[a-z]|I{1,}|V{1,}|X{1,}|I{1,}V{1,}|V{1,}I{1,}).");
        levelSelector.put(2, "^[A-H].");

        baselDocMeta.setLevelSelector(levelSelector);

        return baselDocMeta;
    }
    /*private BaselDocMeta getGlossaryMetadata() {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setMetaDocName("basel4");
        baselDocMeta.setStartPage(3);
        baselDocMeta.setEndPage(3);
        baselDocMeta.setStartText("I. Introduction");
        baselDocMeta.setEndText("B. Part 4: Third Pillar; Section II Disclosure requirements");

        int[] paraSelectorLevels = {1, 2};
        baselDocMeta.setParagraphSelectorLevel(paraSelectorLevels);
        baselDocMeta.setParaEndRegEx("[.]{5,}[0-9]{1,}|[.]{5,}|\\d+$");

        Map<Integer, String> levelSelector = new HashMap<>();
        levelSelector.put(1, "^(I{1,}[a-z]|I{1,}|V{1,}|X{1,}|I{1,}V{1,}|V{1,}I{1,}).");
        levelSelector.put(2, "^[A-H].");

        baselDocMeta.setLevelSelector(levelSelector);

        return baselDocMeta;
    }*/

/*    private BaselDocMeta getGlossaryMetadata() {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setMetaDocName("basel1");
        baselDocMeta.setStartPage(5);
        baselDocMeta.setEndPage(10);
        baselDocMeta.setStartText("Part 2: The First Pillar – Minimum Capital Requirements");
        baselDocMeta.setEndText("6. Interest rate risk in the banking book");

        int[] paraSelectorLevels = {2, 3};
        baselDocMeta.setParagraphSelectorLevel(paraSelectorLevels);
        baselDocMeta.setParaEndRegEx("[.]{5,}[0-9]{1,}|[.]{5,}|\\d+$");

        Map<Integer, String> levelSelector = new HashMap<>();
        levelSelector.put(1, "^Part[ ]{1,}[0-9]{1,}:");
        levelSelector.put(2, "^(I{1,}[a-z]|I{1,}|V{1,}|X{1,}|I{1,}V{1,}|V{1,}I{1,}).");
        levelSelector.put(3, "^[A-H].");
        levelSelector.put(4, "^[1-9].");

        baselDocMeta.setLevelSelector(levelSelector);

        return baselDocMeta;
    }*/

    /*private BaselDocMeta getGlossaryMetadata() {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setMetaDocName("basel3");
        baselDocMeta.setStartPage(3);
        baselDocMeta.setEndPage(3);
        baselDocMeta.setStartText("I. Background");
        baselDocMeta.setEndText("IV.  Implication for the IMM shortcut method");

        int[] paraSelectorLevels = {1, 2};
        baselDocMeta.setParagraphSelectorLevel(paraSelectorLevels);
        baselDocMeta.setParaEndRegEx("[.]{5,}[0-9]{1,}|[.]{5,}|\\d+$");

        Map<Integer, String> levelSelector = new HashMap<>();
        levelSelector.put(1, "^(I{1,}[a-z]|I{1,}|V{1,}|X{1,}|I{1,}V{1,}|V{1,}I{1,}).");
        levelSelector.put(2, "^[A-H].");

        baselDocMeta.setLevelSelector(levelSelector);

        return baselDocMeta;
    }*/

    private BaselDocMeta getGlossaryMetadata(JSONObject glossaryMetaData){
        BaselDocMeta baselDocMeta = new BaselDocMeta();

        baselDocMeta.setMetaDocName(glossaryMetaData.getString("levelIdPrefix"));
        baselDocMeta.setStartPage(glossaryMetaData.getInt("fromPage"));
        baselDocMeta.setEndPage(glossaryMetaData.getInt("toPage"));
        baselDocMeta.setStartText(glossaryMetaData.getString("fromText"));
        baselDocMeta.setEndText(glossaryMetaData.getString("toText"));
        baselDocMeta.setParaEndRegEx(glossaryMetaData.getString("paraEndRegex"));

        JSONArray levels = glossaryMetaData.getJSONArray("levels");

        Map<Integer, String> levelSelector = new HashMap<>();
        List<Integer> paraSelectorList = new ArrayList<>();

        for(int i=0; i<levels.length(); i++){
            JSONObject aLevel = levels.getJSONObject(i);
            levelSelector.put(aLevel.getInt("level"), aLevel.getString("regex"));
            if(aLevel.has("hasPara") && aLevel.getBoolean("hasPara")){
                paraSelectorList.add(aLevel.getInt("level"));
            }
        }

        int[] paraSelectorLevels = new int[paraSelectorList.size()];
        for(int i=0; i<paraSelectorList.size(); i++){
            paraSelectorLevels[i] = paraSelectorList.get(i);
        }

        baselDocMeta.setLevelSelector(levelSelector);
        baselDocMeta.setParagraphSelectorLevel(paraSelectorLevels);
        return baselDocMeta;
    }



    public List<DocumentElement> getFlatParaList() {
        return flatParaList;
    }
}
